package com.dyiz.kidslearningapp.Screens.SplashScreens

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.VideoView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import com.dyiz.kidslearningapp.NavGraph.NavRoutes
import com.dyiz.kidslearningapp.R
import kotlin.math.max
import kotlin.math.roundToInt

private class CenterCropVideoView(context: Context) : VideoView(context) {

    private var intrinsicW = 0
    private var intrinsicH = 0

    fun setIntrinsicVideoSize(w: Int, h: Int) {
        if (w > 0 && h > 0) {
            intrinsicW = w
            intrinsicH = h
            requestLayout()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val parentW = MeasureSpec.getSize(widthMeasureSpec)
        val parentH = MeasureSpec.getSize(heightMeasureSpec)
        if (intrinsicW == 0 || intrinsicH == 0) {
            setMeasuredDimension(parentW, parentH)
            return
        }
        val scale = max(
            parentW.toFloat() / intrinsicW,
            parentH.toFloat() / intrinsicH
        )
        setMeasuredDimension(
            (intrinsicW * scale).roundToInt(),
            (intrinsicH * scale).roundToInt()
        )
    }
}

@Composable
fun SplashScreen(
    navController: NavHostController
) {
    val context = LocalContext.current
    val videoUri = remember(context) {
        Uri.parse("android.resource://${context.packageName}/${R.raw.splashvideo}")
    }

    var isVideoActuallyRendering by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF7CB0D6)),
    ) {
        // 1. Video View
        AndroidView(
            factory = { ctx ->
                FrameLayout(ctx).apply {
                    clipChildren = true
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    val video = CenterCropVideoView(ctx).apply {
                        setVideoURI(videoUri)
                        alpha = 0f // Shuru mein invisible

                        setOnPreparedListener { mp ->
                            setIntrinsicVideoSize(mp.videoWidth, mp.videoHeight)
                            mp.isLooping = true

                            mp.setOnInfoListener { _, what, _ ->
                                if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {

                                    animate().alpha(1f).setDuration(400).start()
                                    isVideoActuallyRendering = true
                                    true
                                } else {
                                    false
                                }
                            }
                            start()
                        }

                        layoutParams = FrameLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            Gravity.CENTER
                        )
                    }
                    addView(video)
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        androidx.compose.animation.AnimatedVisibility(
            visible = isVideoActuallyRendering,
            enter = androidx.compose.animation.fadeIn(animationSpec = androidx.compose.animation.core.tween(600)),
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .navigationBarsPadding()
                    .padding(bottom = 70.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
                Image(
                    painter = painterResource(id = R.drawable.splashtextimage),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .wrapContentHeight(),
                    contentScale = ContentScale.Fit
                )

                Spacer(modifier = Modifier.height(20.dp))

                Image(
                    painter = painterResource(id = R.drawable.splashplaybutton),
                    contentDescription = "Start",
                    modifier = Modifier
                        .size(90.dp)
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) {
                            navController.navigate(NavRoutes.SECOND_SPLASH) {
                                popUpTo(NavRoutes.SPLASH) { inclusive = true }
                            }
                        }
                )
            }
        }
    }
}
