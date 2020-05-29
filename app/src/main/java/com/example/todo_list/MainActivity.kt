package com.example.todo_list

import android.app.AlertDialog
import android.os.Bundle
import android.app.SearchManager
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todo_list.todo.TodoList
import com.example.todo_list.todo.TodoListAdapter
import com.example.todo_list.todo.TodoListViewModel
import com.example.todo_list.util.Common
import com.example.todo_list.util.Dialog

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_todo.view.*

class MainActivity : AppCompatActivity() {

    companion object{
        var isSortByDateCreated = true
    }

    private lateinit var todoViewModel: TodoListViewModel
    private lateinit var todoAdapter: TodoListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val layoutManager = LinearLayoutManager(this)
        recyclerview.layoutManager = layoutManager

        todoAdapter =
            TodoListAdapter() { todo, _ ->
                val options = resources.getStringArray(R.array.option_edit_delete)
                Common.showSelector(
                    this,
                    "Choose action",
                    options
                ) { _, i ->
                    when (i) {
                        0 -> showDetailsDialog(todo)
                        1 -> showEditDialog(todo)
                        2 -> showDeleteDialog(todo)
                    }
                }
            }

        recyclerview.adapter = todoAdapter

        todoViewModel = ViewModelProvider(this).get(TodoListViewModel::class.java)

        //tombol tambah data (floating action button)
        fab.setOnClickListener {
            showInsertDialog()
        }
    }

    override fun onResume() {
        super.onResume()
        observeData()
    }

    private fun observeData(){
        todoViewModel.getTodos()?.observe(this, Observer {
            todoAdapter.setTodoList(it)
            setProgressbarVisibility(false)
        })
    }

    private fun refreshData(){
        setProgressbarVisibility(true)
        observeData()
    }

    //insert data
    private fun showInsertDialog(){
        val view = LayoutInflater.from(this).inflate(R.layout.fragment_todo, null)

        view.input_due_date.setOnClickListener {
            Common.showDatePickerDialog(this, view.input_due_date)
        }

        view.input_time.setOnClickListener {
            Common.showTimePickerDialog(this, view.input_time)
        }

        val dialogTitle = "Add data"
        val toastMessage = "Data has been added successfully"
        val failAlertMessage = "Please fill all the required fields"

        Dialog(
            this,
            dialogTitle,
            view
        ) {
            val title = view.input_title.text.toString().trim()
            val date = view.input_due_date.text.toString().trim()
            val time = view.input_time.text.toString().trim()
            val note = view.input_note.text.toString()

            val remindMe = view.input_remind_me.isChecked

            if (title == "" || date == "" || time == "") {
                AlertDialog.Builder(this).setMessage(failAlertMessage).setCancelable(false)
                    .setPositiveButton("OK") { dialogInterface, _ ->
                        dialogInterface.cancel()
                    }.create().show()
            } else {
                val parsedDate =
                    Common.convertStringToDate(
                        "dd/MM/yy",
                        date
                    )
                val dueDate =
                    Common.formatDate(
                        parsedDate,
                        "dd/MM/yy"
                    )

                val currentDate =
                    Common.getCurrentDateTime()
                val dateCreated =
                    Common.formatDate(
                        currentDate,
                        "dd/MM/yy HH:mm:ss"
                    )

                val todo = TodoList(
                    title = title,
                    note = note,
                    dateCreated = dateCreated,
                    dateUpdated = dateCreated,
                    dueDate = dueDate,
                    dueTime = time,
                    remindMe = remindMe
                )

                todoViewModel.insertTodo(todo)

                if (remindMe) {
                }
                Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT).show()
            }
        }.show()
    }

    private fun showEditDialog(todo: TodoList) {
        val view = LayoutInflater.from(this).inflate(R.layout.fragment_todo, null)

        view.input_due_date.setOnClickListener {
            Common.showDatePickerDialog(this, view.input_due_date)
        }

        view.input_time.setOnClickListener {
            Common.showTimePickerDialog(this, view.input_time)
        }

        view.input_title.setText(todo.title)
        view.input_note.setText(todo.note)
        view.input_due_date.setText(todo.dueDate)
        view.input_time.setText(todo.dueTime)
        view.input_remind_me.isChecked = todo.remindMe

        val dialogTitle = "Edit data"
        val toastMessage = "Data has been updated successfully"
        val failAlertMessage = "Please fill all the required fields"

        Dialog(
            this,
            dialogTitle,
            view
        ) {
            val title = view.input_title.text.toString().trim()
            val date = view.input_due_date.text.toString().trim()
            val time = view.input_time.text.toString().trim()
            val note = view.input_note.text.toString()

            val dateCreated = todo.dateCreated
            val remindMe = view.input_remind_me.isChecked
            val prevDueTime = todo.dueTime

            if (title == "" || date == "" || time == "") {
                AlertDialog.Builder(this).setMessage(failAlertMessage).setCancelable(false)
                    .setPositiveButton("OK") { dialogInterface, _ ->
                        dialogInterface.cancel()
                    }.create().show()
            } else {
                val parsedDate =
                    Common.convertStringToDate(
                        "dd/MM/yy",
                        date
                    )
                val dueDate =
                    Common.formatDate(
                        parsedDate,
                        "dd/MM/yy"
                    )

                val currentDate =
                    Common.getCurrentDateTime()
                val dateUpdated =
                    Common.formatDate(
                        currentDate,
                        "dd/MM/yy HH:mm:ss"
                    )

                todo.title = title
                todo.note = note
                todo.dateCreated = dateCreated
                todo.dateUpdated = dateUpdated
                todo.dueDate = dueDate
                todo.dueTime = time
                todo.remindMe = remindMe

                todoViewModel.updateTodo(todo)

                if (remindMe && prevDueTime != time) {
                }

                Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT).show()
            }
        }.show()
    }

    private fun showDeleteDialog(todo: TodoList) {
        todoViewModel.deleteTodo(todo)
        Toast.makeText(this, "Data Telah Dihapus", Toast.LENGTH_SHORT).show()
    }

    private fun showDetailsDialog(todo: TodoList) {
        val title = "Title: ${todo.title}"
        val dueDate = "Due date : ${todo.dueDate}, ${todo.dueTime}"
        val note = "Note: ${todo.note}"
        val dateCreated = "Date created: ${todo.dateCreated}"
        val dateUpdated = "Date updated: ${todo.dateUpdated}"

        val strReminder = if(todo.remindMe) "Enabled" else "Disabled"
        val remindMe = "Reminder: $strReminder"

        val strMessage = "$title\n$dueDate\n$note\n\n$dateCreated\n$dateUpdated\n$remindMe"

        AlertDialog.Builder(this).setMessage(strMessage).setCancelable(false)
            .setPositiveButton("OK") { dialogInterface, _ ->
                dialogInterface.cancel()
            }.create().show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        val inflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_sort -> true
            R.id.action_sort_date_created -> {
                isSortByDateCreated = true
                refreshData()
                true
            }
            R.id.action_sort_due_date -> {
                isSortByDateCreated = false
                refreshData()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setProgressbarVisibility(state: Boolean) {
        if (state) progressbar.visibility = View.VISIBLE
        else progressbar.visibility = View.INVISIBLE
    }
}