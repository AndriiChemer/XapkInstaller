package com.inkbook.installer.storytel.ui.presentation

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.inkbook.installer.storytel.R
import org.koin.android.viewmodel.ext.android.viewModel


class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModel()
    private val message = "Installation of Storytel will take a few steps.\tPlease install all components which will appear during installation process."

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val isPermissionGranted = requestPermissions(this)
        showInstallingDialogDialog()

        if (!isPermissionGranted) {
            //TODO show dialog
        }

        observeData()
        observeError()
    }

    private fun observeData() {
        viewModel.data.observe(this) {
            startApp(it.packageName)
            removeSelf()
        }
    }

    private fun removeSelf() {
        viewModel.uninstallSelf(packageName)
    }

    private fun observeError() {
        viewModel.error.observe(this) {
            Log.d(TAG, "Error = ${it.printStackTrace()}")
            Toast.makeText(this@MainActivity, "Error", Toast.LENGTH_LONG).show()
        }
    }

    private fun startApp(packageName: String) {
        val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
        launchIntent?.let { startActivity(it) }
    }

    private fun requestPermissions(context: Context) : Boolean {
        return if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    context as Activity,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )) {
                val alertBuilder: AlertDialog.Builder = AlertDialog.Builder(context)
                alertBuilder.setCancelable(true)
                alertBuilder.setTitle("Permission necessary")
                alertBuilder.setMessage("External storage permission is necessary")
                alertBuilder.setPositiveButton(
                    android.R.string.ok
                ) { _, _ ->
                    ActivityCompat.requestPermissions(
                        context, arrayOf(
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ), 2
                    )
                }
                val alert: AlertDialog = alertBuilder.create()
                alert.show()
            } else {
                ActivityCompat.requestPermissions(
                    context, arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ), 2
                )
            }
            false
        } else {
            true
        }
    }

    //TODO
    private fun showInstallingDialogDialog() {
        val builder = AlertDialog.Builder(this) //, R.style.ArtaTechTheme_Dialog
        builder.setTitle("Installing")
        builder.setMessage(message)
            .setCancelable(false)
            .setPositiveButton(
                android.R.string.ok
            ) { dialog, id ->
                viewModel.unzipXapk()
            }
            .setNegativeButton(
                resources.getString(android.R.string.cancel)
            ) { dialog, which ->
                dialog.dismiss()
                finish()
            }
        val alert = builder.create()
        alert.show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (grantResults[0] != PackageManager.PERMISSION_GRANTED &&
            grantResults[1] != PackageManager.PERMISSION_GRANTED) {
            //TODO show dialog
        }
    }

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }
}