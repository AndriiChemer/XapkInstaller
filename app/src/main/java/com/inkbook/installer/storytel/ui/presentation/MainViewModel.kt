package com.inkbook.installer.storytel.ui.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inkbook.installer.storytel.core.utils.SingleLiveEvent
import com.inkbook.installer.storytel.models.InstallResultModel
import com.inkbook.installer.storytel.ui.usecase.InstallUseCase
import com.inkbook.installer.storytel.ui.usecase.UnzipXapkUseCase
import java.io.File

class MainViewModel(private val unzipXapkUseCase: UnzipXapkUseCase,
                    private val installUseCase: InstallUseCase
): ViewModel() {

    val data = MutableLiveData<InstallResultModel>()
    val error = SingleLiveEvent<Throwable>()

    fun onViewCreated() {
        unzipAndInstall()
    }

    private fun unzipAndInstall() {
        unzipXapkUseCase(viewModelScope) { result ->
            result.onFailure { error.value = it }
            result.onSuccess { installApk(it) }

        }
    }

    private fun installApk(file: File) {
        installUseCase(file, viewModelScope) { result ->
            result.onSuccess { data.value = it }
            result.onFailure { error.value = it }

        }
    }
}