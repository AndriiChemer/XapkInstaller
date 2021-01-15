package com.inkbook.installer.storytel.ui.usecase

import com.inkbook.installer.storytel.core.managers.InstallManager
import com.inkbook.installer.storytel.models.InstallResultModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class InstallUseCase(private val installManager: InstallManager) {

    operator fun invoke(file: File, coroutineScope: CoroutineScope, onResult: (Result<InstallResultModel>) -> Unit) {
        coroutineScope.launch {
            val result = withContext(Dispatchers.IO) {
                runCatching { installManager.installApkList(file) }
            }

            onResult(result)
        }
    }
}