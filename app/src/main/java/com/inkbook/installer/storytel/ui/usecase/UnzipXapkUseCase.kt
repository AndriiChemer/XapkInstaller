package com.inkbook.installer.storytel.ui.usecase

import com.inkbook.installer.storytel.core.managers.UnzipManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.*

class UnzipXapkUseCase(private val unzipManager: UnzipManager) {

    operator fun invoke(coroutineScope: CoroutineScope, onResult: (Result<File>) -> Unit) {
        coroutineScope.launch {
            val result = withContext(Dispatchers.IO) {
                runCatching { unzipManager.getUnzippedXapkFile() }
            }

            onResult(result)
        }
    }
}