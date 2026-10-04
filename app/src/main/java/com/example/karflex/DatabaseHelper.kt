package com.example.karflex

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "KarFlex.db"
        private const val DATABASE_VERSION = 1

        // Users table
        const val TABLE_USERS = "Users"

        const val COL_USER_ID = "UserID"
        const val COL_USERNAME = "Username"
        const val COL_PASSWORD = "Password"
        const val COL_PHONE = "PhoneNumber"
    }

    override fun onCreate(db: SQLiteDatabase) {

        val createUsersTable = """
            CREATE TABLE $TABLE_USERS (
                $COL_USER_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_USERNAME TEXT NOT NULL UNIQUE,
                $COL_PASSWORD TEXT NOT NULL,
                $COL_PHONE TEXT NOT NULL
            )
        """.trimIndent()

        db.execSQL(createUsersTable)
    }

    override fun onUpgrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int
    ) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)
    }

    // =========================
    // REGISTER USER
    // =========================

    fun registerUser(
        username: String,
        password: String,
        phone: String
    ): Boolean {

        val db = writableDatabase

        val values = ContentValues().apply {
            put(COL_USERNAME, username)
            put(COL_PASSWORD, password)
            put(COL_PHONE, phone)
        }

        return try {

            val result = db.insert(
                TABLE_USERS,
                null,
                values
            )

            result != -1L

        } catch (e: Exception) {

            false
        }
    }

    // =========================
    // CHECK USER LOGIN
    // =========================

    fun checkLogin(
        username: String,
        password: String
    ): Boolean {

        val db = readableDatabase

        val cursor = db.query(
            TABLE_USERS,
            arrayOf(COL_USER_ID),
            "$COL_USERNAME = ? AND $COL_PASSWORD = ?",
            arrayOf(username, password),
            null,
            null,
            null
        )

        val exists = cursor.moveToFirst()

        cursor.close()

        return exists
    }

    // =========================
    // GET USER ID
    // =========================

    fun getUserId(username: String): Int {

        val db = readableDatabase

        val cursor = db.query(
            TABLE_USERS,
            arrayOf(COL_USER_ID),
            "$COL_USERNAME = ?",
            arrayOf(username),
            null,
            null,
            null
        )

        var userId = -1

        if (cursor.moveToFirst()) {
            userId = cursor.getInt(
                cursor.getColumnIndexOrThrow(COL_USER_ID)
            )
        }

        cursor.close()

        return userId
    }

    // =========================
    // GET USER PHONE
    // =========================

    fun getUserPhone(username: String): String? {

        val db = readableDatabase

        val cursor = db.query(
            TABLE_USERS,
            arrayOf(COL_PHONE),
            "$COL_USERNAME = ?",
            arrayOf(username),
            null,
            null,
            null
        )

        var phone: String? = null

        if (cursor.moveToFirst()) {
            phone = cursor.getString(
                cursor.getColumnIndexOrThrow(COL_PHONE)
            )
        }

        cursor.close()

        return phone
    }

    // =========================
    // CHECK USERNAME
    // =========================

    fun isUsernameExists(username: String): Boolean {

        val db = readableDatabase

        val cursor = db.query(
            TABLE_USERS,
            arrayOf(COL_USER_ID),
            "$COL_USERNAME = ?",
            arrayOf(username),
            null,
            null,
            null
        )

        val exists = cursor.moveToFirst()

        cursor.close()

        return exists
    }
}