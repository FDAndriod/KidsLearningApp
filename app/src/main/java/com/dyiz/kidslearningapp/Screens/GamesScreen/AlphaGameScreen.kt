package com.dyiz.kidslearningapp.Screens.GamesScreen

import android.app.Activity
import android.content.Context
import android.media.MediaPlayer
import android.speech.tts.TextToSpeech
import android.view.WindowManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavHostController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.dyiz.kidslearningapp.Database.ViewModel.MainViewModel
import com.dyiz.kidslearningapp.NavGraph.NavRoutes
import com.dyiz.kidslearningapp.R
import com.dyiz.kidslearningapp.badges.BadgeGameType
import com.dyiz.kidslearningapp.badges.TrackGameBadgeProgress
import com.dyiz.kidslearningapp.utils.AlphabetData
import com.dyiz.kidslearningapp.utils.BadgeUnlockedDialog
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.text.lowercase

// Distance Helper
fun Offset.distanceTo(other: Offset): Float {
    return sqrt((x - other.x).pow(2) + (y - other.y).pow(2))
}

// Complete Alphabet Tracing Data
object AlphabetDataf {
    private fun interpolate(segment: List<Offset>): List<Offset> {
        val result = mutableListOf<Offset>()
        if (segment.isEmpty()) return result
        result.add(segment.first())
        for (i in 0 until segment.size - 1) {
            val p1 = segment[i]; val p2 = segment[i + 1]
            val dist = p1.distanceTo(p2)
            val numPoints = (dist / 0.01f).toInt().coerceAtLeast(1)
            for (j in 1..numPoints) {
                val fraction = j.toFloat() / numPoints
                result.add(Offset(p1.x + (p2.x - p1.x) * fraction, p1.y + (p2.y - p1.y) * fraction))
            }
        }
        return result
    }

    fun getPoints(letter: Char, isUppercase: Boolean): List<List<Offset>> {
        val rawPoints = if(isUppercase){
            when (letter.uppercaseChar()) {
                'A' -> listOf(listOf(Offset(0.5f, 0.15f), Offset(0.25f, 0.85f)), listOf(Offset(0.5f, 0.15f), Offset(0.75f, 0.85f)), listOf(Offset(0.35f, 0.55f), Offset(0.65f, 0.55f)))

                'B' -> listOf(
                    // 1. Vertical Line (Backbone)
                    listOf(
                        Offset(0.3f, 0.15f),
                        Offset(0.3f, 0.5f),
                        Offset(0.3f, 0.85f)
                    ),
                    // 2. Top Bubble (Rounded Corners)
                    listOf(
                        Offset(0.3f, 0.15f),
                        Offset(0.55f, 0.15f),
                        Offset(0.70f, 0.18f), // Corner
                        Offset(0.75f, 0.32f), // Peak point
                        Offset(0.70f, 0.46f), //   curve
                        Offset(0.55f, 0.5f),
                        Offset(0.3f, 0.5f)
                    ),
                    // 3. Bottom Bubble (Balanced Curves)
                    listOf(
                        Offset(0.3f, 0.5f),
                        Offset(0.58f, 0.5f),
                        Offset(0.74f, 0.54f),
                        Offset(0.78f, 0.68f), // Bottom peak
                        Offset(0.74f, 0.81f),
                        Offset(0.58f, 0.85f),
                        Offset(0.3f, 0.85f)
                    )
                )
                'C' -> listOf(listOf(Offset(0.75f, 0.25f), Offset(0.7f, 0.18f), Offset(0.5f, 0.15f), Offset(0.35f, 0.18f), Offset(0.25f, 0.35f), Offset(0.25f, 0.65f), Offset(0.35f, 0.82f), Offset(0.5f, 0.85f), Offset(0.7f, 0.82f), Offset(0.75f, 0.75f)))
                'D' -> listOf(listOf(Offset(0.35f, 0.15f), Offset(0.35f, 0.85f)), listOf(Offset(0.35f, 0.15f), Offset(0.6f, 0.15f), Offset(0.75f, 0.22f), Offset(0.85f, 0.4f), Offset(0.85f, 0.6f), Offset(0.75f, 0.78f), Offset(0.6f, 0.85f), Offset(0.35f, 0.85f)))
                'E' -> listOf(listOf(Offset(0.35f, 0.15f), Offset(0.35f, 0.85f)), listOf(Offset(0.35f, 0.15f), Offset(0.75f, 0.15f)), listOf(Offset(0.35f, 0.5f), Offset(0.65f, 0.5f)), listOf(Offset(0.35f, 0.85f), Offset(0.75f, 0.85f)))
                'F' -> listOf(listOf(Offset(0.35f, 0.15f), Offset(0.35f, 0.85f)), listOf(Offset(0.35f, 0.15f), Offset(0.75f, 0.15f)), listOf(Offset(0.35f, 0.5f), Offset(0.65f, 0.5f)))
                'G' -> listOf(listOf(Offset(0.75f, 0.3f), Offset(0.65f, 0.18f), Offset(0.5f, 0.15f), Offset(0.35f, 0.18f), Offset(0.25f, 0.35f), Offset(0.25f, 0.65f), Offset(0.35f, 0.82f), Offset(0.5f, 0.85f), Offset(0.75f, 0.85f), Offset(0.75f, 0.5f), Offset(0.55f, 0.5f)))
                'H' -> listOf(listOf(Offset(0.3f, 0.15f), Offset(0.3f, 0.85f)), listOf(Offset(0.7f, 0.15f), Offset(0.7f, 0.85f)), listOf(Offset(0.3f, 0.5f), Offset(0.7f, 0.5f)))
                'I' -> listOf(listOf(Offset(0.5f, 0.15f), Offset(0.5f, 0.85f)))
                'J' -> listOf(listOf(Offset(0.4f, 0.15f), Offset(0.8f, 0.15f)), listOf(Offset(0.6f, 0.15f), Offset(0.6f, 0.7f), Offset(0.55f, 0.82f), Offset(0.4f, 0.85f), Offset(0.25f, 0.75f)))
                'K' -> listOf(listOf(Offset(0.35f, 0.15f), Offset(0.35f, 0.85f)), listOf(Offset(0.7f, 0.15f), Offset(0.35f, 0.5f)), listOf(Offset(0.35f, 0.5f), Offset(0.7f, 0.85f)))
                'L' -> listOf(listOf(Offset(0.35f, 0.15f), Offset(0.35f, 0.85f)), listOf(Offset(0.35f, 0.85f), Offset(0.75f, 0.85f)))
                'M' -> listOf(listOf(Offset(0.25f, 0.85f), Offset(0.25f, 0.15f)), listOf(Offset(0.25f, 0.15f), Offset(0.5f, 0.5f)), listOf(Offset(0.5f, 0.5f), Offset(0.75f, 0.15f)), listOf(Offset(0.75f, 0.15f), Offset(0.75f, 0.85f)))
                'N' -> listOf(listOf(Offset(0.3f, 0.85f), Offset(0.3f, 0.15f)), listOf(Offset(0.3f, 0.15f), Offset(0.7f, 0.85f)), listOf(Offset(0.7f, 0.85f), Offset(0.7f, 0.15f)))
                'O' -> listOf(listOf(Offset(0.5f, 0.15f), Offset(0.72f, 0.22f), Offset(0.82f, 0.5f), Offset(0.72f, 0.78f), Offset(0.5f, 0.85f), Offset(0.28f, 0.78f), Offset(0.18f, 0.5f), Offset(0.28f, 0.22f), Offset(0.5f, 0.15f)))
                'P' -> listOf(listOf(Offset(0.35f, 0.15f), Offset(0.35f, 0.85f)), listOf(Offset(0.35f, 0.15f), Offset(0.6f, 0.15f), Offset(0.75f, 0.32f), Offset(0.6f, 0.5f), Offset(0.35f, 0.5f)))
                'Q' -> listOf(listOf(Offset(0.5f, 0.15f), Offset(0.7f, 0.22f), Offset(0.8f, 0.5f), Offset(0.7f, 0.78f), Offset(0.5f, 0.85f), Offset(0.3f, 0.78f), Offset(0.2f, 0.5f), Offset(0.3f, 0.22f), Offset(0.5f, 0.15f)), listOf(Offset(0.6f, 0.65f), Offset(0.85f, 0.85f)))
                'R' -> listOf(listOf(Offset(0.35f, 0.15f), Offset(0.35f, 0.85f)), listOf(Offset(0.35f, 0.15f), Offset(0.6f, 0.15f), Offset(0.75f, 0.32f), Offset(0.6f, 0.5f), Offset(0.35f, 0.5f)), listOf(Offset(0.45f, 0.5f), Offset(0.75f, 0.85f)))
                'S' -> listOf(listOf(Offset(0.75f, 0.25f), Offset(0.6f, 0.15f), Offset(0.4f, 0.15f), Offset(0.25f, 0.3f), Offset(0.35f, 0.45f), Offset(0.5f, 0.5f), Offset(0.65f, 0.55f), Offset(0.75f, 0.7f), Offset(0.6f, 0.85f), Offset(0.4f, 0.85f), Offset(0.25f, 0.75f)))
                'T' -> listOf(listOf(Offset(0.25f, 0.15f), Offset(0.75f, 0.15f)), listOf(Offset(0.5f, 0.15f), Offset(0.5f, 0.85f)))
                'U' -> listOf(listOf(Offset(0.3f, 0.15f), Offset(0.3f, 0.65f), Offset(0.4f, 0.85f), Offset(0.6f, 0.85f), Offset(0.7f, 0.65f), Offset(0.7f, 0.15f)))
                'V' -> listOf(listOf(Offset(0.25f, 0.15f), Offset(0.5f, 0.85f), Offset(0.75f, 0.15f)))
                'W' -> listOf(listOf(Offset(0.15f, 0.15f), Offset(0.3f, 0.85f)), listOf(Offset(0.3f, 0.85f), Offset(0.5f, 0.45f)), listOf(Offset(0.5f, 0.45f), Offset(0.7f, 0.85f)), listOf(Offset(0.7f, 0.85f), Offset(0.85f, 0.15f)))
                'X' -> listOf(listOf(Offset(0.3f, 0.15f), Offset(0.7f, 0.85f)), listOf(Offset(0.7f, 0.15f), Offset(0.3f, 0.85f)))
                'Y' -> listOf(listOf(Offset(0.3f, 0.15f), Offset(0.5f, 0.5f)), listOf(Offset(0.7f, 0.15f), Offset(0.5f, 0.5f)), listOf(Offset(0.5f, 0.5f), Offset(0.5f, 0.85f)))
                'Z' -> listOf(listOf(Offset(0.3f, 0.15f), Offset(0.7f, 0.15f)), listOf(Offset(0.7f, 0.15f), Offset(0.3f, 0.85f)), listOf(Offset(0.3f, 0.85f), Offset(0.7f, 0.85f)))
                '0' -> listOf(listOf(Offset(0.5f, 0.15f), Offset(0.72f, 0.22f), Offset(0.82f, 0.5f), Offset(0.72f, 0.78f), Offset(0.5f, 0.85f), Offset(0.28f, 0.78f), Offset(0.18f, 0.5f), Offset(0.28f, 0.22f), Offset(0.5f, 0.15f)))
                else -> listOf(listOf(Offset(0.5f, 0.15f), Offset(0.5f, 0.85f)))
            }
        }
        else {
            // Lowercase letters (Small abc) design
            when (letter.lowercaseChar()) {
                'a' -> listOf(
                    listOf(Offset(0.75f, 0.4f), Offset(0.75f, 0.85f)),
                    listOf(
                        Offset(0.75f, 0.5f),
                        Offset(0.65f, 0.4f),
                        Offset(0.45f, 0.4f),
                        Offset(0.35f, 0.5f),
                        Offset(0.35f, 0.75f),
                        Offset(0.45f, 0.85f),
                        Offset(0.65f, 0.85f),
                        Offset(0.75f, 0.75f)
                    )
                )

                'b' -> listOf(
                    listOf(Offset(0.35f, 0.15f), Offset(0.35f, 0.85f)),
                    listOf(
                        Offset(0.35f, 0.55f),
                        Offset(0.5f, 0.45f),
                        Offset(0.65f, 0.45f),
                        Offset(0.75f, 0.55f),
                        Offset(0.75f, 0.75f),
                        Offset(0.65f, 0.85f),
                        Offset(0.5f, 0.85f),
                        Offset(0.35f, 0.75f)
                    )
                )

                'c' -> listOf(
                    listOf(
                        Offset(0.7f, 0.55f),
                        Offset(0.6f, 0.45f),
                        Offset(0.45f, 0.45f),
                        Offset(0.35f, 0.55f),
                        Offset(0.35f, 0.75f),
                        Offset(0.45f, 0.85f),
                        Offset(0.6f, 0.85f),
                        Offset(0.7f, 0.75f)
                    )
                )

                'd' -> listOf(
                    listOf(Offset(0.65f, 0.15f), Offset(0.65f, 0.85f)),
                    listOf(
                        Offset(0.65f, 0.55f),
                        Offset(0.5f, 0.45f),
                        Offset(0.35f, 0.45f),
                        Offset(0.25f, 0.55f),
                        Offset(0.25f, 0.75f),
                        Offset(0.35f, 0.85f),
                        Offset(0.5f, 0.85f),
                        Offset(0.65f, 0.75f)
                    )
                )

                'e' -> listOf(
                    listOf(
                        Offset(0.35f, 0.65f),
                        Offset(0.75f, 0.65f),
                        Offset(0.75f, 0.5f),
                        Offset(0.65f, 0.4f),
                        Offset(0.45f, 0.4f),
                        Offset(0.35f, 0.5f),
                        Offset(0.35f, 0.75f),
                        Offset(0.45f, 0.85f),
                        Offset(0.75f, 0.85f)
                    )
                )

                'f' -> listOf(
                    listOf(
                        Offset(0.65f, 0.15f),
                        Offset(0.55f, 0.15f),
                        Offset(0.45f, 0.25f),
                        Offset(0.45f, 0.85f)
                    ), listOf(Offset(0.3f, 0.45f), Offset(0.6f, 0.45f))
                )

                'g' -> listOf(
                    listOf(
                        Offset(0.75f, 0.45f),
                        Offset(0.75f, 0.8f),
                        Offset(0.7f, 0.95f),
                        Offset(0.5f, 0.98f),
                        Offset(0.35f, 0.92f)
                    ),
                    listOf(
                        Offset(0.75f, 0.55f),
                        Offset(0.65f, 0.45f),
                        Offset(0.45f, 0.45f),
                        Offset(0.35f, 0.55f),
                        Offset(0.35f, 0.75f),
                        Offset(0.45f, 0.85f),
                        Offset(0.65f, 0.85f),
                        Offset(0.75f, 0.75f)
                    )
                )

                'h' -> listOf(
                    listOf(Offset(0.35f, 0.15f), Offset(0.35f, 0.85f)),
                    listOf(
                        Offset(0.35f, 0.55f),
                        Offset(0.45f, 0.45f),
                        Offset(0.65f, 0.45f),
                        Offset(0.75f, 0.55f),
                        Offset(0.75f, 0.85f)
                    )
                )

                'i' -> listOf(
                    listOf(Offset(0.5f, 0.45f), Offset(0.5f, 0.85f)),
                    listOf(Offset(0.5f, 0.25f), Offset(0.51f, 0.25f))
                ) // Dot and line
                'j' -> listOf(
                    listOf(
                        Offset(0.65f, 0.45f),
                        Offset(0.65f, 0.85f),
                        Offset(0.55f, 0.95f),
                        Offset(0.4f, 0.95f)
                    ), listOf(Offset(0.65f, 0.25f), Offset(0.66f, 0.25f))
                )

                'k' -> listOf(
                    listOf(Offset(0.35f, 0.15f), Offset(0.35f, 0.85f)),
                    listOf(Offset(0.65f, 0.45f), Offset(0.35f, 0.65f)),
                    listOf(Offset(0.45f, 0.65f), Offset(0.65f, 0.85f))
                )

                'l' -> listOf(listOf(Offset(0.5f, 0.15f), Offset(0.5f, 0.85f)))
                'm' -> listOf(
                    listOf(Offset(0.25f, 0.45f), Offset(0.25f, 0.85f)),
                    listOf(
                        Offset(0.25f, 0.55f),
                        Offset(0.35f, 0.45f),
                        Offset(0.45f, 0.45f),
                        Offset(0.5f, 0.55f),
                        Offset(0.5f, 0.85f)
                    ),
                    listOf(
                        Offset(0.5f, 0.55f),
                        Offset(0.6f, 0.45f),
                        Offset(0.7f, 0.45f),
                        Offset(0.75f, 0.55f),
                        Offset(0.75f, 0.85f)
                    )
                )

                'n' -> listOf(
                    listOf(Offset(0.35f, 0.45f), Offset(0.35f, 0.85f)),
                    listOf(
                        Offset(0.35f, 0.55f),
                        Offset(0.45f, 0.45f),
                        Offset(0.65f, 0.45f),
                        Offset(0.75f, 0.55f),
                        Offset(0.75f, 0.85f)
                    )
                )

                'o' -> listOf(
                    listOf(
                        Offset(0.5f, 0.45f),
                        Offset(0.7f, 0.52f),
                        Offset(0.75f, 0.65f),
                        Offset(0.7f, 0.78f),
                        Offset(0.5f, 0.85f),
                        Offset(0.3f, 0.78f),
                        Offset(0.25f, 0.65f),
                        Offset(0.3f, 0.52f),
                        Offset(0.5f, 0.45f)
                    )
                )

                'p' -> listOf(
                    listOf(Offset(0.35f, 0.45f), Offset(0.35f, 0.98f)),
                    listOf(
                        Offset(0.35f, 0.55f),
                        Offset(0.45f, 0.45f),
                        Offset(0.65f, 0.45f),
                        Offset(0.75f, 0.55f),
                        Offset(0.75f, 0.75f),
                        Offset(0.65f, 0.85f),
                        Offset(0.45f, 0.85f),
                        Offset(0.35f, 0.75f)
                    )
                )

                'q' -> listOf(
                    listOf(Offset(0.65f, 0.45f), Offset(0.65f, 0.98f)),
                    listOf(
                        Offset(0.65f, 0.55f),
                        Offset(0.55f, 0.45f),
                        Offset(0.35f, 0.45f),
                        Offset(0.25f, 0.55f),
                        Offset(0.25f, 0.75f),
                        Offset(0.35f, 0.85f),
                        Offset(0.55f, 0.85f),
                        Offset(0.65f, 0.75f)
                    )
                )

                'r' -> listOf(
                    listOf(Offset(0.35f, 0.45f), Offset(0.35f, 0.85f)),
                    listOf(Offset(0.35f, 0.6f), Offset(0.45f, 0.45f), Offset(0.65f, 0.45f))
                )

                's' -> listOf(
                    listOf(
                        Offset(0.7f, 0.5f),
                        Offset(0.6f, 0.45f),
                        Offset(0.45f, 0.45f),
                        Offset(0.4f, 0.55f),
                        Offset(0.5f, 0.65f),
                        Offset(0.65f, 0.7f),
                        Offset(0.65f, 0.8f),
                        Offset(0.55f, 0.85f),
                        Offset(0.4f, 0.85f),
                        Offset(0.35f, 0.75f)
                    )
                )

                't' -> listOf(
                    listOf(
                        Offset(0.45f, 0.15f),
                        Offset(0.45f, 0.75f),
                        Offset(0.55f, 0.85f),
                        Offset(0.65f, 0.85f)
                    ), listOf(Offset(0.3f, 0.4f), Offset(0.6f, 0.4f))
                )

                'u' -> listOf(
                    listOf(
                        Offset(0.35f, 0.45f),
                        Offset(0.35f, 0.75f),
                        Offset(0.45f, 0.85f),
                        Offset(0.65f, 0.85f)
                    ), listOf(Offset(0.65f, 0.45f), Offset(0.65f, 0.85f))
                )

                'v' -> listOf(listOf(Offset(0.3f, 0.45f), Offset(0.5f, 0.85f), Offset(0.7f, 0.45f)))
                'w' -> listOf(
                    listOf(
                        Offset(0.2f, 0.45f),
                        Offset(0.35f, 0.85f),
                        Offset(0.5f, 0.6f),
                        Offset(0.65f, 0.85f),
                        Offset(0.8f, 0.45f)
                    )
                )

                'x' -> listOf(
                    listOf(Offset(0.35f, 0.45f), Offset(0.65f, 0.85f)),
                    listOf(Offset(0.65f, 0.45f), Offset(0.35f, 0.85f))
                )

                'y' -> listOf(
                    listOf(
                        Offset(0.35f, 0.45f),
                        Offset(0.35f, 0.75f),
                        Offset(0.45f, 0.85f),
                        Offset(0.65f, 0.85f)
                    ), listOf(Offset(0.65f, 0.45f), Offset(0.65f, 0.98f), Offset(0.45f, 0.98f))
                )

                'z' -> listOf(
                    listOf(
                        Offset(0.35f, 0.45f),
                        Offset(0.65f, 0.45f),
                        Offset(0.35f, 0.85f),
                        Offset(0.65f, 0.85f)
                    )
                )

                else -> listOf(listOf(Offset(0.5f, 0.45f), Offset(0.5f, 0.85f)))
            }
        }
        return rawPoints.map { interpolate(it) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlphaGameScreen(
    navController: NavHostController,
    mainViewModel: MainViewModel
){
    val scope = rememberCoroutineScope() // New scope
    var isTransitioning by remember { mutableStateOf(false) } // Transition flag
    var showLottie by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val activity = context as? Activity
    val window = activity?.window

    val timestamps = listOf(
        0L, 11000L, 14000L, 16000L, 18000L, 21000L, 23000L, 24000L, 28000L,
        30000L, 32000L, 33000L, 37000L, 39000L, 41000L, 43000L, 46000L, 48000L,
        51000L, 53000L, 54000L, 57000L, 59000L, 61000L, 65000L, 67000L
    )
    val prefs = remember { context.getSharedPreferences("game_prefs", Context.MODE_PRIVATE) }
    var currentIndex by remember { mutableStateOf(prefs.getInt("last_index", 0)) }
    val savedSeekPos = remember { prefs.getInt("last_seek", 0) }
    var isPlaying by remember { mutableStateOf(false) }
    var selectedGame by remember { mutableStateOf("Sound Play") }
    var currentProgress by remember { mutableStateOf(0f) }
    // Tracing States
    var showAlphaGrid by remember { mutableStateOf(false) }
    var tracingLetter by remember { mutableStateOf('A') }
    var isUppercaseState by remember{ mutableStateOf(true)}
    var tracingColor by remember { mutableStateOf(Color(0xFF4CAF50))}
    val reachedPoints = remember { mutableStateListOf<Offset>() }
    var redrawTrigger by remember { mutableIntStateOf(0) }

    //try these cards animation
    var startSpreading by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(300)
        startSpreading = true
    }

    // MediaPlayer setup
    val mediaPlayer = remember {
        MediaPlayer.create(context, R.raw.abc).apply {
            seekTo(savedSeekPos)
            setOnCompletionListener {
                isPlaying = false
                currentIndex = 0
                seekTo(0)
                prefs.edit().putInt("last_index", 0).apply()
                prefs.edit().putInt("last_seek", 0).apply()
            }
        }
    }
    var showBadgeDialog by remember { mutableStateOf<Int?>(null) }
    LaunchedEffect(Unit) {
        mainViewModel.badgeGameProgress.newlyUnlockedBadge.collect { badgeId ->
            showBadgeDialog = badgeId
        }
    }

    val isLocked by mainViewModel.isLocked.collectAsState()
    LaunchedEffect(isLocked) {
        if (isLocked) {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.stop()
            }
            navController.navigate(NavRoutes.HOME) {
                popUpTo(NavRoutes.HOME) { inclusive = true }
            }
        }
    }

    LaunchedEffect(isPlaying,selectedGame) {
        if (isPlaying && selectedGame == "Sound Play") {
            mainViewModel.setGlobalMusicMuted(true)
            if(!mediaPlayer.isPlaying) mediaPlayer.start()
            delay(100)
            while (isPlaying && mediaPlayer.isPlaying) {
                val currentPos = mediaPlayer.currentPosition.toLong()
                val duration = mediaPlayer.duration.toFloat()
                currentProgress = if(duration>0) currentPos/duration else 0f
                if(currentPos<9000L){
                    // slide each image after 300ms
                    val introIndex = ((currentPos / 350) % AlphabetData.list.size).toInt()
                    currentIndex = introIndex
                }
                else{
                    val newIndex = timestamps.indexOfLast { currentPos >= it }
                    if (newIndex != -1 && newIndex != currentIndex && newIndex < AlphabetData.list.size) {
                        currentIndex = newIndex
                        prefs.edit().putInt("last_index", newIndex).apply()
                    }
                }
                prefs.edit().putInt("last_seek", mediaPlayer.currentPosition).apply()
                delay(200)
            }
        }else{
            if (mediaPlayer.isPlaying) {
                mediaPlayer.pause()
            }
            mainViewModel.setGlobalMusicMuted(false)//here music I will continue this music
        }
    }


    val tts = remember {
        var ttsInstance: TextToSpeech? = null
        ttsInstance = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                ttsInstance?.language = Locale.US
            }
        }
        ttsInstance
    }
    DisposableEffect(Unit) {
        onDispose {
            prefs.edit().putInt("last_seek", mediaPlayer.currentPosition).apply()
            prefs.edit().putInt("last_index", currentIndex).apply()
            mainViewModel.setGlobalMusicMuted(false)//here music will continue to play
            mediaPlayer.release()
            tts.stop()
            tts.shutdown()
        }
    }
    DisposableEffect(selectedGame) {
        if (selectedGame == "Sound Play") {
            window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
        onDispose {
            window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    if (showAlphaGrid) {
        AlphabetSelectorOverlay(
            isUppercase = isUppercaseState,
            onToggle = { isUppercaseState = it },
            onDismiss = { showAlphaGrid = false },
            onSelect = { letter, isUpper ->
                tracingLetter = letter
                isUppercaseState = isUpper
                reachedPoints.clear()
                redrawTrigger++
                showAlphaGrid = false
                tts.speak("Let's trace the Alphabet $letter", TextToSpeech.QUEUE_FLUSH, null, null)
            }
        )
    }

    showBadgeDialog?.let { id ->
        BadgeUnlockedDialog(badgeIndex = id) {
            showBadgeDialog = null
        }
    }
    Box(modifier = Modifier.fillMaxSize()){
        TrackGameBadgeProgress(mainViewModel, BadgeGameType.Alpha)
        CompositionLocalProvider(
            LocalDensity provides Density(
                LocalDensity.current.density,
                fontScale = 1f
            )
        ) {
            Image(
                painter = painterResource(id = R.drawable.alphabetsgamebgnew),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .navigationBarsPadding(),
                contentScale = ContentScale.FillBounds
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .verticalScroll(rememberScrollState())
            ) {
                // Top Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.backarrowgame),
                        contentDescription = null,
                        modifier = Modifier
                            .size(16.dp)
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ) { navController.navigateUp() })
                    Text(
                        text = if (selectedGame == "Sound Play") "ABC Sound Play" else "ABC $selectedGame",
                        fontSize = 22.sp,
                        fontFamily = FontFamily(Font(R.font.balootwomediam)),
                        color = Color.Black,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }

                // White Main Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, start = 16.dp, end = 16.dp, bottom = 12.dp)
                        .height(450.dp)
                ) {

                    Crossfade(
                        targetState = selectedGame,
                        animationSpec = tween(durationMillis = 500),
                        label = "GameTransition"
                    ) { targetGame ->

                        if (targetGame != "Pop") {

                            Card(
                                modifier = Modifier.fillMaxSize(),
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Box(modifier = Modifier.fillMaxSize()) {
                                    Canvas(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(12.dp)
                                    ) {
                                        drawRoundRect(
                                            color = Color(0xFFFCDF93),
                                            cornerRadius = CornerRadius(15.dp.toPx()),
                                            style = Stroke(
                                                width = 3.dp.toPx(),
                                                pathEffect = PathEffect.dashPathEffect(
                                                    floatArrayOf(
                                                        10f,
                                                        10f
                                                    ), 0f
                                                )
                                            )
                                        )
                                    }

                                    if (targetGame == "Sound Play") {
                                        val currentItem = AlphabetData.list[currentIndex]
                                        Column(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .animateContentSize()
                                                .padding(
                                                    top = 8.dp,
                                                    bottom = 32.dp,
                                                    start = 24.dp,
                                                    end = 24.dp
                                                ),
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .offset(y = (-8).dp),
                                                horizontalArrangement = Arrangement.Start,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = currentItem.letter.toString(),
                                                    fontSize = 100.sp,
                                                    fontFamily = FontFamily(Font(R.font.balootwomediam)),
                                                    color = Color(0xFFFFC830)
                                                )
                                                Text(
                                                    text = currentItem.word,
                                                    fontSize = 40.sp,
                                                    maxLines = 2,
                                                    overflow = TextOverflow.Ellipsis,
                                                    fontFamily = FontFamily(Font(R.font.balootwomediam)),
                                                    color = Color(0xFFFFC830),
                                                    modifier = Modifier.padding(
                                                        start = 16.dp,
                                                        top = 16.dp
                                                    )
                                                )
                                            }
                                            Image(
                                                painter = painterResource(id = currentItem.imageRes),
                                                contentDescription = null,
                                                modifier = Modifier
                                                    .offset(y = (-18).dp)
                                                    .size(300.dp)
                                                    .weight(1f),
                                                contentScale = ContentScale.Fit
                                            )
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(100.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                androidx.compose.animation.AnimatedVisibility(
                                                    visible = isPlaying,
                                                    enter = fadeIn() + expandVertically(),
                                                    exit = fadeOut() + shrinkVertically()
                                                ) {
                                                    Row(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .padding(bottom = 20.dp),
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.spacedBy(
                                                            10.dp
                                                        )
                                                    ) {

                                                        // Left: Small Pause Icon
                                                        Image(
                                                            painter = painterResource(id = R.drawable.smallpauseicon),
                                                            contentDescription = "pause",
                                                            modifier = Modifier
//                                                                .size(25.dp)
                                                                .clickable(
                                                                    indication = null,
                                                                    interactionSource = remember { MutableInteractionSource() },
                                                                ) { isPlaying = false }
                                                        )
                                                        // Center: Custom Progress Bar
                                                        Slider(
                                                            value = currentProgress,onValueChange = { newValue ->
                                                                currentProgress = newValue
                                                            },
                                                            onValueChangeFinished = {
                                                                val seekToPosition = (currentProgress * mediaPlayer.duration).toInt()
                                                                mediaPlayer.seekTo(seekToPosition)
                                                            },
                                                            modifier = Modifier
                                                                .weight(1f)
                                                                .height(20.dp),
                                                            track = { sliderState ->
                                                                val fraction = (currentProgress - 0f) / (1f - 0f) // Progress percentage

                                                                Box(
                                                                    modifier = Modifier
                                                                        .fillMaxWidth()
                                                                        .height(10.dp)
                                                                        .background(
                                                                            Color(0xFFE0E0E0),
                                                                            RoundedCornerShape(5.dp)
                                                                        ),
                                                                    contentAlignment = Alignment.CenterStart
                                                                ) {
                                                                    Box(
                                                                        modifier = Modifier
                                                                            .fillMaxWidth(fraction)
                                                                            .fillMaxHeight()
                                                                            .background(
                                                                                Color(
                                                                                    0xFFFB923C
                                                                                ),
                                                                                RoundedCornerShape(5.dp)
                                                                            ) // Active bar
                                                                    )
                                                                }
                                                            },
                                                            thumb = {
                                                                Box(
                                                                    Modifier
                                                                        .size(
                                                                            width = 2.dp,
                                                                            height = 0.dp
                                                                        )
                                                                        .background(Color(0xFFFB923C))
                                                                )
                                                            }
                                                        )
                                                        // Right: Media Reset Icon
                                                        Image(
                                                            painter = painterResource(id = R.drawable.mediareseticon),
                                                            contentDescription = "reset",
                                                            modifier = Modifier
//                                                                .size(25.dp)
                                                                .clickable(
                                                                    indication = null,
                                                                    interactionSource = remember { MutableInteractionSource() },
                                                                ) {
                                                                    mediaPlayer.seekTo(0)
                                                                    currentProgress = 0f
                                                                    currentIndex = 0
                                                                }
                                                        )

                                                    }
                                                }
                                                androidx.compose.animation.AnimatedVisibility(
                                                    visible = !isPlaying,
                                                    enter = fadeIn(animationSpec = tween(500)),
                                                    exit = fadeOut(animationSpec = tween(500))
                                                ) {
                                                    Card(
                                                        shape = RoundedCornerShape(50.dp),
                                                        elevation = CardDefaults.cardElevation(
                                                            defaultElevation = 8.dp
                                                        ),
                                                        modifier = Modifier.size(70.dp)
                                                    ) {
                                                        Box(
                                                            modifier = Modifier
                                                                .fillMaxSize()
                                                                .background(
                                                                    brush = Brush.verticalGradient(
                                                                        colors = listOf(
                                                                            Color(0xFF915423),
                                                                            colorResource(id = R.color.hometoptextcolor),
                                                                            colorResource(id = R.color.hometoptextcolor)
                                                                        ),
                                                                        startY = 0f, endY = 60f
                                                                    )
                                                                )
                                                                .border(
                                                                    width = 1.dp,
                                                                    brush = Brush.verticalGradient(
                                                                        listOf(
                                                                            Color.Transparent,
                                                                            Color.White.copy(alpha = 0.3f)
                                                                        )
                                                                    ),
                                                                    shape = RoundedCornerShape(50.dp)
                                                                )
                                                                .clickable(
                                                                    indication = null,
                                                                    interactionSource = remember { MutableInteractionSource() }) {
                                                                    isPlaying = !isPlaying
                                                                },
                                                            contentAlignment = Alignment.Center
                                                        ) {
                                                            Image(
                                                                painter = if (isPlaying) painterResource(
                                                                    id = R.drawable.pauseicon
                                                                ) else painterResource(
                                                                    id = R.drawable.playicon
                                                                ),
                                                                contentDescription = "bigPlay",
                                                                modifier = Modifier
                                                                    .size(40.dp)
                                                                    .offset(x = if (isPlaying) 0.dp else 5.dp)
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    else if (targetGame == "Tracing") {
                                        TracingGameUI(
                                            isUpper = isUppercaseState,
                                            letter = tracingLetter,//AlphabetData.list[currentIndex].letter
                                            color = tracingColor,
                                            reachedPoints = reachedPoints,
                                            trigger = redrawTrigger,
                                            showLottie = showLottie,
                                            onUpdate = {
                                                if (isTransitioning) return@TracingGameUI
                                                val allPoints = AlphabetDataf.getPoints(
                                                    tracingLetter,
                                                    isUppercaseState
                                                ).flatten()
                                                val totalPoints = allPoints.size

                                                if (totalPoints > 0) {
                                                    //changes 1
                                                    val currentReachedUnique =
                                                        reachedPoints.distinct().size
                                                    val progress =
                                                        currentReachedUnique.toFloat() / totalPoints

                                                    val lastPoint = allPoints.lastOrNull()
                                                    val isAtEnd =
                                                        lastPoint != null && reachedPoints.contains(
                                                            lastPoint
                                                        )

                                                    if (progress >= 1.0f || (progress >= 0.93f && isAtEnd)) {
                                                        isTransitioning = true
                                                        showLottie = true

                                                        scope.launch {
                                                            delay(2000)
                                                            val nextChar = if (isUppercaseState) {
                                                                if (tracingLetter < 'Z') tracingLetter + 1 else 'A'
                                                            } else {
                                                                if (tracingLetter < 'z') tracingLetter + 1 else 'a'
                                                            }

                                                            reachedPoints.clear()
                                                            tracingLetter = nextChar
                                                            tts?.speak("Let's trace the Alphabet $nextChar", TextToSpeech.QUEUE_FLUSH, null, null)
                                                            showLottie = false
                                                            redrawTrigger++
                                                            isTransitioning = false
                                                        }
                                                    } else {
                                                        redrawTrigger++

                                                    }
                                                }
                                            },
                                            onGrid = { showAlphaGrid = true },
                                            onColor = { tracingColor = it }
                                        )
                                    }
                                    else if (targetGame == "Matching") {
                                        AlphabetMatchingUI()
                                    }
                                }
                            }
                        }
                        else {
                            AlphabetPopUI()
                        }
                    }
                }

                Text(
                    text = "Try these",
                    fontSize = 16.sp,
                    fontFamily = FontFamily(Font(R.font.balootwomediam)),
                    color = Color.Black,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
                val games = listOf(
                    GameItem("Tracing", R.drawable.tracinggame),
                    GameItem("Pop", R.drawable.popupgame),
                    GameItem("Matching", R.drawable.matchinggame),
                    GameItem("Sound Play", R.drawable.soundplaygame)
                )
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(games.filter { it.name != selectedGame }) { index, game ->
                        AnimatedVisibility(
                            visible = startSpreading,
                            enter = slideInHorizontally(
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioLowBouncy,
                                    stiffness = Spring.StiffnessLow
                                ),
                                initialOffsetX = { -500 }
                            ) + fadeIn(animationSpec = tween(durationMillis = 500 + (index * 100)))
                                    + expandHorizontally(expandFrom = Alignment.Start),
                            modifier = Modifier.padding(start = if(index == 0) 8.dp else 0.dp)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .width(100.dp)
                                    .clickable(
                                        indication = null,
                                        interactionSource = remember { MutableInteractionSource() }) {
                                        selectedGame = game.name
                                        if (game.name == "Sound Play") {
                                            isPlaying = false
                                        } else {
                                            isPlaying = true
                                        }
                                        if (game.name == "Tracing") showAlphaGrid = true
                                    }) {
                                Image(
                                    painter = painterResource(id = game.image),
                                    contentDescription = null,
                                    modifier = Modifier.size(80.dp),
                                    contentScale = ContentScale.Crop
                                )
                                Text(
                                    text = game.name,
                                    fontSize = 14.sp,
                                    fontFamily = FontFamily(Font(R.font.balootworegular)),
                                    color = Color(0xFF464646),
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}


// Pop Ballon Game UI
@Composable
fun AlphabetPopUI() {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    var targetLetter by remember { mutableStateOf(('A'..'Z').random().toString()) }
    var popCount by remember { mutableIntStateOf(0) }
    val maxTarget = 7
    var showSuccessLottie by remember { mutableStateOf(false) }

    // Sound effect setup
    val popSound = remember { MediaPlayer.create(context, R.raw.popballonaudio) } // Ensure pop_sound.mp3 is in res/raw
    val successSound = remember { MediaPlayer.create(context, R.raw.successsound) }
    val burstEffects = remember { mutableStateListOf<BurstState>() } // New State
    var showHandGuide by remember { mutableStateOf(true) }
    var isTutorialActive by remember { mutableStateOf(true) }


    // Active balloons ki list
    val activeBalloons = remember { mutableStateListOf<PopBalloonState>() }
    val scope = rememberCoroutineScope()

    // In states ke niche add karein
    val ringScale by animateFloatAsState(
        targetValue = if (showSuccessLottie) 1.3f else 1f,
        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
        label = "RingScale"
    )

    val glowAlpha by animateFloatAsState(
        targetValue = if (showSuccessLottie) 0.8f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "GlowAlpha"
    )

    //hand animation
    val infiniteTransition = rememberInfiniteTransition(label = "HandAnim")
    val handOffset by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 20f,
        animationSpec = infiniteRepeatable(tween(600, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = ""
    )

    //TTS ENGINE

    val tts = remember {
        var ttsInstance: TextToSpeech? = null
        ttsInstance = TextToSpeech(context){status->
            if(status == TextToSpeech.SUCCESS){
                ttsInstance?.language = Locale.US
                //when game get start then it will guide
                ttsInstance?.speak("Pop Letter $targetLetter", TextToSpeech.QUEUE_FLUSH,null,null)
            }
        }
        ttsInstance
    }


    LaunchedEffect(targetLetter, showSuccessLottie, isTutorialActive) {

        if (isTutorialActive) return@LaunchedEffect
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            while (true) {//true
                if (!showSuccessLottie) {
                    val randomChar =
                        if ((0..10).random() > 7) targetLetter else ('A'..'Z').random().toString()
                    val randomX = 0.05f + (Math.random().toFloat() * (0.85f - 0.05f))
                    val newBalloon = PopBalloonState(
                        letter = randomChar,
                        color = listOf(
                            Color(0xFFFFB322),
                            Color(0xFFEF711F),
                            Color(0xFF0050C8),
                            Color(0xFFEA3070),
                            Color(0xFF67419F)
                        ).random(),
                        initialX = randomX,
                    )
                    activeBalloons.add(newBalloon)
                    scope.launch {
                        newBalloon.currentY.animateTo(
                            targetValue = -0.2f,
                            animationSpec = tween(durationMillis = (5000), easing = LinearEasing)
                        )
                        activeBalloons.remove(newBalloon)
                    }
                }
                delay(900)
            }
        }
    }

    // Clean up media players
    DisposableEffect(Unit) {
        onDispose {
            popSound.release()
            successSound.release()
            tts?.stop()
            tts?.shutdown()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        /*Counter Row*/
        Row(
            modifier = Modifier
                .offset(y = (-10).dp)
                .align(Alignment.TopStart)
                .graphicsLayer(
                    scaleX = ringScale, scaleY = ringScale,
                    transformOrigin = TransformOrigin(0f, 0f)
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.zIndex(2f)
            ) {
                // Progress Ring
                Canvas(modifier = Modifier.size(65.dp)) {
                    // 1. OUTER GLOW EFFECT (you can see on success)
                    if (showSuccessLottie) {
                        drawCircle(
                            color = Color(0xFF1F9856).copy(alpha = glowAlpha),
                            radius = (size.minDimension / 2) + 2.dp.toPx(),
                            style = Stroke(width = 15.dp.toPx())
                        )
                    }
                    // White Background Ring
                    drawCircle(
                        color = Color.White,
                        radius = size.minDimension / 2
                    )
                    drawArc(
                        color = Color.White,
                        startAngle = 0f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(width = 5.dp.toPx())
                    )
                    drawArc(
                        color = Color(0xFF1F9856),
                        startAngle = -90f,
                        sweepAngle = (popCount.toFloat() / maxTarget.coerceAtLeast(1)) * 360f,
                        useCenter = false,
                        style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round)
                    )
                }

                // Inner Orange Circle
                Card(
                    shape = CircleShape,
                    modifier = Modifier
                        .size(50.dp)
                        .fillMaxSize(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFB322)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = targetLetter,
                            fontSize = 24.sp,
                            fontFamily = FontFamily(Font(R.font.rubkioneregular)),
                            color = Color.White ,modifier = Modifier.offset(y = (2).dp)
                        )
                        Text(
                            text = "($popCount/$maxTarget)",
                            fontSize = 12.sp,
                            fontFamily = FontFamily(Font(R.font.balootworegular)),
                            color = Color.Black,
                            modifier = Modifier.offset(y = (-2).dp)
                        )
                    }
                }
            }
            Surface(
                color = Color(0xFF0050C8),
                shape = RoundedCornerShape(topEnd = 20.dp, bottomEnd = 20.dp),
                modifier = Modifier
                    .height(38.dp)
                    .offset(x = (-20).dp)
                    .zIndex(1f)
            ) {
                Box(
                    modifier = Modifier.padding(start = 28.dp, end = 16.dp),
                    contentAlignment = Alignment.Center
                ){
                    Text(
                        text = "Pop Letter $targetLetter",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontFamily = FontFamily(Font(R.font.balootwobold))
                    )
                }
            }
        }

        // --- FLOATING BALLOONS ---
        BoxWithConstraints(modifier = Modifier.fillMaxSize()
        ) {
            val width = constraints.maxWidth
            val height = constraints.maxHeight

            //Tutorial Guide
            if(isTutorialActive){
                val centerX = width / 2f - 110f // Center position
                val centerY = height / 2f - 110f
                Box(
                    modifier = Modifier
                        .offset { IntOffset(centerX.toInt(), centerY.toInt()) }
                        .size(120.dp)
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) {
                            // Tutorial khatam
                            isTutorialActive = false
                            popSound.start()
                            tts?.speak(targetLetter, TextToSpeech.QUEUE_FLUSH, null, null)
                            burstEffects.add(BurstState(x = centerX, y = centerY, isTarget = true))
                            popCount++
                        },
                    contentAlignment = Alignment.Center
                ) {
                    // Balloon Glow Effect
                    val infiniteGlow = rememberInfiniteTransition()
                    val scale by infiniteGlow.animateFloat(
                        initialValue = 1f, targetValue = 1.2f,
                        animationSpec = infiniteRepeatable(tween(800), RepeatMode.Reverse)
                    )

                    Image(
                        painter = painterResource(id = R.drawable.ballona),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer(scaleX = scale, scaleY = scale),
                        colorFilter = ColorFilter.tint(Color(0xFFFFB322))
                    )
                    Text(targetLetter, fontSize = 40.sp, color = Color.White, fontFamily = FontFamily(Font(R.font.rubkioneregular)))

                    // Hand Guide
                    Image(
                        painter = painterResource(id = R.drawable.guidehandimage),
                        contentDescription = null,
                        modifier = Modifier
                            .size(60.dp)
                            .offset(y = 60.dp + handOffset.dp, x = 30.dp)
                    )

                }
            }

            if(!isTutorialActive) {
                activeBalloons.forEach { balloon ->
                    val xPos = balloon.initialX * width
                    val yPos = balloon.currentY.value * height
                    val isTarget = balloon.letter == targetLetter

                    Box(
                        modifier = Modifier
                            .offset { IntOffset(xPos.toInt(), yPos.toInt()) }
                            .size(80.dp)
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ) {
                                // 1. Play Pop Sound (Har click par)
                                if (popSound.isPlaying) {
                                    popSound.pause()
                                    popSound.seekTo(0)
                                }
                                popSound.start()
                                val isTargetBalloon = balloon.letter == targetLetter

                                // 2. Add Brust Effect (New Logic)

                                val effect =
                                    BurstState(x = xPos, y = yPos, isTarget = isTargetBalloon)
                                burstEffects.add(effect)

                                // 3. Check if it's the target letter

                                if (isTargetBalloon) {
                                    showHandGuide = false
                                    popCount++

                                    //TTS sound of letter
                                    tts?.speak(targetLetter, TextToSpeech.QUEUE_FLUSH, null, null)

                                    if (popCount >= maxTarget) {
                                        scope.launch {
                                            successSound.start()
                                            showSuccessLottie = true
                                            delay(2500)
                                            showSuccessLottie = false
                                            popCount = 0
                                            val nextLetter = ('A'..'Z').random().toString()
                                            targetLetter = nextLetter
                                            delay(500)
                                            tts?.speak(
                                                "Now Pop Letter $nextLetter",
                                                TextToSpeech.QUEUE_FLUSH,
                                                null,
                                                null
                                            )
                                        }
                                    }
                                }
                                activeBalloons.remove(balloon)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ballona),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            colorFilter = ColorFilter.tint(balloon.color)
                        )
                        Text(
                            balloon.letter,
                            color = Color.White,
                            fontSize = 28.sp,
                            fontFamily = FontFamily(Font(R.font.rubkioneregular)),
                            modifier = Modifier.offset(y = (-5).dp)
                        )
                    }
                }
            }
            // --- BURST LOTTIE OVERLAY ---
            burstEffects.forEach { burst ->
                key(burst.id) {
                    val animationSize = 190.dp
                    Box(
                        modifier = Modifier
                            .offset { IntOffset(burst.x.toInt() - 80, burst.y.toInt() - 80) }
                            .size(animationSize)
                            .zIndex(5f)
                    ) {
                        val lottieRes = if(burst.isTarget){
                            R.raw.popperlottienew
                        }else{
                            R.raw.popperlottie
                        }

                        val burstComposition by rememberLottieComposition(LottieCompositionSpec.RawRes(lottieRes))
                        val progress = animateLottieCompositionAsState(
                            composition = burstComposition,
                            iterations = 1
                        )
                        LottieAnimation(
                            composition = burstComposition,
                            modifier = Modifier.fillMaxSize(),
                            progress = { progress.value },
                            contentScale = ContentScale.FillBounds
                        )
                        if (progress.value >= 1.0f) {
                            SideEffect { burstEffects.remove(burst) }
                        }
                    }
                }
            }
        }
    }
    // Success Lottie Overlay
    if (showSuccessLottie) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(10f),
            contentAlignment = Alignment.Center
        ) {
            val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.tracinglottie))
            LottieAnimation(
                composition = composition,
                iterations = 1,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(500.dp),
                contentScale = ContentScale.Fit
            )
        }
    }
}
// Tracing Game UI
@Composable
fun TracingGameUI(
    isUpper: Boolean,
    letter: Char,
    color: Color,
    reachedPoints: MutableList<Offset>,
    trigger: Int,
    showLottie: Boolean,
    onUpdate: () -> Unit,
    onGrid: () -> Unit,
    onColor: (Color) -> Unit
) {
    val context = LocalContext.current
    val guideSegments = remember(letter,isUpper) { AlphabetDataf.getPoints(letter,isUpper) }
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.tracinglottie))

    val completionSound = remember(letter, isUpper) {
        MediaPlayer.create(context, R.raw.tracingonesidesound)
    }


    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "pulseScale"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(600),
            repeatMode = RepeatMode.Reverse
        ), label = "pulseAlpha"
    )
    LaunchedEffect(showLottie) {
        if (showLottie) {
            try {
                if (completionSound.isPlaying) {
                    completionSound.stop()
                    completionSound.prepare()
                }
                completionSound.seekTo(0)
                completionSound.start()
            }catch (e: Exception) {
                completionSound.start()
            }
        }
    }

    DisposableEffect(letter, isUpper) {
        onDispose {
            completionSound.stop()
            completionSound.release()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            IconButton(
                onClick = onGrid, modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(10.dp)
                    .zIndex(1f)
                    .size(30.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.dialpad),
                    contentDescription = "grid_button",modifier = Modifier
                        .fillMaxSize()
                        .size(25.dp)
                )
            }
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 40.dp)//20dp
                    .pointerInput(letter, isUpper) {
                        if (showLottie) return@pointerInput///here is a bug
                        detectDragGestures { change, _ ->
                            change.consume()
                            guideSegments.forEach { segment ->
                                for (i in segment.indices) {
                                    val pt = segment[i]
                                    val isFirstPoint = i == 0
                                    val isPreviousPointReached =
                                        if (!isFirstPoint) reachedPoints.contains(segment[i - 1]) else true
                                    // Segment loop ke andar...
                                    val segmentIndex = guideSegments.indexOf(segment)
                                    val isPreviousSegmentFinished =
                                        if (segmentIndex > 0) reachedPoints.contains(guideSegments[segmentIndex - 1].last()) else true
                                    if (isPreviousPointReached && isPreviousSegmentFinished) {
                                        val targetX = pt.x * size.width
                                        val targetY = pt.y * size.height

                                        if (change.position.distanceTo(
                                                Offset(
                                                    targetX,
                                                    targetY
                                                )
                                            ) < 80f
                                        ) {
                                            if (!reachedPoints.contains(pt)) {
                                                reachedPoints.add(pt)
                                                onUpdate()
                                            }
                                        }
                                    }
                                }
                            }
                        }


                    }
            ) {
                val t = trigger;
                val w = size.width;
                val h = size.height
                val path = Path()

                /*Layer 1*/
                guideSegments.forEach { seg ->
                    seg.forEachIndexed { i, pt ->
                        if (i == 0) path.moveTo(
                            pt.x * w,
                            pt.y * h
                        ) else path.lineTo(pt.x * w, pt.y * h)
                    }
                }
                drawPath(
                    path,
                    Color(0xFFF5B64E),
                    style = Stroke(100f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                )
                drawPath(
                    path,
                    Color.White,
                    style = Stroke(75f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                )
                drawPath(
                    path,
                    Color(0xFFFDEBB7),
                    style = Stroke(
                        10f,
                        cap = StrokeCap.Round,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 30f))
                    )
                )

                /*Layer 2*/
                guideSegments.forEach { seg ->
                    for (i in 0 until seg.size - 1) {
                        if (reachedPoints.contains(seg[i]) && reachedPoints.contains(seg[i + 1])) {
                            drawLine(
                                color,
                                Offset(seg[i].x * w, seg[i].y * h),
                                Offset(seg[i + 1].x * w, seg[i + 1].y * h),
                                80f,
                                cap = StrokeCap.Round
                            )
                        }
                    }
                }

                /*Layer 3*/
                guideSegments.forEachIndexed { index, seg ->
                    // Logic:
                    val isPreviousSegmentDone = if (index > 0) reachedPoints.contains(guideSegments[index - 1].last()) else true
                    val isCurrentSegmentDone = reachedPoints.contains(seg.last())

                    if (isPreviousSegmentDone && !isCurrentSegmentDone && seg.size >= 2) {
                        // Find the start of current segment
                        val startPoint = Offset(seg[0].x * w, seg[0].y * h)
                        val nextPoint = Offset(seg[1].x * w, seg[1].y * h)

                        // Pass the animation values here
                        drawStaticGuide(
                            start = startPoint,
                            next = nextPoint,
                            color = color,
                            scale = pulseScale,
                            alpha = pulseAlpha
                        )
                    }
                }
            }


            // Lottie Overlay
            if (showLottie) {
                LottieAnimation(
                    composition = composition,
                    iterations = LottieConstants.IterateForever,
                    modifier = Modifier
                        .fillMaxSize()
                        .scale(1.5f)
                        .zIndex(3f)
                )
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(bottom = 10.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            listOf(Color(0xFF4CAF50), Color(0xFF2196F3), Color(0xFFE52B3C)).forEach { c ->
                val isSelected = color == c
                // Bounce aur Offset animation
                val offsetBy by animateDpAsState(
                    targetValue = if (isSelected) (0).dp else 0.dp,//here y is the value
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    ),
                    label = "ColorBounce"
                )
                val scale by animateFloatAsState(
                    targetValue = if (isSelected) 1.2f else 1f,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                    label = "ColorScale"
                )
                Box(modifier = Modifier
                    .offset(y = offsetBy)
                    .graphicsLayer(scaleX = scale, scaleY = scale)
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(c)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) { onColor(c) })
            }
        }
    }
}


// Matching game
@Composable
fun AlphabetMatchingUI() {
    val context = LocalContext.current
    var currentLevel by remember { mutableStateOf(0) }
    var showLevelLottie by remember { mutableStateOf(false) }
    var isTutorialActive by remember { mutableStateOf(true) } // Start mein true
    // --- SETUP COMPLETION SOUND ---
    val completionSound = remember {
        MediaPlayer.create(context, R.raw.tracingcompletionsound)
    }


    // generate 3 letters for each level
    val currentLetters = remember(currentLevel) {
        val startChar = 'A' + (currentLevel * 3)
        listOf(
            (startChar).toString(),
            (startChar + 1).toString(),
            (startChar + 2).toString()
        ).filter { it[0] <= 'Z' }
    }

    val leftBalloons = remember(currentLetters) {
        currentLetters.mapIndexed { index, char ->
            val fixedColor = when(index) {
                0 -> Color(0xFF0050C8)
                1 -> Color(0xFFEF721F)
                else -> Color(0xFF67419F)
            }
            BalloonItem(char, char, fixedColor)
        }
    }
    val rightBalloons = remember(currentLetters) {
        if (currentLetters.isEmpty()) return@remember emptyList<BalloonItem>()

        currentLetters.mapIndexed { index, char ->
            val balloonColor = when(index) {
                0 -> Color(0xFF0050C8)
                1 -> Color(0xFFFFB322)
                else -> Color(0xFFEA3071)
            }

            BalloonItem(
                id = char,
                label = char.lowercase(),
                color = balloonColor
            )
        }.shuffled()
    }

    // Hand movement animation (Left se Right)
    val infiniteTransition = rememberInfiniteTransition(label = "Tutorial")
    val tutorialProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "HandMove"
    )

    val balloonPositions = remember { mutableStateMapOf<String, Offset>() }
    val completedMatches = remember { mutableStateMapOf<String, Pair<Offset, Offset>>() }
    var dragStartPoint by remember { mutableStateOf<Offset?>(null) }
    var dragEndPoint by remember { mutableStateOf<Offset?>(null) }
    var activeStartId by remember { mutableStateOf<String?>(null) }
    var boxCoordinates by remember { mutableStateOf<LayoutCoordinates?>(null) }
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.tracinglottie))

    // Match Completion Logic
    LaunchedEffect(completedMatches.size) {
        if (completedMatches.size == leftBalloons.size && leftBalloons.isNotEmpty()) {
            // --- PLAY SOUND HERE ---
            try {
                if (completionSound.isPlaying) {
                    completionSound.stop()
                    completionSound.prepare()
                }
                completionSound.seekTo(0)
                completionSound.start()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            showLevelLottie = true
            delay(2500)
            showLevelLottie = false

            completedMatches.clear()
            balloonPositions.clear()

            if ('A' + ((currentLevel+1) * 3) > 'Z') {//BODMAS
                currentLevel = 0
            }else{
                currentLevel++
            }
        }
    }
    val tts = remember {
        var ttsInstance: TextToSpeech? = null
        ttsInstance = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                ttsInstance?.language = Locale.US
                ttsInstance?.speak("Match the big letters with the small letters!", TextToSpeech.QUEUE_FLUSH, null, null)
            }
        }
        ttsInstance
    }

    // Sound release logic
    DisposableEffect(Unit) {
        onDispose {
            completionSound.release()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .onGloballyPositioned { boxCoordinates = it }
            .pointerInput(currentLevel) {
                detectDragGestures(
                    onDragStart = { offset ->
                        leftBalloons.forEach { balloon ->
                            val pos = balloonPositions["left_${balloon.id}"]
                            if (pos != null && (offset - pos).getDistance() < 100f) {
                                if (!completedMatches.containsKey(balloon.id)) {
                                    dragStartPoint = pos
                                    activeStartId = balloon.id
                                }
                            }
                        }
                    },
                    onDrag = { change, _ ->
                        change.consume()
                        if (activeStartId != null) dragEndPoint = change.position
                    },
                    onDragEnd = {
                        if (activeStartId != null && dragEndPoint != null) {
                            rightBalloons.forEach { balloon ->
                                val targetPos = balloonPositions["right_${balloon.id}"]
                                if (balloon.id == activeStartId && targetPos != null) {
                                    if ((dragEndPoint!! - targetPos).getDistance() < 120f) {
                                        completedMatches[activeStartId!!] =
                                            Pair(dragStartPoint!!, targetPos)

                                        // AGAR PEHLA MATCH HO GAYA TO TUTORIAL KHATAM
                                        isTutorialActive = false
                                    }
                                }
                            }
                        }
                        dragStartPoint = null
                        dragEndPoint = null
                        activeStartId = null
                    }
                )
            }
    ) {
        // Lines Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            completedMatches.values.forEach { (start, end) ->
                drawLine(Color(0xFFED1C2A), start, end, strokeWidth = 8f, cap = StrokeCap.Round)
            }
            if (dragStartPoint != null && dragEndPoint != null) {
                drawLine(Color.Gray.copy(0.6f), dragStartPoint!!, dragEndPoint!!, strokeWidth = 6f, cap = StrokeCap.Round, pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f)))
            }
        }

        // Layout for Balloons
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left Column
            Column(modifier = Modifier.fillMaxHeight(), verticalArrangement = Arrangement.SpaceEvenly) {
                leftBalloons.forEachIndexed { index, item ->
                    key(item.id+currentLevel) {
                        MatchingBalloon(item,index) { coords ->
                            boxCoordinates?.let { root ->
                                balloonPositions["left_${item.id}"] = root.localPositionOf(coords, Offset(coords.size.width / 2f, coords.size.height / 2f))
                            }
                        }
                    }
                }
            }
            // Right Column
            Column(modifier = Modifier.fillMaxHeight(), verticalArrangement = Arrangement.SpaceEvenly) {
                rightBalloons.forEachIndexed { index,item ->
                    key(item.id+currentLevel) {
                        MatchingBalloon(item,index) { coords ->
                            boxCoordinates?.let { root ->
                                balloonPositions["right_${item.id}"] = root.localPositionOf(coords, Offset(coords.size.width / 2f, coords.size.height / 2f))
                            }
                        }
                    }
                }
            }
        }

// 3. Lottie Animation Overlay
        if (showLevelLottie) {
            LottieAnimation(
                composition = composition,
                iterations = 1,
                modifier = Modifier
                    .fillMaxSize()
                    .scale(1.8f)
                    .zIndex(10f)
            )
        }

        // --- TUTORIAL HAND OVERLAY ---
        if (isTutorialActive && leftBalloons.isNotEmpty() && rightBalloons.isNotEmpty()) {
            val firstId = leftBalloons[0].id
            val startPos = balloonPositions["left_$firstId"]
            val endPos = balloonPositions["right_$firstId"]

            if (startPos != null && endPos != null) {
                val currentX = startPos.x + (endPos.x - startPos.x) * tutorialProgress
                val currentY = startPos.y + (endPos.y - startPos.y) * tutorialProgress
                Image(
                    painter = painterResource(id = R.drawable.guidehandimage),
                    contentDescription = null,
                    modifier = Modifier
                        .size(60.dp)
                        .offset { IntOffset(currentX.toInt(), currentY.toInt()) }
                        .zIndex(15f)
                )
            }
        }
    }
}
@Composable
fun MatchingBalloon(
    item: BalloonItem,
    index: Int,
    onLoaded: (LayoutCoordinates) -> Unit
) {

    // 1. Entrance state
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(item.id) {
        delay(index * 100L)
        isVisible = true
    }
    // 2. Floating (Idle)
    val infiniteTransition = rememberInfiniteTransition(label = "floating")
    val floatingOffset by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "yOffset"
    )
    AnimatedVisibility(
        visible = isVisible,
        enter = scaleIn(initialScale = 0.5f) + fadeIn() + slideInVertically { it / 2 },
        modifier = Modifier.onGloballyPositioned { onLoaded(it) }
    ) {
        Box(
            modifier = Modifier
                .size(85.dp)
                .onGloballyPositioned { onLoaded(it) },
            contentAlignment = Alignment.Center
        ) {

            Image(
                painter = painterResource(id = R.drawable.ballona),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                colorFilter = ColorFilter.tint(item.color)
            )
            Text(
                text = item.label,
                color = Color.White,
                fontSize = 32.sp,
                fontFamily = FontFamily(Font(R.font.rubkioneregular)),
                modifier = Modifier.offset(y = (-5).dp)
            )
        }
    }
}


@Composable
fun AlphabetSelectorOverlay(
    isUppercase: Boolean,
    onToggle: (Boolean) -> Unit,
    onDismiss: () -> Unit,
    onSelect: (Char, Boolean) -> Unit
) {
    val letters = if (isUppercase) ('A'..'Z').toList() else ('a'..'z').toList()
    val context = LocalContext.current
    val offsetX by animateDpAsState(
        targetValue = if (isUppercase) 0.dp else 100.dp, // Adjust 100.dp based on your button width
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "ToggleAnimation"
    )
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFE0DFDF)),
            contentAlignment = Alignment.Center
        ) {
            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(20.dp)//add1
                    .size(30.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.crossicon),
                    contentDescription = null
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.9f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center

            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(30.dp))
                        .background(Color.Transparent) // Main toggle container
                        .padding(4.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Box(
                        modifier = Modifier
                            .offset(x = offsetX)
                            .shadow(8.dp, RoundedCornerShape(20.dp))
                            .background(Color.White, RoundedCornerShape(20.dp))
                            .padding(horizontal = 24.dp), // Width matching text padding
                        contentAlignment = Alignment.Center
                    ) {
                        // Invisible text to maintain exact width of the white box
                        Text(
                            text = if (isUppercase) "ABC" else "abc",
                            fontFamily = FontFamily(Font(R.font.balootworegular)),
                            fontSize = 29.sp,
                            color = Color.Transparent
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {

                        // "ABC" Toggle Button
                        Box(
                            modifier = Modifier
                                .clickable(
                                    indication = null,
                                    interactionSource = remember { MutableInteractionSource() }
                                ) {
                                    onToggle(true)
                                }
                                .padding(horizontal = 24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "ABC",
                                fontFamily = FontFamily(Font(R.font.balootworegular)),
                                fontSize = 29.sp,
                                color = Color.Black
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clickable(
                                    indication = null,
                                    interactionSource = remember { MutableInteractionSource() })
                                {
                                    onToggle(false)
                                }
                                .padding(horizontal = 24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "abc",
                                fontFamily = FontFamily(Font(R.font.balootworegular)),
                                fontSize = 29.sp,
                                color = Color.Black
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.64f)
                        .shadow(10.dp, RoundedCornerShape(28.dp))
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFFC5C5C5),
                                    colorResource(id = R.color.white),
                                    colorResource(id = R.color.white)
                                ),
                                startY = 0f, endY = 40f
                            ),
                            RoundedCornerShape(28.dp)
                        )
                        .drawBehind {
                            val stripHeight = 5.dp.toPx()
                            val cornerRadiusPx = 28.dp.toPx()
                            drawRoundRect(
                                color = Color(0xFFFDEBB7),
                                topLeft = Offset(0f, size.height - stripHeight),
                                size = Size(size.width, stripHeight),
                                cornerRadius = CornerRadius(cornerRadiusPx, cornerRadiusPx)
                            )
                        }
                ) {
                    /*Grid portion*/
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(5),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentPadding = PaddingValues(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(
                            letters
                        ) { letter ->
                            Box(
                                modifier = Modifier
                                    .aspectRatio(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        brush = Brush.verticalGradient(
                                            colors = listOf(
                                                Color(0xFF915423),
                                                colorResource(id = R.color.hometoptextcolor),
                                                colorResource(id = R.color.hometoptextcolor)
                                            ),
                                            startY = 0f, endY = 30f
                                        )
                                    )
                                    .clickable(
                                        indication = null,
                                        interactionSource = remember { MutableInteractionSource() }
                                    ) {
                                        onSelect(letter, isUppercase)
                                    },
                                contentAlignment = Alignment.Center
                            )
                            {
                                Text(
                                    text = letter.toString(),
                                    color = Color.White,
                                    fontSize = 35.sp,
                                    fontFamily = FontFamily(Font(R.font.balootwomediam))
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

fun DrawScope.drawStaticGuide(start: Offset, next: Offset, color: Color, scale: Float, alpha: Float) {
    val angle = atan2(next.y - start.y, next.x - start.x)
    val arrowLength = 28f * scale
    val arrowAngle = Math.toRadians(30.0).toFloat()


    drawCircle(
        color = Color.White.copy(alpha = alpha),
        radius = 20f,
        center = start
    )

    // 2. Main Starting Dot
    drawCircle(color = color, radius = 15f, center = start)

    // 3. Arrow Head Logic
    val arrowTip = Offset(
        start.x + (45f * scale) * cos(angle),
        start.y + (45f * scale) * sin(angle)
    )

    val p1 = Offset(arrowTip.x - arrowLength * cos(angle + arrowAngle), arrowTip.y - arrowLength * sin(angle + arrowAngle))
    val p2 = Offset(arrowTip.x - arrowLength * cos(angle - arrowAngle), arrowTip.y - arrowLength * sin(angle - arrowAngle))

    drawLine(Color.White.copy(alpha = alpha), arrowTip, p1, strokeWidth = 14f, cap = StrokeCap.Round)
    drawLine(Color.White.copy(alpha = alpha), arrowTip, p2, strokeWidth = 14f, cap = StrokeCap.Round)

    // Arrow ki Main Lines
    drawLine(color.copy(alpha = alpha), arrowTip, p1, strokeWidth = 8f, cap = StrokeCap.Round)
    drawLine(color.copy(alpha = alpha), arrowTip, p2, strokeWidth = 8f, cap = StrokeCap.Round)
    drawLine(color.copy(alpha = alpha), start, arrowTip, strokeWidth = 8f, cap = StrokeCap.Round)
}

data class BurstState(
    val id: Long = System.nanoTime(),
    val x: Float,
    val y: Float,
    val isTarget: Boolean
)

data class BalloonItem(
    val id: String,
    val label: String,
    val color: Color
)

data class PopBalloonState(
    val id: Long = System.nanoTime(),
    val letter: String,
    val color: Color,
    val initialX: Float,
    var currentY: Animatable<Float, AnimationVector1D> = Animatable(0.86f)
)
data class GameItem(val name: String, val image: Int)
private fun Modifier.width(dp: Dp) = this.then(Modifier.size(width = dp, height = 130.dp))
