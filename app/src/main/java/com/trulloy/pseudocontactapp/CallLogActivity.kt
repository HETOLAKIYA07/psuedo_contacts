package com.trulloy.pseudocontactapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.CallLog
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class CallLogActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_call_log)

        recyclerView = findViewById(R.id.callLogRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_CALL_LOG), 100)
            return
        }

        loadCallLogs()
    }

    private fun loadCallLogs() {
        val allContacts = ContactRepository.getAllContacts(this)
        val callLogs = mutableListOf<CallLogItem>()
        val seenNumbers = mutableSetOf<String>()

        val cursor = contentResolver.query(
            CallLog.Calls.CONTENT_URI,
            null,
            null,
            null,
            CallLog.Calls.DATE + " DESC"
        )

        while (cursor?.moveToNext() == true) {
            val numberRaw = cursor.getString(cursor.getColumnIndexOrThrow(CallLog.Calls.NUMBER))
            val typeInt = cursor.getInt(cursor.getColumnIndexOrThrow(CallLog.Calls.TYPE))
            val timeMillis = cursor.getLong(cursor.getColumnIndexOrThrow(CallLog.Calls.DATE))
            val durationSecs = cursor.getLong(cursor.getColumnIndexOrThrow(CallLog.Calls.DURATION))

            val normalizedNumber = normalizePhoneNumber(numberRaw)
            if (seenNumbers.contains(normalizedNumber)) continue
            seenNumbers.add(normalizedNumber)

            val matchedContact = allContacts.find { contact ->
                listOfNotNull(contact.phone1, contact.phone2, contact.phone3, contact.phone4)
                    .map { normalizePhoneNumber(it) }
                    .contains(normalizedNumber)
            }

            val name = matchedContact?.getFullName()
            val callType = when (typeInt) {
                CallLog.Calls.INCOMING_TYPE -> "Incoming"
                CallLog.Calls.OUTGOING_TYPE -> "Outgoing"
                CallLog.Calls.MISSED_TYPE -> "Missed"
                else -> "Other"
            }
            val formattedTime = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(timeMillis))
            val formattedDuration = "$durationSecs sec"

            callLogs.add(CallLogItem(name, normalizedNumber, callType, formattedTime, formattedDuration))
        }

        cursor?.close()

        recyclerView.adapter = CallLogAdapter(
            callLogs,
            onItemClick = { number ->
                val match = allContacts.find { contact ->
                    listOfNotNull(contact.phone1, contact.phone2, contact.phone3, contact.phone4)
                        .map { normalizePhoneNumber(it) }
                        .contains(normalizePhoneNumber(number))
                }

                if (match != null) {
                    val intent = Intent(this, ContactDetailActivity::class.java).apply {
                        putExtra("contact", match)
                    }
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "No contact found", Toast.LENGTH_SHORT).show()
                }
            },
            onHistoryClick = { number ->
                val normalized = normalizePhoneNumber(number)

                val match = allContacts.find { contact ->
                    listOfNotNull(contact.phone1, contact.phone2, contact.phone3, contact.phone4)
                        .map { normalizePhoneNumber(it) }
                        .contains(normalized)
                }

                val name = match?.getFullName()

                val intent = Intent(this, CallHistoryActivity::class.java).apply {
                    putExtra("number", number)
                    putExtra("name", name)
                }
                startActivity(intent)
            }

        )
    }

    private fun normalizePhoneNumber(number: String): String {
        val digitsOnly = number.replace(Regex("[^0-9]"), "")
        return if (digitsOnly.length > 10) digitsOnly.takeLast(10) else digitsOnly
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            loadCallLogs()
        } else {
            Toast.makeText(this, "Permission denied to read call logs", Toast.LENGTH_SHORT).show()
        }
    }
}
