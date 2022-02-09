package com.example.calendar

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.CalendarView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.DateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    lateinit var recyclerView:RecyclerView
    lateinit var calendarView: CalendarView
    lateinit var textDate: TextView
    val task = Task()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView= findViewById<RecyclerView>(R.id.RecyclerViewId)


    calendarView=findViewById<CalendarView>(R.id.calendarView)
        textDate= findViewById<TextView>(R.id.textViewdate)
        textDate.text="DATE"
        task.description="Описание дела"
        val taskListExample = listOf(task)
        setList(taskListExample)

        calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            textDate.text = "$dayOfMonth.${month + 1}.$year"
            task.description="$dayOfMonth.${month + 1}.$year"
            val taskListExample = listOf(task)
            setList(taskListExample)
        }


    }

    fun setList (taskList: List<Task>){

        val adapter= TaskAdapter(taskList)
        recyclerView.adapter=adapter
        recyclerView.layoutManager=LinearLayoutManager(this)


    }
    fun getDate():String{
        val selectedDate = calendarView.date
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = selectedDate
        val dateFormatter = DateFormat.getDateInstance(DateFormat.MEDIUM)
        val date = "Selected date: ${dateFormatter.format(calendar.time)}"
        return date
    }

}