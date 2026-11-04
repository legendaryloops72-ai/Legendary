package com.aistudio.kidspolice.abcd.ui.components

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.util.Log
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.libraries.ads.mobile.sdk.MobileAds
import com.google.android.libraries.ads.mobile.sdk.banner.AdSize
import com.google.android.libraries.ads.mobile.sdk.banner.AdView
import com.google.android.libraries.ads.mobile.sdk.banner.BannerAd
import com.google.android.libraries.ads.mobile.sdk.banner.BannerAdEventCallback
import com.google.android.libraries.ads.mobile.sdk.banner.BannerAdRequest
import com.google.android.libraries.ads.mobile.sdk.common.AdLoadCallback
import com.google.android.libraries.ads.mobile.sdk.common.LoadAdError
import kotlinx.coroutines.delay

private const val TAG = "BANNER_DEBUG"

private fun Context.findActivity(): Activity? {
    var currentContext = this
    while (currentContext is ContextWrapper) {
        if (currentContext is Activity) {
            return currentContext
        }
        currentContext = currentContext.baseContext
    }
    return null
}

@Composable
fun TestBannerAdView(modifier: Modifier = Modifier) {
    var isSdkInitialized by remember { mutableStateOf(MobileAds.isInitialized) }
    var adViewInstance by remember { mutableStateOf<AdView?>(null) }
    val localContext = LocalContext.current

    LaunchedEffect(Unit) {
        while (!MobileAds.isInitialized) {
            delay(100)
        }
        isSdkInitialized = true
        Log.d(TAG, "[BANNER_SDK_READY] Mobile Ads SDK initialized.")
    }

    DisposableEffect(Unit) {
        onDispose {
            try {
                adViewInstance?.destroy()
                adViewInstance = null
            } catch (e: Exception) {
                Log.e(TAG, "Error destroying AdView: ${e.message}", e)
            }
        }
    }

    BoxWithConstraints(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        // Use the actual width available inside the banner card. A fixed 320dp
        // banner can be wider than the card on compact phones.
        val bannerWidthDp = maxWidth.value.toInt().coerceAtLeast(1)
        val bannerAdSize = remember(localContext, bannerWidthDp) {
            AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(
                localContext,
                bannerWidthDp
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(bannerAdSize.height.dp),
            contentAlignment = Alignment.Center
        ) {
            if (isSdkInitialized) {
                AndroidView(
                    modifier = Modifier.wrapContentSize(Alignment.Center),
                    factory = { context ->
                        val activity = context.findActivity() ?: localContext.findActivity()
                        val adUnitId = if (com.aistudio.kidspolice.abcd.BuildConfig.DEBUG) {
                            "ca-app-pub-3940256099942544/6300978111"
                        } else {
                            "ca-app-pub-4760027279848820/5268836214"
                        }

                        Log.d(
                            TAG,
                            "[BANNER_INIT] unitId=$adUnitId, widthDp=$bannerWidthDp, " +
                                "heightDp=${bannerAdSize.height}, isDebug=${com.aistudio.kidspolice.abcd.BuildConfig.DEBUG}"
                        )

                        val adView = AdView(activity ?: context).apply {
                            layoutParams = FrameLayout.LayoutParams(
                                ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                            ).apply {
                                gravity = android.view.Gravity.CENTER
                            }
                        }
                        adViewInstance = adView

                        try {
                            val adRequest = BannerAdRequest.Builder(
                                adUnitId,
                                bannerAdSize
                            ).build()

                            Log.d(TAG, "[BANNER_LOAD_START] Requesting adaptive banner.")
                            adView.loadAd(
                                adRequest,
                                object : AdLoadCallback<BannerAd> {
                                    override fun onAdLoaded(ad: BannerAd) {
                                        Log.d(
                                            TAG,
                                            "[BANNER_LOAD_SUCCESS] Adaptive banner loaded. " +
                                                "size=${ad.adSize.width}x${ad.adSize.height}"
                                        )
                                        ad.adEventCallback = object : BannerAdEventCallback {
                                            override fun onAdImpression() {
                                                Log.d(TAG, "[BANNER_EVENT] Banner impression recorded.")
                                            }

                                            override fun onAdClicked() {
                                                Log.d(TAG, "[BANNER_EVENT] Banner clicked.")
                                            }
                                        }
                                    }

                                    override fun onAdFailedToLoad(error: LoadAdError) {
                                        Log.e(
                                            TAG,
                                            "[BANNER_LOAD_FAILED] Code=${error.code}, " +
                                                "Message='${error.message}', ResponseInfo=${error.responseInfo}"
                                        )
                                    }
                                }
                            )
                        } catch (e: Throwable) {
                            Log.e(TAG, "[BANNER_EXCEPTION] Exception during banner load: ${e.message}", e)
                        }

                        adView
                    }
                )
            }
        }
    }
}
