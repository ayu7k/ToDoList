package com.example.todolist.DTO

class ToDo {

    var id: Long = -1
    var name = ""
    var createdAt = ""
    var items: MutableList<ToDoItems> = ArrayList()

}