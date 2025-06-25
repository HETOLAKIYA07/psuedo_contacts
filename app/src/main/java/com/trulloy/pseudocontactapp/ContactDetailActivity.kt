package com.trulloy.pseudocontactapp

import Contact
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class ContactDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_detail)

        val avatarImage = findViewById<ImageView>(R.id.avatarImage)
        val fullNameText = findViewById<TextView>(R.id.fullNameText)
        val emailText = findViewById<TextView>(R.id.emailText)
        val birthDateText = findViewById<TextView>(R.id.birthDateText)
        val phoneListLayout = findViewById<LinearLayout>(R.id.phoneListLayout)
        val editButton = findViewById<Button>(R.id.editButton)
        val deleteButton = findViewById<Button>(R.id.deleteButton)

        val contact = intent.getSerializableExtra("contact") as? Contact

        if (contact != null) {
            fullNameText.text = contact.getFullName()
            emailText.text = "Email: ${contact.email}"
            birthDateText.text = "Birth Date: ${contact.birthDate ?: ""}"

            if (!contact.imageUri.isNullOrEmpty()) {
                avatarImage.setImageURI(Uri.parse(contact.imageUri))
            } else {
                avatarImage.setImageResource(R.drawable.ic_person)
            }

            val phones = listOfNotNull(
                contact.phone1.takeIf { it.isNotBlank() },
                contact.phone2?.takeIf { it.isNotBlank() },
                contact.phone3?.takeIf { it.isNotBlank() },
                contact.phone4?.takeIf { it.isNotBlank() }
            )

            phoneListLayout.removeAllViews()
            phones.forEach { phone ->
                val phoneText = TextView(this).apply {
                    text = "📞 $phone"
                    textSize = 16f
                    setPadding(0, 8, 0, 8)
                    setOnClickListener {
                        val intent = Intent(Intent.ACTION_DIAL).apply {
                            data = Uri.parse("tel:$phone")
                        }
                        startActivity(intent)
                    }
                }
                phoneListLayout.addView(phoneText)
            }

            editButton.setOnClickListener {
                val intent = Intent(this, AddContactActivity::class.java).apply {
                    putExtra("contactToEdit", contact)
                }
                startActivity(intent)
                finish()
            }

            deleteButton.setOnClickListener {
                android.app.AlertDialog.Builder(this)
                    .setTitle("Delete Contact")
                    .setMessage("Are you sure you want to delete ${contact.getFullName()}?")
                    .setPositiveButton("Delete") { _, _ ->
                        ContactRepository.deleteContact(this, contact.id)
                        Toast.makeText(this, "Contact deleted", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }

        } else {
            fullNameText.text = "No contact data"
        }
    }
}
