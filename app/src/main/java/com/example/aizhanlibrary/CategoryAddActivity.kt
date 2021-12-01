package com.example.aizhanlibrary

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class CategoryAddActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var progressDialog: ProgressDialog
    private var backBtn: Button ?= null
    private var submitBtn: Button ?= null
    private var categoryEt: EditText?= null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_add)

        firebaseAuth = FirebaseAuth.getInstance()
        backBtn = findViewById(R.id.backBtn)
        submitBtn = findViewById(R.id.submitBtn)
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait...")

        progressDialog.setCanceledOnTouchOutside(false)
        categoryEt = findViewById(R.id.category_et)

        backBtn?.setOnClickListener {
            onBackPressed()
        }

        submitBtn?.setOnClickListener {
            validateData()
        }

    }

    private var category = ""
    private fun validateData() {
       category =categoryEt?.text.toString().trim()
        if(category.isEmpty()){
            Toast.makeText(this, "Enter category...", Toast.LENGTH_SHORT).show()
        }else {
            addCategoryFirebase()
        }
    }

    private fun addCategoryFirebase() {
        progressDialog.show()
        val timestamp = System.currentTimeMillis()
        val hashMap = HashMap<String, Any>()

        hashMap["id"] = "$timestamp"
        hashMap["category"] = category
        hashMap["timestamp"] = timestamp
        hashMap["uid"] = "${firebaseAuth.uid}"
        val ref = FirebaseDatabase.getInstance().getReference("Categories")
        ref.child("$timestamp")
            .setValue(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(this, "Successfully added", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(this, "Failed to add due to ${e.message}", Toast.LENGTH_SHORT).show()
            }

    }
}