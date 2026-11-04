package com.aladdin.dailyplate

import android.app.Application
import android.content.pm.ApplicationInfo
import com.aladdin.dailyplate.data.remote.RemoteConfigRepository
import com.google.firebase.remoteconfig.FirebaseRemoteConfig

class DailyPlateApp : Application() {
    lateinit var remoteConfig: RemoteConfigRepository
        private set

    override fun onCreate() {
        super.onCreate()
        val isDebug = (applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
        remoteConfig = RemoteConfigRepository(FirebaseRemoteConfig.getInstance(), isDebug)
        remoteConfig.initialize()
    }
}
