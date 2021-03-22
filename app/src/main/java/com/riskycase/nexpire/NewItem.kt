package com.riskycase.nexpire

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import java.sql.Time
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.exp
import kotlin.math.min

class NewItem : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_item)
        setSupportActionBar(findViewById(R.id.toolbar))

        val expDate = findViewById<TextView>(R.id.editTextDate)
        val remTime = findViewById<TextView>(R.id.editTextTime)

        var expiry = Calendar.getInstance()
        var reminder = Calendar.getInstance()

        val date = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            expiry.set(Calendar.YEAR, year)
            expiry.set(Calendar.MONTH, month)
            expiry.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            expiry.set(Calendar.HOUR_OF_DAY, 0)
            expiry.set(Calendar.MINUTE, 0)
            expDate.text = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(Date(expiry.timeInMillis))
        }

        val time = TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
            reminder.timeInMillis = expiry.timeInMillis - Integer.parseInt(findViewById<TextView>(R.id.days).text.toString())*1000*3600*24
            reminder.set(Calendar.HOUR_OF_DAY, hourOfDay)
            reminder.set(Calendar.MINUTE, minute)
            remTime.text = SimpleDateFormat("hh:mm aa", Locale.ENGLISH).format(Date(reminder.timeInMillis))
        }

        expDate.text = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(Date())
        expDate.setOnClickListener { view ->
            DatePickerDialog(this, date, expiry.get(Calendar.YEAR), expiry.get(Calendar.MONTH), expiry.get(Calendar.DAY_OF_MONTH)).show()
        }
        remTime.text = SimpleDateFormat("hh:mm aa", Locale.ENGLISH).format(Date())
        remTime.setOnClickListener { view ->
            TimePickerDialog(this, time, reminder.get(Calendar.HOUR_OF_DAY), reminder.get(Calendar.MINUTE), false).show()
        }

        val days = findViewById<TextView>(R.id.days)
        findViewById<TextView>(R.id.daysPlus).setOnClickListener { view ->
            val daysInt = Integer.parseInt(days.text.toString()) + 1
            var reminderTime = Calendar.getInstance()
            reminderTime.set(Calendar.YEAR, expiry.get(Calendar.YEAR))
            reminderTime.set(Calendar.MONTH, expiry.get(Calendar.MONTH))
            reminderTime.set(Calendar.DAY_OF_MONTH, expiry.get(Calendar.DAY_OF_MONTH))
            reminderTime.set(Calendar.HOUR_OF_DAY, reminder.get(Calendar.HOUR_OF_DAY))
            reminderTime.set(Calendar.MINUTE, expiry.get(Calendar.MINUTE))
            reminderTime.timeInMillis = reminderTime.timeInMillis - daysInt*1000*3600*24
            days.text = Integer.toString(daysInt)
        }
        findViewById<TextView>(R.id.daysMinus).setOnClickListener { view ->
            val daysInt = Integer.parseInt(days.text.toString()) - 1
            if(daysInt > 0)
                days.text = Integer.toString(daysInt)
            else
                Toast.makeText(applicationContext, "Reminder needs to be at least one day before", Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.button_first).setOnClickListener { view ->
            val name = findViewById<AutoCompleteTextView>(R.id.autoCompleteTextView)
            Toast.makeText(applicationContext, "Adding reminder for ${name.text} at date ${expDate.text} and to be reminded ${days.text} day(s) before expiry at ${remTime.text}", Toast.LENGTH_LONG).show()
            val dbh = DatabaseHelper(applicationContext);
            dbh.addItem(Item(Calendar.getInstance().timeInMillis, name.text.toString(), expiry.timeInMillis, reminder.timeInMillis))

            val myIntent = Intent(this, MainActivity::class.java)
            startActivity(myIntent)
        }

    }
}