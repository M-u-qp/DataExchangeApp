package com.muqp.datareceiverapp.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.muqp.datareceiverapp.ui.theme.TestDataExchangeAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TestDataExchangeAppTheme {
                Scaffold(modifier = Modifier.Companion.fillMaxSize()) { innerPadding ->
                    ReceiverScreen(
                        modifier = Modifier.Companion.padding(innerPadding)
                    )
                }
            }
        }
    }
    companion object {
        const val MY_TEST_KEY = "myTestKey"
        const val MY_TEST_VALUE = "myTestValue"
        const val PREFS_AIDL_DATA_KEY = "prefsAidlDataKey"
        const val BROADCAST_PREFS_KEY = "broadcastPrefsKey"
        const val BROADCAST_PREFS_DATA = "broadcastPrefsData"
        const val CONTENT_URI_SENDER = "content://com.muqp.testdataexchangeapp/data"
    }
}