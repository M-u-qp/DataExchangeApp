package com.muqp.datareceiverapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.edit
import com.muqp.datareceiverapp.screens.MainActivity.Companion.BROADCAST_PREFS_DATA
import com.muqp.datareceiverapp.screens.MainActivity.Companion.BROADCAST_PREFS_KEY
import com.muqp.datareceiverapp.screens.MainActivity.Companion.MY_TEST_KEY
import com.muqp.datareceiverapp.screens.MainActivity.Companion.MY_TEST_VALUE

class ReceiverWithBroadcast : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val key = intent?.getStringExtra(MY_TEST_KEY) ?: "N/A"
        val value = intent?.getStringExtra(MY_TEST_VALUE) ?: "N/A"

        context?.getSharedPreferences(BROADCAST_PREFS_KEY, Context.MODE_PRIVATE)?.edit {
            putString(BROADCAST_PREFS_DATA, "$key=$value")
            apply()
        }
    }
}