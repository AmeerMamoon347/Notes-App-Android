package com.example.todo

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todo.Database.DatabaseHandler
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class MainActivity : AppCompatActivity() {


    lateinit var fab: FloatingActionButton
    lateinit var toolbar: Toolbar
    lateinit var recyclerView: RecyclerView
    lateinit var adapterList: adapter

    lateinit var db:DatabaseHandler

    lateinit var firebaseAuth: FirebaseAuth
    lateinit var userId:String

    var list:ArrayList<Model>?= null
    var taskArray:ArrayList<String>?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fab = findViewById(R.id.fab)
        toolbar = findViewById(R.id.toolbar)
        recyclerView = findViewById(R.id.reycler_View)
        list = ArrayList()

        firebaseAuth = FirebaseAuth.getInstance()
        userId = firebaseAuth.currentUser!!.uid
        
        taskArray = ArrayList()
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        db = DatabaseHandler(this)
        db.openDatabase()


        Log.d("result List ",list.toString())

        var linearLayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = linearLayoutManager
        adapterList = adapter(list!!,this)
        recyclerView.adapter = adapterList

        //Local Database
        getFromDB()

        // To add a Note
        fab.setOnClickListener { view ->

            val dialog = Dialog(this, R.style.DialogCustomTheme)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.add_item_dialogue)
            dialog.show()


            // Note adding Dialogue
            dialog.findViewById<View>(R.id.btn_AddItem).setOnClickListener {
                val item =
                    (dialog.findViewById<View>(R.id.editText_AddItem) as EditText).text.toString()
                if (!TextUtils.isEmpty(item))
                {
                    // SQLite db
                    var task = Model()
                    task.setTask(item)
                    task.setStatus("0")
                    task.setId(1)

                    db.insertTask(task)
                    list?.add(task)

                    adapterList.notifyDataSetChanged()

                    




                } else {
                    Toast.makeText(
                        this,
                        "Please Enter Text",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                dialog.dismiss()


            }



        }


    }


    private fun getFromDB() {
       var listDB = db.getAllTasks

       for(data in listDB)
       {
           Log.d("result",data.getTask()+" "+data.getStatus()+" "+data.getId())
           list?.add(data)

       }
    }




}