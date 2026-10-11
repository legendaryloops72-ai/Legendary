package com.aistudio.kidspolice.abcd.ads

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.libraries.ads.mobile.sdk.MobileAds
import com.google.android.libraries.ads.mobile.sdk.appopen.AppOpenAd
import com.google.android.libraries.ads.mobile.sdk.appopen.AppOpenAdEventCallback
import com.google.android.libraries.ads.mobile.sdk.appopen.AppOpenAdRequest
import com.google.android.libraries.ads.mobile.sdk.common.AdLoadCallback
import com.google.android.libraries.ads.mobile.sdk.common.FullScreenContentError
import com.google.android.libraries.ads.mobile.sdk.common.LoadAdError
import java.util.Date

class AppOpenAdManager(private val context: Context) {

    companion object {
        private const val TAG = "AppOpenAdManager"
        // Test Ad Unit ID provided by Google Mobile Ads and requested for development
        const val TEST_AD_UNIT_ID = "ca-app-pub-3940256099942544/9257395921"
        // Production Ad Unit ID
        const val PRODUCTION_AD_UNIT_ID = "ca-app-pub-4760027279848820/1995705924"

        private const val PREFS_NAME = "app_open_ad_prefs"
        private const val KEY_APP_OPEN_COUNT = "app_open_count"
        private const val MIN_APP_OPENS_BEFORE_AD = 2 // Do not show on first launch
        private const val FOUR_HOURS_IN_MILLIS = 4 * 60 * 60 * 1000L
    }

    private var appOpenAd: AppOpenAd? = null
    private var isLoadingAd = false
    private var isShowingAd = false
    private var loadTime: Long = 0

    // Callback provider to query whether audio or call is active without modifying PoliceAudioPlayer
    var isAudioActiveProvider: (() -> Boolean)? = null
    // Callback provider to query whether user is currently in an active game/call/screen
    var isSafeScreenToDisplayProvider: (() -> Boolean)? = null

    init {
        incrementAppOpenCount()
    }

    private fun incrementAppOpenCount(): Int {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val currentCount = prefs.getInt(KEY_APP_OPEN_COUNT, 0) + 1
        prefs.edit().putInt(KEY_APP_OPEN_COUNT, currentCount).apply()
        Log.d(TAG, "Current app open count: $currentCount")
        return currentCount
    }

    private fun getAppOpenCount(): Int {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getInt(KEY_APP_OPEN_COUNT, 0)
    }

    private fun isAdAvailable(): Boolean {
        val ad = appOpenAd ?: return false
        val isNotExpired = (Date().time - loadTime) < FOUR_HOURS_IN_MILLIS
        return isNotExpired
    }

    fun loadAd() {
        if (isLoadingAd || isAdAvailable()) {
            return
        }

        if (!MobileAds.isInitialized) {
            Log.d(TAG, "MobileAds is not initialized yet. Skipping load.")
            return
        }

        isLoadingAd = true
        val adUnitId = TEST_AD_UNIT_ID
        Log.d(TAG, "Requesting AppOpenAd with unit ID: $adUnitId")

        try {
            val adRequest = AppOpenAdRequest.Builder(adUnitId).build()
            AppOpenAd.load(
                adRequest,
                object : AdLoadCallback<AppOpenAd> {
                    override fun onAdLoaded(ad: AppOpenAd) {
                        Log.d(TAG, "AppOpenAd loaded successfully.")
                        appOpenAd = ad
                        isLoadingAd = false
                        loadTime = Date().time
                    }

                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        Log.w(TAG, "AppOpenAd failed to load: ${loadAdError.message} (code: ${loadAdError.code})")
                        isLoadingAd = false
                        appOpenAd = null
                    }
                }
            )
        } catch (e: Exception) {
            Log.e(TAG, "Exception during AppOpenAd.load: ${e.message}", e)
            isLoadingAd = false
            appOpenAd = null
        }
    }

    fun showAdIfAvailable(activity: Activity) {
        if (isShowingAd) {
            Log.d(TAG, "Ad is already showing. Suppressing.")
            return
        }

        // Rule 4: Do not show on first launch (minimum opens required)
        if (getAppOpenCount() <= MIN_APP_OPENS_BEFORE_AD) {
            Log.d(TAG, "App open count (${getAppOpenCount()}) <= $MIN_APP_OPENS_BEFORE_AD. Suppressing ad for early sessions.")
            loadAd() // Preload for subsequent sessions
            return
        }

        // Rule 5: Do not show while playing sounds, sirens or call speech
        val isAudioPlaying = isAudioActiveProvider?.invoke() ?: false
        if (isAudioPlaying) {
            Log.d(TAG, "Audio or call is currently active. Suppressing AppOpenAd to protect children experience.")
            return
        }

        // Rule 5: Verify if the current screen is safe for display (e.g. not in the middle of a call)
        val isSafeScreen = isSafeScreenToDisplayProvider?.invoke() ?: true
        if (!isSafeScreen) {
            Log.d(TAG, "Current screen is not in a resting state. Suppressing AppOpenAd.")
            return
        }

        if (!isAdAvailable()) {
            Log.d(TAG, "AppOpenAd is not available or has expired. Requesting a new ad.")
            loadAd()
            return
        }

        val ad = appOpenAd ?: return

        ad.adEventCallback = object : AppOpenAdEventCallback {
            override fun onAdShowedFullScreenContent() {
                isShowingAd = true
                Log.d(TAG, "AppOpenAd showed full screen content.")
            }

            override fun onAdDismissedFullScreenContent() {
                Log.d(TAG, "AppOpenAd dismissed full screen content.")
                appOpenAd = null
                isShowingAd = false
                // Preload next ad seamlessly
                loadAd()
            }

            override fun onAdFailedToShowFullScreenContent(fullScreenContentError: FullScreenContentError) {
                Log.w(TAG, "AppOpenAd failed to show: ${fullScreenContentError.message}")
                appOpenAd = null
                isShowingAd = false
                loadAd()
            }
        }

        isShowingAd = true
        ad.show(activity)
    }
}
