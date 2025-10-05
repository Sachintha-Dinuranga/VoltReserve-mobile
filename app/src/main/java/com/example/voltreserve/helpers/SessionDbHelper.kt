package com.example.voltreserve.helpers

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class SessionDbHelper(context: Context) :
    SQLiteOpenHelper(context, "session.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE session(id INTEGER PRIMARY KEY AUTOINCREMENT, token TEXT, role TEXT, email TEXT, expiresAt TEXT)")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS session")
        onCreate(db)
    }

    fun saveSession(token: String, role: String, email: String, expiresAt: String) {
        val db = writableDatabase
        db.execSQL("DELETE FROM session") // keep only one session
        val values = ContentValues().apply {
            put("token", token)
            put("role", role)
            put("email", email)
            put("expiresAt", expiresAt)
        }
        db.insert("session", null, values)
        db.close()
    }

    fun getSession(): String? {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT token FROM session LIMIT 1", null)
        var token: String? = null
        if (cursor.moveToFirst()) {
            token = cursor.getString(0)
        }
        cursor.close()
        db.close()
        return token
    }

    // Add this method to clear the session
    fun clearSession() {
        val db = writableDatabase
        db.execSQL("DELETE FROM session")
        db.close()
    }
}