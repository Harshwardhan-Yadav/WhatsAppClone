package com.example.whatsappclone

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

class AddFriendActivity : AppCompatActivity() {

    private lateinit var database: FirebaseDatabase

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    fun addFriend(view: View){
        val email = findViewById<EditText>(R.id.addFriendEditText)
        val mail = email.text.toString().trim()
        if(mail.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email.text).matches()){
            email.error = "Enter valid email"
            email.requestFocus()
            return
        }
        var Ref = database.getReference("users")
        Ref.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(Snapshot: DataSnapshot) {
                val userList = Snapshot.getValue<ArrayList<String>>()
                var valid = false
                if (userList != null) {
                    for (s in userList){
                        if(s.indexOf(mail)!=-1){
                            valid = true
                            break
                        }
                    }
                }
                if(valid){
                    var ref = database.getReference(Firebase.auth.currentUser?.uid.toString())
                    ref.addListenerForSingleValueEvent(object: ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if(snapshot.child("friends").value==null){
                                var friends = ArrayList<String>()
                                friends.add(mail)
                                ref.child("friends").setValue(friends)
                            } else {
                                var friends = snapshot.child("friends").getValue<ArrayList<String>>()
                                friends?.add(mail)
                                ref.child("friends").setValue(friends)
                            }
                            Toast.makeText(applicationContext,"Friend added successfully.",Toast.LENGTH_LONG).show()
                            finish()
                        }
                        override fun onCancelled(error: DatabaseError) {
                            Toast.makeText(applicationContext,error.message, Toast.LENGTH_LONG).show()
                        }
                    })
                } else {
                    Toast.makeText(applicationContext,"Required email doesn't exist.", Toast.LENGTH_LONG).show()
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext,error.message, Toast.LENGTH_LONG).show()
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_friend)
        database = Firebase.database
    }
}