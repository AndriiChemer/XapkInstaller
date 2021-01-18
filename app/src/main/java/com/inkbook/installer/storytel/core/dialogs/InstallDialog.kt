package com.inkbook.installer.storytel.core.dialogs

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface

class InstallDialog {

    companion object {
        fun showInstallingDialogDialog(context: Context, listener: InstallDialogClickListener) {
            //TODO message
            val message = "Installation of Storytel will take a few steps.\tPlease install all components which will appear during installation process."

            val builder = AlertDialog.Builder(context)
            builder.setTitle("Installing")
            builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton(
                    android.R.string.ok
                ) { _, _ ->
                    listener.onPositiveClick()
                }
                .setNegativeButton(
                    context.resources.getString(android.R.string.cancel)
                ) { dialog, _ ->
                    listener.onNegativeClick(dialog)
                }
            val alert = builder.create()
            alert.show()
        }
    }


    interface InstallDialogClickListener {
        fun onPositiveClick()
        fun onNegativeClick(dialog: DialogInterface)
    }
}