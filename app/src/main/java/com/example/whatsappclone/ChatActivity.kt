package com.example.whatsappclone

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

class ChatActivity : AppCompatActivity() {

    private lateinit var message: ArrayList<String>
    private lateinit var secondUser: String
    private lateinit var currentUser: String
    private lateinit var secondUserEmail: String

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
    private fun scrollMyListViewToBottom(myListView: ListView,myListAdapter: ArrayAdapter<String>) {
        myListView.post(Runnable {
            myListView.setSelection(myListAdapter.getCount() - 1)
        })
    }

    private fun setAdapter(){
        var listView = findViewById<ListView>(R.id.chatList)
        var arrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, message)
        listView.adapter = arrayAdapter
        scrollMyListViewToBottom(listView,arrayAdapter)
    }

    fun send(view: View){
        val chat = findViewById<EditText>(R.id.chatEditText)
        val chatText = chat.text.toString().trim()
        if(chat.text.isEmpty()){
            chat.error = "Chat cannot be empty"
            chat.requestFocus()
        } else {
            val ref1 = Firebase.database.getReference(currentUser).child("inbox")
            val ref2 = Firebase.database.getReference(secondUser).child("inbox")
            ref1.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var list = snapshot.getValue<ArrayList<String>>()
                    if(list == null){
                        list = ArrayList<String>()
                    }
                    list.add("${secondUserEmail}" + "|" + "${chatText}")
                    ref1.setValue(list)
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(applicationContext, error.message, Toast.LENGTH_LONG).show()
                }
            })
            ref2.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var list = snapshot.getValue<ArrayList<String>>()
                    if(list == null){
                        list = ArrayList<String>()
                    }
                    list!!.add("${Firebase.auth.currentUser?.email.toString()}" + "|" + "${chatText}")
                    ref2.setValue(list)
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(applicationContext, error.message, Toast.LENGTH_LONG).show()
                }
            })
        }
        chat.text = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        message = ArrayList()
        currentUser = Firebase.auth.currentUser?.uid.toString()
        secondUser = intent.getStringExtra("SecondUserUid").toString()
        secondUserEmail = intent.getStringExtra("SecondUserEmail").toString()
        setAdapter()
        Firebase.database.getReference(currentUser).child("inbox").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                message = ArrayList()
                val list = snapshot.getValue<ArrayList<String>>() as ArrayList<String>
                if (list != null) {
                    for (s in list) {
                        var arr = s.split("|")
                        if (arr[0].compareTo(secondUserEmail) == 0) {
                            message.add(arr[1])
                        }
                    }
                    setAdapter()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(baseContext, error.message, Toast.LENGTH_LONG).show()
            }
        })
    }
}