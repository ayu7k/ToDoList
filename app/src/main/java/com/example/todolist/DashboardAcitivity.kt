package com.example.todolist

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.DTO.ToDo
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.coroutines.NonCancellable.isCompleted
import org.w3c.dom.Text
import kotlin.coroutines.coroutineContext

class DashboardAcitivity : AppCompatActivity() {

    lateinit var dbHandler: DBHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        setSupportActionBar(dashboard_toolbar)
        title = "Dashboard"

        dbHandler = DBHandler(this)
        rv_recyclerView.layoutManager = LinearLayoutManager(this)


        fab_dashboard.setOnClickListener() {
            val dilog = AlertDialog.Builder(this)
            val view = layoutInflater.inflate(R.layout.dilog_dashboard, null)
            val toDoName = view.findViewById<EditText>(R.id.et_todo)
            dilog.setView(view)
            dilog.setPositiveButton("Add") { _: DialogInterface, i: Int ->
                if (toDoName.text.isNotEmpty()) {
                    val toDo = ToDo()
                    toDo.name = toDoName.text.toString()
                    dbHandler.addToDo(toDo)
                    refreshList()
                }

            }
            dilog.setNegativeButton("Cancel") { _: DialogInterface, i: Int ->

            }
            dilog.show()
        }
    }

    fun updateTodo(toDo: ToDo) {
        val dilog = AlertDialog.Builder(this)
        val view = layoutInflater.inflate(R.layout.dilog_dashboard, null)
        val toDoName = view.findViewById<EditText>(R.id.et_todo)
        toDoName.setText(toDo.name)
        dilog.setView(view)
        dilog.setPositiveButton("Add") { _: DialogInterface, i: Int ->
            if (toDoName.text.isNotEmpty()) {
                //val toDo = ToDo()
                toDo.name = toDoName.text.toString()
                dbHandler.updateToDo(toDo)
                refreshList()
            }

        }
        dilog.setNegativeButton("Cancel") { _: DialogInterface, i: Int ->

        }
        dilog.show()
    }


    override fun onResume() {
        refreshList()
        super.onResume()
    }

    private fun refreshList() {
        rv_recyclerView.adapter = DashboardAdapter(this, dbHandler.getToDos())
    }

    class DashboardAdapter(var activity: DashboardAcitivity, val list: MutableList<ToDo>) :
        RecyclerView.Adapter<DashboardAdapter.ViewHolder>() {
        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
            return ViewHolder(
                LayoutInflater.from(activity).inflate(R.layout.rv_child_dashboard, p0, false)
            )
        }

        override fun getItemCount(): Int {
            return list.size
        }

        override fun onBindViewHolder(holder: ViewHolder, p1: Int) {
            holder.toDoName.text = list[p1].name

            holder.toDoName.setOnClickListener {
                val intent = Intent(activity, ItemActivity::class.java)
                intent.putExtra(INTENT_TODO_ID, list[p1].id)
                intent.putExtra(INTENT_TODO_NAME, list[p1].name)
                activity.startActivity(intent)
            }
            holder.menu.setOnClickListener() {
                val popup = PopupMenu(activity, holder.menu)
                popup.inflate(R.menu.dashboard_child)
                popup.setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.menu_edit -> {
                            activity.updateTodo(list[p1])
                        }
                        R.id.menu_delete -> {
                            val dilog = AlertDialog.Builder(activity)
                            dilog.setTitle("Are you sure")
                            dilog.setMessage("Do you want to delete task?")
                            dilog.setPositiveButton("Continue") { _: DialogInterface, _: Int ->
                                activity.dbHandler.deleteToDo(list[p1].id)
                                activity.refreshList()
                            }
                            dilog.setNegativeButton("Cancel") { _: DialogInterface, _: Int ->

                            }
                            dilog.show()


                        }
                        R.id.menu_completed -> {
                            activity.dbHandler.updateToDoItemCompletedStatus(list[p1].id, true)
                        }
                        R.id.menu_reset -> {
                            activity.dbHandler.updateToDoItemCompletedStatus(list[p1].id, false)
                        }
                    }

                    true
                }
                popup.show()
            }
        }

        class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
            val toDoName: TextView = v.findViewById(R.id.tv_todo_name)
            val menu: ImageView = v.findViewById(R.id.iv_menu)
        }
    }
}
