package com.example.whatsappclone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class HomeActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        var menuInflater = menuInflater
        menuInflater.inflate(R.menu.menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onResume() {
        super.onResume()
        refresh()
    }

    private fun refresh(){
        findViewById<ProgressBar>(R.id.progressBar).visibility = View.VISIBLE
        var mail_uid_map = HashMap<String,String>()
        var friends_map = HashMap<String,Int>()
        database.getReference("users").addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(SnapShot: DataSnapshot) {
                val list = SnapShot.getValue<ArrayList<String>>()
                for(s in list!!){
                    val arr = s.split("-")
                    mail_uid_map[arr[0]] = arr[1]
                }
                database.getReference(auth.currentUser?.uid.toString()).child("friends").addListenerForSingleValueEvent(object: ValueEventListener{
                    override fun onDataChange(Snapshot: DataSnapshot) {
                        val friends = Snapshot.getValue<ArrayList<String>>()
                        if (friends != null) {
                            for(s in friends){
                                friends_map[s]=1
                            }
                            database.getReference(auth.currentUser?.uid.toString()).child("inbox").addListenerForSingleValueEvent(object: ValueEventListener{
                                override fun onDataChange(snapshot: DataSnapshot) {
                                   val inbox = snapshot.getValue<ArrayList<String>>()
                                   if(inbox!=null){
                                       var map = HashMap<String,ArrayList<String>>()
                                       var list = ArrayList<String>()
                                       inbox.reverse()
                                       for(s in inbox){
                                           var friendStatus = 0
                                           val arr = s.split("|")
                                           if(friends_map[arr[0]] == 1){
                                               friendStatus = 1
                                           }
                                           if(map[arr[0]]==null){
                                               var l = ArrayList<String>()
                                               l.add(arr[1])
                                               map[arr[0]] = l
                                               if(friendStatus == 0)
                                                   list.add(" - from ${arr[0]}")
                                               else
                                                   list.add(arr[0])
                                           } else {
                                               var l = map[arr[0]]
                                               l!!.add(arr[1])
                                               map[arr[0]] = l
                                           }
                                       }
                                       var key: String
                                       var arrayAdapter = ArrayAdapter<String>(applicationContext,android.R.layout.simple_list_item_1,list)
                                       var listView = findViewById<ListView>(R.id.homeActivityListView)
                                       listView.adapter = arrayAdapter
                                       listView.setOnItemClickListener{_,_,position,_ ->
                                           if(list[position].indexOf(" - ")!=-1){
                                               key = list[position].substring(8)
                                           } else {
                                               key = list[position]
                                           }
                                            startActivity(Intent(applicationContext,ChatActivity::class.java).apply {
                                                putExtra("SecondUserEmail", key)
                                                putExtra("SecondUserUid",mail_uid_map[key])
                                            })
                                       }
                                       findViewById<ProgressBar>(R.id.progressBar).visibility = View.INVISIBLE
                                   }
                                }
                                override fun onCancelled(error: DatabaseError) {
                                    Toast.makeText(baseContext,error.message,Toast.LENGTH_LONG).show()
                                    findViewById<ProgressBar>(R.id.progressBar).visibility = View.INVISIBLE
                                }
                            })
                        }
                        else{
                            database.getReference(auth.currentUser?.uid.toString()).child("inbox").addListenerForSingleValueEvent(object: ValueEventListener{
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    val inbox = snapshot.getValue<ArrayList<String>>()
                                    if(inbox!=null){
                                        var map = HashMap<String,ArrayList<String>>()
                                        var list = ArrayList<String>()
                                        inbox.reverse()
                                        for(s in inbox){
                                            var friendStatus = 0
                                            val arr = s.split("|")
                                            if(friends_map[arr[0]] == 1){
                                                friendStatus = 1
                                            }
                                            if(map[arr[0]]==null){
                                                var l = ArrayList<String>()
                                                l.add(arr[1])
                                                map[arr[0]] = l
                                                if(friendStatus == 0)
                                                    list.add(" - from ${arr[0]}")
                                                else
                                                    list.add(arr[0])
                                            } else {
                                                var l = map[arr[0]]
                                                l!!.add(arr[1])
                                                map[arr[0]] = l
                                            }
                                        }
                                        var key: String
                                        var arrayAdapter = ArrayAdapter<String>(applicationContext,android.R.layout.simple_list_item_1,list)
                                        var listView = findViewById<ListView>(R.id.homeActivityListView)
                                        listView.adapter = arrayAdapter
                                        listView.setOnItemClickListener{_,_,position,_ ->
                                            if(list[position].indexOf(" - ")!=-1){
                                                key = list[position].substring(8)
                                            } else {
                                                key = list[position]
                                            }
                                            startActivity(Intent(applicationContext,ChatActivity::class.java).apply {
                                                putExtra("SecondUserEmail", key)
                                                putExtra("SecondUserUid",mail_uid_map[key])
                                            })
                                        }
                                        findViewById<ProgressBar>(R.id.progressBar).visibility = View.INVISIBLE
                                    }
                                }
                                override fun onCancelled(error: DatabaseError) {
                                    Toast.makeText(baseContext,error.message,Toast.LENGTH_LONG).show()
                                    findViewById<ProgressBar>(R.id.progressBar).visibility = View.INVISIBLE
                                }
                            })
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(baseContext,error.message,Toast.LENGTH_LONG).show()
                        findViewById<ProgressBar>(R.id.progressBar).visibility = View.INVISIBLE
                    }
                })
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(baseContext,error.message,Toast.LENGTH_LONG).show()
                findViewById<ProgressBar>(R.id.progressBar).visibility = View.INVISIBLE
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.menuItemAddFriend){
            startActivity(Intent(this,AddFriendActivity::class.java))
        }
        else if(item.itemId == R.id.sendMessageToFriend){
          startActivity(Intent(this,SendMessageToFriend::class.java))
        } else {
            //logout
            auth.signOut()
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        auth = Firebase.auth
        database = Firebase.database
        refresh()
    }
}