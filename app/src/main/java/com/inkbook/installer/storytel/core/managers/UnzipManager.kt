package com.inkbook.installer.storytel.core.managers

import android.content.res.AssetManager
import com.inkbook.installer.storytel.core.utils.Constants
import org.apache.commons.io.IOUtils
import java.io.*
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipFile


class UnzipManager(private val assetManager: AssetManager) {

    private val appName: String = Constants.APP_NAME
    private val appExternalPath: String = "/storage/emulated/0/Download/${Constants.APP}.zip"


    fun getUnzippedXapkFile(): File {
        val file = File(appExternalPath)
        if (!file.exists()) {
            file.createNewFile()
        }

        val inputStream: InputStream = assetManager.open(appName)
        val fileOutputStream = FileOutputStream(file)

        try {
            val buffer = ByteArray(1024)
            var lenght: Int
            while (inputStream.read(buffer).also { lenght = it } > 0) {
                fileOutputStream.write(buffer, 0, lenght)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            fileOutputStream.close()
            inputStream.close()
        }

        return unzip(file)
    }

    private fun unzip(file: File): File {
        val parent: String = file.parent
            ?: throw NullPointerException("Parent path is null")
        val destinationPath = parent + File.separator + getFileName(file)

        try {
            val zipFile = ZipFile(file)
            val e: Enumeration<*> = zipFile.entries()

            while (e.hasMoreElements()) {
                val entry = e.nextElement() as ZipEntry
                unzipEntry(zipFile, entry, destinationPath)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw IOException("Error while extracting file $file")
        }

        file.delete()
        return File(destinationPath)
    }

    private fun unzipEntry(zipFile: ZipFile, entry: ZipEntry, outputDir: String) {

        val outputMainFolder = File(outputDir)
        if (!outputMainFolder.exists()) {
            outputMainFolder.mkdirs()
        }

        if (entry.isDirectory) {
            createDir(File(outputDir, entry.name))
            return
        }
        val outputFile = File(outputDir, entry.name)

        outputFile.parentFile?.let {
            if (it.exists()) {
                createDir(it)
            }
        }

        var inputStream: BufferedInputStream? = null
        var outputStream: BufferedOutputStream? = null

        try {
            inputStream = BufferedInputStream(zipFile.getInputStream(entry))
            outputStream = BufferedOutputStream(FileOutputStream(outputFile))

            IOUtils.copy(inputStream, outputStream)
        } catch (e: Exception) {

        } finally {
            outputStream?.close()
            inputStream?.close()
        }
    }

    private fun createDir(dir: File) {
        if (dir.exists()) {
            return
        }

        if (!dir.mkdirs()) {
            throw RuntimeException("Can not create dir $dir")
        }
    }

    private fun getFileName(file: File) =
        file.name.split('.')[0]
}