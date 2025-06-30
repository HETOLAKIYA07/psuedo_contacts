package com.trulloy.pseudocontactapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class GroupAdapter(
    private val groups: List<Group>,
    private val activity: ManageGroupsActivity
) : RecyclerView.Adapter<GroupAdapter.GroupViewHolder>() {

    inner class GroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val groupName: TextView = itemView.findViewById(R.id.groupNameTextView)
        val editBtn: ImageButton = itemView.findViewById(R.id.editGroupButton)
        val deleteBtn: ImageButton = itemView.findViewById(R.id.deleteGroupButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_group, parent, false)
        return GroupViewHolder(view)
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        val group = groups[position]
        holder.groupName.text = group.name
        holder.editBtn.setOnClickListener { activity.editGroup(group) }
        holder.deleteBtn.setOnClickListener { activity.confirmDeleteGroup(group) }
    }

    override fun getItemCount(): Int = groups.size
}
