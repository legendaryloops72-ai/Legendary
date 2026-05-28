package com.example.ads

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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object AdManager {
    private var interstitialAd: InterstitialAd? = null
    private var isAdLoading = false
    
    // Sample Ad Unit ID for Interstitial
    private const val AD_UNIT_ID = "ca-app-pub-3940256099942544~3347511713" // TEST ID
    
    fun initialize(context: Context) {
        // إعدادات سياسات الأطفال: Google Play Families Policy و Google Kids Apps Policy
        // تفعيل TagForChildDirectedTreatment و TagForUnderAgeOfConsent = TRUE
        // تقييد محتوى الإعلانات إلى الفئة G (مناسب للجميع/العائلة) لمنع:
        // إعلانات المواعدة، المقامرة، العنف، والمحتوى المخصص للبالغين.
        // وبالتالي يتم تعطيل الإعلانات المخصصة (Personalized Ads) للأطفال بشكل كامل وآمن.
        val requestConfiguration = RequestConfiguration.Builder()
            .setTagForChildDirectedTreatment(RequestConfiguration.TAG_FOR_CHILD_DIRECTED_TREATMENT_TRUE)
            .setTagForUnderAgeOfConsent(RequestConfiguration.TAG_FOR_UNDER_AGE_OF_CONSENT_TRUE)
            .setMaxAdContentRating(RequestConfiguration.MAX_AD_CONTENT_RATING_G)
            .build()
        
        MobileAds.setRequestConfiguration(requestConfiguration)
        MobileAds.initialize(context) {
            Log.d("AdManager", "AdMob initialized safely for kids.")
            loadInterstitialAd(context)
        }
    }

    private fun loadInterstitialAd(context: Context) {
        if (interstitialAd != null || isAdLoading) return

        isAdLoading = true
        // بناء طلب إعلان آمن
        val adRequest = AdRequest.Builder().build()
        
        InterstitialAd.load(
            context,
            "ca-app-pub-3940256099942544/1033173712", // TEST Interstitial ID
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                    isAdLoading = false
                    Log.d("AdManager", "Interstitial Ad loaded.")
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    interstitialAd = null
                    isAdLoading = false
                    Log.e("AdManager", "Interstitial Ad failed to load: ${error.message}")
                }
            }
        )
    }

    fun showInterstitialAd(activity: Activity, onAdDismissed: () -> Unit) {
        val ad = interstitialAd
        if (ad != null) {
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    interstitialAd = null
                    // تحميل الإعلان القادم بذكاء
                    loadInterstitialAd(activity)
                    onAdDismissed()
                }

                override fun onAdFailedToShowFullScreenContent(error: AdError) {
                    interstitialAd = null
                    onAdDismissed()
                }
            }
            ad.show(activity)
        } else {
            // الإعلان غير جاهز، لا تعطل تجربة الطفل
            onAdDismissed()
            loadInterstitialAd(activity)
        }
    }
}
