package com.inkbook.installer.storytel.core.managers

import android.util.Log
import com.google.gson.Gson
import com.inkbook.installer.storytel.core.exceptions.InstallException
import com.inkbook.installer.storytel.core.extentions.deleteExt
import com.inkbook.installer.storytel.core.extentions.isApk
import com.inkbook.installer.storytel.core.extentions.isCompatible
import com.inkbook.installer.storytel.core.extentions.isJson
import com.inkbook.installer.storytel.core.utils.ShellExecutor
import com.inkbook.installer.storytel.models.ApkModel
import com.inkbook.installer.storytel.models.InstallResultModel
import java.io.*

class InstallManager(private val shellExecutor: ShellExecutor) {

    companion object {
        private val TAG = InstallManager::class.java.simpleName
    }

    fun uninstallApk(packageName: String): Boolean {
        val installCommand = "pm uninstall -k $packageName\n"
        return shellExecutor.execute(installCommand)
    }

    fun installApkList(file: File): InstallResultModel {
        val files = file.listFiles() ?: throw InstallException("App doesn't have any apk files!")

        val app = getPackageNameFromManifest(files)
        val apks = files.filter { it.isFile && it.isApk() && it.isCompatible() }
        val installApksResults = ArrayList<Boolean>()

        val installMainApkResult = shellExecutor.installMultipleApk(apks)
        installApksResults.add(installMainApkResult)

        file.deleteExt()
        Log.d(
            TAG,
            "${app.name} is ${if (installApksResults.contains(true)) "" else "not"} installed"
        )
        return InstallResultModel(app.package_name, installApksResults.contains(true))
    }

    private fun getPackageNameFromManifest(files: Array<File>): ApkModel {
        val gson = Gson()
        val json = StringBuilder()
        val manifestFile = files.filter { it.isJson() }.first()

        try {
            val br = BufferedReader(FileReader(manifestFile))
            var line: String?
            while (br.readLine().also { line = it } != null) {
                json.append(line)
            }
            br.close()
            json.toString()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return gson.fromJson(json.toString(), ApkModel::class.java)
    }


}