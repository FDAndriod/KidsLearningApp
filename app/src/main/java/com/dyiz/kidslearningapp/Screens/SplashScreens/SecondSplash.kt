package com.dyiz.kidslearningapp.Screens.SplashScreens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.dyiz.kidslearningapp.NavGraph.NavRoutes
import com.dyiz.kidslearningapp.R
import kotlin.math.atan2
import kotlin.math.roundToInt

@Composable
fun SecondSplash(
    navController: NavHostController
){
    val configuration = LocalConfiguration.current
    val screenWidthPx = with(LocalDensity.current) { configuration.screenWidthDp.dp.toPx() }
    val screenHeightPx = with(LocalDensity.current) { configuration.screenHeightDp.dp.toPx() }
    val rocketSizeDp = 130.dp
    val rocketSizePx = with(LocalDensity.current) { rocketSizeDp.toPx() }
    val animationProgress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        animationProgress.animateTo(
            targetValue = 1f,
            animationSpec = tweenfunction(3000)
        )
        navController.navigate(NavRoutes.THIRD_SPLASH_SCREEN) {
            popUpTo(NavRoutes.SECOND_SPLASH) { inclusive = true }
        }

    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.hometoptextcolor)),
        contentAlignment = Alignment.TopStart
    ){
        val progress = animationProgress.value

        val startX = -rocketSizePx
        val endX = screenWidthPx
        val baseY = (screenHeightPx / 2f) - (rocketSizePx / 2f)
        val peakHeight = screenHeightPx * 0.3f

        val currentX = startX + (endX - startX) * progress
        val currentY = baseY - (4f * peakHeight * progress * (1f - progress))


        // 1. Draw the Dashed Trail (Canvas)
        // ==========================================
        Canvas(modifier = Modifier.fillMaxSize()) {
            if (progress > 0f) {
                val path = Path()
                val step = 0.02f
                var t = 0f
                var isFirst = true

                while (t <= progress) {
                    val cx = startX + (endX - startX) * t + (rocketSizePx / 2f)
                    val cy = baseY - (4f * peakHeight * t * (1f - t)) + (rocketSizePx / 2f)

                    if (isFirst) {
                        path.moveTo(cx, cy)
                        isFirst = false
                    } else {
                        path.lineTo(cx, cy)
                    }
                    t += step
                }
                val finalCx = currentX + (rocketSizePx / 2f)
                val finalCy = currentY + (rocketSizePx / 2f)
                path.lineTo(finalCx, finalCy)
                drawPath(
                    path = path,
                    color = Color.White.copy(alpha = 0.4f),
                    style = Stroke(
                        width = 7f,
                        cap = StrokeCap.Butt,
                        pathEffect = PathEffect.dashPathEffect(
                            intervals = floatArrayOf(
                                20f,
                                20f
                            ),
                            phase = 0f
                        )
                    )
                )
            }
        }

        // 2. Draw the Rocket
        // ==========================================
        val dX = endX - startX
        val dY = -4f * peakHeight * (1f - 2f * progress)
        val angleRad = atan2(dY.toDouble(), dX.toDouble())
        val rotationAngle = Math.toDegrees(angleRad).toFloat()

        Image(
            painter = painterResource(id = R.drawable.horizontalrocket1),
            contentDescription = "Rocket",
            modifier = Modifier
                .size(rocketSizeDp)
                .offset {
                    IntOffset(currentX.roundToInt(), currentY.roundToInt())
                }
                .graphicsLayer {
                    rotationZ = rotationAngle
                }
        )
    }
}

fun tweenfunction(duration: Int): TweenSpec<Float> {
    return tween(
        durationMillis = duration,
        easing = LinearEasing
    )
}

