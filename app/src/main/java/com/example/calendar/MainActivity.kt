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
import io.realm.RealmChangeListener
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
    lateinit var realmChangeListener:RealmChangeListener<Realm>
    lateinit var taskList: List<Task>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recyclerView = findViewById<RecyclerView>(R.id.RecyclerViewId)
        addBottom = findViewById(R.id.addBottom)
        calendarView = findViewById<CalendarView>(R.id.calendarView)
        textDate = findViewById<TextView>(R.id.textViewdate)
        initRealm()
        realm = Realm.getDefaultInstance()
        readFromDB(getDate())
        realmChangeListener = RealmChangeListener<Realm> {setList()}
        realm.addChangeListener(realmChangeListener)
        textDate.text = getDate()
        setList()

        calendarView.setOnDateChangeListener { view, year, monthOfYear, dayOfMonth ->
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, monthOfYear)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            val dateFormatter = DateFormat.getDateInstance(DateFormat.FULL)
            val date = "${dateFormatter.format(calendar.time)}"
            textDate.text=date
            readFromDB(date)
            setList()
        }

        addBottom.setOnClickListener {
            val intent = Intent(this, NewTaskActivity::class.java)
            this.startActivity(intent)

        }
    }

    fun setList() {
        val adapter = TaskAdapter(taskList.sortedWith(compareBy({it.dateAndTime})))
        recyclerView.adapter = adapter
        val layoutManager= LinearLayoutManager(this)
        layoutManager.canScrollVertically()
        recyclerView.layoutManager = layoutManager
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
    fun readFromDB(date: String) {
        realm.beginTransaction()
        val tasksDB=
            realm.where(Task::class.java).equalTo("date",date).findAll()
        realm.commitTransaction()
        taskList=tasksDB
    }
    override fun onDestroy() {
        super.onDestroy()
        realm.removeAllChangeListeners()
        realm.close()
    }
}