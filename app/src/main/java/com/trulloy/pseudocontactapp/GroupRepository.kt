package com.trulloy.pseudocontactapp

import android.content.ContentValues
import android.content.Context

object GroupRepository {

    fun addGroup(context: Context, name: String): Boolean {
        val db = ContactDatabase(context).writableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) FROM groups", null)
        cursor.moveToFirst()
        val count = cursor.getInt(0)
        cursor.close()

        if (count >= 4) return false

        val stmt = db.compileStatement("INSERT INTO groups (name) VALUES (?)")
        stmt.bindString(1, name)
        stmt.executeInsert()
        return true
    }

    fun getAllGroups(context: Context): List<Group> {
        val db = ContactDatabase(context).readableDatabase
        val cursor = db.rawQuery("SELECT id, name FROM groups", null)

        val groups = mutableListOf<Group>()
        while (cursor.moveToNext()) {
            val group = Group(
                id = cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
            )
            groups.add(group)
        }

        cursor.close()
        db.close()
        return groups
    }


    fun deleteGroup(context: Context, groupId: Int) {
        val db = ContactDatabase(context).writableDatabase
        db.execSQL("UPDATE contacts SET group_id = NULL WHERE group_id = $groupId")
        db.execSQL("DELETE FROM groups WHERE id = $groupId")
    }

    fun updateGroup(context: Context, group: Group) {
        val db = ContactDatabase(context).writableDatabase
        val values = ContentValues().apply {
            put("name", group.name)
        }
        db.update("groups", values, "id = ?", arrayOf(group.id.toString()))
        db.close()
    }

}
