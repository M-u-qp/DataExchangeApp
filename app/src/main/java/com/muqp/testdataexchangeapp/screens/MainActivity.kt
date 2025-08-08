package com.muqp.testdataexchangeapp.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.muqp.testdataexchangeapp.ui.theme.TestDataExchangeAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TestDataExchangeAppTheme {
                Scaffold(modifier = Modifier.Companion.fillMaxSize()) { innerPadding ->
                    SenderScreen(
                        modifier = Modifier.Companion.padding(
                            innerPadding
                        )
                    )
                }
            }
        }
    }
    companion object {
        const val MY_TEST_KEY = "myTestKey"
        const val MY_TEST_VALUE = "myTestValue"
        const val AUTHORITY_RECEIVER = "com.muqp.datareceiverapp"
        const val INTENT_AIDL = "com.muqp.aild.IPC_SERVICE"
        const val INTENT_FILTER = "com.muqp.CUSTOM_ACTION"
    }
}