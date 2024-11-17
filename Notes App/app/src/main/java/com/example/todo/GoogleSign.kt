package com.example.todo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.*

class GoogleSign : AppCompatActivity() {

    lateinit var firebaseAuth:FirebaseAuth
    lateinit var googleSignInClient: GoogleSignInClient
    val LOGIN_CODE = 101
    lateinit var btnSign:ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_google_sign)

        supportActionBar?.hide()
         btnSign = findViewById(R.id.btnSign)
         firebaseAuth = FirebaseAuth.getInstance()


        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_clientID))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this,googleSignInOptions)


         btnSign.setOnClickListener {
              signIn()
         }

    }


    private fun signIn() {
        val googleSignInIntent = googleSignInClient.signInIntent
        startActivityForResult(googleSignInIntent,LOGIN_CODE)
    }


    // If user is already logged in
    override fun onStart() {
        super.onStart()

        if(firebaseAuth.currentUser!=null)
        {
            var intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == LOGIN_CODE)
        {
            // Result got from calling the intent... GoogleSignInApI.getSignInIntent()......
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // After successfully sign In, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                Log.d("firebaseAuthWithGoogle:", account.id!!)
                firebaseAuthWithGoogle(account.idToken!!)
            } 
            catch (e: ApiException) {
                e.printStackTrace()
                // Google sign In failed
                Log.d("Google Sign In Failed: ", e.message!!)
            }
        }

    }


    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(this,
            OnCompleteListener<AuthResult?> { task ->
                if (task.isSuccessful) {
                    Log.d("Successfully: ", "Signed in")
                    val user: FirebaseUser = firebaseAuth.currentUser!!
                    Log.d("Current user Info: ", user.uid)
                    Toast.makeText(this, "Successfully signed In!", Toast.LENGTH_SHORT)
                        .show()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    Log.d("Sign In failed !", task.exception.toString())
                }
            }
        )
    }


    //Sign out
    fun signOut() {
        FirebaseAuth.getInstance().signOut()
    }

}