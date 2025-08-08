package com.muqp.testdataexchangeapp

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import androidx.core.net.toUri
import com.muqp.testdataexchangeapp.screens.MainActivity.Companion.MY_TEST_KEY
import com.muqp.testdataexchangeapp.screens.MainActivity.Companion.MY_TEST_VALUE

class ViaContentProvider: ContentProvider() {
    private val data = mutableMapOf<String, String>()

    override fun delete(
        uri: Uri,
        selection: String?,
        selectionArgs: Array<out String?>?
    ): Int = 0

    override fun getType(uri: Uri): String? = null

    override fun insert(
        uri: Uri,
        values: ContentValues?
    ): Uri? {
        if (uri.path?.startsWith("/data") == true) {
            val key =
                values?.getAsString(MY_TEST_KEY) ?: throw IllegalArgumentException("Key required")
            val value =
                values.getAsString(MY_TEST_VALUE) ?: throw IllegalArgumentException("Value required")
            data[key] = value
            return Uri.withAppendedPath(CONTENT_URI_SENDER.toUri(), key)
        }
        throw IllegalArgumentException("Unknown URI: $uri")
    }

    override fun onCreate(): Boolean = true

    override fun query(
        uri: Uri,
        projection: Array<out String?>?,
        selection: String?,
        selectionArgs: Array<out String?>?,
        sortOrder: String?
    ): Cursor? {
        if (uri.path?.startsWith("/data") == true) {
            val cursor = MatrixCursor(arrayOf(MY_TEST_KEY, MY_TEST_VALUE))
            data.forEach { (key, value) ->
                cursor.addRow(arrayOf(key, value))
            }
            return cursor
        }
        throw IllegalArgumentException("Unknown URI: $uri")
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String?>?
    ): Int = 0

    companion object {
        const val AUTHORITY_SENDER = "com.muqp.testdataexchangeapp"
        const val CONTENT_URI_SENDER = "content://$AUTHORITY_SENDER/data"
    }
}