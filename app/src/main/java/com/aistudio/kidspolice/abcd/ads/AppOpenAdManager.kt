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
        private const val FOUR_HOURS_IN_MILLIS = 4 * 60 * 60 * 1000L
    }

    private var appOpenAd: AppOpenAd? = null
    private var isLoadingAd = false
    private var isShowingAd = false
    private var isShowPending = false
    private var loadTime: Long = 0

    // Callback provider to query whether audio or call is active without modifying PoliceAudioPlayer
    var isAudioActiveProvider: (() -> Boolean)? = null
    // Callback provider to query whether user is currently in an active game/call/screen
    var isSafeScreenToDisplayProvider: (() -> Boolean)? = null
    // Callback provider to get current active Activity if in foreground
    var currentActivityProvider: (() -> Activity?)? = null

    init {
        val count = incrementAppOpenCount()
        Log.d(TAG, "AppOpenAdManager initialized. App session count: $count")
    }

    private fun incrementAppOpenCount(): Int {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val currentCount = prefs.getInt(KEY_APP_OPEN_COUNT, 0) + 1
        prefs.edit().putInt(KEY_APP_OPEN_COUNT, currentCount).apply()
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
        if (isLoadingAd) {
            Log.d(TAG, "[LOAD_CHECK] Ad load is already in progress. Skipping duplicate request.")
            return
        }

        if (isAdAvailable()) {
            Log.d(TAG, "[LOAD_CHECK] Existing ad is still valid and ready. Skipping reload.")
            return
        }

        if (!MobileAds.isInitialized) {
            Log.d(TAG, "[LOAD_CHECK] MobileAds is not initialized yet. Skipping load.")
            return
        }

        isLoadingAd = true
        val adUnitId = TEST_AD_UNIT_ID
        Log.d(TAG, "[LOAD_START] Requesting AppOpenAd with unit ID: $adUnitId")

        try {
            val adRequest = AppOpenAdRequest.Builder(adUnitId).build()
            AppOpenAd.load(
                adRequest,
                object : AdLoadCallback<AppOpenAd> {
                    override fun onAdLoaded(ad: AppOpenAd) {
                        Log.d(TAG, "[LOAD_SUCCESS] AppOpenAd loaded successfully.")
                        appOpenAd = ad
                        isLoadingAd = false
                        loadTime = Date().time

                        // Rule 3 & 4: If a show request was pending while waiting for load, evaluate now
                        if (isShowPending) {
                            Log.d(TAG, "[PENDING_EVALUATION] Show was pending. Checking foreground activity and conditions.")
                            val activity = currentActivityProvider?.invoke()
                            if (activity != null) {
                                isShowPending = false
                                activity.runOnUiThread {
                                    showAdIfAvailable(activity)
                                }
                            } else {
                                Log.d(TAG, "[PENDING_EVALUATION] No active foreground activity found. Ad remains cached.")
                            }
                        }
                    }

                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        Log.w(
                            TAG,
                            "[LOAD_FAILED] AppOpenAd failed to load: message='${loadAdError.message}', code=${loadAdError.code}"
                        )
                        isLoadingAd = false
                        appOpenAd = null
                        isShowPending = false
                    }
                }
            )
        } catch (e: Exception) {
            Log.e(TAG, "[LOAD_EXCEPTION] Exception during AppOpenAd.load: ${e.message}", e)
            isLoadingAd = false
            appOpenAd = null
            isShowPending = false
        }
    }

    fun onActivityPaused() {
        // Cancel pending show if activity goes to background before ad arrives
        if (isShowPending) {
            Log.d(TAG, "[LIFECYCLE] Activity paused while show was pending. Cancelling pending show.")
            isShowPending = false
        }
    }

    fun showAdIfAvailable(activity: Activity) {
        Log.d(TAG, "[SHOW_ATTEMPT] Evaluating show conditions for activity: ${activity.localClassName}")

        if (isShowingAd) {
            Log.d(TAG, "[SHOW_SUPPRESSED] Ad is already currently showing.")
            return
        }

        if (activity.isFinishing || activity.isDestroyed) {
            Log.d(TAG, "[SHOW_SUPPRESSED] Activity is finishing or destroyed.")
            return
        }

        // Rule 2 & 6: Do not show on first launch (count == 1). Eligible starting from second launch (count >= 2).
        val openCount = getAppOpenCount()
        if (openCount <= 1) {
            Log.d(TAG, "[SHOW_SUPPRESSED] First launch of the app (count=$openCount). Preserving child onboarding experience. Preloading for future sessions.")
            loadAd()
            return
        }

        // Rule 4 & 5: Check audio, sounds, siren, or call speech
        val isAudioPlaying = isAudioActiveProvider?.invoke() ?: false
        if (isAudioPlaying) {
            Log.d(TAG, "[SHOW_SUPPRESSED] Audio, siren, or call is currently active. Suppressing ad to protect child experience.")
            return
        }

        // Rule 4 & 5: Check screen safety
        val isSafeScreen = isSafeScreenToDisplayProvider?.invoke() ?: true
        if (!isSafeScreen) {
            Log.d(TAG, "[SHOW_SUPPRESSED] Current screen is not safe for ad display (e.g. active call).")
            return
        }

        // Check ad readiness and freshness
        if (!isAdAvailable()) {
            if (isLoadingAd) {
                Log.d(TAG, "[SHOW_DEFERRED] Ad is still loading. Marking show as pending once loaded.")
                isShowPending = true
            } else {
                Log.d(TAG, "[SHOW_DEFERRED] Ad is not available or expired. Triggering load and marking pending.")
                isShowPending = true
                loadAd()
            }
            return
        }

        val ad = appOpenAd ?: return

        ad.adEventCallback = object : AppOpenAdEventCallback {
            override fun onAdShowedFullScreenContent() {
                isShowingAd = true
                isShowPending = false
                Log.d(TAG, "[SHOW_EVENT] AppOpenAd showed full screen content.")
            }

            override fun onAdDismissedFullScreenContent() {
                Log.d(TAG, "[SHOW_EVENT] AppOpenAd dismissed full screen content.")
                appOpenAd = null
                isShowingAd = false
                isShowPending = false
                // Preload next ad seamlessly for next resume
                loadAd()
            }

            override fun onAdFailedToShowFullScreenContent(fullScreenContentError: FullScreenContentError) {
                Log.w(
                    TAG,
                    "[SHOW_FAILED] AppOpenAd failed to show: message='${fullScreenContentError.message}', code=${fullScreenContentError.code}"
                )
                appOpenAd = null
                isShowingAd = false
                isShowPending = false
                loadAd()
            }
        }

        Log.d(TAG, "[SHOW_SUCCESS] All conditions met. Presenting AppOpenAd.")
        isShowingAd = true
        isShowPending = false
        ad.show(activity)
    }
}
