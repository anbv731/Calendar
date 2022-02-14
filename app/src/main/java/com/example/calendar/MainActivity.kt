package com.example.calendar

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.CalendarView
import android.widget.TextView
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.realm.Realm
import io.realm.RealmChangeListener
import io.realm.RealmConfiguration
import java.text.DateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var calendarView: CalendarView
    private lateinit var textDate: TextView
    private lateinit var addButton: ActionMenuItemView
    private lateinit var realm: Realm
    private lateinit var realmChangeListener: RealmChangeListener<Realm>
    private lateinit var taskList: List<Task>
    private var dateAndTime: Long = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recyclerView = findViewById(R.id.RecyclerViewId)
        addButton = findViewById(R.id.deleteButton)
        calendarView = findViewById(R.id.calendarView)
        textDate = findViewById(R.id.textViewdate)
        initRealm()
        realm = Realm.getDefaultInstance()
        readFromDB(getDate())
        realmChangeListener = RealmChangeListener<Realm> { setList() }
        realm.addChangeListener(realmChangeListener)
        textDate.text = getDate()
        dateAndTime = calendarView.date


        setList()

        calendarView.setOnDateChangeListener { view, year, monthOfYear, dayOfMonth ->
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, monthOfYear)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            val dateFormatter = DateFormat.getDateInstance(DateFormat.FULL)
            val date = "${dateFormatter.format(calendar.time)}"
            dateAndTime = calendar.timeInMillis
            textDate.text = date
            readFromDB(date)
            setList()
        }

        addButton.setOnClickListener {
            val intent = Intent(this, NewTaskActivity::class.java)
            intent.putExtra(NewTaskActivity.IS_EDIT, dateAndTime)
            this.startActivity(intent)

        }
    }

    private fun setList() {
        val adapter = TaskAdapter(taskList.sortedWith(compareBy({ it.dateAndTime })))
        recyclerView.adapter = adapter
        val layoutManager = LinearLayoutManager(this)
        layoutManager.canScrollVertically()
        recyclerView.layoutManager = layoutManager
    }

    private fun getDate(): String {
        val selectedDate = calendarView.date
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = selectedDate
        val dateFormatter = DateFormat.getDateInstance(DateFormat.FULL)
        val date = "${dateFormatter.format(calendar.time)}"
        return date
    }

    private fun initRealm() {
        Realm.init(this)
        val config = RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build()
        Realm.setDefaultConfiguration(config)
    }

    private fun readFromDB(date: String) {
        realm.beginTransaction()
        val tasksDB =
            realm.where(Task::class.java).equalTo("date", date).findAll()
        realm.commitTransaction()
        taskList = tasksDB
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.removeAllChangeListeners()
        realm.close()
    }
}