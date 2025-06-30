package com.trulloy.pseudocontactapp

import Contact
import android.content.ContentValues
import android.content.Context
import android.util.Log


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

            if (contact.groupId != null) {
                put("group_id", contact.groupId)
            } else {
                putNull("group_id")
            }
        }

        // 🔍 Log everything before inserting
//        Log.d("DEBUG_CONTACT_INSERT", "Inserting contact:\n" +
//                "First Name: ${contact.firstName}, Last Name: ${contact.lastName}\n" +
//                "Phones: ${contact.phone1}, ${contact.phone2}, ${contact.phone3}, ${contact.phone4}\n" +
//                "Email: ${contact.email}, Birth Date: ${contact.birthDate}\n" +
//                "Image URI: ${contact.imageUri}, Group ID: ${contact.groupId}")
//
//        // 🔍 Simulated SQL Insert for your debugging
//        val simulatedInsert = """
//        INSERT INTO contacts
//        (firstName, lastName, phone1, phone2, phone3, phone4, email, birthDate, imageUri, group_id)
//        VALUES (
//            '${contact.firstName}', '${contact.lastName}', '${contact.phone1}', '${contact.phone2}',
//            '${contact.phone3}', '${contact.phone4}', '${contact.email}', '${contact.birthDate}',
//            '${contact.imageUri}', ${contact.groupId ?: "NULL"}
//        );
//    """.trimIndent()
//        Log.d("DEBUG_CONTACT_SQL", simulatedInsert)

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
                imageUri = cursor.getString(cursor.getColumnIndexOrThrow("imageUri")),
                groupId = cursor.getInt(cursor.getColumnIndexOrThrow("group_id")).takeIf { !cursor.isNull(cursor.getColumnIndexOrThrow("group_id")) }
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

            if (contact.groupId != null) {
                put("group_id", contact.groupId)
            } else {
                putNull("group_id")
            }
        }
        db.update("contacts", values, "id = ?", arrayOf(contact.id.toString()))
        db.close()
    }

}
