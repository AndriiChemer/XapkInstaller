package com.inkbook.installer.storytel.core.managers

import android.util.Log
import com.google.gson.Gson
import com.inkbook.installer.storytel.core.exceptions.InstallException
import com.inkbook.installer.storytel.models.ApkModel
import com.inkbook.installer.storytel.models.InstallResultModel
import java.io.*

class InstallManager {

    companion object {
        private val TAG = InstallManager::class.java.simpleName
    }

    fun installApkList(file: File): InstallResultModel {
        val files = file.listFiles() ?: throw InstallException("App doesn't have any apk files!")

        val packageName = getPackageNameFromManifest(files)
        val apkList = files.filter { it.isFile && it.isApk() }
        val installApksResults = ArrayList<Boolean>()

        apkList.forEach {
            val installResult = adbInstallApk(it)
            Log.d(TAG, "${it.name} is ${if (installResult) "" else "not"} installed")
            installApksResults.add(installResult)
        }

        file.deleteExt()
        Log.d(TAG, "${packageName.name} is ${ if (installApksResults.contains(true)) "" else "not"} installed")
        return InstallResultModel(packageName.package_name, installApksResults.contains(true))
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

    private fun adbInstallApk(file: File): Boolean {
        return if (file.exists()) {
            var os: DataOutputStream? = null
            try {
                val process = Runtime.getRuntime().exec("su")
                os = DataOutputStream(process.outputStream)
                os.writeBytes("yitaoSu\n")
                os.writeBytes("exec\n")
                os.writeBytes("pm install -r ${file.absolutePath}\n")
                os.writeBytes("exit\n")
                os.flush()

                val result = process.waitFor()

                result == 0
            } catch (e: Exception) {
                e.printStackTrace()
                false
            } finally {
                os?.close()
            }
        } else {
            false
        }
    }
}

fun File.isApk(): Boolean {
    return extension.toLowerCase() == "apk"
}

fun File.isJson(): Boolean {
    return extension.toLowerCase() == "json"
}

fun File.deleteExt(): Boolean {
    if (this.exists()) {
        val files = listFiles() ?: return true
        for (i in files.indices) {
            if (files[i].isDirectory) {
                files[i].deleteExt()
            } else {
                files[i].delete()
            }
        }
    }
    return delete()
}