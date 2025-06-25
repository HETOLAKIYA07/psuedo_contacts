package com.trulloy.pseudocontactapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CallLogAdapter(
    private val callLogs: List<CallLogItem>,
    private val onItemClick: (String) -> Unit,
    private val onHistoryClick: (String) -> Unit
) : RecyclerView.Adapter<CallLogAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameText: TextView = view.findViewById(R.id.nameText)
        val numberText: TextView = view.findViewById(R.id.numberText)
        val historyButton: ImageButton = view.findViewById(R.id.historyButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_call_log, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = callLogs.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = callLogs[position]
        val showNumber = item.displayName != null && item.displayName != item.phoneNumber

        holder.nameText.text = item.displayName ?: item.phoneNumber
        holder.numberText.text = if (showNumber) item.phoneNumber else ""

        holder.itemView.setOnClickListener {
            onItemClick(item.phoneNumber)
        }

        holder.historyButton.setOnClickListener {
            onHistoryClick(item.phoneNumber)
        }
    }
}
