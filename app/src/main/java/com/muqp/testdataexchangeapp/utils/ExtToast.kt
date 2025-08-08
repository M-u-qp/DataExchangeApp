package com.muqp.testdataexchangeapp.utils

import android.content.Context
import android.widget.Toast

object ExtToast {
    fun Context.toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}