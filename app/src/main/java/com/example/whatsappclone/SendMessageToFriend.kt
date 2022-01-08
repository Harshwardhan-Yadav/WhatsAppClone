package com.example.whatsappclone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.Toast
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

class SendMessageToFriend : AppCompatActivity() {

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_message_to_friend)
        var database = Firebase.database
        var auth = Firebase.auth
        var mail_uid_map = HashMap<String,String>()
        database.getReference("users").addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(SnapShot: DataSnapshot) {
                val list = SnapShot.getValue<ArrayList<String>>()
                for(s in list!!){
                    val arr = s.split("-")
                    mail_uid_map[arr[0]] = arr[1]
                }
                Firebase.database.getReference(Firebase.auth.currentUser?.uid.toString()).child("friends").addListenerForSingleValueEvent(object: ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        var list = snapshot.getValue<ArrayList<String>>()
                        if(list == null){
                            list = ArrayList<String>()
                            list.add("No friends to send message. Add a friend first")
                            var listView = findViewById<ListView>(R.id.friendsListView)
                            var arrayAdapter = ArrayAdapter(applicationContext,android.R.layout.simple_list_item_1,list)
                            listView.adapter = arrayAdapter
                        } else {
                            var listView = findViewById<ListView>(R.id.friendsListView)
                            var arrayAdapter = ArrayAdapter(applicationContext,android.R.layout.simple_list_item_1,list)
                            listView.adapter = arrayAdapter
                            listView.setOnItemClickListener { _, _, position, _ ->
                                val key = list[position]
                                startActivity(Intent(applicationContext, ChatActivity::class.java).apply {
                                    putExtra("SecondUserEmail", key)
                                    putExtra("SecondUserUid", mail_uid_map[key])
                                })
                            }
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(applicationContext,error.message,Toast.LENGTH_LONG).show()
                    }
                })
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(baseContext,error.message,Toast.LENGTH_LONG).show()
                findViewById<ProgressBar>(R.id.progressBar).visibility = View.INVISIBLE
            }
        })
    }
}