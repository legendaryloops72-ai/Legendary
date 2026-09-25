package com.aistudio.kidspolice.abcd.ui.components

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.libraries.ads.mobile.sdk.MobileAds
import com.google.android.libraries.ads.mobile.sdk.banner.AdView
import com.google.android.libraries.ads.mobile.sdk.banner.AdSize
import com.google.android.libraries.ads.mobile.sdk.banner.BannerAdRequest
import com.google.android.libraries.ads.mobile.sdk.common.AdLoadCallback
import com.google.android.libraries.ads.mobile.sdk.common.LoadAdError
import kotlinx.coroutines.delay

@Composable
fun TestBannerAdView(modifier: Modifier = Modifier) {
    var isInitialized by remember { mutableStateOf(MobileAds.isInitialized) }

    LaunchedEffect(Unit) {
        while (!MobileAds.isInitialized) {
            delay(100)
        }
        isInitialized = true
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 50.dp),
        contentAlignment = Alignment.Center
    ) {
        if (isInitialized) {
            AndroidView(
                modifier = Modifier.fillMaxWidth(),
                factory = { context ->
                    try {
                        AdView(context).apply {
                            val adUnitId = if (com.aistudio.kidspolice.abcd.BuildConfig.DEBUG) {
                                "ca-app-pub-3940256099942544/6300978111"
                            } else {
                                "ca-app-pub-4760027279848820/5268836214"
                            }
                            val adRequest = BannerAdRequest.Builder(
                                adUnitId,
                                AdSize.BANNER
                            ).build()
                            Log.d("BANNER_DEBUG", "loadAd called")
                            loadAd(
                                adRequest,
                                object : AdLoadCallback<com.google.android.libraries.ads.mobile.sdk.banner.BannerAd> {
                                    override fun onAdLoaded(ad: com.google.android.libraries.ads.mobile.sdk.banner.BannerAd) {
                                        Log.d("BANNER_DEBUG", "onAdLoaded")
                                    }
                                    override fun onAdFailedToLoad(error: LoadAdError) {
                                        Log.e("BANNER_DEBUG", "onAdFailedToLoad")
                                        Log.e("BANNER_DEBUG", "code: ${error.code}")
                                        Log.e("BANNER_DEBUG", "message: ${error.message}")
                                    }
                                }
                            )
                        }
                    } catch (e: Throwable) {
                        Log.e("BANNER_DEBUG", "Exception: ${e.message}")
                        android.view.View(context)
                    }
                }
            )
        }
    }
}

