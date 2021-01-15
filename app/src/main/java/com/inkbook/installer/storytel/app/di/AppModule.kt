package com.inkbook.installer.storytel.app.di

import com.inkbook.installer.storytel.core.managers.InstallManager
import com.inkbook.installer.storytel.core.managers.UnzipManager
import com.inkbook.installer.storytel.ui.usecase.InstallUseCase
import com.inkbook.installer.storytel.ui.usecase.UnzipXapkUseCase
import com.inkbook.installer.storytel.ui.presentation.MainViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    factory { InstallManager() }
    factory { UnzipManager(androidContext().assets) }
}

val mainModule = module {
    factory { InstallUseCase(get()) }
    factory { UnzipXapkUseCase(get()) }
    viewModel { MainViewModel(get(), get()) }
}