package com.ubrain.sqlitedatabasedemo

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

import com.ubrain.sqlitedatabasedemo.ModelMaster.DBModel
import java.util.*
import android.database.sqlite.SQLiteException as SQLiteException1
import kotlin.collections.ArrayList as ArrayList1

/**
 * Created by Mansi on 18-01-2019.
 */

private const val TABLE_NAME = "entry"
private const val COLUMN_NAME_ID = "id"
private const val COLUMN_NAME_TITLE = "title"
private const val COLUMN_NAME_SUBTITLE = "subtitle"
private const val DATABASE_VERSION = 1
private const val DATABASE_NAME = "MyDbExample"

private const val SQL_CREATE_ENTRIES =
        "CREATE TABLE $TABLE_NAME (" +
                "$COLUMN_NAME_ID INTEGER AUTO INCREMENT PRIMARY KEY," +
                "$COLUMN_NAME_TITLE TEXT," +
                "$COLUMN_NAME_SUBTITLE TEXT)"

private const val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS $TABLE_NAME"

class DbQueryClass(val context: Context) {

    inner class MyDbHelper : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

        override fun onCreate(p0: SQLiteDatabase?) {
            p0?.execSQL(SQL_CREATE_ENTRIES)
        }

        override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
            p0!!.execSQL(SQL_DELETE_ENTRIES)
            onCreate(p0)
        }
    }

    fun insertRow(dbModel: ModelMaster.DBModel): Long {
        val db = MyDbHelper().writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME_ID, dbModel.userId)
            put(COLUMN_NAME_TITLE, dbModel.title)
            put(COLUMN_NAME_SUBTITLE, dbModel.subTitle)
        }
        val newRowId = db.insert(TABLE_NAME, null, values)
        db.close()
        return newRowId
    }

    fun updateData(dbModel: ModelMaster.DBModel): Int {
        val db = MyDbHelper().writableDatabase
        val contentValues = ContentValues().apply {
            put(COLUMN_NAME_ID, dbModel.userId)
            put(COLUMN_NAME_TITLE, dbModel.title)
            put(COLUMN_NAME_SUBTITLE, dbModel.subTitle)
        }
        val success = db.update(TABLE_NAME, contentValues, "$COLUMN_NAME_ID=" + dbModel.userId, null)
        db.close()
        return success
    }

    fun deleteData(dbModel: DBModel): Int {
        val db = MyDbHelper().writableDatabase
        val success = db.delete(TABLE_NAME, "$COLUMN_NAME_ID=" + dbModel.userId, null)
        db.close()
        return success
    }

    @SuppressLint("Recycle")
    fun viewAllData(): MutableList<DBModel> {
        val empList: MutableList<DBModel> = mutableListOf()
        val selectQuery = "SELECT  * FROM $TABLE_NAME"
        val db = MyDbHelper().readableDatabase
        val cursor: Cursor?
        try {
            cursor = db.rawQuery(selectQuery, null)
        } catch (e: SQLiteException1) {
            db.execSQL(selectQuery)
            return ArrayList()
        }
        var userId: Int
        var userName: String
        var userEmail: String
        if (cursor.moveToFirst()) {
            do {
                cursor.run {
                    userId = getInt(getColumnIndex(COLUMN_NAME_ID))
                    userName = getString(getColumnIndex(COLUMN_NAME_TITLE))
                    userEmail = getString(getColumnIndex(COLUMN_NAME_SUBTITLE))
                    val emp = DBModel(userId = userId, title = userName, subTitle = userEmail)
                    empList.add(emp)
                }
            } while (cursor.moveToNext())
        }
        cursor!!.close()
        db.close()
        return empList
    }

    fun viewTotalCount(): Int {
        var totalCount: Int? = 0
        val selectQuery = "SELECT  * FROM $TABLE_NAME"
        val db = MyDbHelper().readableDatabase
        val cursor: Cursor?
        try {
            cursor = db.rawQuery(selectQuery, null)
        } catch (e: SQLiteException1) {
            db.execSQL(selectQuery)
            return 0
        }
        if (cursor!!.moveToFirst()) {
            do {
                totalCount = totalCount!! + 1
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return totalCount!!
    }
}