package com.example.calendar

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toolbar
import io.realm.Realm
import io.realm.RealmAny
import io.realm.RealmConfiguration
import io.realm.RealmResults
import java.text.DateFormat

class DetailActivity : AppCompatActivity() {
    lateinit var toolBar: Toolbar
    lateinit var textDate: TextView
    lateinit var textDescription: TextView
    lateinit var task: Task
    lateinit var realm: Realm
    companion object {
        const val TASK_TEXT = "TASK_TEXT"

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        val id=intent?.extras?.getString(TASK_TEXT)
        textDate=findViewById(R.id.textViewDateDetail)
        textDescription=findViewById(R.id.textViewDescriptionDetail)
        toolBar=findViewById(R.id.toolbarDetail)
        initRealm()
        realm = Realm.getDefaultInstance()
        task=readFromDB(id!!)
        setupActionBar()
        val dateFormatter = DateFormat.getDateInstance(DateFormat.FULL)
        val date = "${dateFormatter.format(task.dateAndTime)}"
        textDescription.text=task.description
        textDate.text=date
        toolBar.title=task.name
    }
    private fun setupActionBar() {
        supportActionBar?.apply {
            setDisplayShowHomeEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            title = task.name
        }
    }
    private fun initRealm() {
        Realm.init(this)
        val config = RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build()
        Realm.setDefaultConfiguration(config)
    }
    fun readFromDB(id: String):Task{

        realm.beginTransaction()
        val tasksDB =realm.where(Task::class.java).equalTo("id",id.toInt()).findAll()
        realm.commitTransaction()
        return tasksDB[0]!!
    }
}