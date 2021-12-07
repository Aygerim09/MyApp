package com.example.library

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity2 : AppCompatActivity() {
    var userId: String ?=null
    var mailId: String ?=null
    var profile_layout : LinearLayout?=null
    var mybooks_layout : LinearLayout?=null
    var search_layout : LinearLayout?=null
    var home_layout : LinearLayout?=null
    var textView : TextView?=null
    var addCategoryBtn : Button?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        userId = FirebaseAuth.getInstance().currentUser!!.uid
        mailId = FirebaseAuth.getInstance().currentUser!!.email.toString()
        profile_layout = findViewById(R.id.profile_layout)
        search_layout = findViewById(R.id.search_layout)
        mybooks_layout = findViewById(R.id.mybooks_layout)
        home_layout = findViewById(R.id.home_layout)
        textView = findViewById(R.id.textView)
        textView?.setText(mailId)
        addCategoryBtn = findViewById(R.id.addCategoryBtn)
        addCategoryBtn?.setOnClickListener {
            startActivity(Intent(this@MainActivity2, CategoryAddActivity::class.java))
        }
        val navigationView: BottomNavigationView =findViewById(R.id.navigation)
        navigationView.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.navigation_search -> {
                    startActivity(Intent(this@MainActivity2, BookAddActivity::class.java))
                }
                R.id.navigation_profile -> {
                    showProfile()
                }
                R.id.navigation_home -> {
                    startActivity(Intent(this@MainActivity2, CategoryAddActivity::class.java))
                }
            }
            true
        }

    }
    fun showProfile(){
        search_layout?.visibility= View.INVISIBLE
        home_layout?.visibility= View.INVISIBLE
        mybooks_layout?.visibility= View.INVISIBLE
        profile_layout?.visibility= View.VISIBLE
    }
    fun showMyBooks(){
        search_layout?.visibility= View.INVISIBLE
        home_layout?.visibility= View.INVISIBLE
        mybooks_layout?.visibility= View.VISIBLE
        profile_layout?.visibility= View.INVISIBLE
    }
    fun showHome(){
        search_layout?.visibility= View.INVISIBLE
        home_layout?.visibility= View.VISIBLE
        mybooks_layout?.visibility= View.INVISIBLE
        profile_layout?.visibility= View.INVISIBLE
    }
    fun showSearch(){
        search_layout?.visibility= View.INVISIBLE
        home_layout?.visibility= View.INVISIBLE
        mybooks_layout?.visibility= View.INVISIBLE
        profile_layout?.visibility= View.VISIBLE
    }
}