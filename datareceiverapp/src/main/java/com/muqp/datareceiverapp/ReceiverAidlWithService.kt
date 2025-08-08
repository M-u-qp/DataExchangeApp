package com.muqp.datareceiverapp

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.muqp.datareceiverapp.screens.MainActivity.Companion.MY_TEST_KEY
import com.muqp.datareceiverapp.screens.MainActivity.Companion.MY_TEST_VALUE
import com.muqp.datareceiverapp.screens.MainActivity.Companion.PREFS_AIDL_DATA_KEY
import com.muqp.shared.aidl.AidlDataExchange

class ReceiverAidlWithService : Service() {
    private val prefs by lazy {
        getSharedPreferences(PREFS_AIDL_DATA_KEY, MODE_PRIVATE)
    }

    override fun onBind(intent: Intent): IBinder {
        return object : AidlDataExchange.Stub() {
            override fun sendData(key: String, value: String) {
                try {
                    prefs.edit().apply {
                        putString(MY_TEST_KEY, key)
                        putString(MY_TEST_VALUE, value)
                        apply()
                    }
                    Log.d("AIDL_DEBUG", "Saved to prefs: $key=$value")
                } catch (e: Exception) {
                    Log.e("AIDL_SAVE", "Error saving data", e)
                }
            }
        }
    }
}