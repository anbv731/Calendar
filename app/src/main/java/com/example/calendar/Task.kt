package com.example.calendar

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required
import java.sql.Timestamp
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date



open class Task: RealmObject()  {
    @PrimaryKey
    var id:Int =0
    lateinit var date: String
    var dateAndTime: Long = 0
    lateinit var name: String
    lateinit var description: String


}