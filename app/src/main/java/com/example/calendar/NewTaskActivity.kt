package com.example.calendar

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.Intent
import android.os.Bundle
import android.text.format.DateUtils
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import io.realm.Realm
import io.realm.RealmConfiguration
import java.text.DateFormat
import java.util.*


class NewTaskActivity : AppCompatActivity() {
    companion object {
        const val IS_EDIT = "IS_EDIT"

    }

    private lateinit var currentDateTime: TextView
    private var dateAndTime = Calendar.getInstance()
    private lateinit var textName: EditText
    private lateinit var textDescription: EditText
    private lateinit var saveButton: FloatingActionButton
    private lateinit var toolbar: Toolbar
    private lateinit var realm: Realm
    private var isEditable = false
    private val newTask = Task()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_task)
        val isEdit = intent?.extras?.get(IS_EDIT)
        currentDateTime = findViewById(R.id.currentDateTime)
        textName = findViewById(R.id.editTextTaskName)
        textDescription = findViewById(R.id.editTextTask)
        saveButton = findViewById(R.id.floatingActionButtonSave)
        toolbar = findViewById(R.id.toolbarNewTask)
        initRealm()
        realm = Realm.getDefaultInstance()
        toolbar.setNavigationOnClickListener {
            this.finish()
        }
        if (isEdit is Long) {
            dateAndTime.timeInMillis = isEdit.toLong()
            toolbar.title = "Новое cобытие"
        } else if (isEdit is Int) {
            readFromDB(isEdit.toString())
            isEditable = true
            newTask.id = isEdit
            toolbar.title = "Редактирование"
        }
        setInitialDateTime()


        saveButton.setOnClickListener {
            saveTask()
            val intent = Intent(this, MainActivity::class.java)
            this.startActivity(intent)
            this.finish()


        }


    }


    private fun saveTask() {
        newTask.description = textDescription.text.toString()
        newTask.dateAndTime = dateAndTime.getTimeInMillis()
        newTask.date =
            DateFormat.getDateInstance(DateFormat.FULL).format(dateAndTime.getTimeInMillis())
        newTask.name = textName.text.toString()

        saveIntoDB(newTask)

    }

    private fun initRealm() {
        Realm.init(this)
        val config = RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build()
        Realm.setDefaultConfiguration(config)
    }

    private fun readFromDB(id: String) {

        realm.beginTransaction()
        val tasksDB = realm.where(Task::class.java).equalTo("id", id.toInt()).findAll()
        realm.commitTransaction()
        val task = tasksDB[0]!!
        dateAndTime.timeInMillis = task.dateAndTime
        textDescription.setText(task.description)
        textName.setText(task.name)
    }

    private fun saveIntoDB(task: Task) {
        if (!isEditable) {
            val maxId = realm.where(Task::class.java).max("id")
            if (maxId == null) {
                task.id = 1
            } else {
                task.id = (maxId.toInt()) + 1
            }
        }
        realm.beginTransaction()
        realm.copyToRealmOrUpdate(task)

        realm.commitTransaction()

    }


    fun setDate(v: View?) {
        DatePickerDialog(
            this@NewTaskActivity, d,
            dateAndTime.get(Calendar.YEAR),
            dateAndTime.get(Calendar.MONTH),
            dateAndTime.get(Calendar.DAY_OF_MONTH)
        )
            .show()
    }

    fun setTime(v: View?) {
        TimePickerDialog(
            this@NewTaskActivity, t,
            dateAndTime.get(Calendar.HOUR_OF_DAY),
            dateAndTime.get(Calendar.MINUTE), true
        )
            .show()
    }

    private fun setInitialDateTime() {
        currentDateTime.setText(
            DateUtils.formatDateTime(
                this,
                dateAndTime.getTimeInMillis(),
                DateUtils.FORMAT_SHOW_DATE or DateUtils.FORMAT_SHOW_YEAR
                        or DateUtils.FORMAT_SHOW_TIME
            )
        )
    }

    private var t =
        OnTimeSetListener { view, hourOfDay, minute ->
            dateAndTime.set(Calendar.HOUR_OF_DAY, hourOfDay)
            dateAndTime.set(Calendar.MINUTE, minute)
            setInitialDateTime()
        }

    private var d =
        OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            dateAndTime.set(Calendar.YEAR, year)
            dateAndTime.set(Calendar.MONTH, monthOfYear)
            dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            setInitialDateTime()
        }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }
}