package com.aistudio.kidspolice.abcd.ads

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
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
     * Initializes Google Mobile Ads SDK on a background thread
     * with child-directed COPPA and under-age consent configurations.
     */
    fun initialize(context: Context, onInitialized: (() -> Unit)? = null) {
        if (isInitialized) {
            onInitialized?.invoke()
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Configure request parameters
                val requestConfiguration = RequestConfiguration.Builder()
                    .setTagForChildDirectedTreatment(RequestConfiguration.TAG_FOR_CHILD_DIRECTED_TREATMENT_TRUE)
                    .setTagForUnderAgeOfConsent(RequestConfiguration.TAG_FOR_UNDER_AGE_OF_CONSENT_TRUE)
                    .setMaxAdContentRating(RequestConfiguration.MAX_AD_CONTENT_RATING_G)
                    .build()

                MobileAds.setRequestConfiguration(requestConfiguration)

                // Initialize MobileAds SDK
                MobileAds.initialize(context.applicationContext) { status ->
                    Log.d(TAG, "AdMob SDK initialized: $status")
                    isInitialized = true
                    loadInterstitial(context.applicationContext)
                    CoroutineScope(Dispatchers.Main).launch {
                        onInitialized?.invoke()
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error initializing MobileAds SDK", e)
            }
        }
    }

    /**
     * Loads an Interstitial Ad using Google Mobile Ads SDK APIs.
     */
    fun loadInterstitial(context: Context) {
        if (isLoadingInterstitial || interstitialAd != null) {
            return
        }

        isLoadingInterstitial = true
        try {
            val adRequest = AdRequest.Builder().build()

            InterstitialAd.load(
                context,
                TEST_INTERSTITIAL_AD_UNIT_ID,
                adRequest,
                object : InterstitialAdLoadCallback() {
                    override fun onAdLoaded(ad: InterstitialAd) {
                        Log.d(TAG, "Interstitial Ad loaded successfully")
                        interstitialAd = ad
                        isLoadingInterstitial = false
                    }

                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        Log.e(TAG, "Interstitial Ad failed to load: ${loadAdError.message}")
                        interstitialAd = null
                        isLoadingInterstitial = false
                    }
                }
            )
        } catch (e: Exception) {
            Log.e(TAG, "Exception loading interstitial ad", e)
            isLoadingInterstitial = false
            interstitialAd = null
        }
    }

    /**
     * Shows the Interstitial Ad if available, otherwise executes onDismiss callback immediately.
     */
    fun showInterstitial(activity: Activity, onDismiss: () -> Unit) {
        val currentAd = interstitialAd
        if (currentAd != null) {
            try {
                currentAd.fullScreenContentCallback = object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        Log.d(TAG, "Interstitial Ad dismissed")
                        interstitialAd = null
                        loadInterstitial(activity.applicationContext)
                        onDismiss()
                    }

                    override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                        Log.e(TAG, "Interstitial Ad failed to show: ${adError.message}")
                        interstitialAd = null
                        loadInterstitial(activity.applicationContext)
                        onDismiss()
                    }

                    override fun onAdShowedFullScreenContent() {
                        Log.d(TAG, "Interstitial Ad showed full screen")
                    }

                    override fun onAdImpression() {
                        Log.d(TAG, "Interstitial Ad impression recorded")
                    }

                    override fun onAdClicked() {
                        Log.d(TAG, "Interstitial Ad clicked")
                    }
                }

                currentAd.show(activity)
            } catch (e: Exception) {
                Log.e(TAG, "Exception showing interstitial ad", e)
                interstitialAd = null
                onDismiss()
            }
        } else {
            loadInterstitial(activity.applicationContext)
            onDismiss()
        }
    }
}
