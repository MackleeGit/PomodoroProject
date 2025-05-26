package com.example.pomodoroproject

import java.text.SimpleDateFormat
import java.util.*

fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("MMM dd, yyyy - h:mm a", Locale.getDefault())
    return sdf.format(Date(timestamp))
}


fun getFormattedTime(): String {
    val sdf = SimpleDateFormat("hh:mm:ss a", Locale.getDefault())
    return sdf.format(Date())
}

fun getFormattedDate(): String {
    val sdf = SimpleDateFormat("EEE, MMM d yyyy", Locale.getDefault())
    return sdf.format(Date())
}
