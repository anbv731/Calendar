package com.example.calendar

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.format.DateUtils
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.RealmResults
import java.text.DateFormat
import java.util.*


class NewTaskActivity : AppCompatActivity() {
    lateinit var currentDateTime: TextView
    var dateAndTime = Calendar.getInstance()
    lateinit var textName: EditText
    lateinit var textDescription: EditText
    lateinit var saveButton: FloatingActionButton
    lateinit var realm: Realm
    val newTask = Task()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_task)
        currentDateTime = findViewById(R.id.currentDateTime)
        setInitialDateTime()
        initRealm()
        realm = Realm.getDefaultInstance()
        textName = findViewById(R.id.editTextTaskName)
        textDescription = findViewById(R.id.editTextTask)
        saveButton = findViewById(R.id.floatingActionButtonSave)
        saveButton.setOnClickListener {
            saveTask()
            this.finish()

        }


    }


    fun saveTask() {
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

    fun saveIntoDB(task: Task) {
                val maxId = realm.where(Task::class.java).max("id")
                if (maxId == null) {
                    task.id = 1
                } else {
                    task.id= (maxId.toInt())+1
                }
                realm . beginTransaction ()
            realm.copyToRealm(task)

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

    var t =
        OnTimeSetListener { view, hourOfDay, minute ->
            dateAndTime.set(Calendar.HOUR_OF_DAY, hourOfDay)
            dateAndTime.set(Calendar.MINUTE, minute)
            setInitialDateTime()
        }

    var d =
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