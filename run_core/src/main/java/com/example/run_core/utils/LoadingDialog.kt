package com.example.run_core.utils

import android.app.Activity
import android.app.AlertDialog
import android.view.LayoutInflater
import com.example.run_core.R
import com.example.run_core.databinding.DialogLayoutBinding


class LoadingDialog(private val activity: Activity) {
    private var alertDialog: AlertDialog? = null

    fun startLoading() {
        val builder = AlertDialog.Builder(activity, R.style.loadingDialogStyle)
        val binding: DialogLayoutBinding =
            DialogLayoutBinding.inflate(LayoutInflater.from(activity), null, false)
        builder.setView(binding.getRoot())
        builder.setCancelable(false)
        alertDialog = builder.create()
        binding.rotateLoading.start()
        alertDialog!!.show()
    }

    fun stopLoading() {
        alertDialog!!.dismiss()
    }
}
