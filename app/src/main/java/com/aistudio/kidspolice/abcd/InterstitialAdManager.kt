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
        if (isLoadingAd || isAdAvailable()) {
            return
        }

        isLoadingAd = true
        val request = AdRequest.Builder(AD_UNIT_ID).build()
        InterstitialAd.load(
            request,
            object : AdLoadCallback<InterstitialAd> {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                    isLoadingAd = false
                    Log.d("InterstitialAdManager", "Interstitial ad loaded successfully.")
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    isLoadingAd = false
                    Log.d("InterstitialAdManager", "Interstitial ad failed to load: ${loadAdError.message}")
                }
            }
        )
    }

    fun isAdAvailable(): Boolean {
        return interstitialAd != null
    }

    fun showAd(activity: Activity, onShowComplete: () -> Unit = {}) {
        if (isShowingAd) {
            Log.d("InterstitialAdManager", "Interstitial ad is already showing.")
            return
        }

        if (!isAdAvailable()) {
            Log.d("InterstitialAdManager", "Interstitial ad is not ready.")
            onShowComplete()
            loadAd()
            return
        }

        interstitialAd?.adEventCallback = object : InterstitialAdEventCallback {
            override fun onAdDismissedFullScreenContent() {
                interstitialAd = null
                isShowingAd = false
                Log.d("InterstitialAdManager", "Interstitial ad dismissed.")
                onShowComplete()
                loadAd()
            }

            override fun onAdShowedFullScreenContent() {
                isShowingAd = true
                Log.d("InterstitialAdManager", "Interstitial ad showed fullscreen content (Impression).")
            }
        }

        isShowingAd = true
        interstitialAd?.show(activity)
    }
}
