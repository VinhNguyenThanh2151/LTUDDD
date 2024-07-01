package com.example.run_core.utils

import android.app.Activity
import android.app.AlertDialog
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import com.example.run_core.R
import com.google.android.material.textfield.TextInputEditText



class ChangeEmailDialog(private val activity: Activity) {
    private var alertDialog: AlertDialog? = null

    fun show(onEmailChanged: (String) -> Unit) {
        val builder = AlertDialog.Builder(activity)
        val inflater = LayoutInflater.from(activity)
        val view = inflater.inflate(R.layout.layout_dialog_change_email, null)
        val editTextNewEmail = view.findViewById<TextInputEditText>(R.id.editTextNewEmail)
        val buttonUpdateEmail = view.findViewById<Button>(R.id.buttonUpdateEmail)

        builder.setView(view)
        alertDialog = builder.create()
        alertDialog?.show()

        buttonUpdateEmail.setOnClickListener {
            val newEmail = editTextNewEmail.text.toString().trim()
            if (newEmail.isNotEmpty()) {
                onEmailChanged(newEmail)
                alertDialog?.dismiss()
            } else {
                editTextNewEmail.error = "Email cannot be empty"
            }
        }
    }

    fun dismiss() {
        alertDialog?.dismiss()
    }
}
