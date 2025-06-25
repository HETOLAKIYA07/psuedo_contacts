package com.trulloy.pseudocontactapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.RecyclerView

class CallHistoryAdapter(private val history: List<CallLogItem>) :
    RecyclerView.Adapter<CallHistoryAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameText: TextView = view.findViewById(R.id.nameText)
        val numberText: TextView = view.findViewById(R.id.numberText)
        val typeText: TextView = view.findViewById(R.id.typeText)
        val timeText: TextView = view.findViewById(R.id.timeText)
        val durationText: TextView = view.findViewById(R.id.durationText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_call_history, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = history.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = history[position]

        holder.nameText.text = item.displayName ?: item.phoneNumber
        holder.numberText.text = item.phoneNumber
        holder.typeText.text = item.callType
        holder.timeText.text = "Time: ${item.callTime}"

        // Set call type color
        when (item.callType) {
            "Missed", "Rejected" -> {
                holder.typeText.setTextColor("#FF3B30".toColorInt()) // Red
                holder.durationText.text = ""
                holder.durationText.visibility = View.GONE
            }
            "Incoming", "Outgoing" -> {
                holder.typeText.setTextColor("#008000".toColorInt()) // Blue
                holder.durationText.text = "Duration: ${item.durationSeconds}"
                holder.durationText.visibility = View.VISIBLE
            }
            else -> {
                holder.typeText.setTextColor("#8E8E93".toColorInt()) // Gray for "Other"
                holder.durationText.text = "Duration: ${item.durationSeconds}"
                holder.durationText.visibility = View.VISIBLE
            }
        }
    }

}
