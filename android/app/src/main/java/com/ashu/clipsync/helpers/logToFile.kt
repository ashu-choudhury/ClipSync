package com.ashu.clipsync.helpers

import android.content.Context
import android.os.Environment
import java.io.File
import java.io.FileOutputStream


fun logToFile(message: String, context: Context) {
    val logFileName = "log.txt"
    val logEntry = "$message\n"

    try {
        val downloadsPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val logFile = File(downloadsPath, logFileName)

        // Create the file if it doesn't exist
        if (!logFile.exists()) {
            logFile.createNewFile()
        }

        // Append message
        FileOutputStream(logFile, true).bufferedWriter().use {
            it.write(logEntry)
        }

    } catch (e: Exception) {
        e.printStackTrace()
    }
}

