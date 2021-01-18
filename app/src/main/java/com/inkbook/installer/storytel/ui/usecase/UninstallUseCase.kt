package com.inkbook.installer.storytel.ui.usecase

import com.inkbook.installer.storytel.core.managers.InstallManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UninstallUseCase(private val installManager: InstallManager) {

    operator fun invoke(packageName: String, coroutineScope: CoroutineScope, onResult: (Result<Boolean>) -> Unit) {
        coroutineScope.launch {
            val result = withContext(Dispatchers.IO) {
                runCatching { installManager.uninstallApk(packageName) }
            }

            onResult(result)
        }
    }
}