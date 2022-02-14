package com.example.calendar


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.view.menu.ActionMenuItemView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.RealmResults
import java.text.SimpleDateFormat

class DetailActivity : AppCompatActivity() {
    private lateinit var toolBar: androidx.appcompat.widget.Toolbar
    private lateinit var textDate: TextView
    private lateinit var textDescription: TextView
    private lateinit var buttonEdit: FloatingActionButton
    private lateinit var deleteButton: ActionMenuItemView
    private lateinit var task: Task
    private lateinit var realm: Realm

    companion object {
        const val TASK_TEXT = "TASK_TEXT"

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        val id = intent?.extras?.getString(TASK_TEXT)
        textDate = findViewById(R.id.textViewDateDetail)
        textDescription = findViewById(R.id.textViewDescriptionDetail)
        toolBar = findViewById(R.id.toolbarDetail)
        buttonEdit = findViewById(R.id.floatingActionButtonEdit)
        deleteButton = findViewById(R.id.deleteButton)
        initRealm()
        realm = Realm.getDefaultInstance()
        task = readFromDB(id!!)
        val date = SimpleDateFormat("EEEE, d MMMM y HH:mm").format(task.dateAndTime)
        textDescription.text = task.description
        textDate.text = date
        toolBar.title = task.name
        toolBar.setNavigationOnClickListener {
            this.finish()
        }

        deleteButton.setOnClickListener {
            deletFromDB(task)
            this.finish()
        }
        buttonEdit.setOnClickListener {
            val intent = Intent(this, NewTaskActivity::class.java)
            intent.putExtra(NewTaskActivity.IS_EDIT, task.id)
            this.startActivity(intent)
        }

    }

    private fun initRealm() {
        Realm.init(this)
        val config = RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build()
        Realm.setDefaultConfiguration(config)
    }

    private fun readFromDB(id: String): Task {

        realm.beginTransaction()
        val tasksDB = realm.where(Task::class.java).equalTo("id", id.toInt()).findAll()
        realm.commitTransaction()
        return tasksDB[0]!!
    }

    private fun deletFromDB(task: Task) {
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