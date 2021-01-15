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
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.viewmodel.ext.android.viewModel


class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val isPermissionGranted = requestPermissions(this)

        startButton.setOnClickListener {
            if (isPermissionGranted) {
                viewModel.onViewCreated()
            }
        }

        installButton.setOnClickListener {

        }

        observeData()
        observeError()
    }

    private fun observeData() {
        viewModel.data.observe(this) {
            startApp(it.packageName)
            //TODO remove this app
        }
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
            grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            viewModel.onViewCreated()
        }
    }

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }
}