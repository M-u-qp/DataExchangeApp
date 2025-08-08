package com.muqp.datareceiverapp.screens

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.muqp.datareceiverapp.screens.MainActivity.Companion.BROADCAST_PREFS_DATA
import com.muqp.datareceiverapp.screens.MainActivity.Companion.BROADCAST_PREFS_KEY
import com.muqp.datareceiverapp.screens.MainActivity.Companion.CONTENT_URI_SENDER
import com.muqp.datareceiverapp.screens.MainActivity.Companion.MY_TEST_KEY
import com.muqp.datareceiverapp.screens.MainActivity.Companion.MY_TEST_VALUE
import com.muqp.datareceiverapp.screens.MainActivity.Companion.PREFS_AIDL_DATA_KEY

@Composable
fun ReceiverScreen(
    modifier: Modifier = Modifier
) {
    var contentProviderData by remember { mutableStateOf("") }
    var broadcastData by remember { mutableStateOf("") }
    var intentData by remember { mutableStateOf("") }
    var aidlData by remember { mutableStateOf("") }
    var fileData by remember { mutableStateOf("") }
    val context = LocalContext.current
    val prefsAidl =
        remember { context.getSharedPreferences(PREFS_AIDL_DATA_KEY, Context.MODE_PRIVATE) }

    val initialIntent = remember {
        (context as Activity).intent?.let {
            "Ключ: ${it.getStringExtra(MY_TEST_KEY)}, Значение: ${it.getStringExtra(MY_TEST_VALUE)}"
        } ?: "Ничего не передано"
    }

    val prefsBroadcast =
        remember { context.getSharedPreferences(BROADCAST_PREFS_KEY, Context.MODE_PRIVATE) }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri ->
            uri?.let {
                try {
                    val content = context.contentResolver.openInputStream(uri)?.use { stream ->
                        stream.bufferedReader().readText()
                    }
                    fileData = content ?: "Файл пустой"
                } catch (e: Exception) {
                    fileData = "Ошибка чтения файла: ${e.message}"
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
            text = "Полученные данные",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        val buttonModifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .height(48.dp)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                modifier = buttonModifier.weight(1f),
                shape = MaterialTheme.shapes.large,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                onClick = {
                    try {
                        val cursor = context.contentResolver.query(
                            CONTENT_URI_SENDER.toUri(),
                            null, null, null, null
                        )
                        Log.d("ContentProvider", "Cursor: $cursor")
                        contentProviderData = cursor?.use {
                            buildString {
                                while (it.moveToNext()) {
                                    append("${it.getString(0)}: ${it.getString(1)}\n")
                                }
                            }
                        } ?: "Нет данных в ContentProvider"
                    } catch (e: Exception) {
                        Log.e("ContentProvider", "Error querying", e)
                        contentProviderData = "Error: ${e.message}"
                    }

                    broadcastData =
                        prefsBroadcast.getString(BROADCAST_PREFS_DATA, "No broadcast received")
                            ?: ""
                    intentData = initialIntent

                    val key = prefsAidl.getString(MY_TEST_KEY, null)
                    val value = prefsAidl.getString(MY_TEST_VALUE, null)

                    aidlData = if (key != null && value != null) {
                        "\nКлюч: $key\nЗначение: $value"
                    } else {
                        "Нет данных в AIDL"
                    }

                }) {
                Icon(Icons.Default.Download, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Загрузить всё")
            }

            Button(
                modifier = buttonModifier.weight(1f),
                shape = MaterialTheme.shapes.large,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary
                ),
                onClick = {
                    filePickerLauncher.launch(arrayOf("text/plain"))
                }) {
                Icon(Icons.Default.FileOpen, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Выбрать файл")
            }
        }

        Button(
            modifier = buttonModifier,
            shape = MaterialTheme.shapes.large,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.error,
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
            onClick = {
                contentProviderData = ""
                broadcastData = ""
                intentData = ""
                aidlData = ""
                fileData = ""
            }) {
            Icon(Icons.Default.Delete, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Очистить всё")
        }

        Spacer(modifier = Modifier.height(16.dp))

        val cardModifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)

        if (contentProviderData.isNotEmpty()) {
            DataCard(
                title = "Content Provider",
                data = contentProviderData,
                icon = Icons.Default.Storage,
                modifier = cardModifier
            )
        }

        if (broadcastData.isNotEmpty()) {
            DataCard(
                title = "Broadcast",
                data = broadcastData,
                icon = Icons.Default.BroadcastOnPersonal,
                modifier = cardModifier
            )
        }

        if (intentData.isNotEmpty()) {
            DataCard(
                title = "Intent",
                data = intentData,
                icon = Icons.AutoMirrored.Filled.Send,
                modifier = cardModifier
            )
        }

        if (aidlData.isNotEmpty()) {
            DataCard(
                title = "AIDL",
                data = aidlData,
                icon = Icons.Default.Android,
                modifier = cardModifier
            )
        }

        if (fileData.isNotEmpty()) {
            DataCard(
                title = "Файл",
                data = fileData,
                icon = Icons.AutoMirrored.Filled.InsertDriveFile,
                modifier = cardModifier
            )
        }
    }
}

@Composable
fun DataCard(
    title: String,
    data: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Text(
                text = data,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}