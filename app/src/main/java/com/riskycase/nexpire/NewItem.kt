package com.riskycase.nexpire

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class NewItem : AppCompatActivity() {

    override fun onBackPressed() {
        val myIntent = Intent(this, MainActivity::class.java)
        startActivity(myIntent)
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_item)
        setSupportActionBar(findViewById(R.id.toolbar))

        val expDate = findViewById<TextView>(R.id.editTextDate)
        val remTime = findViewById<TextView>(R.id.editTextTime)
        val name = findViewById<AutoCompleteTextView>(R.id.autoCompleteTextView)
        val dbh = DatabaseHelper(applicationContext)

        val expiry = Calendar.getInstance()
        val reminder = Calendar.getInstance()
        val days = findViewById<TextView>(R.id.days)

        val slider = findViewById<com.google.android.material.slider.Slider>(R.id.daysBefore)

        slider.setLabelFormatter { value ->
            val format = NumberFormat.getNumberInstance()
            format.maximumFractionDigits = 0
            format.format(value.toDouble())
        }

        slider.addOnChangeListener { _, value, _ ->
            val format = NumberFormat.getNumberInstance()
            format.maximumFractionDigits = 0
            val temp = Calendar.getInstance()
            temp.timeInMillis = expiry.timeInMillis - (1000*3600*24*value.toLong())
            reminder.set(Calendar.DAY_OF_MONTH, temp.get(Calendar.DAY_OF_MONTH))
            reminder.set(Calendar.MONTH, temp.get(Calendar.MONTH))
            reminder.set(Calendar.YEAR, temp.get(Calendar.YEAR))
            days.text = format.format(value.toDouble())
        }

        if(intent.hasExtra("id")) {
            dbh.getItem(intent.getLongExtra("id", 0)).also {
                name.setText(it.name)
                expiry.timeInMillis = it.expiry
                reminder.timeInMillis = it.reminder
                expDate.text = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(Date(expiry.timeInMillis))
                remTime.text = SimpleDateFormat("hh:mm aa", Locale.ENGLISH).format(Date(reminder.timeInMillis))
            }
        }
        else {
            expiry.timeInMillis = Calendar.getInstance().timeInMillis + 1000*3600*24
            expiry.set(Calendar.HOUR_OF_DAY, 0)
            expiry.set(Calendar.MINUTE, 0)
            expiry.set(Calendar.SECOND, 0)
            expiry.set(Calendar.MILLISECOND, 0)
            remTime.text = SimpleDateFormat("hh:mm aa", Locale.ENGLISH).format(Date(reminder.timeInMillis))
            expDate.text = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(Date(expiry.timeInMillis))
        }

        val date = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            expiry.set(Calendar.YEAR, year)
            expiry.set(Calendar.MONTH, month)
            expiry.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            expDate.text = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(Date(expiry.timeInMillis))
            val start = Calendar.getInstance()
            start.set(Calendar.HOUR_OF_DAY, 0)
            start.set(Calendar.MINUTE, 0)
            start.set(Calendar.SECOND, 0)
            start.set(Calendar.MILLISECOND, 0)
            val daysDifference = (expiry.timeInMillis - start.timeInMillis)/(1000*3600*24)
            if(daysDifference.toInt() == 1) {
                findViewById<LinearLayout>(R.id.reminderDate).visibility = View.GONE
            }
            else {
                findViewById<LinearLayout>(R.id.reminderDate).visibility = View.VISIBLE
                if(slider.value > daysDifference.toFloat())
                    slider.value = daysDifference.toFloat()
                slider.valueTo = daysDifference.toFloat()
            }
        }

        val time = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            reminder.timeInMillis = expiry.timeInMillis - Integer.parseInt(days.text.toString()) * 1000 * 3600 * 24
            reminder.set(Calendar.HOUR_OF_DAY, hourOfDay)
            reminder.set(Calendar.MINUTE, minute)
            remTime.text = SimpleDateFormat("hh:mm aa", Locale.ENGLISH).format(Date(reminder.timeInMillis))
        }

        expDate.setOnClickListener {
            val dpd = DatePickerDialog(this, date, expiry.get(Calendar.YEAR), expiry.get(Calendar.MONTH), expiry.get(Calendar.DAY_OF_MONTH))
            dpd.datePicker.minDate = Calendar.getInstance().timeInMillis + 1000*3600*24
            dpd.show()
        }
        remTime.setOnClickListener {
            TimePickerDialog(this, time, reminder.get(Calendar.HOUR_OF_DAY), reminder.get(Calendar.MINUTE), false).show()
        }

        findViewById<Button>(R.id.button_first).setOnClickListener {
            val id: Long
            Toast.makeText(applicationContext, "Added reminder for ${name.text} successfully!", Toast.LENGTH_LONG).show()
            if(intent.hasExtra("id")){
                id = intent.getLongExtra("id", 0)
                dbh.updateItem(Item(intent.getLongExtra("id", 0), name.text.toString(), expiry.timeInMillis, reminder.timeInMillis))
            }
            else{
                id = Calendar.getInstance().timeInMillis
                dbh.addItem(Item(id, name.text.toString(), expiry.timeInMillis, reminder.timeInMillis))
            }

            val notificationIntent = Intent()
                    .setAction("com.riskycase.nexpire.notify")
                    .setPackage(applicationContext.packageName)
                    .putExtra("id", id)

            val pintent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            val alarmManager: AlarmManager = applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, reminder.timeInMillis, pintent)
            val myIntent = Intent(this, MainActivity::class.java)
            startActivity(myIntent)
            finish()
        }

    }
}