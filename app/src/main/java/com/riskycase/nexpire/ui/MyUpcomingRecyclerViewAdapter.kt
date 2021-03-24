package com.riskycase.nexpire.ui

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.provider.ContactsContract
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.riskycase.nexpire.DatabaseHelper
import com.riskycase.nexpire.Item
import com.riskycase.nexpire.NewItem
import com.riskycase.nexpire.R
import java.text.SimpleDateFormat
import java.util.*

/**
 * [RecyclerView.Adapter] that can display a [DummyItem].
 * TODO: Replace the implementation with code for your data type.
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
        end.set(Calendar.HOUR_OF_DAY, 0)
        end.set(Calendar.MINUTE, 0)
        end.set(Calendar.SECOND, 0)
        end.set(Calendar.MILLISECOND, 0)
        val days = (end.timeInMillis - start.timeInMillis) / (1000 * 3600 * 24)
        holder.expiredView.text = "Expiring ${
            if(days.toInt() == 1)
                "tomorrow"
            else if(days.toInt() <= 7)
                "on ${SimpleDateFormat("EEEE").format(Date(item.expiry))} (${SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(Date(item.expiry))})"
            else
                "in ${days} days (${SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(Date(item.expiry))})"
        }"
        if(days.toInt() <= 7) {
            holder.expiredView.setTextColor(Color.parseColor("#FFF32013"))
        }
        holder.deleteView.setOnClickListener { view ->
            deleteId = item.id
            AlertDialog.Builder(view.context)
                    .setMessage("Delete ${item.name} from the list?")
                    .setPositiveButton("YES", dcl)
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
        val expiredView: TextView = view.findViewById(R.id.expired)
        val editView: ImageView = view.findViewById(R.id.edit_item)
        val deleteView: ImageView = view.findViewById(R.id.delete_item)

        override fun toString(): String {
            return super.toString() + " '" + nameView.text + "'"
        }
    }
}