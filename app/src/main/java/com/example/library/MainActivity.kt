package com.example.library

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class MainActivity : AppCompatActivity() {
    var skipBtn : Button?=null
    var loginBtn : Button?=null
    var registerBtn : Button?=null
    var backBtn : Button?=null
    var back_btn : Button?=null

    var signinBtn: Button?=null
    var saveBtn: Button?=null

    var name: EditText?=null
    var email: EditText?=null
    var password: EditText?=null


    var login_password: EditText?=null
    var login_mail: EditText?=null

    var registration_layout : LinearLayout?=null
    var login_layout : LinearLayout?=null
    var main_layout : LinearLayout?=null
    var userId: String?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        skipBtn = findViewById(R.id.skipBtn)
        loginBtn = findViewById(R.id.loginBtn)
        registerBtn = findViewById(R.id.registerBtn)
        backBtn = findViewById(R.id.backBtn)
        back_btn = findViewById(R.id.back_btn)
        registration_layout=findViewById(R.id.register_layout)
        login_layout=findViewById(R.id.login_layout)
        main_layout=findViewById(R.id.main_layout)
        signinBtn=findViewById(R.id.signin_btn)
        saveBtn=findViewById(R.id.save_btn)
        password=findViewById(R.id.password)
        email=findViewById(R.id.email)
        name=findViewById(R.id.name)
        login_mail=findViewById(R.id.login_mail)
        login_password=findViewById(R.id.login_password)

        skipBtn?.setOnClickListener {
            startActivity(Intent(this, MainActivity2::class.java))
        }

        loginBtn?.setOnClickListener {
            showLogin()
        }
        registerBtn?.setOnClickListener {
            showRegistration()
        }
        backBtn?.setOnClickListener {
            showMain()
        }
        back_btn?.setOnClickListener {
            showMain()
        }

        saveBtn?.setOnClickListener {
            when {
                TextUtils.isEmpty(email?.text.toString().trim() {it <= ' '})-> {
                    Toast.makeText(
                        this,
                        "Please enter email",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                TextUtils.isEmpty(password?.text.toString().trim() {it <= ' '})-> {
                    Toast.makeText(
                        this,
                        "Please enter password",
                        Toast.LENGTH_SHORT
                    ).show()
                } else -> {
                val email_rt: String = email?.text.toString().trim() {it<=' '}
                val password_rt: String = password?.text.toString().trim() {it<=' '}
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email_rt, password_rt)
                    .addOnCompleteListener(
                        OnCompleteListener<AuthResult>{
                                task ->
                            if(task.isSuccessful){
                                val firebaseUser: FirebaseUser = task.result!!.user!!
                                Toast.makeText(
                                    this,
                                    "You are registered successfully",
                                    Toast.LENGTH_SHORT
                                ).show()

                                val intent =
                                    Intent(this, MainActivity2::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                intent.putExtra("user_id", firebaseUser.uid)
                                intent.putExtra("email_id", email_rt)
                                startActivity(intent)
                                finish()
                            } else {
                                Toast.makeText(
                                    this,
                                    task.exception!!.message.toString(),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        })
            }
        }
        }

        signinBtn?.setOnClickListener {
            Log.d("TEST???", "signinBtn!")

            when {
                TextUtils.isEmpty(login_mail?.text.toString().trim() {it <= ' '})-> {
                    Toast.makeText(
                        this,
                        "Please enter email",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                TextUtils.isEmpty(login_password?.text.toString().trim() {it <= ' '})-> {
                    Toast.makeText(
                        this,
                        "Please enter password",
                        Toast.LENGTH_SHORT
                    ).show()
                } else -> {
                val email_rt: String = login_mail?.text.toString().trim() {it<=' '}
                val password_rt: String = login_password?.text.toString().trim() {it<=' '}
                FirebaseAuth.getInstance().signInWithEmailAndPassword(email_rt, password_rt)
                    .addOnCompleteListener(
                        OnCompleteListener<AuthResult>{
                                task ->
                            if(task.isSuccessful){
                                Toast.makeText(
                                    this,
                                    "You are logged in successfully",
                                    Toast.LENGTH_SHORT
                                ).show()

                                val intent =
                                    Intent(this, MainActivity2::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                intent.putExtra("user_id",
                                    FirebaseAuth.getInstance().currentUser!!.uid)
                                intent.putExtra("email_id", FirebaseAuth.getInstance().currentUser!!.email)
                                startActivity(intent)
                                finish()
                            } else {
                                Toast.makeText(
                                    this,
                                    task.exception!!.message.toString(),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        })
            }
            }
        }
    }


    fun showRegistration(){
        login_layout?.visibility= View.INVISIBLE
        main_layout?.visibility= View.INVISIBLE
        registration_layout?.visibility= View.VISIBLE
    }
    fun showLogin(){
        login_layout?.visibility= View.VISIBLE
        main_layout?.visibility= View.INVISIBLE
        registration_layout?.visibility= View.INVISIBLE
    }
    fun showMain(){
        main_layout?.visibility= View.VISIBLE
        login_layout?.visibility= View.INVISIBLE
        registration_layout?.visibility= View.INVISIBLE
    }
}