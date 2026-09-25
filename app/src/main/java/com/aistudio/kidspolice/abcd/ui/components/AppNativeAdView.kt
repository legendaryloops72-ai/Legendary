package com.aistudio.kidspolice.abcd.ui.components

import android.content.Context
import android.graphics.Color as AndroidColor
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.aistudio.kidspolice.abcd.BuildConfig
import com.google.android.libraries.ads.mobile.sdk.MobileAds
import com.google.android.libraries.ads.mobile.sdk.common.LoadAdError
import com.google.android.libraries.ads.mobile.sdk.nativead.MediaView
import com.google.android.libraries.ads.mobile.sdk.nativead.NativeAd
import com.google.android.libraries.ads.mobile.sdk.nativead.NativeAdLoader
import com.google.android.libraries.ads.mobile.sdk.nativead.NativeAdLoaderCallback
import com.google.android.libraries.ads.mobile.sdk.nativead.NativeAdRequest
import com.google.android.libraries.ads.mobile.sdk.nativead.NativeAdView
import kotlinx.coroutines.delay

@Composable
fun AppNativeAdView(modifier: Modifier = Modifier) {
    var isInitialized by remember { mutableStateOf(MobileAds.isInitialized) }
    var loadedNativeAd by remember { mutableStateOf<NativeAd?>(null) }
    var loadFailed by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        while (!MobileAds.isInitialized) {
            delay(100)
        }
        isInitialized = true
    }

    DisposableEffect(Unit) {
        onDispose {
            // NativeAd doesn't require explicit destroy in next-gen sdk, but release reference
            loadedNativeAd = null
        }
    }

    LaunchedEffect(isInitialized) {
        if (!isInitialized || loadedNativeAd != null || loadFailed) return@LaunchedEffect

        val adUnitId = if (BuildConfig.DEBUG) {
            "ca-app-pub-3940256099942544/2247696110"
        } else {
            "ca-app-pub-4760027279848820/2122927929"
        }

        try {
            Log.d("NATIVE_AD_DEBUG", "NativeAdLoader.load() called with adUnitId: $adUnitId")
            val request = NativeAdRequest.Builder(
                adUnitId,
                listOf(NativeAd.NativeAdType.NATIVE)
            ).build()

            NativeAdLoader.load(
                request,
                object : NativeAdLoaderCallback {
                    override fun onNativeAdLoaded(nativeAd: NativeAd) {
                        Log.d("NATIVE_AD_DEBUG", "onNativeAdLoaded: Native ad loaded successfully")
                        loadedNativeAd = nativeAd
                    }

                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        Log.e("NATIVE_AD_DEBUG", "onAdFailedToLoad: code=${loadAdError.code}, message=${loadAdError.message}")
                        loadFailed = true
                    }
                }
            )
        } catch (e: Exception) {
            Log.e("NATIVE_AD_DEBUG", "Exception initiating native ad load: ${e.message}", e)
            loadFailed = true
        }
    }

    if (loadedNativeAd != null) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            AndroidView(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                factory = { context ->
                    createNativeAdViewGroup(context)
                },
                update = { nativeAdView ->
                    populateNativeAd(nativeAdView, loadedNativeAd!!)
                }
            )
        }
    }
}

private fun createNativeAdViewGroup(context: Context): NativeAdView {
    val nativeAdView = NativeAdView(context).apply {
        layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )
    }

    val rootLayout = LinearLayout(context).apply {
        orientation = LinearLayout.VERTICAL
        layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        val p = dpToPx(context, 16)
        setPadding(p, p, p, p)
    }

    // Top Row: Ad attribution badge + Advertiser/App title
    val headerRow = LinearLayout(context).apply {
        orientation = LinearLayout.HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL
        layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
    }

    // Ad Attribution Badge (إعلان)
    val adBadge = TextView(context).apply {
        text = "إعلان"
        textSize = 11f
        setTextColor(AndroidColor.WHITE)
        setTypeface(null, Typeface.BOLD)
        val badgeBg = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = dpToPx(context, 4).toFloat()
            setColor(AndroidColor.parseColor("#E65100")) // Orange badge
        }
        background = badgeBg
        val hp = dpToPx(context, 6)
        val vp = dpToPx(context, 2)
        setPadding(hp, vp, hp, vp)
        id = View.generateViewId()
    }
    headerRow.addView(adBadge)

    // Advertiser
    val advertiserView = TextView(context).apply {
        textSize = 12f
        setTextColor(AndroidColor.parseColor("#718096"))
        setTypeface(null, Typeface.NORMAL)
        val lp = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            marginStart = dpToPx(context, 8)
        }
        layoutParams = lp
        id = View.generateViewId()
    }
    headerRow.addView(advertiserView)
    rootLayout.addView(headerRow)

    // Middle Row: Icon + Headline + Body
    val infoRow = LinearLayout(context).apply {
        orientation = LinearLayout.HORIZONTAL
        gravity = Gravity.TOP
        layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            topMargin = dpToPx(context, 10)
            bottomMargin = dpToPx(context, 10)
        }
    }

    // Icon View
    val iconView = ImageView(context).apply {
        val s = dpToPx(context, 48)
        layoutParams = LinearLayout.LayoutParams(s, s).apply {
            marginEnd = dpToPx(context, 12)
        }
        scaleType = ImageView.ScaleType.FIT_CENTER
        val iconBg = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = dpToPx(context, 10).toFloat()
            setColor(AndroidColor.parseColor("#EDF2F7"))
        }
        background = iconBg
        clipToOutline = true
        id = View.generateViewId()
    }
    infoRow.addView(iconView)

    // Text Column
    val textCol = LinearLayout(context).apply {
        orientation = LinearLayout.VERTICAL
        layoutParams = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            1f
        )
    }

    val headlineView = TextView(context).apply {
        textSize = 16f
        setTextColor(AndroidColor.parseColor("#18263D"))
        setTypeface(null, Typeface.BOLD)
        maxLines = 1
        id = View.generateViewId()
    }
    textCol.addView(headlineView)

    val bodyView = TextView(context).apply {
        textSize = 13f
        setTextColor(AndroidColor.parseColor("#4A5568"))
        setTypeface(null, Typeface.NORMAL)
        maxLines = 2
        id = View.generateViewId()
    }
    textCol.addView(bodyView)
    infoRow.addView(textCol)
    rootLayout.addView(infoRow)

    // Media View
    val mediaView = MediaView(context).apply {
        layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            dpToPx(context, 160)
        ).apply {
            bottomMargin = dpToPx(context, 12)
        }
        id = View.generateViewId()
    }
    rootLayout.addView(mediaView)

    // Call To Action Button
    val ctaButton = Button(context).apply {
        layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            dpToPx(context, 46)
        )
        val btnBg = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = dpToPx(context, 14).toFloat()
            setColor(AndroidColor.parseColor("#0D47A1")) // Police Blue
        }
        background = btnBg
        setTextColor(AndroidColor.WHITE)
        textSize = 15f
        setTypeface(null, Typeface.BOLD)
        id = View.generateViewId()
    }
    rootLayout.addView(ctaButton)

    nativeAdView.addView(rootLayout)

    // Bind Views to NativeAdView
    nativeAdView.headlineView = headlineView
    nativeAdView.advertiserView = advertiserView
    nativeAdView.bodyView = bodyView
    nativeAdView.iconView = iconView
    nativeAdView.callToActionView = ctaButton

    return nativeAdView
}

private fun populateNativeAd(nativeAdView: NativeAdView, nativeAd: NativeAd) {
    // 1. Headline
    (nativeAdView.headlineView as? TextView)?.apply {
        text = nativeAd.headline
        visibility = if (nativeAd.headline.isNullOrEmpty()) View.GONE else View.VISIBLE
    }

    // 2. Advertiser
    (nativeAdView.advertiserView as? TextView)?.apply {
        text = nativeAd.advertiser
        visibility = if (nativeAd.advertiser.isNullOrEmpty()) View.GONE else View.VISIBLE
    }

    // 3. Body
    (nativeAdView.bodyView as? TextView)?.apply {
        text = nativeAd.body
        visibility = if (nativeAd.body.isNullOrEmpty()) View.GONE else View.VISIBLE
    }

    // 4. Icon
    (nativeAdView.iconView as? ImageView)?.apply {
        val icon = nativeAd.icon
        if (icon?.drawable != null) {
            setImageDrawable(icon.drawable)
            visibility = View.VISIBLE
        } else {
            visibility = View.GONE
        }
    }

    // 5. Call To Action
    (nativeAdView.callToActionView as? Button)?.apply {
        val cta = nativeAd.callToAction
        if (!cta.isNullOrEmpty()) {
            text = cta
            visibility = View.VISIBLE
        } else {
            text = "تثبيت / زيارة"
            visibility = View.VISIBLE
        }
    }

    // 6. MediaView registration
    val mediaView = nativeAdView.findViewById<MediaView>(nativeAdView.mediaView?.id ?: 0)
        ?: findFirstMediaView(nativeAdView)

    if (mediaView != null) {
        val mediaContent = nativeAd.mediaContent
        if (mediaContent != null) {
            mediaView.mediaContent = mediaContent
            mediaView.visibility = View.VISIBLE
        } else {
            mediaView.visibility = View.GONE
        }
        nativeAdView.registerNativeAd(nativeAd, mediaView)
    }
}

private fun findFirstMediaView(view: View): MediaView? {
    if (view is MediaView) return view
    if (view is android.view.ViewGroup) {
        for (i in 0 until view.childCount) {
            val found = findFirstMediaView(view.getChildAt(i))
            if (found != null) return found
        }
    }
    return null
}

private fun dpToPx(context: Context, dp: Int): Int {
    return (dp * context.resources.displayMetrics.density).toInt()
}
