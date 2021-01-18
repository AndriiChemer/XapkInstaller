package com.inkbook.installer.storytel.core.extentions

import android.os.Build
import java.io.File

fun File.isApk(): Boolean {
    return extension.toLowerCase() == "apk"
}

fun File.isJson(): Boolean {
    return extension.toLowerCase() == "json"
}

fun File.isCompatible(): Boolean {
    if (name.toLowerCase().contains("arm")) {
        return isArchitecture64()
    }
    return true
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

private fun isArchitecture64(): Boolean {

    return when(Build.MODEL) {
        "CalypsoPlus" -> false
        "Focus" -> false
        else -> true
    }
}