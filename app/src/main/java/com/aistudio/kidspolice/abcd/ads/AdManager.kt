package com.aistudio.kidspolice.abcd.ads

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.libraries.ads.mobile.sdk.common.AdError
import com.google.android.libraries.ads.mobile.sdk.common.AdLoadCallback
import com.google.android.libraries.ads.mobile.sdk.common.AdRequest
import com.google.android.libraries.ads.mobile.sdk.common.LoadAdError
import com.google.android.libraries.ads.mobile.sdk.common.RequestConfiguration
import com.google.android.libraries.ads.mobile.sdk.initialization.MobileAds
import com.google.android.libraries.ads.mobile.sdk.interstitial.InterstitialAd
import com.google.android.libraries.ads.mobile.sdk.interstitial.InterstitialAdEventCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object AdManager {
    private const val TAG = "AdManager"

    // Standard AdMob Test Ad Unit IDs
    const val TEST_BANNER_AD_UNIT_ID = "ca-app-pub-3940256099942544/6300978111"
    const val TEST_INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712"

    private var isInitialized = false
    private var interstitialAd: InterstitialAd? = null
    private var isLoadingInterstitial = false

    /**
     * Initializes Google Mobile Ads Next-Gen SDK on a background thread
     * with child-directed COPPA and under-age consent configurations.
     */
    fun initialize(context: Context, onInitialized: (() -> Unit)? = null) {
        if (isInitialized) {
            onInitialized?.invoke()
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val requestConfiguration = RequestConfiguration.Builder()
                    .setTagForChildDirectedTreatment(RequestConfiguration.TAG_FOR_CHILD_DIRECTED_TREATMENT_TRUE)
                    .setTagForUnderAgeOfConsent(RequestConfiguration.TAG_FOR_UNDER_AGE_OF_CONSENT_TRUE)
                    .setMaxAdContentRating(RequestConfiguration.MAX_AD_CONTENT_RATING_G)
                    .build()

                MobileAds.setRequestConfiguration(requestConfiguration)

                MobileAds.initialize(context.applicationContext) { status ->
                    Log.d(TAG, "GMA Next-Gen SDK initialized: $status")
                    isInitialized = true
                    loadInterstitial(context.applicationContext)
                    CoroutineScope(Dispatchers.Main).launch {
                        onInitialized?.invoke()
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error initializing GMA Next-Gen SDK", e)
            }
        }
    }

    /**
     * Loads an Interstitial Ad using GMA Next-Gen SDK APIs.
     */
    fun loadInterstitial(context: Context) {
        if (isLoadingInterstitial || interstitialAd != null) {
            return
        }

        isLoadingInterstitial = true
        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(
            context,
            TEST_INTERSTITIAL_AD_UNIT_ID,
            adRequest,
            object : AdLoadCallback<InterstitialAd> {
                override fun onAdLoaded(ad: InterstitialAd) {
                    Log.d(TAG, "Interstitial Ad loaded successfully")
                    interstitialAd = ad
                    isLoadingInterstitial = false

                    ad.adEventCallback = object : InterstitialAdEventCallback {
                        override fun onAdShowedFullScreenContent() {
                            Log.d(TAG, "Interstitial Ad showed full screen")
                        }

                        override fun onAdDismissedFullScreenContent() {
                            Log.d(TAG, "Interstitial Ad dismissed")
                            interstitialAd = null
                            loadInterstitial(context)
                        }

                        override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                            Log.e(TAG, "Interstitial Ad failed to show: ${adError.message}")
                            interstitialAd = null
                            loadInterstitial(context)
                        }

                        override fun onAdImpression() {
                            Log.d(TAG, "Interstitial Ad impression recorded")
                        }

                        override fun onAdClicked() {
                            Log.d(TAG, "Interstitial Ad clicked")
                        }
                    }
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    Log.e(TAG, "Interstitial Ad failed to load: ${loadAdError.message}")
                    interstitialAd = null
                    isLoadingInterstitial = false
                }
            }
        )
    }

    /**
     * Shows the Interstitial Ad if available, otherwise executes onDismiss callback immediately.
     */
    fun showInterstitial(activity: Activity, onDismiss: () -> Unit) {
        val currentAd = interstitialAd
        if (currentAd != null) {
            val existingCallback = currentAd.adEventCallback
            currentAd.adEventCallback = object : InterstitialAdEventCallback {
                override fun onAdShowedFullScreenContent() {
                    existingCallback?.onAdShowedFullScreenContent()
                }

                override fun onAdDismissedFullScreenContent() {
                    existingCallback?.onAdDismissedFullScreenContent()
                    onDismiss()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    existingCallback?.onAdFailedToShowFullScreenContent(adError)
                    onDismiss()
                }

                override fun onAdImpression() {
                    existingCallback?.onAdImpression()
                }

                override fun onAdClicked() {
                    existingCallback?.onAdClicked()
                }
            }

            currentAd.show(activity)
        } else {
            loadInterstitial(activity.applicationContext)
            onDismiss()
        }
    }
}
