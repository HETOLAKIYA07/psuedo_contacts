package com.trulloy.pseudocontactapp

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class ContactDatabase(context: Context) : SQLiteOpenHelper(context, "contacts.db", null, 2) { // Database version 2

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
        CREATE TABLE groups (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            name TEXT UNIQUE
        )
    """)

        db.execSQL("""
        CREATE TABLE contacts (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            firstName TEXT,
            lastName TEXT,
            phone1 TEXT,
            phone2 TEXT,
            phone3 TEXT,
            phone4 TEXT,
            email TEXT,
            birthDate TEXT,
            imageUri TEXT,
            group_id INTEGER,
            FOREIGN KEY(group_id) REFERENCES groups(id) ON DELETE SET NULL
        )
    """)
    }


    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS contacts")
        db.execSQL("DROP TABLE IF EXISTS groups")
        onCreate(db)
    }
}
