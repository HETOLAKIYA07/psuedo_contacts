package com.trulloy.pseudocontactapp

import android.Manifest
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

class CallHistoryActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var historyAdapter: CallHistoryAdapter
    private val callHistoryList = mutableListOf<CallLogItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_call_history)

        recyclerView = findViewById(R.id.historyRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val number = intent.getStringExtra("number") ?: ""
        val displayName = intent.getStringExtra("name") // ✅ get passed name

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_CALL_LOG), 200)
            return
        }

        loadHistoryForNumber(number, displayName)
    }

    private fun loadHistoryForNumber(number: String, name: String?) {
        val normalizedTarget = normalizePhoneNumber(number)

        val cursor = contentResolver.query(
            CallLog.Calls.CONTENT_URI,
            null,
            null,
            null,
            "${CallLog.Calls.DATE} DESC"
        )

        cursor?.use {
            while (it.moveToNext()) {
                val rawNumber = it.getString(it.getColumnIndexOrThrow(CallLog.Calls.NUMBER))
                val normalized = normalizePhoneNumber(rawNumber)

                if (normalized == normalizedTarget) {
                    val callTypeCode = it.getInt(it.getColumnIndexOrThrow(CallLog.Calls.TYPE))
                    val timestamp = it.getLong(it.getColumnIndexOrThrow(CallLog.Calls.DATE))
                    val duration = it.getString(it.getColumnIndexOrThrow(CallLog.Calls.DURATION))

                    val type = when (callTypeCode) {
                        CallLog.Calls.INCOMING_TYPE -> "Incoming"
                        CallLog.Calls.OUTGOING_TYPE -> "Outgoing"
                        CallLog.Calls.MISSED_TYPE -> "Missed"
                        CallLog.Calls.REJECTED_TYPE -> "Rejected"
                        else -> "Other"
                    }

                    val formattedTime = SimpleDateFormat("dd MMM yyyy hh:mm a", Locale.getDefault()).format(Date(timestamp))
                    val durationStr = formatDuration(duration.toIntOrNull() ?: 0)

                    val item = CallLogItem(
                        displayName = name, // ✅ use passed name here
                        phoneNumber = rawNumber,
                        callType = type,
                        callTime = formattedTime,
                        durationSeconds = durationStr
                    )
                    callHistoryList.add(item)
                }
            }
        }

        historyAdapter = CallHistoryAdapter(callHistoryList)
        recyclerView.adapter = historyAdapter
    }

    private fun normalizePhoneNumber(number: String): String {
        val digitsOnly = number.replace(Regex("[^0-9]"), "")
        return if (digitsOnly.length > 10) digitsOnly.takeLast(10) else digitsOnly
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 200 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            val number = intent.getStringExtra("number") ?: ""
            val displayName = intent.getStringExtra("name")
            loadHistoryForNumber(number, displayName)
        } else {
            Toast.makeText(this, "Permission denied to read call logs", Toast.LENGTH_SHORT).show()
        }
    }

    private fun formatDuration(durationInSeconds: Int): String {
        if (durationInSeconds == 0) return "Missed"

        val hours = durationInSeconds / 3600
        val minutes = (durationInSeconds % 3600) / 60
        val seconds = durationInSeconds % 60

        val parts = mutableListOf<String>()
        if (hours > 0) parts.add("${hours}hr")
        if (minutes > 0) parts.add("${minutes}min")
        if (seconds > 0) parts.add("${seconds}sec")

        return parts.joinToString(", ")
    }
}
