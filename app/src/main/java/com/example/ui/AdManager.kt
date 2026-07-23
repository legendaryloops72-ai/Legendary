package com.example.ui

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

object AdManager {
    private const val TAG = "AdManager"
    
    // Production AdMob Interstitial Ad ID
    private const val INTERSTITIAL_ID = "ca-app-pub-4760027279848820/8312965208"
    
    private var mInterstitialAd: InterstitialAd? = null
    private var isAdLoading = false

    /**
     * Preloads an interstitial ad to be displayed later.
     */
    fun loadInterstitial(context: Context, adUnitId: String = INTERSTITIAL_ID) {
        if (mInterstitialAd != null || isAdLoading) return
        
        isAdLoading = true
        val adRequest = AdRequest.Builder().build()
        
        InterstitialAd.load(
            context,
            adUnitId,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.d(TAG, "Interstitial ad failed to load: ${adError.message}")
                    mInterstitialAd = null
                    isAdLoading = false
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    Log.d(TAG, "Interstitial ad loaded successfully.")
                    mInterstitialAd = interstitialAd
                    isAdLoading = false
                }
            }
        )
    }

    /**
     * Shows the preloaded interstitial ad if it is ready.
     * Triggers [onAdClosed] when the ad is closed or fails to show.
     */
    fun showInterstitial(activity: Activity, onAdClosed: () -> Unit) {
        val ad = mInterstitialAd
        if (ad != null) {
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    Log.d(TAG, "Interstitial ad dismissed.")
                    mInterstitialAd = null
                    onAdClosed()
                    // Pre-load the next one immediately
                    loadInterstitial(activity)
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    Log.e(TAG, "Interstitial ad failed to show: ${adError.message}")
                    mInterstitialAd = null
                    onAdClosed()
                    // Try to load again
                    loadInterstitial(activity)
                }
            }
            ad.show(activity)
        } else {
            Log.d(TAG, "Interstitial ad is not ready yet.")
            onAdClosed()
            // Preload it for next time
            loadInterstitial(activity)
        }
    }
}
