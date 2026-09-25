package com.aistudio.kidspolice.abcd.ui.components

import android.graphics.Color as AndroidColor
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.libraries.ads.mobile.sdk.MobileAds
import com.google.android.libraries.ads.mobile.sdk.common.LoadAdError
import com.google.android.libraries.ads.mobile.sdk.nativead.MediaView
import com.google.android.libraries.ads.mobile.sdk.nativead.NativeAd
import com.google.android.libraries.ads.mobile.sdk.nativead.NativeAdLoader
import com.google.android.libraries.ads.mobile.sdk.nativead.NativeAdLoaderCallback
import com.google.android.libraries.ads.mobile.sdk.nativead.NativeAdRequest
import com.google.android.libraries.ads.mobile.sdk.nativead.NativeAdView
import kotlinx.coroutines.delay

private const val PRODUCTION_NATIVE_AD_UNIT_ID = "ca-app-pub-4760027279848820/2122927929"
private const val TEST_NATIVE_AD_UNIT_ID = "ca-app-pub-3940256099942544/2247696110"

@Composable
fun NativeHomeAd(modifier: Modifier = Modifier) {
    var isInitialized by remember { mutableStateOf(MobileAds.isInitialized) }
    var nativeAd by remember { mutableStateOf<NativeAd?>(null) }

    LaunchedEffect(Unit) {
        while (!MobileAds.isInitialized) {
            delay(100)
        }
        isInitialized = true
    }

    LaunchedEffect(isInitialized) {
        if (!isInitialized || nativeAd != null) return@LaunchedEffect

        val adUnitId = if (com.aistudio.kidspolice.abcd.BuildConfig.DEBUG) {
            TEST_NATIVE_AD_UNIT_ID
        } else {
            PRODUCTION_NATIVE_AD_UNIT_ID
        }

        val request = NativeAdRequest.Builder(
            adUnitId,
            listOf(NativeAd.NativeAdType.NATIVE)
        ).build()

        val callback = object : NativeAdLoaderCallback {
            override fun onNativeAdLoaded(ad: NativeAd) {
                nativeAd = ad
                Log.d("NATIVE_AD_DEBUG", "Native ad loaded")
            }

            override fun onAdFailedToLoad(error: LoadAdError) {
                Log.e(
                    "NATIVE_AD_DEBUG",
                    "Native ad failed: code=" + error.code + ", message=" + error.message
                )
            }
        }

        NativeAdLoader.load(request, callback)
    }

    DisposableEffect(nativeAd) {
        onDispose {
            nativeAd?.destroy()
        }
    }

    nativeAd?.let { ad ->
        AndroidView(
            modifier = modifier.fillMaxWidth(),
            factory = { context -> createNativeAdView(context) },
            update = { view -> populateNativeAdView(view, ad) }
        )
    }
}

private fun createNativeAdView(context: android.content.Context): NativeAdView {
    val nativeAdView = NativeAdView(context).apply {
        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        setBackgroundColor(AndroidColor.WHITE)
    }

    val density = context.resources.displayMetrics.density
    fun dpToPx(dp: Int): Int = (dp * density).toInt()

    val root = LinearLayout(context).apply {
        orientation = LinearLayout.VERTICAL
        setPadding(dpToPx(16), dpToPx(12), dpToPx(16), dpToPx(12))
        layoutParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    val attribution = TextView(context).apply {
        text = "إعلان"
        textSize = 12f
        setTextColor(AndroidColor.rgb(90, 90, 90))
        gravity = Gravity.END
    }

    val topRow = LinearLayout(context).apply {
        orientation = LinearLayout.HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL
        layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    val icon = ImageView(context).apply {
        val iconSize = dpToPx(48)
        layoutParams = LinearLayout.LayoutParams(iconSize, iconSize)
        scaleType = ImageView.ScaleType.CENTER_CROP
    }

    val textColumn = LinearLayout(context).apply {
        orientation = LinearLayout.VERTICAL
        layoutParams = LinearLayout.LayoutParams(
            0,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            1f
        ).apply {
            marginStart = dpToPx(12)
        }
    }

    val headline = TextView(context).apply {
        textSize = 16f
        setTextColor(AndroidColor.rgb(24, 38, 61))
        setTypeface(typeface, android.graphics.Typeface.BOLD)
    }

    val advertiser = TextView(context).apply {
        textSize = 12f
        setTextColor(AndroidColor.rgb(74, 85, 104))
    }

    val body = TextView(context).apply {
        textSize = 13f
        setTextColor(AndroidColor.rgb(55, 65, 81))
        maxLines = 3
    }

    val media = MediaView(context).apply {
        minimumWidth = dpToPx(120)
        minimumHeight = dpToPx(120)
        layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            dpToPx(180)
        ).apply {
            topMargin = dpToPx(10)
        }
    }

    val callToAction = TextView(context).apply {
        textSize = 14f
        setTextColor(AndroidColor.WHITE)
        setBackgroundColor(AndroidColor.rgb(25, 118, 210))
        gravity = Gravity.CENTER
        val hp = dpToPx(16)
        val vp = dpToPx(10)
        setPadding(hp, vp, hp, vp)
        layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            topMargin = dpToPx(10)
        }
    }

    textColumn.addView(headline)
    textColumn.addView(advertiser)
    topRow.addView(icon)
    topRow.addView(textColumn)

    root.addView(attribution)
    root.addView(topRow)
    root.addView(body)
    root.addView(media)
    root.addView(callToAction)

    nativeAdView.addView(root)

    nativeAdView.tag = NativeAdViewAssets(
        icon = icon,
        headline = headline,
        advertiser = advertiser,
        body = body,
        media = media,
        callToAction = callToAction
    )

    return nativeAdView
}

private data class NativeAdViewAssets(
    val icon: ImageView,
    val headline: TextView,
    val advertiser: TextView,
    val body: TextView,
    val media: MediaView,
    val callToAction: TextView
)

private fun populateNativeAdView(nativeAdView: NativeAdView, nativeAd: NativeAd) {
    val assets = nativeAdView.tag as NativeAdViewAssets

    nativeAdView.headlineView = assets.headline
    nativeAdView.advertiserView = assets.advertiser
    nativeAdView.bodyView = assets.body
    nativeAdView.iconView = assets.icon
    nativeAdView.callToActionView = assets.callToAction

    assets.headline.text = nativeAd.headline
    assets.advertiser.text = nativeAd.advertiser
    assets.body.text = nativeAd.body
    assets.callToAction.text = nativeAd.callToAction

    nativeAd.icon?.drawable?.let {
        assets.icon.setImageDrawable(it)
        assets.icon.visibility = View.VISIBLE
    } ?: run {
        assets.icon.visibility = View.GONE
    }

    assets.headline.visibility = if (nativeAd.headline.isNullOrBlank()) View.GONE else View.VISIBLE
    assets.advertiser.visibility = if (nativeAd.advertiser.isNullOrBlank()) View.GONE else View.VISIBLE
    assets.body.visibility = if (nativeAd.body.isNullOrBlank()) View.GONE else View.VISIBLE
    assets.callToAction.visibility = if (nativeAd.callToAction.isNullOrBlank()) View.GONE else View.VISIBLE

    val mediaContent = nativeAd.mediaContent
    if (mediaContent != null) {
        assets.media.mediaContent = mediaContent
        assets.media.visibility = View.VISIBLE
    } else {
        assets.media.visibility = View.GONE
    }

    nativeAdView.registerNativeAd(nativeAd, assets.media)
}
