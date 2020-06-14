package com.example.todolist

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.DTO.ToDo
import com.example.todolist.DTO.ToDoItems
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.activity_item.*

class ItemActivity : AppCompatActivity() {

    lateinit var dbHandler: DBHandler
    var todoId :Long = -1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item)
        setSupportActionBar(item_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.title= intent.getStringExtra(INTENT_TODO_NAME)

        dbHandler = DBHandler(this)
        todoId = intent.getLongExtra(INTENT_TODO_ID, -1)
        rv_item.layoutManager = LinearLayoutManager(this)

        fab_item.setOnClickListener(){
            val dilog = AlertDialog.Builder(this)
            dilog.setTitle("Add ToDo Item")
            val view = layoutInflater.inflate(R.layout.dilog_dashboard, null)
            val toDoName = view.findViewById<EditText>(R.id.et_todo)
            dilog.setView(view)
            dilog.setPositiveButton("Add") { _: DialogInterface, i: Int ->
                if (toDoName.text.isNotEmpty() ){
                    val item = ToDoItems()
                    item.toDoId = todoId
                    item.itemName = toDoName.text.toString()
                    item.isCompleted = false
                   dbHandler.addToDoItem(item)
                    refreshList()
                }
            }
            dilog.setNegativeButton("Cancel")  { _: DialogInterface, i: Int ->
            }
            dilog.show()
        }
    }
    fun updateItem(item : ToDoItems){
        val dilog = AlertDialog.Builder(this)
        dilog.setTitle("Update ToDo Item")
        val view = layoutInflater.inflate(R.layout.dilog_dashboard, null)
        val toDoName = view.findViewById<EditText>(R.id.et_todo)
        toDoName.setText(item.itemName)
        dilog.setView(view)
        dilog.setPositiveButton("Update") { _: DialogInterface, i: Int ->
            if (toDoName.text.isNotEmpty() ){
                item.toDoId = todoId
                item.itemName = toDoName.text.toString()
                item.isCompleted = false
                dbHandler.updateToDoItem(item)
                refreshList()
            }
        }
        dilog.setNegativeButton("Cancel")  { _: DialogInterface, i: Int ->
        }
        dilog.show()

    }
    override fun onResume() {
        refreshList()
        super.onResume()
    }
    private fun refreshList(){
        rv_item.adapter = ItemActivity.ItemAdapter(this, dbHandler.getTodoItem(todoId))
    }
    class ItemAdapter(val activity: ItemActivity, val list: MutableList<ToDoItems>) :
        RecyclerView.Adapter<ItemAdapter.ViewHolder>() {
        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
            return ViewHolder(LayoutInflater.from(activity).inflate(R.layout.rv_child_item, p0, false))
        }

        override fun getItemCount(): Int {
            return list.size
        }

        override fun onBindViewHolder(holder: ViewHolder, p1: Int) {
            holder.itemName.text = list[p1].itemName
            holder.itemName.isChecked = list[p1].isCompleted
            holder.itemName.setOnClickListener {
                list[p1].isCompleted =! list[p1].isCompleted
                activity.dbHandler.updateToDoItem(list[p1])
            }
            holder.delete.setOnClickListener(){
                val dilog = AlertDialog.Builder(activity)
                dilog.setTitle("Are you sure")
                dilog.setMessage("Do you want to delete this?")
                dilog.setPositiveButton("Continue") { _: DialogInterface, _: Int ->
                    activity.dbHandler.deleteToDoItems(list[p1].id)
                    activity.refreshList()
                }
                dilog.setNegativeButton("Cancel") { _: DialogInterface, _: Int ->

                }
                dilog.show()
            }
            holder.edit.setOnClickListener(){
                activity.updateItem(list[p1])
                activity.refreshList()
            }
        }

        class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
            val itemName: CheckBox = v.findViewById(R.id.cb_item)
            val edit: ImageView = v.findViewById(R.id.iv_edit)
            val delete: ImageView = v.findViewById(R.id.iv_delete)
        }
    }

    fun OnOptionsItemSelected(item: MenuItem?) : Boolean{
        return if(item?.itemId == android.R.id.home){
                  finish()
                    true
                }else{
                        super.onOptionsItemSelected(item)
                     }

    }
}