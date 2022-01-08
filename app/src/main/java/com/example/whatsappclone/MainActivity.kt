package com.example.whatsappclone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private var flip = 0
    private lateinit var auth: FirebaseAuth
    private lateinit var email: String
    private lateinit var password: String

    fun flip(view: View){
        if(flip==0){
            flip = 1
            findViewById<Button>(R.id.signInButton).text = "Sign Up"
            findViewById<TextView>(R.id.optionsTextView).text = "Existing User?"
            findViewById<TextView>(R.id.flipTextView).text = "Login"
        } else {
            flip = 0
            findViewById<Button>(R.id.signInButton).text = "Login"
            findViewById<TextView>(R.id.optionsTextView).text = "New User?"
            findViewById<TextView>(R.id.flipTextView).text = "Sign Up"
        }
    }

    fun authenticate(view: View){
        email = findViewById<EditText>(R.id.emailEditText).text.toString().trim()
        password = findViewById<EditText>(R.id.passwordEditText).text.toString().trim()
        if(!(Patterns.EMAIL_ADDRESS.matcher(findViewById<EditText>(R.id.emailEditText).text).matches() && email.isNotEmpty())){
            findViewById<EditText>(R.id.emailEditText).error = "Email entered is invalid"
            findViewById<EditText>(R.id.emailEditText).requestFocus()
            return
        }
        if(password.length<8){
            findViewById<EditText>(R.id.passwordEditText).error = "Password length must be greater than 8"
            findViewById<EditText>(R.id.passwordEditText).requestFocus()
            return
        }
        if(findViewById<Button>(R.id.signInButton).text.toString().compareTo("Login")==0){
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        startActivity(Intent(this,HomeActivity::class.java))
                        finish()
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(baseContext, "Authentication failed.",
                            Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        var ref = Firebase.database.getReference("users")
                        ref.addListenerForSingleValueEvent(object: ValueEventListener{
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if(snapshot.value==null){
                                    //first user for the app
                                    var list = ArrayList<String>()
                                    list.add(email+"-"+auth.currentUser?.uid.toString())
                                    ref.setValue(list)
                                } else {
                                    var list = snapshot.getValue<ArrayList<String>>()
                                    list?.add(email+"-"+auth.currentUser?.uid.toString())
                                    ref.setValue(list)
                                }
                            }
                            override fun onCancelled(error: DatabaseError) {
                                Toast.makeText(applicationContext,error.message,Toast.LENGTH_LONG).show()
                            }
                        })
                        startActivity(Intent(this,HomeActivity::class.java))
                        finish()
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(baseContext, "Authentication failed.",
                            Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if(currentUser != null){
            startActivity(Intent(this,HomeActivity::class.java))
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        auth = Firebase.auth
    }
}