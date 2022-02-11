package com.example.calendar

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.format.DateUtils
import android.widget.ActionMenuView
import android.widget.Button
import android.widget.CalendarView
import android.widget.TextView
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.RealmResults
import java.text.DateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    lateinit var recyclerView: RecyclerView
    lateinit var calendarView: CalendarView
    lateinit var textDate: TextView
    lateinit var addBottom: ActionMenuItemView
    lateinit var realm: Realm
    val task = Task()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initRealm()
        realm = Realm.getDefaultInstance()


        recyclerView = findViewById<RecyclerView>(R.id.RecyclerViewId)
        addBottom = findViewById(R.id.addBottom)


        calendarView = findViewById<CalendarView>(R.id.calendarView)
        textDate = findViewById<TextView>(R.id.textViewdate)
        textDate.text = getDate()
        task.description = "Описание дела"
        val taskListExample = listOf(task)
        setList(readFromDB(getDate()))


        calendarView.setOnDateChangeListener { view, year, monthOfYear, dayOfMonth ->
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, monthOfYear)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            val selectedDate= calendar.timeInMillis
            val dateFormatter = DateFormat.getDateInstance(DateFormat.FULL)
            val date = "${dateFormatter.format(calendar.time)}"
            textDate.text=date
            val taskListExample = listOf(task)
            setList(readFromDB(date))
        }

        addBottom.setOnClickListener {
            val intent = Intent(this, NewTaskActivity::class.java)
            this.startActivity(intent)

        }
    }

    fun setList(taskList: List<Task>) {
        val adapter = TaskAdapter(taskList)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)


    }

    fun getDate(): String {
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
    fun readFromDB(date: String) :List<Task>{
        realm.beginTransaction()
        val tasksDB: RealmResults<Task> =
            realm.where(Task::class.java).contains("date",date).findAll()
//        if (!tasksDB.isEmpty()) {setList(tasksDB)}
        realm.commitTransaction()
        return tasksDB
    }
}