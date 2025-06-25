package com.trulloy.pseudocontactapp

import Contact
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ContactsAdapter(
    private val contacts: List<Contact>,
    private val onItemClick: (Contact) -> Unit
) : RecyclerView.Adapter<ContactsAdapter.ContactViewHolder>() {

    inner class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameText: TextView = itemView.findViewById(R.id.contactName)
        val phoneText: TextView = itemView.findViewById(R.id.contactPhone)
        val avatarImage: ImageView = itemView.findViewById(R.id.avatarImage)
        val avatarText: TextView = itemView.findViewById(R.id.avatarText)
        val callButton: ImageButton = itemView.findViewById(R.id.callButton)

        init {
            itemView.setOnClickListener {
                val contact = contacts[adapterPosition]
                onItemClick(contact)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_contact, parent, false)
        return ContactViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact = contacts[position]

        holder.nameText.text = contact.getFullName()
        holder.phoneText.text = contact.phone1

        if (!contact.imageUri.isNullOrEmpty()) {
            holder.avatarImage.visibility = View.VISIBLE
            holder.avatarImage.setImageURI(Uri.parse(contact.imageUri))
            holder.avatarText.visibility = View.GONE
        } else {
            holder.avatarText.text = contact.getInitials()
            holder.avatarImage.visibility = View.GONE
            holder.avatarText.visibility = View.VISIBLE
        }

        // ☎️ Set call button click to initiate call
        holder.callButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:${contact.phone1}")
            }
            it.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = contacts.size
}
