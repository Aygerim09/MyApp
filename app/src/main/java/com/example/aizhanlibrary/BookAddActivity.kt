package com.example.aizhanlibrary

import android.app.AlertDialog
import android.app.Application
import android.app.Instrumentation
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

class BookAddActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var progressDialog: ProgressDialog

    private lateinit var categoryArrayList: ArrayList<ModelCategory>

    private var pdfUri: Uri?=null

    private val TAG = "PDF_ADD_TAG"

    private var backBtn: Button?= null
    private var submitBtn: Button?= null
    private var titleEt: EditText?= null
    private var descriptionEt: EditText?= null
    private var categoryEt: EditText?= null
    private var attachPdfBtn: ImageButton?= null

    private var categoryTv: TextView?= null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_add)

        firebaseAuth = FirebaseAuth.getInstance()

        loadPdfCategories()
        backBtn = findViewById(R.id.backBtn)
        submitBtn = findViewById(R.id.submitBtn)
        categoryEt = findViewById(R.id.category_et)
        descriptionEt = findViewById(R.id.description_et)
        titleEt = findViewById(R.id.title_et)

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait...")
        progressDialog.setCanceledOnTouchOutside(false)


        categoryTv = findViewById(R.id.categoryTv)
        categoryTv?.setOnClickListener {
            categoryPickDialog()
        }
        attachPdfBtn = findViewById(R.id.attachPdfBtn)
        attachPdfBtn?.setOnClickListener {
            pdfPickIntent()
        }

        backBtn?.setOnClickListener {
            onBackPressed()
        }

        submitBtn?.setOnClickListener {
            validateData()
        }

    }

    private fun loadPdfCategories() {
        Log.d(TAG, "LoadPDfCategories")
        categoryArrayList = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference("Categories")
        ref.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                categoryArrayList.clear()
                for(ds in snapshot.children){
                    val model = ds.getValue(ModelCategory::class.java)
                    Log.d("Loop Go... ","${model!!.uid}")
                    categoryArrayList.add(model!!)
                    Log.d(TAG, "Ondata${model.category}")
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
    private var selectedCategoryId = ""
    private var selectedCategoryTitle = ""
    private fun categoryPickDialog(){
        Log.d(TAG, "categpryPickDialog")

        val categoriesArray = arrayOfNulls<String>(categoryArrayList.size)
        for (i in categoryArrayList.indices){
            categoriesArray[i] = categoryArrayList[i].category

        }
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Pick Category")
            .setItems(categoriesArray){dialog, which->
                selectedCategoryTitle = categoryArrayList[which].category
                selectedCategoryId = categoryArrayList[which].id

                categoryTv?.text = selectedCategoryTitle
                Log.d(TAG, "Picked Category Dialog ID: $selectedCategoryId" )
            }.show()
    }

    fun pdfPickIntent(){
        Log.d(TAG, "PdfPickIntent: starting pdf intent")

        val intent = Intent()
        intent.type = "application/pdf"
        intent.action = Intent.ACTION_GET_CONTENT
        pdfActivityResultLauncher.launch(intent)
    }
    val pdfActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback<ActivityResult> { result ->
            if (result.resultCode == RESULT_OK){
                Log.d(TAG, "PDF Picked: ")
                pdfUri = result.data!!.data
            }
            else{
                Log.d(TAG, "PDF Picked cancelled")
                Toast.makeText(this, "cancelled", Toast.LENGTH_SHORT).show()
            }
        }
    )

    private var title = ""
    private var description = ""
    private var category = ""
    private fun validateData() {
        title =titleEt?.text.toString().trim()
        description =descriptionEt?.text.toString().trim()
        category =categoryEt?.text.toString().trim()
        if(title.isEmpty()){
            Toast.makeText(this, "Enter category...", Toast.LENGTH_SHORT).show()
        }else if (description.isEmpty()){
            Toast.makeText(this, "Enter category...", Toast.LENGTH_SHORT).show()
        }else if (category.isEmpty()){
            Toast.makeText(this, "Enter category...", Toast.LENGTH_SHORT).show()
        } else if(pdfUri==null) {
            Toast.makeText(this, "Enter category...", Toast.LENGTH_SHORT).show()
        }
            else {
                uploadPdfToStorage()
            }
    }

    private fun uploadPdfToStorage() {
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Uploading PDF..")
        progressDialog.show()
        val timestamp = System.currentTimeMillis()

        val filePathAndName = "Books/$timestamp"

        val storageReference = FirebaseStorage.getInstance().getReference(filePathAndName)
        storageReference.putFile(pdfUri!!)
            .addOnSuccessListener {taskSnapshot->
                val uriTask: Task<Uri> = taskSnapshot.storage.downloadUrl
                while(!uriTask.isSuccessful);
                val uploadedPdfUrl = "${uriTask.result}"
                uploadPdfInfoToDb(uploadedPdfUrl, timestamp)

            }
            .addOnFailureListener { e->
                Log.d(TAG, "uploadTOPDf: failed to upload due tp ${e.message}")
                progressDialog.dismiss()
                Toast.makeText(this, "failed to upload due tp ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun uploadPdfInfoToDb(uploadedPdfUrl: String, timestamp: Long) {
        progressDialog.setMessage("Uploading pdf info")
        val uid = firebaseAuth.uid

        val hashMap: HashMap<String, Any> = HashMap()
        hashMap["uid"] = "$uid"
        hashMap["id"] = "$timestamp"
        hashMap["title"] = "$title"
        hashMap["description"] = "$description"
        hashMap["categoryId"] = "$selectedCategoryId"
        hashMap["url"] = "$uploadedPdfUrl"
        hashMap["timestamp"] = timestamp
        hashMap["viewsCount"] = 0
        hashMap["downloadsCount"] = 0

        val ref = FirebaseDatabase.getInstance().getReference("Books")
        ref.child("$timestamp")
            .setValue(hashMap)
            .addOnSuccessListener {
                Log.d(TAG, "uploadTOPDf: success to upload")
                progressDialog.dismiss()
                Toast.makeText(this, "Uploaded PDF ", Toast.LENGTH_SHORT).show()
                pdfUri=null
            }
            .addOnFailureListener { e->
                Log.d(TAG, "uploadTOPDf: failed to upload due tp ${e.message}")
                progressDialog.dismiss()
                Toast.makeText(this, "failed to upload due tp ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}