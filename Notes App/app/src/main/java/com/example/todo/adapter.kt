package com.example.todo

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.todo.Database.DatabaseHandler
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class adapter(itemOfList:ArrayList<Model>,mainActivityContext: MainActivity) : RecyclerView.Adapter<adapter.viewHolder>(){

       var list:ArrayList<Model> ?= null
       var context:Context?= null
       var dbHandler:DatabaseHandler?=null
       var mainActivityContext:MainActivity?=null



        inner class viewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),View.OnClickListener,View.OnLongClickListener {

                  lateinit var checkBox:CheckBox
                  lateinit var mView:View
                  lateinit var editImg:ImageView

            init {
                itemView.setOnLongClickListener(this)
                itemView.setOnClickListener(this)

                checkBox = itemView.findViewById<CheckBox>(R.id.checkbox)
                mView = itemView
                editImg = itemView.findViewById(R.id.editImaage)
            }


            override fun onClick(v: View?) {
            }

            override fun onLongClick(v: View?): Boolean {
               return true
            }


        }

      init {
         list = itemOfList
        this.mainActivityContext = mainActivityContext
          notifyDataSetChanged()

      }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_view,parent,false)
        context = parent.context

        return viewHolder(view)

    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        dbHandler = DatabaseHandler(mainActivityContext)
        dbHandler?.openDatabase()

        val itemOfList = list?.get(position)


        holder.checkBox.isChecked =  itemOfList?.status?.toInt() == 1
        holder.checkBox.setText(itemOfList?.getTask())


        holder.checkBox.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                dbHandler?.updateStatus(itemOfList!!.getId(), 1)

            } else {
                dbHandler?.updateStatus(itemOfList!!.getId(), 0)
            }
        })


        // For editing Notes
        holder.editImg.setOnClickListener {
            var task = itemOfList?.getTask()

            val dialog = Dialog(context!!, R.style.DialogCustomTheme)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.edit_item_dialogue)
            dialog.show()

            var editTxtUpdate = dialog.findViewById<EditText>(R.id.editText_UpdateItem)
            editTxtUpdate.setText(task)

            dialog.findViewById<View>(R.id.btn_UpdateItem).setOnClickListener {
                val item =
                    (dialog.findViewById<View>(R.id.editText_UpdateItem) as EditText).text.toString()
                if (!TextUtils.isEmpty(item))
                {
                    // SQLite db
                    dbHandler!!.updateTask(itemOfList!!.getId(),item)

                    var m = Model()
                    m.setId(itemOfList.getId())
                    m.setStatus(itemOfList.getStatus())
                    m.setTask(item)
                    list?.set(position,m)

                    notifyItemChanged(position)


                } else {
                    Toast.makeText(
                        context,
                        "Please Enter Text ",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                dialog.dismiss()


            }

        }


        // for removing Notes -> Long click on item

        holder.itemView.setOnLongClickListener {

            removeTask(position,itemOfList!!.getId())
            true
        }


        holder.checkBox.setOnLongClickListener{
            removeTask(position,itemOfList!!.getId())
            true
        }


    }



    override fun getItemCount(): Int {
        return list?.size!!
    }

    private fun removeTask(position:Int, itemId:Int)
    {
        Log.d("size",list?.size.toString())

        val dialog = Dialog(context!!, R.style.DialogCustomTheme)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.delete_item_dialogue)
        dialog.show()

        dialog.findViewById<View>(R.id.btn_deleteItem).setOnClickListener {

            list?.removeAt(position)
            dbHandler?.deleteTask(itemId)
            notifyDataSetChanged()

            dialog.dismiss()
        }

    }












}