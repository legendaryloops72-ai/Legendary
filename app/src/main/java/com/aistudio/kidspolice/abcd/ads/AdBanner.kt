package com.aistudio.kidspolice.abcd.ads

import android.util.DisplayMetrics
import android.util.Log
import android.view.ViewGroup
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

@Composable
fun AdBanner(
    modifier: Modifier = Modifier,
    adUnitId: String = AdManager.TEST_BANNER_AD_UNIT_ID
) {
    val context = LocalContext.current

    val adView = remember {
        try {
            AdView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                this.adUnitId = adUnitId

                val displayMetrics: DisplayMetrics = context.resources.displayMetrics
                val adWidthPixels = displayMetrics.widthPixels.toFloat()
                val density = displayMetrics.density
                val adWidth = (adWidthPixels / density).toInt()

                val adaptiveSize = AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, adWidth)
                this.setAdSize(adaptiveSize)

                val adRequest = AdRequest.Builder().build()
                this.loadAd(adRequest)
            }
        } catch (e: Exception) {
            Log.e("AdBanner", "Error creating or loading AdView", e)
            null
        }
    }

    DisposableEffect(adView) {
        onDispose {
            try {
                adView?.destroy()
            } catch (e: Exception) {
                Log.e("AdBanner", "Error destroying AdView", e)
            }
        }
    }

    if (adView != null) {
        Box(
            modifier = modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            AndroidView(
                factory = { adView },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
