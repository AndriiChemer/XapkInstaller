package com.inkbook.installer.storytel.ui.presentation

import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.inkbook.installer.storytel.R
import com.inkbook.installer.storytel.core.dialogs.InstallDialog
import com.inkbook.installer.storytel.core.dialogs.PermissionDialog
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.viewmodel.ext.android.viewModel


class MainActivity : AppCompatActivity(), InstallDialog.InstallDialogClickListener {

    private val viewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        PermissionDialog.requestPermissions(this)

        observeData()
        observeError()
    }

    private fun observeData() {
        viewModel.data.observe(this) {
            startApp(it.packageName)
            removeSelf()
        }

        viewModel.installingProgress.observe(this) {
            if (it) {
                showProgress()
            } else {
                hideProgress()
            }
        }
    }

    private fun removeSelf() {
        viewModel.uninstallSelf(packageName)
    }

    private fun observeError() {
        viewModel.error.observe(this) {
            Log.d(TAG, "Error = ${it.printStackTrace()}")
        }
    }

    private fun startApp(packageName: String) {
        val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
        launchIntent?.let { startActivity(it) }
    }

    private fun showProgress() {
        titleInstalling.visibility = View.VISIBLE
        progressBar.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        titleInstalling.visibility = View.INVISIBLE
        progressBar.visibility = View.INVISIBLE
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (grantResults[0] != PackageManager.PERMISSION_GRANTED &&
            grantResults[1] != PackageManager.PERMISSION_GRANTED) {
            PermissionDialog.requestPermissions(this)
        } else {
            InstallDialog.showInstallingDialogDialog(this, this)
        }
    }

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }

    override fun onPositiveClick() {
        viewModel.unzipXapk()
    }

    override fun onNegativeClick(dialog: DialogInterface) {
        dialog.dismiss()
        finish()
    }
}