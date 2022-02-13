package com.example.calendar

import com.google.gson.Gson
import com.google.gson.JsonObject
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required
import org.json.JSONArray
import org.json.JSONObject
import java.sql.Timestamp
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date


open class Task : RealmObject() {
    @PrimaryKey
    var id: Int = 0
    lateinit var date: String
    var dateAndTime: Long = 0
    lateinit var name: String
    lateinit var description: String

    fun toJson(task: Task): String {
        val time = Timestamp(task.dateAndTime)
        return """{"id":"${task.id}","dateAndTime":"$time","name":"${task.name}","description":"${task.description}"}"""
    }

    fun fromJson(responseText: String): Task {
        val task = Task()
        val jsonTask = JSONObject(responseText)
        task.name = jsonTask.getString("name")
        task.description = jsonTask.getString("description")
        task.id = jsonTask.getString("id").toInt()
        task.dateAndTime =
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse(jsonTask.getString("dateAndTime")).time
        task.date = DateFormat.getDateInstance(DateFormat.FULL).format(task.dateAndTime)
        return task
    }
}
