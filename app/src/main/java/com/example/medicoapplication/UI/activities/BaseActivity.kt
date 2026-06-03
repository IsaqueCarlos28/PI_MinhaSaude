package com.example.medicoapplication.UI.activities

import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.medicoapplication.UI.common.mappers.ErrorMapper
import com.example.medicoapplication.data.remote.NetworkError

abstract class BaseActivity : AppCompatActivity() {
    protected fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    protected open fun handleError(error: NetworkError) {
        showToast(ErrorMapper.getMessage(error))
    }

    protected fun showValidationError(
        field: EditText,
        message: String
    ) {
        field.error = message
        field.requestFocus()
    }
}