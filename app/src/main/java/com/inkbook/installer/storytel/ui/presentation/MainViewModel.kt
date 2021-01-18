package com.inkbook.installer.storytel.ui.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inkbook.installer.storytel.core.utils.SingleLiveEvent
import com.inkbook.installer.storytel.models.InstallResultModel
import com.inkbook.installer.storytel.ui.usecase.InstallUseCase
import com.inkbook.installer.storytel.ui.usecase.UninstallUseCase
import com.inkbook.installer.storytel.ui.usecase.UnzipXapkUseCase
import java.io.File

class MainViewModel(private val unzipXapkUseCase: UnzipXapkUseCase,
                    private val installUseCase: InstallUseCase,
                    private val uninstallUseCase: UninstallUseCase
): ViewModel() {

    val uninstallResult = SingleLiveEvent<Boolean>()
    val installingProgress = MutableLiveData<Boolean>()
    val data = MutableLiveData<InstallResultModel>()
    val error = SingleLiveEvent<Throwable>()

    fun uninstallSelf(packageName: String) {
        uninstallUseCase(packageName, viewModelScope) { result ->
            result.onSuccess { uninstallResult.value = it }
            result.onFailure { error.value = it }
        }
    }

    fun unzipXapk() {
        installingProgress.value = true

        unzipXapkUseCase(viewModelScope) { result ->
            result.onFailure { error.value = it }
            result.onSuccess { installApk(it) }
        }
    }

    private fun installApk(file: File) {
        installUseCase(file, viewModelScope) { result ->
            result.onSuccess {
                installingProgress.value = false
                data.value = it
            }
            result.onFailure {
                installingProgress.value = false
                error.value = it
            }

        }
    }
}