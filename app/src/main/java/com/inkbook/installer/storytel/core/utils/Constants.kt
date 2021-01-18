package com.inkbook.installer.storytel.core.utils

import com.inkbook.installer.storytel.BuildConfig


internal object Constants {
    const val APP = BuildConfig.XAPK_FILE_NAME
//    const val APP = "MusicPlayer"

    private const val EXTENSION_FORMAT = ".xapk"
    const val APP_NAME = APP + EXTENSION_FORMAT
}