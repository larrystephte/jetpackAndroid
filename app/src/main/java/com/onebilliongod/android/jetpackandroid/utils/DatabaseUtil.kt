package com.onebilliongod.android.jetpackandroid.utils

import android.content.Context
import android.util.Log
import java.io.File
import java.io.IOException

class DatabaseUtil(private val context: Context) {
    // Database backed up
    fun backupDatabase() {
        val sourceFile = File(context.getDatabasePath("app-database").absolutePath)
        val backupFile = File(context.getExternalFilesDir(null), "app-database-backup.db")

        try {
            sourceFile.copyTo(backupFile, overwrite = true)
            Log.d("Backup", "Database backed up successfully:${backupFile.absolutePath}")
        } catch (e: IOException) {
            Log.e("Backup", "Failed to backup database: ${e.message}")
        }
    }

    // Database restored
    fun restoreDatabase() {
        val backupFile = File(context.getExternalFilesDir(null), "app-database-backup.db")
        val destinationFile = File(context.getDatabasePath("app-database").absolutePath)

        if (backupFile.exists()) {
            try {
                backupFile.copyTo(destinationFile, overwrite = true)
                Log.d("Restore", "Database restored successfully.")
            } catch (e: IOException) {
                Log.e("Restore", "Failed to restore database: ${e.message}")
            }
        }
    }
}