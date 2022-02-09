package com.example.calendar

import io.realm.RealmObject
import java.sql.Timestamp


open class Task: RealmObject()  {

    lateinit var dateStart: String
    lateinit var dateFinish: String
    lateinit var name: String
    lateinit var description: String
    lateinit var text: String

}