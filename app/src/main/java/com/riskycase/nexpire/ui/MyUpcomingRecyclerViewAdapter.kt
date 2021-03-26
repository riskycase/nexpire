package com.riskycase.nexpire.ui

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.riskycase.nexpire.DatabaseHelper
import com.riskycase.nexpire.Item
import com.riskycase.nexpire.NewItem
import com.riskycase.nexpire.R
import java.text.SimpleDateFormat
import java.util.*

/**
 * [RecyclerView.Adapter] that can display a [Item].
 */
class MyUpcomingRecyclerViewAdapter(
        private var values: List<Item>)
    : RecyclerView.Adapter<MyUpcomingRecyclerViewAdapter.ViewHolder>() {

    lateinit var dbh: DatabaseHelper

    var deleteId: Long = 0

    val dcl = DialogInterface.OnClickListener { dialog, which ->
        when (which) {
            DialogInterface.BUTTON_POSITIVE -> {
                dbh.deleteItem(deleteId)
                values = dbh.allItems
                notifyDataSetChanged()
            }
            else -> ""
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_upcoming, parent, false)
        dbh = DatabaseHelper(parent.context)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.nameView.text = item.name
        val start = Calendar.getInstance()
        start.set(Calendar.HOUR_OF_DAY, 0)
        start.set(Calendar.MINUTE, 0)
        start.set(Calendar.SECOND, 0)
        start.set(Calendar.MILLISECOND, 0)
        val end = Calendar.getInstance()
        end.timeInMillis = item.expiry
        val days = ((end.timeInMillis - start.timeInMillis) / (1000 * 3600 * 24)).toInt()
        if(days in 1..4)
            holder.upcomingDaysView.setTextColor(Color.parseColor("#FFF32013"))
        holder.upcomingDaysView.text = "Expiring ${
            if(days == 1)
                "tomorrow"
            else if(days < 8)
                "on ${SimpleDateFormat("EEEE").format(Date(item.expiry))}"
            else
                "in ${days} days"
        }"
        if(days > 1)
            holder.upcomingDateView.text = "(${SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(Date(item.expiry))})"
        holder.deleteView.setOnClickListener { view ->
            deleteId = item.id
            AlertDialog.Builder(view.context)
                    .setMessage("Delete ${item.name} from the list?")
                    .setPositiveButton("Yes", dcl)
                    .setNegativeButton("No", dcl)
                    .show()
        }
        holder.editView.setOnClickListener { view ->
            val myIntent = Intent(view.context, NewItem::class.java)
                    .putExtra("id", item.id)
            view.context.startActivity(myIntent)
        }
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameView: TextView = view.findViewById(R.id.item_name)
        val upcomingDaysView: TextView = view.findViewById(R.id.upcomingDays)
        val upcomingDateView: TextView = view.findViewById(R.id.upcomingDate)
        val editView: ImageView = view.findViewById(R.id.edit_item)
        val deleteView: ImageView = view.findViewById(R.id.delete_item)

        override fun toString(): String {
            return super.toString() + " '" + nameView.text + "'"
        }
    }
}