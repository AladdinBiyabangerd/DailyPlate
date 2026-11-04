package com.aladdin.dailyplate.data.remote

import android.util.Log
import com.aladdin.dailyplate.R
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings

/** Typed, non-blocking access to Firebase Remote Config; defaults live in res/xml. */
class RemoteConfigRepository(
    private val remoteConfig: FirebaseRemoteConfig,
    private val isDebug: Boolean,
) {
    /** Applies settings and defaults, then fetches in the background. Never blocks startup. */
    fun initialize() {
        val settings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = if (isDebug) 0L else RELEASE_FETCH_INTERVAL_SECONDS
        }
        remoteConfig.setConfigSettingsAsync(settings)
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
        remoteConfig.fetchAndActivate().addOnFailureListener { error ->
            Log.w(TAG, "Remote Config fetch failed; using cached or default values", error)
        }
    }

    val freeDailyAiScans: Long get() = remoteConfig.getLong(KEY_FREE_DAILY_AI_SCANS)
    val paywallEnabled: Boolean get() = remoteConfig.getBoolean(KEY_PAYWALL_ENABLED)
    val minSupportedVersion: Long get() = remoteConfig.getLong(KEY_MIN_SUPPORTED_VERSION)
    val aiModelDefault: String get() = remoteConfig.getString(KEY_AI_MODEL_DEFAULT)

    companion object {
        const val KEY_FREE_DAILY_AI_SCANS = "free_daily_ai_scans"
        const val KEY_PAYWALL_ENABLED = "paywall_enabled"
        const val KEY_MIN_SUPPORTED_VERSION = "min_supported_version"
        const val KEY_AI_MODEL_DEFAULT = "ai_model_default"
        private const val RELEASE_FETCH_INTERVAL_SECONDS = 3600L
        private const val TAG = "RemoteConfig"
    }
}
