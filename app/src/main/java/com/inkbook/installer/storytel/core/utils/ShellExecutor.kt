package com.inkbook.installer.storytel.core.utils

import android.util.Log
import java.io.*
import java.util.regex.Matcher
import java.util.regex.Pattern

class ShellExecutor {

    companion object {
        private val TAG = ShellExecutor::class.java.simpleName
    }

    fun execute(installCommand: String): Boolean {
        var os: DataOutputStream? = null
        try {
            val process = Runtime.getRuntime().exec("su")
            os = DataOutputStream(process.outputStream)
            os.writeBytes("yitaoSu\n")
            os.writeBytes("exec\n")
            os.writeBytes(installCommand)
            os.writeBytes("exit\n")
            os.flush()

            val result = process.waitFor()

            return result == 0
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        } finally {
            os?.close()
        }
    }

    fun installMultipleApk(apks: List<File>): Boolean {
        var total: Long = 0
        for (apk: File in apks) {
            total += apk.length()
        }
        Log.d(TAG, "installMultipleCmd: total apk size $total")
        var sessionID: Long = 0
        try {
            val pmInstallCreateProcess = Runtime.getRuntime().exec("su")
            val writer = BufferedWriter(OutputStreamWriter(pmInstallCreateProcess.outputStream))
            writer.write("yitaoSu\n")
            writer.write("exec\n")
            writer.write("pm install-create\n")
            writer.flush()
            writer.close()
            val ret = pmInstallCreateProcess.waitFor()
            Log.d(TAG, "installMultipleCmd: pm install-create return $ret")
            val pmCreateReader =
                BufferedReader(InputStreamReader(pmInstallCreateProcess.inputStream))
            var l: String?
            val sessionIDPattern: Pattern = Pattern.compile(".*(\\[\\d+\\])")
            while (pmCreateReader.readLine().also { l = it } != null) {
                val matcher: Matcher = sessionIDPattern.matcher(l)
                if (matcher.matches()) {
                    sessionID = matcher.group(1).removePrefix("[").removeSuffix("]").toLong()
                }
            }
            Log.d(TAG, "installMultipleCmd: pm install-create sessionID $sessionID")
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        val pmInstallWriteBuilder = java.lang.StringBuilder()
        for (apk: File in apks) {
            pmInstallWriteBuilder.append(
                "cat " + apk.absolutePath.toString() + " | " +
                        "pm install-write -S " + apk.length()
                    .toString() + " " + sessionID.toString() + " " + apk.name.toString() + " -"
            )
            pmInstallWriteBuilder.append("\n")
        }
        Log.d(
            TAG,
            "installMultipleCmd: will perform pm install write \n$pmInstallWriteBuilder"
        )
        try {
            val pmInstallWriteProcess = Runtime.getRuntime().exec("su")
            val writer = BufferedWriter(OutputStreamWriter(pmInstallWriteProcess.outputStream))
            writer.write("yitaoSu\n")
            writer.write("exec\n")
            writer.write(pmInstallWriteBuilder.toString())
            writer.flush()
            writer.close()
            val ret = pmInstallWriteProcess.waitFor()
            Log.d(TAG, "installMultipleCmd: pm install-write return $ret")
            checkShellError(ret, pmInstallWriteProcess)
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        try {
            val pmInstallCommitProcess = Runtime.getRuntime().exec("su")
            val writer = BufferedWriter(OutputStreamWriter(pmInstallCommitProcess.outputStream))
            writer.write("pm install-commit $sessionID")
            writer.flush()
            writer.close()
            val ret = pmInstallCommitProcess.waitFor()
            Log.d(TAG, "installMultipleCmd: pm install-commit return $ret")
            checkShellError(ret, pmInstallCommitProcess)
            return ret == 0
        } catch (e: IOException) {
            e.printStackTrace()
            return false
        } catch (e: InterruptedException) {
            e.printStackTrace()
            return false
        }
    }

    private fun checkShellError(ret: Int, process: Process?) {
        if (process != null && ret != 0) {
            var reader: BufferedReader? = null
            try {
                reader = BufferedReader(InputStreamReader(process.errorStream))
                var l: String
                while ((reader.readLine().also { l = it }) != null) {
                    Log.d(TAG, "checkShouldShowError: $l")
                }
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                if (reader != null) {
                    try {
                        reader.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }
}