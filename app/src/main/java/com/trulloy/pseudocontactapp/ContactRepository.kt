package com.trulloy.pseudocontactapp

import Contact
import android.content.ContentValues
import android.content.Context

object ContactRepository {

    fun addContact(context: Context, contact: Contact) {
        val db = ContactDatabase(context).writableDatabase
        val values = ContentValues().apply {
            put("firstName", contact.firstName)
            put("lastName", contact.lastName)
            put("phone1", contact.phone1)
            put("phone2", contact.phone2)
            put("phone3", contact.phone3)
            put("phone4", contact.phone4)
            put("email", contact.email)
            put("birthDate", contact.birthDate)
            put("imageUri", contact.imageUri)
        }
        db.insert("contacts", null, values)
        db.close()
    }

    fun getAllContacts(context: Context): List<Contact> {
        val contacts = mutableListOf<Contact>()
        val db = ContactDatabase(context).readableDatabase
        val cursor = db.rawQuery("SELECT * FROM contacts", null)

        while (cursor.moveToNext()) {
            val contact = Contact(
                id = cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                firstName = cursor.getString(cursor.getColumnIndexOrThrow("firstName")),
                lastName = cursor.getString(cursor.getColumnIndexOrThrow("lastName")),
                phone1 = cursor.getString(cursor.getColumnIndexOrThrow("phone1")),
                phone2 = cursor.getString(cursor.getColumnIndexOrThrow("phone2")),
                phone3 = cursor.getString(cursor.getColumnIndexOrThrow("phone3")),
                phone4 = cursor.getString(cursor.getColumnIndexOrThrow("phone4")),
                email = cursor.getString(cursor.getColumnIndexOrThrow("email")),
                birthDate = cursor.getString(cursor.getColumnIndexOrThrow("birthDate")),
                imageUri = cursor.getString(cursor.getColumnIndexOrThrow("imageUri"))
            )
            contacts.add(contact)
        }

        cursor.close()
        db.close()
        return contacts
    }

    fun deleteContact(context: Context, contactId: Long) {
        val db = ContactDatabase(context).writableDatabase
        db.delete("contacts", "id = ?", arrayOf(contactId.toString()))
        db.close()
    }

    fun updateContact(context: Context, contact: Contact) {
        val db = ContactDatabase(context).writableDatabase
        val values = ContentValues().apply {
            put("firstName", contact.firstName)
            put("lastName", contact.lastName)
            put("phone1", contact.phone1)
            put("phone2", contact.phone2)
            put("phone3", contact.phone3)
            put("phone4", contact.phone4)
            put("email", contact.email)
            put("birthDate", contact.birthDate)
            put("imageUri", contact.imageUri)
        }
        db.update("contacts", values, "id = ?", arrayOf(contact.id.toString()))
        db.close()
    }

}
