package com.trulloy.pseudocontactapp

data class CallLogItem(
    val displayName: String?,
    val phoneNumber: String,
    val callType: String,
    val callTime: String,
    val durationSeconds: String
)