package com.aistudio.kidspolice.abcd

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.libraries.ads.mobile.sdk.interstitial.InterstitialAd
import com.google.android.libraries.ads.mobile.sdk.interstitial.InterstitialAdEventCallback
import com.google.android.libraries.ads.mobile.sdk.common.AdLoadCallback
import com.google.android.libraries.ads.mobile.sdk.common.LoadAdError
import com.google.android.libraries.ads.mobile.sdk.common.AdRequest

class InterstitialAdManager(private val context: Context) {
    private var interstitialAd: InterstitialAd? = null
    private var isLoadingAd = false
    var isShowingAd = false

    companion object {
        const val AD_UNIT_ID = "ca-app-pub-4760027279848820/7989290323"
    }

    fun loadAd() {
        Log.d("InterstitialAdManager", "loadAd() started. isLoadingAd=$isLoadingAd, isAdAvailable=${isAdAvailable()}")
        if (isLoadingAd || isAdAvailable()) {
            Log.d("InterstitialAdManager", "loadAd() skipped: already loading or ad available.")
            return
        }

        isLoadingAd = true
        Log.d("InterstitialAdManager", "Requesting load with Ad Unit ID: $AD_UNIT_ID")
        val request = AdRequest.Builder(AD_UNIT_ID).build()
        Log.d("InterstitialAdManager", "Calling InterstitialAd.load()...")
        InterstitialAd.load(
            request,
            object : AdLoadCallback<InterstitialAd> {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                    isLoadingAd = false
                    Log.d("InterstitialAdManager", "onAdLoaded: Interstitial ad loaded successfully.")
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    isLoadingAd = false
                    Log.e("InterstitialAdManager", "onAdFailedToLoad: code=${loadAdError.code}, message=${loadAdError.message}")
                    Log.d("InterstitialAdManager", "loadAd failed, reload will be triggered when needed.")
                }
            }
        )
    }

    fun isAdAvailable(): Boolean {
        return interstitialAd != null
    }

    fun showAd(activity: Activity, onShowComplete: () -> Unit = {}) {
        Log.d("InterstitialAdManager", "showAd() started. isShowingAd=$isShowingAd, isAdAvailable=${isAdAvailable()}")
        if (isShowingAd) {
            Log.d("InterstitialAdManager", "Interstitial ad is already showing.")
            return
        }

        val available = isAdAvailable()
        Log.d("InterstitialAdManager", "isAdAvailable() result: $available")
        if (!available) {
            Log.d("InterstitialAdManager", "Interstitial ad is not ready. Calling onShowComplete() and reloading ad.")
            onShowComplete()
            loadAd()
            return
        }

        interstitialAd?.adEventCallback = object : InterstitialAdEventCallback {
            override fun onAdDismissedFullScreenContent() {
                interstitialAd = null
                isShowingAd = false
                Log.d("InterstitialAdManager", "onAdDismissedFullScreenContent: Ad dismissed.")
                onShowComplete()
                Log.d("InterstitialAdManager", "Reloading ad after dismiss...")
                loadAd()
            }

            override fun onAdShowedFullScreenContent() {
                isShowingAd = true
                Log.d("InterstitialAdManager", "onAdShowedFullScreenContent: Ad showed fullscreen content (Impression).")
            }
        }

        if (activity.isFinishing || activity.isDestroyed) {
            Log.d("InterstitialAdManager", "Activity is finishing or destroyed. Skipping ad display.")
            onShowComplete()
            return
        }

        isShowingAd = true
        Log.d("InterstitialAdManager", "Executing ad.show(activity)...")
        interstitialAd?.show(activity)
    }
}
