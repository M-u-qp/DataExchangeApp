package com.muqp.testdataexchangeapp.screens

import android.content.ComponentName
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.BroadcastOnPersonal
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.muqp.shared.aidl.AidlDataExchange
import com.muqp.testdataexchangeapp.ViaContentProvider.Companion.CONTENT_URI_SENDER
import com.muqp.testdataexchangeapp.screens.MainActivity.Companion.AUTHORITY_RECEIVER
import com.muqp.testdataexchangeapp.screens.MainActivity.Companion.INTENT_AIDL
import com.muqp.testdataexchangeapp.screens.MainActivity.Companion.INTENT_FILTER
import com.muqp.testdataexchangeapp.screens.MainActivity.Companion.MY_TEST_KEY
import com.muqp.testdataexchangeapp.screens.MainActivity.Companion.MY_TEST_VALUE
import com.muqp.testdataexchangeapp.utils.ExtToast.toast

@Composable
fun SenderScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var key by remember { mutableStateOf("") }
    var value by remember { mutableStateOf("") }

    val createFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/plain"),
        onResult = { uri ->
            uri?.let {
                try {
                    context.contentResolver.openOutputStream(uri)?.use { stream ->
                        stream.write("$key=$value".toByteArray())
                    }
                    context.toast("Сохранено в файл")
                } catch (e: Exception) {
                    context.toast("Ошибка: ${e.message}")
                }
            }
        }
    )

    Column(
        modifier = modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Отправить данные",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        OutlinedTextField(
            value = key,
            onValueChange = { key = it },
            label = { Text("Ключ") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            shape = MaterialTheme.shapes.medium,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            )
        )
        OutlinedTextField(
            value = value,
            onValueChange = { value = it },
            label = { Text("Значение") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = MaterialTheme.shapes.medium,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            )
        )

        val buttonModifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .height(48.dp)

        Button(
            modifier = buttonModifier,
            shape = MaterialTheme.shapes.large,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ),
            onClick = {
            try {
                val values = ContentValues().apply {
                    put(MY_TEST_KEY, key)
                    put(MY_TEST_VALUE, value)
                }
                val uri = context.contentResolver.insert(
                    CONTENT_URI_SENDER.toUri(),
                    values
                )
                context.toast("Отправлено: $uri")
            } catch (e: Exception) {
                context.toast("Ошибка: ${e.message}")
                Log.e("ContentProvider", "Insert failed", e)
            }
        }) {
            Icon(imageVector = Icons.Default.Storage, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Отправить через ContentProvider")
        }

        Button(
            modifier = buttonModifier,
            shape = MaterialTheme.shapes.large,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
            ),
            onClick = {
            val intent = Intent(INTENT_FILTER).apply {
                putExtra(MY_TEST_KEY, key)
                putExtra(MY_TEST_VALUE, value)
                setPackage(AUTHORITY_RECEIVER)
            }
            context.sendBroadcast(intent)
            context.toast("Отправлено: $key=$value")
        }) {
            Icon(Icons.Default.BroadcastOnPersonal, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Отправить через Broadcast")
        }

        Button(
            modifier = buttonModifier,
            shape = MaterialTheme.shapes.large,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                contentColor = MaterialTheme.colorScheme.onTertiaryContainer
            ),
            onClick = {
            val intent = Intent().apply {
                setClassName(
                    AUTHORITY_RECEIVER,
                    "$AUTHORITY_RECEIVER.screens.MainActivity"
                )
                putExtra(MY_TEST_KEY, key)
                putExtra(MY_TEST_VALUE, value)
            }
            context.startActivity(intent)
        }) {
            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Отправить через Intent")
        }

        Button(
            modifier = buttonModifier,
            shape = MaterialTheme.shapes.large,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            onClick = {
            sendViaAidl(context, key, value)
        }) {
            Icon(Icons.Default.Android, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Отправить через AIDL")
        }

        Button(
            modifier = buttonModifier,
            shape = MaterialTheme.shapes.large,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            onClick = {
            createFileLauncher.launch("shared_data.txt")
        }) {
            Icon(Icons.AutoMirrored.Filled.InsertDriveFile, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Отправить через File")
        }
    }
}

private fun sendViaAidl(context: Context, key: String, value: String) {
    val intent = Intent(INTENT_AIDL).apply {
        setPackage(AUTHORITY_RECEIVER)
    }

    val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            try {
                val aidl = AidlDataExchange.Stub.asInterface(service)
                aidl.sendData(key, value)
                context.toast("Отправлено через AIDL")
            } catch (e: Exception) {
                context.toast("Ошибка AIDL: ${e.message}")
            } finally {
                context.unbindService(this)
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            Log.w("AIDL", "Connection to the service was lost")
        }
    }

    try {
        if (!context.bindService(intent, connection, Context.BIND_AUTO_CREATE)) {
            context.toast("Сервис AIDL не найден")
        }
    } catch (e: SecurityException) {
        context.toast("Ошибка: ${e.message}")
    }
}