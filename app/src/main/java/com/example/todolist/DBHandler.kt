package com.example.todolist

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.todolist.DTO.ToDo
import com.example.todolist.DTO.ToDoItems

class DBHandler(val context: Context) : SQLiteOpenHelper(context,  DB_NAME,  null, DB_VERSION ) {
    override fun onCreate(db: SQLiteDatabase) {
        val createToDoTable = "  CREATE TABLE $TABLE_TODO (" +
                "$COL_ID integer PRIMARY KEY AUTOINCREMENT," +
                "$COL_CREATED_AT datetime DEFAULT CURRENT_TIMESTAMP," +
                "$COL_NAME varchar);"
        val createToDoItemTable =
            "CREATE TABLE $TABLE_TODO_ITEM (" +
                    "$COL_ID integer PRIMARY KEY AUTOINCREMENT," +
                    "$COL_CREATED_AT datetime DEFAULT CURRENT_TIMESTAMP," +
                    "$COL_TODO_ID integer," +
                    "$COL_ITEM_NAME varchar," +
                    "$COL_IS_COLPLETED integer);"


        db.execSQL(createToDoTable)
        db.execSQL(createToDoItemTable)
    }


    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {

    }

    fun addToDo(toDo: ToDo): Boolean {
        val db = writableDatabase
        val cv = ContentValues()
        cv.put(COL_NAME, toDo.name)
        val result = db.insert(TABLE_TODO, null, cv)
        return result != (-1).toLong()
    }
    fun updateToDo(toDo: ToDo){
        val db = writableDatabase
        val cv = ContentValues()
        cv.put(COL_NAME, toDo.name)
        db.update(TABLE_TODO, cv, "$COL_ID = ?", arrayOf(toDo.id.toString()))
    }
    fun deleteToDo(todoId: Long){
        val db = writableDatabase
        db.delete( TABLE_TODO_ITEM, "$COL_TODO_ID =?", arrayOf(todoId.toString()))
        db.delete( TABLE_TODO, "$COL_ID =?", arrayOf(todoId.toString()))
    }
    fun updateToDoItemCompletedStatus(todoId: Long, isCompleted :Boolean){
        val db = writableDatabase
        var queryResult = db.rawQuery("SELECT * FROM $TABLE_TODO_ITEM WHERE $COL_TODO_ID = $todoId ", null)
        if (queryResult.moveToFirst()) {
            do {
                val item = ToDoItems()
                item.id = queryResult.getLong(queryResult.getColumnIndex(COL_ID))
                item.toDoId = queryResult.getLong(queryResult.getColumnIndex(COL_TODO_ID))
                item.itemName = queryResult.getString(queryResult.getColumnIndex(COL_ITEM_NAME))
                item.isCompleted = isCompleted

                updateToDoItem(item)

            } while (queryResult.moveToNext())
        }

        queryResult.close()
    }

    fun getToDos(): MutableList<ToDo> {
        var result: MutableList<ToDo> = ArrayList()
        var db: SQLiteDatabase = readableDatabase
        var queryResult = db.rawQuery("SELECT * FROM $TABLE_TODO ", null)
        if (queryResult.moveToFirst()) {
            do {
                val todo = ToDo()
                todo.id = queryResult.getLong(queryResult.getColumnIndex(COL_ID))
                todo.name = queryResult.getString(queryResult.getColumnIndex(COL_NAME))
                result.add(todo)

            } while (queryResult.moveToNext())
        }

        queryResult.close()
        return result
    }

    fun addToDoItem(item: ToDoItems): Boolean {
        val db = writableDatabase
        val cv = ContentValues()
        cv.put(COL_ITEM_NAME, item.itemName)
        cv.put(COL_TODO_ID, item.toDoId)
        cv.put(COL_IS_COLPLETED, item.isCompleted)

        val result = db.insert(TABLE_TODO_ITEM, null, cv)
        return result != (-1).toLong()
    }

    fun updateToDoItem(item: ToDoItems) {
        val db = writableDatabase
        val cv = ContentValues()
        cv.put(COL_ITEM_NAME, item.itemName)
        cv.put(COL_TODO_ID, item.toDoId)
        cv.put(COL_IS_COLPLETED, item.isCompleted)

        db.update(TABLE_TODO_ITEM, cv, "$COL_ID=?", arrayOf(item.id.toString()))
        //val result = db.insert(TABLE_TODO_ITEM, null, cv)
        //return result != (-1).toLong()
    }
    fun deleteToDoItems(itemId: Long){
        val db = writableDatabase
        db.delete(TABLE_TODO_ITEM, "$COL_ID=?", arrayOf(itemId.toString()))
    }


    fun getTodoItem(todoId :Long) : MutableList<ToDoItems>{
        var result: MutableList<ToDoItems> = ArrayList()
        var db: SQLiteDatabase = readableDatabase
        var queryResult = db.rawQuery("SELECT * FROM $TABLE_TODO_ITEM WHERE $COL_TODO_ID = $todoId ", null)
        if (queryResult.moveToFirst()) {
            do {
                val item = ToDoItems()
                item.id = queryResult.getLong(queryResult.getColumnIndex(COL_ID))
                item.toDoId = queryResult.getLong(queryResult.getColumnIndex(COL_TODO_ID))
                item.itemName = queryResult.getString(queryResult.getColumnIndex(COL_ITEM_NAME))
                item.isCompleted = queryResult.getInt(queryResult.getColumnIndex(COL_IS_COLPLETED)) == 1

                result.add(item)

            } while (queryResult.moveToNext())
        }

        queryResult.close()
        return result
    }
}