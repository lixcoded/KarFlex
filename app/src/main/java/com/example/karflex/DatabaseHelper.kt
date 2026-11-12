package com.example.karflex

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "KarFlex.db"
        private const val DATABASE_VERSION = 3

        // Users table
        const val TABLE_USERS = "Users"

        const val COL_USER_ID = "UserID"
        const val COL_USERNAME = "Username"
        const val COL_PASSWORD = "Password"
        const val COL_PHONE = "PhoneNumber"

        // Transactions table
        const val TABLE_TRANSACTIONS = "Transactions"

        const val COL_TRANSACTION_ID = "TransactionID"
        const val COL_VEHICLE_ID = "VehicleID"
        const val COL_DURATION = "Duration"
        const val COL_START_DATE = "StartDate"
        const val COL_TRANSACTION_USER_ID = "UserID"
        const val COL_STATUS = "Status"

        const val STATUS_ACTIVE = "Active"
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

        createTransactionsTable(db)
    }

    private fun createTransactionsTable(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE $TABLE_TRANSACTIONS (
                $COL_TRANSACTION_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_VEHICLE_ID INTEGER NOT NULL,
                $COL_DURATION INTEGER NOT NULL,
                $COL_START_DATE TEXT NOT NULL,
                $COL_TRANSACTION_USER_ID INTEGER NOT NULL DEFAULT -1,
                $COL_STATUS TEXT NOT NULL DEFAULT '$STATUS_ACTIVE'
            )
        """.trimIndent()

        db.execSQL(createTable)
    }

    override fun onUpgrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int
    ) {
        if (oldVersion < 2) {
            createTransactionsTable(db)
        }

        if (oldVersion < 3) {
            db.execSQL(
                """
                ALTER TABLE $TABLE_TRANSACTIONS
                ADD COLUMN $COL_TRANSACTION_USER_ID INTEGER NOT NULL DEFAULT -1
                """.trimIndent()
            )

            db.execSQL(
                """
                ALTER TABLE $TABLE_TRANSACTIONS
                ADD COLUMN $COL_STATUS TEXT NOT NULL DEFAULT '$STATUS_ACTIVE'
                """.trimIndent()
            )
        }
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

        return cursor.use {
            it.moveToFirst()
        }
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

        return cursor.use {
            if (it.moveToFirst()) {
                it.getInt(
                    it.getColumnIndexOrThrow(COL_USER_ID)
                )
            } else {
                -1
            }
        }
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

        return cursor.use {
            if (it.moveToFirst()) {
                it.getString(
                    it.getColumnIndexOrThrow(COL_PHONE)
                )
            } else {
                null
            }
        }
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

        return cursor.use {
            it.moveToFirst()
        }
    }

    // =========================
    // INSERT RENTAL TRANSACTION
    // =========================

    fun insertTransaction(
        vehicleId: Int,
        userId: Int,
        duration: Int,
        startDate: String
    ): Boolean {

        if (vehicleId <= 0 || userId <= 0 || duration <= 0) {
            return false
        }

        val db = writableDatabase

        // Prevent a vehicle from being rented more than once.
        if (isVehicleRented(vehicleId)) {
            return false
        }

        val values = ContentValues().apply {
            put(COL_VEHICLE_ID, vehicleId)
            put(COL_TRANSACTION_USER_ID, userId)
            put(COL_DURATION, duration)
            put(COL_START_DATE, startDate)
            put(COL_STATUS, STATUS_ACTIVE)
        }

        return try {
            val result = db.insert(
                TABLE_TRANSACTIONS,
                null,
                values
            )

            result != -1L
        } catch (e: Exception) {
            false
        }
    }

    // =========================
    // CHECK VEHICLE AVAILABILITY
    // =========================

    fun isVehicleRented(vehicleId: Int): Boolean {

        val db = readableDatabase

        val cursor = db.query(
            TABLE_TRANSACTIONS,
            arrayOf(COL_TRANSACTION_ID),
            "$COL_VEHICLE_ID = ? AND $COL_STATUS = ? AND $COL_TRANSACTION_USER_ID > 0",
            arrayOf(vehicleId.toString(), STATUS_ACTIVE),
            null,
            null,
            null
        )

        return cursor.use {
            it.moveToFirst()
        }
    }

    // =========================
    // GET ALL RENTED VEHICLE IDS
    // =========================

    fun getRentedVehicleIds(): Set<Int> {

        val rentedIds = mutableSetOf<Int>()

        val db = readableDatabase

        val cursor = db.query(
            true,
            TABLE_TRANSACTIONS,
            arrayOf(COL_VEHICLE_ID),
            "$COL_STATUS = ? AND $COL_TRANSACTION_USER_ID > 0",
            arrayOf(STATUS_ACTIVE),
            null,
            null,
            null,
            null
        )

        cursor.use {
            while (it.moveToNext()) {
                rentedIds.add(
                    it.getInt(
                        it.getColumnIndexOrThrow(COL_VEHICLE_ID)
                    )
                )
            }
        }

        return rentedIds
    }

    // =========================
    // GET ACTIVE RENTALS BY USER
    // =========================

    fun getActiveRentals(userId: Int): List<Rental> {

        val rentals = mutableListOf<Rental>()

        if (userId <= 0) {
            return rentals
        }

        val db = readableDatabase

        val cursor = db.query(
            TABLE_TRANSACTIONS,
            arrayOf(
                COL_TRANSACTION_ID,
                COL_VEHICLE_ID,
                COL_DURATION,
                COL_START_DATE,
                COL_STATUS
            ),
            "$COL_TRANSACTION_USER_ID = ? AND $COL_STATUS = ?",
            arrayOf(userId.toString(), STATUS_ACTIVE),
            null,
            null,
            "$COL_TRANSACTION_ID DESC"
        )

        cursor.use {
            while (it.moveToNext()) {

                val transactionId = it.getInt(
                    it.getColumnIndexOrThrow(COL_TRANSACTION_ID)
                )

                val vehicleId = it.getInt(
                    it.getColumnIndexOrThrow(COL_VEHICLE_ID)
                )

                val duration = it.getInt(
                    it.getColumnIndexOrThrow(COL_DURATION)
                )

                val startDate = it.getString(
                    it.getColumnIndexOrThrow(COL_START_DATE)
                )

                val status = it.getString(
                    it.getColumnIndexOrThrow(COL_STATUS)
                )

                val vehicle = SampleData.vehicleById(vehicleId)

                if (vehicle != null) {
                    rentals.add(
                        Rental(
                            id = transactionId,
                            vehicle = vehicle.copy(isRented = true),
                            userId = userId,
                            duration = duration,
                            startDate = startDate,
                            status = status
                        )
                    )
                }
            }
        }

        return rentals
    }

    // =========================
    // GET ALL RENTAL HISTORY BY USER
    // =========================

    fun getRentalHistory(userId: Int): List<Rental> {

        val rentals = mutableListOf<Rental>()

        if (userId <= 0) {
            return rentals
        }

        val db = readableDatabase

        val cursor = db.query(
            TABLE_TRANSACTIONS,
            arrayOf(
                COL_TRANSACTION_ID,
                COL_VEHICLE_ID,
                COL_DURATION,
                COL_START_DATE,
                COL_STATUS
            ),
            "$COL_TRANSACTION_USER_ID = ?",
            arrayOf(userId.toString()),
            null,
            null,
            "$COL_TRANSACTION_ID DESC"
        )

        cursor.use {
            while (it.moveToNext()) {

                val transactionId = it.getInt(
                    it.getColumnIndexOrThrow(COL_TRANSACTION_ID)
                )

                val vehicleId = it.getInt(
                    it.getColumnIndexOrThrow(COL_VEHICLE_ID)
                )

                val duration = it.getInt(
                    it.getColumnIndexOrThrow(COL_DURATION)
                )

                val startDate = it.getString(
                    it.getColumnIndexOrThrow(COL_START_DATE)
                )

                val status = it.getString(
                    it.getColumnIndexOrThrow(COL_STATUS)
                )

                val vehicle = SampleData.vehicleById(vehicleId)

                if (vehicle != null) {
                    rentals.add(
                        Rental(
                            id = transactionId,
                            vehicle = vehicle.copy(
                                isRented = status == STATUS_ACTIVE
                            ),
                            userId = userId,
                            duration = duration,
                            startDate = startDate,
                            status = status
                        )
                    )
                }
            }
        }

        return rentals
    }

    // =========================
    // UPDATE RENTAL DURATION
    // =========================

    fun updateRentalDuration(
        transactionId: Int,
        userId: Int,
        duration: Int
    ): Boolean {

        if (transactionId <= 0 || userId <= 0 || duration <= 0) {
            return false
        }

        val values = ContentValues().apply {
            put(COL_DURATION, duration)
        }

        val updatedRows = writableDatabase.update(
            TABLE_TRANSACTIONS,
            values,
            "$COL_TRANSACTION_ID = ? AND $COL_TRANSACTION_USER_ID = ? AND $COL_STATUS = ?",
            arrayOf(
                transactionId.toString(),
                userId.toString(),
                STATUS_ACTIVE
            )
        )

        return updatedRows > 0
    }

    // =========================
    // CANCEL RENTAL
    // =========================

    fun deleteRental(
        transactionId: Int,
        userId: Int
    ): Boolean {

        if (transactionId <= 0 || userId <= 0) {
            return false
        }

        val deletedRows = writableDatabase.delete(
            TABLE_TRANSACTIONS,
            "$COL_TRANSACTION_ID = ? AND $COL_TRANSACTION_USER_ID = ?",
            arrayOf(
                transactionId.toString(),
                userId.toString()
            )
        )

        return deletedRows > 0
    }
}
