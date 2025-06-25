package com.trulloy.pseudocontactapp

import Contact
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
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

    private val contactsList = mutableListOf<Contact>()
    private val filteredContactsList = mutableListOf<Contact>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        contactsRecyclerView = findViewById(R.id.contactsRecyclerView)
        addContactFab = findViewById(R.id.addContactFab)
        searchEditText = findViewById(R.id.searchEditText)
        emptyStateLayout = findViewById(R.id.emptyStateLayout)

        contactsAdapter = ContactsAdapter(filteredContactsList) { contact ->
            val intent = Intent(this, ContactDetailActivity::class.java).apply {
                putExtra("contact", contact)
            }
            startActivity(intent)
        }

        contactsRecyclerView.layoutManager = LinearLayoutManager(this)
        contactsRecyclerView.adapter = contactsAdapter

        addContactFab.setOnClickListener {
            startActivity(Intent(this, AddContactActivity::class.java))
        }

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterContacts(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
        findViewById<CardView>(R.id.viewCallLogCard).setOnClickListener {
            startActivity(Intent(this, CallLogActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        loadContactsFromDb()
    }

    private fun loadContactsFromDb() {
        contactsList.clear()
        contactsList.addAll(ContactRepository.getAllContacts(this))
        filterContacts(searchEditText.text.toString())
    }

    private fun filterContacts(query: String) {
        filteredContactsList.clear()
        filteredContactsList.addAll(
            if (query.isEmpty()) {
                contactsList
            } else {
                contactsList.filter {
                    it.getFullName().contains(query, ignoreCase = true) ||
                            it.phone1.contains(query, ignoreCase = true) ||
                            it.phone2?.contains(query, ignoreCase = true) == true ||
                            it.phone3?.contains(query, ignoreCase = true) == true ||
                            it.phone4?.contains(query, ignoreCase = true) == true ||
                            it.email.contains(query, ignoreCase = true)
                }
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
