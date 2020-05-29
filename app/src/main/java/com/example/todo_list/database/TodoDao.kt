package com.example.todo_list.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.todo_list.todo.TodoList

//Database Access Object
@Dao
interface TodoDao{
    @Query("SELECT * FROM todo_table")
    fun getTodos(): LiveData<List<TodoList>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTodo(todo: TodoList)

    @Update
    suspend fun updateTodo(todo: TodoList)

    @Delete
    suspend fun deleteTodo(todo: TodoList)
}
