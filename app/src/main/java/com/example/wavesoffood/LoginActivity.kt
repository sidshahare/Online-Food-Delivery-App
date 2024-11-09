package com.example.wavesoffood

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.wavesoffood.databinding.ActivityLoginBinding
import com.example.wavesoffood.model.UserModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {
    private  var userName:String?=null
    private lateinit var email:String
    private lateinit var password:String
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var googleSignInClient: GoogleSignInClient
    private val binding:ActivityLoginBinding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        // Initialize Firebase Auth
        auth = Firebase.auth

        // Initialize Firebase Database
        database = Firebase.database.reference

        // Initialize Google Sign-In client
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)

        // login with email and password
     binding.loginButton.setOnClickListener{

         // get data frpm text field
         email=binding.emailAddress.text.toString().trim()
         password=binding.password.text.toString().trim()

         if (email.isBlank()|| password.isEmpty()){
             Toast.makeText(this, "Please Fill All the Details", Toast.LENGTH_SHORT).show()

         }else{
             createUser()
             Toast.makeText(this, "Login Successfully", Toast.LENGTH_SHORT).show()
         }
     }

        binding.donthavebutton.setOnClickListener{
            val intent = Intent(this,SignActivity::class.java)
            startActivity(intent)
        }

        // google sign in
       binding.googleButton.setOnClickListener{
           val signIntent=googleSignInClient.signInIntent
           launcher.launch(signIntent)
       }
    }

    //launcher for google sign-in
    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result->
        if(result.resultCode== Activity.RESULT_OK){
            val task =GoogleSignIn.getSignedInAccountFromIntent(result.data)
            if(task.isSuccessful){
                val account : GoogleSignInAccount?=task.result
                val credential= GoogleAuthProvider.getCredential(account?.idToken,null)
                auth.signInWithCredential(credential).addOnCompleteListener{task ->
                    if(task.isSuccessful){
                        Toast.makeText(this, "Sign-In Successfully", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this,MainActivity::class.java))
                        finish()
                    }else{
                        Toast.makeText(this, "Sign in Failed ", Toast.LENGTH_SHORT).show()
                    }

                }
            }
        }else{
            Toast.makeText(this, "Sign in Failed ", Toast.LENGTH_SHORT).show()
        }

    }

    private fun createUser() {
auth.signInWithEmailAndPassword(email,password).addOnCompleteListener{task->
    if(task.isSuccessful){
        val user = auth.currentUser
        updateUi(user)

    }else{
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener{task->
            if(task.isSuccessful){
                saveUserData()
                val user=auth.currentUser
                updateUi(user)
            }else{
                Toast.makeText(this, "Sign-in Failed", Toast.LENGTH_SHORT).show()
            }
        }

    }
      }
        
    }

    private fun saveUserData() {
        // get data frpm text field
        email=binding.emailAddress.text.toString().trim()
        password=binding.password.text.toString().trim()

        val user=UserModel(userName,email,password)
        val userId=FirebaseAuth.getInstance().currentUser!!.uid

        // save data into data base
        database.child("user").child(userId).setValue(user)
    }

    override fun onStart() {
        super.onStart()
        val currentUser=auth.currentUser
        if (currentUser!=null){
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
    private fun updateUi(user: FirebaseUser?) {
        val intent = Intent(this,MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}