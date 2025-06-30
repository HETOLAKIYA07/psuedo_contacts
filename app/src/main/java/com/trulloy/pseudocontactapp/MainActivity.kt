package com.trulloy.pseudocontactapp

import Contact
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var contactsRecyclerView: RecyclerView
    private lateinit var contactsAdapter: ContactsAdapter
    private lateinit var addContactFab: FloatingActionButton
    private lateinit var searchEditText: EditText
    private lateinit var emptyStateLayout: LinearLayout
    private lateinit var openAboutUs: ImageView
    private lateinit var groupSpinner: Spinner

    private val contactsList = mutableListOf<Contact>()
    private val filteredContactsList = mutableListOf<Contact>()
    private var allGroups: List<Group> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        contactsRecyclerView = findViewById(R.id.contactsRecyclerView)
        addContactFab = findViewById(R.id.addContactFab)
        searchEditText = findViewById(R.id.searchEditText)
        emptyStateLayout = findViewById(R.id.emptyStateLayout)
        openAboutUs = findViewById(R.id.aboutUs)
        groupSpinner = findViewById(R.id.groupFilterSpinner) // Spinner must exist in layout

        contactsAdapter = ContactsAdapter(filteredContactsList) { contact ->
            val intent = Intent(this, ContactDetailActivity::class.java).apply {
                putExtra("contact", contact)
            }
            startActivity(intent)
        }

        contactsRecyclerView.layoutManager = LinearLayoutManager(this)
        contactsRecyclerView.adapter = contactsAdapter

        openAboutUs.setOnClickListener {
            startActivity(Intent(this, AboutUs::class.java))
        }

        addContactFab.setOnClickListener {
            val options = arrayOf("Add Contact", "Manage Groups")

            AlertDialog.Builder(this)
                .setTitle("Choose Action")
                .setItems(options) { _, which ->
                    when (which) {
                        0 -> startActivity(Intent(this, AddContactActivity::class.java))
                        1 -> startActivity(Intent(this, ManageGroupsActivity::class.java))
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }


        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterContacts(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        groupSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View?, position: Int, id: Long
            ) {
                filterContacts(searchEditText.text.toString())
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        findViewById<CardView>(R.id.viewCallLogCard).setOnClickListener {
            startActivity(Intent(this, CallLogActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        loadGroups()
        loadContactsFromDb()
    }

    private fun loadGroups() {
        allGroups = GroupRepository.getAllGroups(this)
        val groupNames = listOf("All Groups") + allGroups.map { it.name }
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, groupNames)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        groupSpinner.adapter = spinnerAdapter
    }

    private fun loadContactsFromDb() {
        contactsList.clear()
        contactsList.addAll(ContactRepository.getAllContacts(this))
        filterContacts(searchEditText.text.toString())
    }

    private fun filterContacts(query: String) {
        val selectedGroupId = groupSpinner.selectedItemPosition
            .takeIf { it > 0 }
            ?.let { allGroups[it - 1].id }

        filteredContactsList.clear()
        filteredContactsList.addAll(
            contactsList.filter { contact ->
                val matchesSearch = query.isEmpty() ||
                        contact.getFullName().contains(query, ignoreCase = true) ||
                        contact.phone1.contains(query, ignoreCase = true) ||
                        contact.phone2?.contains(query, ignoreCase = true) == true ||
                        contact.phone3?.contains(query, ignoreCase = true) == true ||
                        contact.phone4?.contains(query, ignoreCase = true) == true ||
                        contact.email.contains(query, ignoreCase = true)

                val contactGroupId = contact.groupId?.toLong()
                val matchesGroup = selectedGroupId == null || contactGroupId == selectedGroupId

                matchesSearch && matchesGroup
            }
        )

        contactsAdapter.notifyDataSetChanged()
        updateEmptyState()
    }



    private fun updateEmptyState() {
        if (filteredContactsList.isEmpty()) {
            emptyStateLayout.visibility = View.VISIBLE
            contactsRecyclerView.visibility = View.GONE
        } else {
            emptyStateLayout.visibility = View.GONE
            contactsRecyclerView.visibility = View.VISIBLE
        }
    }
}
