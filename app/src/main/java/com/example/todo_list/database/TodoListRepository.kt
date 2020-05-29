package com.example.todo_list.database


import android.app.Application
import androidx.lifecycle.LiveData
import com.example.todo_list.todo.TodoList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

//Penyimpanan
class TodoListRepository(application: Application){
    private val todoDao: TodoDao?
    private var todos: LiveData<List<TodoList>>? = null

    init{
        val db =
            AppDatabase.getInstance(
                application.applicationContext
            )
        todoDao = db?.todoDao()
        todos = todoDao?.getTodos()
    }

    fun getTodos(): LiveData<List<TodoList>>?{
        return todos
    }

    fun insert(todo: TodoList) = runBlocking {
        this.launch(Dispatchers.IO){
            todoDao?.insertTodo(todo)
        }
    }

    fun update(todo: TodoList) = runBlocking {
        this.launch(Dispatchers.IO){
            todoDao?.updateTodo(todo)
        }
    }

    fun delete(todo: TodoList) = runBlocking {
        this.launch(Dispatchers.IO){
            todoDao?.deleteTodo(todo)
        }
    }

}