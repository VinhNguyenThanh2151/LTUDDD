package com.example.run_core.utils

import android.app.Activity
import android.app.AlertDialog
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import com.example.run_core.R
import com.google.android.material.textfield.TextInputEditText



class ChangePasswordDialog(private val activity: Activity) {
    private var alertDialog: AlertDialog? = null

    fun show(onPasswordChanged: (String) -> Unit) {
        val builder = AlertDialog.Builder(activity)
        val inflater = LayoutInflater.from(activity)
        val view = inflater.inflate(R.layout.layout_diglog_change_password, null)
        val editTextNewPassword = view.findViewById<TextInputEditText>(R.id.editTextNewPassword)
        val editTextConfirmPassword = view.findViewById<TextInputEditText>(R.id.editTextConfirmPassword)
        val buttonChangePassword = view.findViewById<Button>(R.id.buttonChangePassword)

        builder.setView(view)
        alertDialog = builder.create()
        alertDialog?.show()

        buttonChangePassword.setOnClickListener {
            val newPassword = editTextNewPassword.text.toString().trim()
            val confirmPassword = editTextConfirmPassword.text.toString().trim()
            if (newPassword.isNotEmpty() && newPassword == confirmPassword) {
                onPasswordChanged(newPassword)
                alertDialog?.dismiss()
            } else {
                if (newPassword.isEmpty()) {
                    editTextNewPassword.error = "Password cannot be empty"
                }
                if (newPassword != confirmPassword) {
                    editTextConfirmPassword.error = "Passwords do not match"
                }
            }
        }
    }

    fun dismiss() {
        alertDialog?.dismiss()
    }
}
