package com.example.calendar

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import io.realm.Realm
import io.realm.RealmAny
import io.realm.RealmConfiguration
import io.realm.RealmResults
import java.text.DateFormat
import java.text.SimpleDateFormat

class DetailActivity : AppCompatActivity() {
    lateinit var toolBar: androidx.appcompat.widget.Toolbar
    lateinit var textDate: TextView
    lateinit var textDescription: TextView
    lateinit var buttonDelete: FloatingActionButton
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
        buttonDelete=findViewById(R.id.floatingActionButtonDelete)
        initRealm()
        realm = Realm.getDefaultInstance()
        task=readFromDB(id!!)
        val date = SimpleDateFormat("EEEE, d MMMM y HH:mm").format(task.dateAndTime)
        textDescription.text=task.description
        textDate.text=date
        toolBar.title=task.name
        toolBar.setNavigationOnClickListener{
            this.finish()
        }
        buttonDelete.setOnClickListener{
            deletFromDB(task)
            this.finish()
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
    fun deletFromDB(task: Task) {
        realm.beginTransaction()
        val tasksDB: RealmResults<Task> =
            realm.where(Task::class.java).equalTo("id", task.id).findAll()
        if (!tasksDB.isEmpty()) {
            for (i in tasksDB.size - 1 downTo 0) {
                tasksDB.get(i)!!.deleteFromRealm()
            }
        }
        realm.commitTransaction()

    }
    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }
}