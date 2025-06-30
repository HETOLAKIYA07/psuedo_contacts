package com.trulloy.pseudocontactapp

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ManageGroupsActivity : AppCompatActivity() {

    private lateinit var groupList: MutableList<Group>
    private lateinit var adapter: GroupAdapter
    private lateinit var groupRecyclerView: RecyclerView
    private lateinit var addGroupButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_groups)

        groupRecyclerView = findViewById(R.id.groupRecyclerView)
        addGroupButton = findViewById(R.id.addGroupButton)

        groupList = GroupRepository.getAllGroups(this).toMutableList()
        adapter = GroupAdapter(groupList, this)
        groupRecyclerView.layoutManager = LinearLayoutManager(this)
        groupRecyclerView.adapter = adapter

        addGroupButton.setOnClickListener {
            showGroupDialog(null)
        }
    }

    private fun showGroupDialog(existingGroup: Group?) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_group, null)
        val groupEditText = dialogView.findViewById<EditText>(R.id.groupNameEditText)
        groupEditText.setText(existingGroup?.name ?: "")

        AlertDialog.Builder(this)
            .setTitle(if (existingGroup == null) "Add Group" else "Edit Group")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val name = groupEditText.text.toString().trim()
                if (name.isNotEmpty()) {
                    if (existingGroup == null) {
                        val newGroup = Group(id = 0, name = name)
                        GroupRepository.addGroup(this, name)
                    } else {
                        val updatedGroup = existingGroup.copy(name = name)
                        GroupRepository.updateGroup(this, updatedGroup)
                    }
                    refreshList()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun refreshList() {
        Log.d("ManageGroupsActivity", "Refreshing group list...")
        groupList.clear()
        groupList.addAll(GroupRepository.getAllGroups(this))
        adapter.notifyDataSetChanged()
        Log.d("ManageGroupsActivity", "Group count: ${groupList.size}")
    }

    fun confirmDeleteGroup(group: Group) {
        AlertDialog.Builder(this)
            .setTitle("Delete Group")
            .setMessage("Are you sure you want to delete the group '${group.name}'? This will unlink it from all contacts.")
            .setPositiveButton("Delete") { _, _ ->
                GroupRepository.deleteGroup(this, group.id.toInt())
                refreshList()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    fun editGroup(group: Group) {
        showGroupDialog(group)
    }
}
