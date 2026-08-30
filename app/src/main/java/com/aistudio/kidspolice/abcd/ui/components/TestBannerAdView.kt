package com.aistudio.kidspolice.abcd.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.libraries.ads.mobile.sdk.banner.AdView
import com.google.android.libraries.ads.mobile.sdk.banner.AdSize
import com.google.android.libraries.ads.mobile.sdk.banner.BannerAdRequest
import com.google.android.libraries.ads.mobile.sdk.common.AdLoadCallback
import com.google.android.libraries.ads.mobile.sdk.common.LoadAdError
import com.google.android.libraries.ads.mobile.sdk.common.AdRequest

@Composable
fun TestBannerAdView(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp),
        contentAlignment = Alignment.Center
    ) {
        AndroidView(
            modifier = Modifier.fillMaxWidth(),
            factory = { context ->
                try {
                    AdView(context).apply {
                        val adRequest = BannerAdRequest.Builder(
                            "ca-app-pub-4760027279848820/5268836214",
                            AdSize.BANNER
                        ).build()
                        loadAd(
                            adRequest,
                            object : AdLoadCallback<com.google.android.libraries.ads.mobile.sdk.banner.BannerAd> {
                                override fun onAdLoaded(ad: com.google.android.libraries.ads.mobile.sdk.banner.BannerAd) {
                                    // Ad loaded
                                }
                                override fun onAdFailedToLoad(error: LoadAdError) {
                                    // Ad failed to load
                                }
                            }
                        )
                    }
                } catch (e: Throwable) {
                    android.view.View(context)
                }
            }
        )
    }
}

