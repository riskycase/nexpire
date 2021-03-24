package com.riskycase.nexpire.ui

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.icu.text.LocaleDisplayNames
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.riskycase.nexpire.DatabaseHelper
import com.riskycase.nexpire.Item
import com.riskycase.nexpire.R

import java.text.SimpleDateFormat
import java.util.*

/**
 * [RecyclerView.Adapter] that can display a [DummyItem].
 * TODO: Replace the implementation with code for your data type.
 */
class MyExpiredRecyclerViewAdapter(
    private var values: List<Item>
) : RecyclerView.Adapter<MyExpiredRecyclerViewAdapter.ViewHolder>() {

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
            .inflate(R.layout.fragment_expired, parent, false)
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
        val days = (start.timeInMillis - end.timeInMillis) / (1000 * 3600 * 24)
        holder.expiredView.text = "Expired ${
            if (days.toInt() == 0)
                "today"
            else if (days.toInt() == 1)
                "yesterday"
            else
                "${days} days ago (at ${SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(Date(item.expiry))})"
        }"
        holder.deleteView.setOnClickListener { view ->
            AlertDialog.Builder(view.context).setMessage("Delete ${item.name} from the list?").setPositiveButton("YES", dcl).setNegativeButton("No", dcl).show()
        }
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameView: TextView = view.findViewById(R.id.item_name)
        val expiredView: TextView = view.findViewById(R.id.expired)
        val deleteView: ImageView = view.findViewById(R.id.delete_item)

        override fun toString(): String {
            return super.toString() + " '" + nameView.text + "'"
        }
    }
}