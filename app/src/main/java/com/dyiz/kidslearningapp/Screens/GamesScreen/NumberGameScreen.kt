package com.dyiz.kidslearningapp.Screens.GamesScreen

import android.content.Context
import android.media.MediaPlayer
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.speech.tts.TextToSpeech
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
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
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController

import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.dyiz.kidslearningapp.Database.ViewModel.MainViewModel
import com.dyiz.kidslearningapp.NavGraph.NavRoutes
import com.dyiz.kidslearningapp.R
import com.dyiz.kidslearningapp.badges.BadgeGameType
import com.dyiz.kidslearningapp.badges.TrackGameBadgeProgress
import com.dyiz.kidslearningapp.utils.BadgeUnlockedDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt


object NumberData {
    private fun interpolate(segment: List<Offset>): List<Offset> {
        val result = mutableListOf<Offset>()
        if (segment.isEmpty()) return result
        for (i in 0 until segment.size - 1) {
            val p1 = segment[i];
            val p2 = segment[i + 1]
            val dist = sqrt((p1.x - p2.x).pow(2) + (p1.y - p2.y).pow(2))
            val numPoints = (dist / 0.015f).toInt()
                .coerceAtLeast(1)
            for (j in 0 until numPoints) {
                val f = j.toFloat() / numPoints
                result.add(Offset(p1.x + (p2.x - p1.x) * f, p1.y + (p2.y - p1.y) * f))
            }
        }
        result.add(segment.last())
        return result
    }

    fun getPoints(number: Int): List<List<Offset>> {
        return if (number < 10) {
            getDigitSegments(number, isDouble = false, isFirst = true)
        } else {
            val first = number / 10
            val second = number % 10
            getDigitSegments(first, true, true) + getDigitSegments(second, true, false)
        }
    }

    private fun getDigitSegments(
        digit: Int,
        isDouble: Boolean,
        isFirst: Boolean
    ): List<List<Offset>> {
        val scale = if (isDouble) 0.72f else 0.85f
        val xShift = if (!isDouble) 0.5f else (if (isFirst) 0.27f else 0.71f)
        fun Offset.adj() = Offset((x - 0.5f) * scale + xShift, (y - 0.5f) * scale + 0.5f)

        val raw = when (digit) {
            1 -> listOf(listOf(Offset(0.42f, 0.28f), Offset(0.5f, 0.2f), Offset(0.5f, 0.8f)))
            2 -> listOf(
                listOf(
                    Offset(0.32f, 0.35f),
                    Offset(0.35f, 0.22f),
                    Offset(0.5f, 0.18f),
                    Offset(0.65f, 0.22f),
                    Offset(0.68f, 0.35f),
                    Offset(0.32f, 0.8f),
                    Offset(0.68f, 0.8f)
                )
            )
            // 3: Balanced rounded curves
            3 -> listOf(
                listOf(Offset(0.35f, 0.25f), Offset(0.65f, 0.25f), Offset(0.48f, 0.48f)),
                listOf(
                    Offset(0.48f, 0.48f),
                    Offset(0.68f, 0.55f),
                    Offset(0.68f, 0.75f),
                    Offset(0.5f, 0.82f),
                    Offset(0.32f, 0.75f)
                )
            )

            // 4: Open Top Triangle style (Exactly same for 4, 14, 24)
            4 -> listOf(
                listOf(Offset(0.52f, 0.2f), Offset(0.3f, 0.62f), Offset(0.7f, 0.62f)),
                listOf(Offset(0.55f, 0.45f), Offset(0.55f, 0.82f))
            )

            // 5: Clean belly
            5 -> listOf(
                listOf(
                    Offset(0.65f, 0.22f),
                    Offset(0.38f, 0.22f),
                    Offset(0.35f, 0.45f),
                    Offset(0.65f, 0.45f),
                    Offset(0.68f, 0.68f),
                    Offset(0.55f, 0.82f),
                    Offset(0.32f, 0.75f)
                )
            )

            // 6: Smooth loop
            6 -> listOf(
                listOf(
                    Offset(0.58f, 0.2f),
                    Offset(0.38f, 0.4f),
                    Offset(0.32f, 0.65f),
                    Offset(0.42f, 0.82f),
                    Offset(0.62f, 0.82f),
                    Offset(0.68f, 0.65f),
                    Offset(0.5f, 0.48f),
                    Offset(0.32f, 0.65f)
                )
            )

            // 7: Simple slanted
            7 -> listOf(listOf(Offset(0.32f, 0.22f), Offset(0.68f, 0.22f), Offset(0.45f, 0.82f)))
            8 -> listOf(
                listOf(
                    Offset(0.5f, 0.48f),
                    // Top loop ka right side
                    Offset(0.60f, 0.40f),
                    Offset(0.65f, 0.32f),
                    Offset(0.58f, 0.18f),
                    Offset(0.5f, 0.16f),
                    // Top loop ka left side
                    Offset(0.42f, 0.18f),
                    Offset(0.35f, 0.32f),
                    Offset(0.40f, 0.40f),
                    Offset(0.5f, 0.48f),
                    // Bottom loop ka right side
                    Offset(0.62f, 0.58f),
                    Offset(0.70f, 0.70f),
                    Offset(0.60f, 0.84f),
                    Offset(0.5f, 0.86f),
                    // Bottom loop ka left side
                    Offset(0.40f, 0.84f),
                    Offset(0.30f, 0.70f),
                    Offset(0.38f, 0.58f),
                    Offset(0.5f, 0.48f)
                )
            )

            // 9: Circle top and straight tail
            9 -> listOf(
                listOf(
                    Offset(0.68f, 0.52f),
                    Offset(0.48f, 0.55f),
                    Offset(0.32f, 0.42f),
                    Offset(0.35f, 0.22f),
                    Offset(0.55f, 0.18f),
                    Offset(0.68f, 0.32f),
                    Offset(0.68f, 0.82f)
                )
            )
            0 -> listOf(
                listOf(
                    Offset(0.5f, 0.18f),
                    Offset(0.35f, 0.22f), Offset(0.32f, 0.35f), // Top Left Corner
                    Offset(0.32f, 0.65f), Offset(0.35f, 0.78f), // Bottom Left Corner
                    Offset(0.5f, 0.82f),
                    Offset(0.65f, 0.78f), Offset(0.68f, 0.65f), // Bottom Right Corner
                    Offset(0.68f, 0.35f), Offset(0.65f, 0.22f), // Top Right Corner
                    Offset(0.5f, 0.18f)
                )
            )

            else -> listOf(listOf(Offset(0.5f, 0.2f), Offset(0.5f, 0.8f)))
        }
        return raw.map { interpolate(it.map { p -> p.adj() }) }

    }
}

@Composable
fun NumberGameScreen(navController: NavHostController,mainViewModel: MainViewModel) {

    var selectedGame by remember { mutableStateOf("Counting Fun") }

    var showBadgeDialog by remember { mutableStateOf<Int?>(null) }
    LaunchedEffect(Unit) {
        mainViewModel.badgeGameProgress.newlyUnlockedBadge.collect { badgeId ->
            showBadgeDialog = badgeId
        }
    }
    showBadgeDialog?.let { id ->
        BadgeUnlockedDialog(badgeIndex = id) {
            showBadgeDialog = null
        }
    }

    val isLocked by mainViewModel.isLocked.collectAsState()
    LaunchedEffect(isLocked) {
        if (isLocked) {
            navController.navigate(NavRoutes.HOME) {
                popUpTo(NavRoutes.HOME) { inclusive = true }
            }
        }
    }
    //try these cards animation
    var startSpreading by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(300)
        startSpreading = true
    }

    Box(modifier = Modifier.fillMaxSize()){
        TrackGameBadgeProgress(mainViewModel, BadgeGameType.Number)
        CompositionLocalProvider(
            LocalDensity provides Density(
                LocalDensity.current.density,
                fontScale = 1f
            )
        ) {
            Image(
                painter = painterResource(id = R.drawable.numbergamebg),
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
                            .clickable (
                                indication = null, interactionSource = remember { MutableInteractionSource() }
                            ){ navController.navigateUp() })
                    Text(
                        text = if (selectedGame == "Counting Fun") "Counting Fun" else selectedGame,
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
                        label = "Game transition"
                    ) { targetGame->
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
                                        color = Color(0xFFD0B0FF),
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
                                when(targetGame){
                                    "Counting Fun" -> CountingFunUI()
                                    "NumQuest" -> NumQuestUI()
                                    "Tracing" -> NumberTracingUI()
                                    "Big vs Small" -> BigVsSmallUI()
                                }
                            }
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
                    GameItem("Counting Fun", R.drawable.countingfungameimage),
                    GameItem("NumQuest", R.drawable.numquestgameimage),
                    GameItem("Tracing", R.drawable.tracinggameimage),
                    GameItem("Big vs Small", R.drawable.bigvssmallgameimage)
                )
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(games.filter { it.name != selectedGame }) { index,game ->
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
                                    }
                            ) {
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

/*Big vs Small game Ui*/
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BigVsSmallUI() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    val mediaPlayer = remember { MediaPlayer.create(context, R.raw.tracingonesidesound) }

    // --- Lottie & Success States ---
    var showSuccessLottie by remember { mutableStateOf(false) }
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.tracinglottie))

    // --- Game States ---
    var levelTrigger by remember { mutableIntStateOf(0) }
    var num1 by remember { mutableIntStateOf((1..20).random()) }
    var num2 by remember { mutableIntStateOf((1..20).random()) }
    var isCorrect by remember { mutableStateOf<Boolean?>(null) }
    var wrongSelection by remember { mutableStateOf<String?>(null) }
    var showResultSign by remember { mutableStateOf(false) }
    var userInteracted by remember { mutableStateOf(false) }

    val correctSign = when {
        num1 > num2 -> ">"
        num1 < num2 -> "<"
        else -> "="
    }

    // Dynamic Color for the Flipped Box
    val flippedBoxColor = when (correctSign) {
        ">" -> Color(0xFFFF09CE)
        "=" -> Color(0xFFFFDD50)
        "<" -> Color(0xFFFB7B79)
        else -> Color.White
    }

    // Flip Animation Logic
    val rotation by animateFloatAsState(
        targetValue = if (showResultSign) 180f else 0f,
        animationSpec = tween(durationMillis = 600), label = "flip"
    )

    // Hand Guide Animation
    val infiniteTransition = rememberInfiniteTransition(label = "hand")
    val handTranslateY by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 20f,
        animationSpec = infiniteRepeatable(animation = tween(800), repeatMode = RepeatMode.Reverse),
        label = "handMove"
    )

    val tts = remember {
        var ttsInstance: TextToSpeech? = null
        ttsInstance = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                ttsInstance?.language = java.util.Locale.US
                ttsInstance?.speak("Let's compare the numbers", TextToSpeech.QUEUE_FLUSH, null, null)
            }
        }
        ttsInstance
    }

    // Logic to generate level
    LaunchedEffect(levelTrigger) {
        num1 = (1..25).random()
        num2 = (1..25).random()
        isCorrect = null
        showResultSign = false
        userInteracted = false
    }

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer.release()
            tts?.stop()
            tts?.shutdown()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Let's compare the numbers",
            fontFamily = FontFamily(Font(R.font.balootwomediam)),
            fontSize = 22.sp,
            color = Color(0xFF4B1B92),
            modifier = Modifier.padding(top = 26.dp, bottom = 0.dp)
        )

        // --- MAIN GAME BOX (F0EBF7) ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFFF0EBF7))
        ) {
            Column(modifier = Modifier.fillMaxSize()) {

                // --- TOP AREA: COMPARISON ROW ---
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        // Left Card
                        NumberComparisonCard(num = num1)

                        // Center Flip Question Mark
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .graphicsLayer {
                                    rotationY = rotation
                                    cameraDistance = 12f * density
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (rotation <= 90f) {
                                Image(
                                    painter = painterResource(id = R.drawable.bigsmallquestmark),
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .graphicsLayer { rotationY = 180f }
                                        .shadow(4.dp, RoundedCornerShape(12.dp))
                                        .background(flippedBoxColor, RoundedCornerShape(12.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(correctSign, fontSize = 45.sp, color = Color.White, fontFamily = FontFamily(Font(R.font.balootwosemibold)))
                                }
                            }
                        }

                        // Right Card
                        NumberComparisonCard(num = num2)
                    }
                }

                // --- BOTTOM AREA: OPTIONS (D0B0FF) ---
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFD0B0FF))
                        .padding(vertical = 18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        val options = listOf(
                            Triple(">", Color(0xFFFF09CE), "Greater Than"),
                            Triple("=", Color(0xFFFFDD50), "Equal"),
                            Triple("<", Color(0xFFFB7B79), "Less Than")
                        )
                        options.forEach { (sign, baseColor, label) ->
                        ComparisonButton(
                            sign = sign,
                            color = if (wrongSelection == sign) Color(0xFFED1C2A) else baseColor,
                            isVisible = !showResultSign
                        ) {
                            val isRight = checkAnswer(sign, correctSign, mediaPlayer, vibrator, tts) {
                                showSuccessLottie = true
                                showResultSign = true
                                scope.launch {
                                    delay(2000)
                                    showSuccessLottie = false
                                    levelTrigger++
                                }
                            }

                            if (!isRight) {
                                wrongSelection = sign
                                scope.launch {
                                    delay(500)
                                    wrongSelection = null
                                }
                            }
                        }
                    }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
    }

    if (showSuccessLottie) {
        LottieAnimation(composition = composition, iterations = 1,
            modifier = Modifier
                .fillMaxSize()
                .scale(1.5f)
                .zIndex(10f))
    }
}

// Helper function to check answer
private fun checkAnswer(
    selected: String,
    correct: String,
    mediaPlayer: MediaPlayer,
    vibrator: Vibrator,
    tts: TextToSpeech?,
    onSuccess: () -> Unit
):Boolean {
  return  if (selected == correct) {
        mediaPlayer.seekTo(0)
        mediaPlayer.start()
        onSuccess()
        true
    } else {
        vibrator.vibrate(200)
        false
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NumberComparisonCard(num: Int) {
    Card(
        modifier = Modifier
            .width(100.dp)
            .height(162.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F7F7)),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header with number
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFD0B0FF))
                    .padding(vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = num.toString(),
                    fontFamily = FontFamily(Font(R.font.balootwosemibold)),
                    fontSize = 18.sp,
                    color = Color.Black
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(6.dp),
                contentAlignment = Alignment.Center
            ) {

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalArrangement = Arrangement.Center,
                    maxItemsInEachRow = 4
                ) {
                    repeat(num) {
                        Image(
                            painter = painterResource(id = R.drawable.bigvsmallstaricon),
                            contentDescription = null,
                            modifier = Modifier
                                .size(16.dp)
                                .padding(1.dp)
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun ComparisonButton(sign: String, color: Color, isVisible: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(70.dp)
            .shadow(4.dp, RoundedCornerShape(15.dp))
            .background(color, RoundedCornerShape(15.dp))
            .clickable(
                enabled = isVisible,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(sign, color = Color.White, fontSize = 40.sp, fontFamily = FontFamily(Font(R.font.balootwosemibold)))
    }
}


/*Number Tracing Game*/
@Composable
fun NumberTracingUI() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var tracingNumber by remember { mutableIntStateOf(1) }
    var tracingColor by remember { mutableStateOf(Color(0xFF4CAF50)) }
    val reachedPoints = remember { mutableStateListOf<Offset>() }
    var isTransitioning by remember { mutableStateOf(false) }
    var showLottie by remember { mutableStateOf(false) }
    var showNumberGrid by remember { mutableStateOf(false) }
    var redrawTrigger by remember { mutableIntStateOf(0) }
    val guideSegments = remember(tracingNumber) { NumberData.getPoints(tracingNumber) }

    // Pulse Animation for Arrows
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = 1.25f,
        animationSpec = infiniteRepeatable(tween(700), RepeatMode.Reverse), label = ""
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(700), RepeatMode.Reverse), label = ""
    )

    val successSound = remember { MediaPlayer.create(context, R.raw.tracingonesidesound) }

    val tts = remember {
        var ttsInstance: TextToSpeech? = null
        ttsInstance = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                ttsInstance?.language = java.util.Locale.US
            }
        }
        ttsInstance
    }
    LaunchedEffect(Unit) {
        showNumberGrid = true
    }

    DisposableEffect(Unit) {
        onDispose {
            successSound.release()
            tts?.stop()
            tts?.shutdown()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(modifier = Modifier
                .weight(1f)
                .fillMaxWidth()) {
                // Grid Button
                IconButton(
                    onClick = {
                        showNumberGrid = true
//                        onGrid/
                    },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(5.dp)
                        .zIndex(5f)) {
                    Image(painter = painterResource(id = R.drawable.dialpad), contentDescription = null, modifier = Modifier.size(35.dp))
                }
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 40.dp)//20dp
                        .pointerInput(tracingNumber, showLottie) {
                            if (showLottie) return@pointerInput///here is a bug
                            detectDragGestures { change, _ ->
                                change.consume()
                                guideSegments.forEach { segment ->
                                    for (i in segment.indices) {
                                        val pt = segment[i]
                                        val isFirstPoint = i == 0
                                        val isPreviousPointReached =
                                            if (!isFirstPoint) reachedPoints.contains(segment[i - 1]) else true
                                        val segmentIndex = guideSegments.indexOf(segment)
                                        val isPreviousSegmentFinished =
                                            if (segmentIndex > 0) reachedPoints.contains(
                                                guideSegments[segmentIndex - 1].last()
                                            ) else true
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
//                                                    onUpdate()
                                                    val allPoints = guideSegments.flatten()
                                                    val totalPoints = allPoints.size
                                                    val progress =
                                                        reachedPoints.size.toFloat() / totalPoints

                                                    val lastPoint = allPoints.lastOrNull()
                                                    val isAtEnd =
                                                        lastPoint != null && reachedPoints.contains(
                                                            lastPoint
                                                        )
                                                    // if 93% trace
                                                    if (progress >= 0.93f && isAtEnd) {
                                                        if (!isTransitioning) {
                                                            isTransitioning = true
                                                            showLottie = true

                                                            // --- SUCCESS SOUND
                                                            if (successSound.isPlaying) {
                                                                successSound.pause()
                                                                successSound.seekTo(0)
                                                            }
                                                            successSound.start()
                                                            scope.launch {
                                                                delay(2000)

                                                                // Next Number Logic
                                                                if (tracingNumber < 25) {
                                                                    tracingNumber++
                                                                    tts?.speak("Let's trace the Number $tracingNumber", TextToSpeech.QUEUE_FLUSH, null, null)

                                                                } else {
                                                                    tracingNumber = 1
                                                                }

                                                                reachedPoints.clear()
                                                                showLottie = false
                                                                isTransitioning = false
                                                                redrawTrigger++

                                                            }
                                                        }
                                                    }
                                                    // --- AUTO NEXT LOGIC END ---
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }) {
                    val w = size.width; val h = size.height

                    // 1. Background Track
                    val trackPath = Path()
                    guideSegments.forEach { seg ->
                        seg.forEachIndexed { i, pt ->
                            if (i == 0) trackPath.moveTo(pt.x * w, pt.y * h) else trackPath.lineTo(pt.x * w, pt.y * h)
                        }
                    }
                    drawPath(trackPath, Color(0xFF67419F), style = Stroke(100f, cap = StrokeCap.Round, join = StrokeJoin.Round))
                    drawPath(trackPath, Color.White, style = Stroke(75f, cap = StrokeCap.Round, join = StrokeJoin.Round))
                    drawPath(trackPath, Color(0xFFFFE9BE), style = Stroke(10f, cap = StrokeCap.Round, pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 30f))))

                    // 2. User Trace
                    guideSegments.forEach { seg ->
                        for (i in 0 until seg.size - 1) {
                            if (reachedPoints.contains(seg[i]) && reachedPoints.contains(seg[i + 1])) {
                                drawLine(tracingColor, Offset(seg[i].x * w, seg[i].y * h), Offset(seg[i + 1].x * w, seg[i + 1].y * h), 85f, cap = StrokeCap.Round)
                            }
                        }
                    }

                    // 3. Guided Arrow (Strict order)
                    guideSegments.forEachIndexed { idx, seg ->
                        val isPrevSegDone = if (idx > 0) reachedPoints.contains(guideSegments[idx-1].last()) else true
                        val isCurrDone = reachedPoints.contains(seg.last())
                        if (isPrevSegDone && !isCurrDone && seg.size >= 2) {
                            drawStaticGuides(Offset(seg[0].x * w, seg[0].y * h), Offset(seg[1].x * w, seg[1].y * h), tracingColor, pulseScale, pulseAlpha)
                        }
                    }
                }

                if (showLottie) {
                    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.tracinglottie))
                    LottieAnimation(composition, iterations = 1, modifier = Modifier
                        .fillMaxSize()
                        .scale(1.5f)
                        .zIndex(10f))
                }
            }

            // Colors
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(bottom = 10.dp)
            ) {
                listOf(Color(0xFF4CAF50), Color(0xFF2196F3), Color(0xFFE52B3C)).forEach { c ->
                    val isSelected  = tracingColor == c
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
                            interactionSource = remember { MutableInteractionSource() }) {
                            tracingColor = c
                        })
                }
            }
        }
        if (showNumberGrid) {
            NumberGridDialog(
                onDismiss = { showNumberGrid = false },
                onSelect = { selectedNum ->
                    tracingNumber = selectedNum
                    reachedPoints.clear() // Clear old progress
                    redrawTrigger++       // Refresh UI
                    showNumberGrid = false // Close dialog
                    tts?.speak("Let's trace the number $selectedNum", TextToSpeech.QUEUE_FLUSH, null, null)

                }
            )
        }
    }
}

@Composable
fun NumberGridDialog(
    onDismiss: () -> Unit,
    onSelect: (Int) -> Unit
) {
    val numbers = (1..25).toList()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFDEDDDF).copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            // --- Cross/Close Icon ---
            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(20.dp)
                    .size(30.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.crossicon),
                    contentDescription = "Close"
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth(0.9f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // --- Main Grid Container ---
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.55f) // Adjust height as needed
                        .shadow(10.dp, RoundedCornerShape(28.dp))
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFFC5C5C5),
                                    Color.White,
                                    Color.White
                                ),
                                startY = 0f, endY = 40f
                            ),
                            RoundedCornerShape(28.dp)
                        )
                        .drawBehind {
                            // Bottom decorative strip
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
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(5),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentPadding = PaddingValues(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(numbers) { num ->
                            Box(
                                modifier = Modifier
                                    .aspectRatio(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        brush = Brush.verticalGradient(
                                            colors = listOf(
                                                Color(0xFF4A2B70), // Darker purple top
                                                Color(0xFF67419F), // Your requested color
                                                Color(0xFF67419F)
                                            ),
                                            startY = 0f, endY = 30f
                                        )
                                    )
                                    .clickable(
                                        indication = null,
                                        interactionSource = remember { MutableInteractionSource() }
                                    ) {
                                        onSelect(num)
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = num.toString(),
                                    color = Color.White,
                                    fontSize = 28.sp,
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

// Helper function remains same as Tracing UI
private fun DrawScope.drawStaticGuides(start: Offset, next: Offset, color: Color, scale: Float, alpha: Float) {
    val angle = atan2(next.y - start.y, next.x - start.x)
    val arrowTip = Offset(start.x + (45f * scale) * cos(angle), start.y + (45f * scale) * sin(angle))
    drawCircle(Color.White.copy(alpha = alpha), 20f, start)
    drawCircle(color, 15f, start)
    val p1 = Offset(arrowTip.x - 28f * scale * cos(angle + 0.5f), arrowTip.y - 28f * scale * sin(angle + 0.5f))
    val p2 = Offset(arrowTip.x - 28f * scale * cos(angle - 0.5f), arrowTip.y - 28f * scale * sin(angle - 0.5f))
    drawLine(Color.White.copy(alpha = alpha), arrowTip, p1, 14f, StrokeCap.Round)
    drawLine(Color.White.copy(alpha = alpha), arrowTip, p2, 14f, StrokeCap.Round)
    drawLine(color.copy(alpha = alpha), arrowTip, p1, 8f, StrokeCap.Round)
    drawLine(color.copy(alpha = alpha), arrowTip, p2, 8f, StrokeCap.Round)
    drawLine(color.copy(alpha = alpha), start, arrowTip, 8f, StrokeCap.Round)
}



/*NumQuest Game*/
@OptIn(ExperimentalLayoutApi::class)@Composable
fun NumQuestUI() {
    val context = LocalContext.current
    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    val mediaPlayer = remember { MediaPlayer.create(context, R.raw.tracingonesidesound) }


    var showSuccessLottie by remember { mutableStateOf(false) }
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.tracinglottie))

    // --- Colors ---
    val starColors = listOf(Color(0xFFFFAD11), Color(0xFFC837AB), Color(0xFF2B7072), Color(0xFFE91E63))
    val correctGreen = Color(0xFF1F9856)

    // --- States ---
    var levelTrigger by remember { mutableIntStateOf(0) }
    var sequence by remember { mutableStateOf(listOf<Int>()) }
    var missingIndex by remember { mutableIntStateOf(0) }
    var options by remember { mutableStateOf(listOf<Int>()) }
    var selectedOption by remember { mutableStateOf<Int?>(null) }
    var isCorrect by remember { mutableStateOf<Boolean?>(null) }

    // Flip Animation State
    val flipRotation by animateFloatAsState(
        targetValue = if (isCorrect == true) 180f else 0f,
        animationSpec = tween(durationMillis = 600)
    )


    val tts = remember {
        var ttsInstance: TextToSpeech? = null
        ttsInstance = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                ttsInstance?.language = java.util.Locale.US
                ttsInstance?.speak("Find the missing number", TextToSpeech.QUEUE_FLUSH, null, null)
            }
        }
        ttsInstance
    }

    // Logic to generate level
    LaunchedEffect(levelTrigger) {
        val startNum = (1..22).random()
        sequence = listOf(startNum, startNum + 1, startNum + 2, startNum + 3)
        missingIndex = (0..3).random()
        val targetNum = sequence[missingIndex]

        val opt = mutableSetOf<Int>()
        opt.add(targetNum)
        while (opt.size < 3) {
            val wrong = (1..25).random()
            if (wrong != targetNum) opt.add(wrong)
        }
        options = opt.toList().shuffled()
        selectedOption = null
        isCorrect = null
    }
    LaunchedEffect(showSuccessLottie) {
        if (!showSuccessLottie) {
            selectedOption = null // Reset selection for next shape
        }
    }

    DisposableEffect (Unit){
        onDispose {
            mediaPlayer.release()
            tts?.stop()
            tts?.shutdown()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Find the missing number",
            fontFamily = FontFamily(Font(R.font.balootwomediam)),
            fontSize = 22.sp,
            color = Color(0xFF4B1B92),
            modifier = Modifier.padding(top = 26.dp, bottom = 0.dp)
        )

        // --- MAIN GAME BOX (F0EBF7) ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFFF0EBF7))
        ) {
            Column(modifier = Modifier.fillMaxSize()) {

                // --- STARS GRID (2x2) ---
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    // FlowRow with restricted size to show 2 per row clearly
                    FlowRow(
                        modifier = Modifier.padding(10.dp),
                        maxItemsInEachRow = 2,
                        horizontalArrangement = Arrangement.spacedBy(15.dp),
                        verticalArrangement = Arrangement.spacedBy(15.dp)
                    ) {
                        sequence.forEachIndexed { index, num ->
                            val isMissing = index == missingIndex

                            Box(
                                modifier = Modifier
                                    .size(100.dp) // Size reduced from 120 to 100 to fit 4 stars
                                    .graphicsLayer {
                                        if (isMissing) {
                                            rotationY = flipRotation
                                            cameraDistance = 12f * density
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                if (isMissing && flipRotation <= 90f) {
                                    // DOTED STAR (Front)
                                    Image(
                                        painter = painterResource(id = R.drawable.numquestdotedstar),
                                        contentDescription = null,
                                        modifier = Modifier.fillMaxSize(),
                                    )

                                } else {
                                    // SOLID STAR
                                    val starColor = if (isMissing && isCorrect == true) correctGreen else starColors[index % starColors.size]

                                    Image(
                                        painter = painterResource(id = R.drawable.numqueststar),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .graphicsLayer {
                                                if (isMissing) rotationY = 180f
                                            },
                                        colorFilter = ColorFilter.tint(starColor)
                                    )
                                    Text(
                                        text = num.toString(),
                                        fontSize = 30.sp,
                                        color = Color.White,
                                        fontFamily = FontFamily(Font(R.font.balootwomediam)),
                                        modifier = Modifier
                                            .offset(y = (4).dp)
                                            .graphicsLayer { if (isMissing) rotationY = 180f }
                                    )
                                }
                            }
                        }
                    }
                }

                // --- BOTTOM AREA (Options) ---
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFD0B0FF))
                        .padding(vertical = 14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        options.forEach { option ->
                            val isThisSelected = selectedOption == option
                            val btnBgColor by animateColorAsState(
                                targetValue = when {
                                    isThisSelected && isCorrect == true -> correctGreen
                                    isThisSelected && isCorrect == false -> Color(0xFFED1C2A)
                                    else -> Color.White
                                }
                            )

                            Card(
                                modifier = Modifier
                                    .width(80.dp)
                                    .height(50.dp)
                                    .clickable(
                                        indication = null,
                                        interactionSource = remember { MutableInteractionSource() }
                                    ) {
                                        if (isCorrect == true) return@clickable
                                        selectedOption = option
                                        if (option == sequence[missingIndex]) {

                                            isCorrect = true
                                            mediaPlayer.start()
                                            showSuccessLottie = true
                                            tts?.speak(
                                                option.toString(),
                                                TextToSpeech.QUEUE_FLUSH,
                                                null,
                                                null
                                            )
                                            CoroutineScope(Dispatchers.Main).launch {
                                                delay(2000)
                                                showSuccessLottie = false
                                                Handler(Looper.getMainLooper())
                                                    .postDelayed({
                                                        levelTrigger++
                                                    }, 500)
                                            }
                                        } else {
                                            isCorrect = false
                                            vibrator.vibrate(200)
                                            Handler(Looper.getMainLooper())
                                                .postDelayed({
                                                    selectedOption = null
                                                    isCorrect = null
                                                }, 500)
                                        }
                                    },
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = btnBgColor),
                                elevation = CardDefaults.cardElevation(4.dp)
                            ) {
                                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Text(
                                        text = option.toString(),
                                        fontSize = 24.sp,
                                        fontFamily = FontFamily(Font(R.font.balootwomediam)),
                                        color = if (isThisSelected && isCorrect != null) Color.White else Color.Black
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
    }
    if (showSuccessLottie) {
        LottieAnimation(
            composition = composition,
            iterations = 1,
            modifier = Modifier
                .fillMaxSize()
                .scale(1.5f)
                .zIndex(10f)
        )
    }
}

/*Counting fun game*/
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CountingFunUI() {
    val context = LocalContext.current
    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    // --- Sound Player Setup ---
    val mediaPlayer = remember {
        MediaPlayer.create(context, R.raw.tracingonesidesound)
    }
    // --- HAND GUIDE STATES ---
    var isHandGuideVisible by remember { mutableStateOf(true) }
    val handAnim = remember { Animatable(0f) }

    // 1. Images List
    val gameImages = remember {
        listOf(
            R.drawable.numquestoctupas, R.drawable.numquestball, R.drawable.numquesticecream,
            R.drawable.numquestapple, R.drawable.numquestcar, R.drawable.numquestbear,
            R.drawable.numquestbutterfly, R.drawable.numquestsun, R.drawable.numquestowl,
            R.drawable.numquesttree
        )
    }

    // lottie animation
    var showSuccessLottie by remember { mutableStateOf(false) }
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.tracinglottie))


    // 2. Game States
    var levelTrigger by remember { mutableIntStateOf(0) }
    var targetCount by remember { mutableIntStateOf(0) }
    var selectedImage by remember { mutableIntStateOf(gameImages[0]) }
    var options by remember { mutableStateOf(listOf<Int>()) }
    var selectedOption by remember { mutableStateOf<Int?>(null) }
    var isCorrect by remember { mutableStateOf<Boolean?>(null) }

    // TTS Setup
    val tts = remember {
        var ttsInstance: TextToSpeech? = null
        ttsInstance = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                ttsInstance?.language = java.util.Locale.US
                ttsInstance?.speak("Let's count the objects", TextToSpeech.QUEUE_FLUSH, null, null)
            }
        }
        ttsInstance
    }

    LaunchedEffect(isHandGuideVisible) {
        if (isHandGuideVisible) {
            while (true) {
                handAnim.animateTo(1f, tween(800, easing = FastOutSlowInEasing))
                handAnim.animateTo(0f, tween(800, easing = FastOutSlowInEasing))
            }
        }
    }
    LaunchedEffect(showSuccessLottie) {
        if (!showSuccessLottie) {
            selectedOption = null // Reset selection for next shape
        }
    }

    // Logic to generate level
    LaunchedEffect(levelTrigger) {
        targetCount = (1..25).random() // Range 1-25
        selectedImage = gameImages.random()


        val opt = mutableSetOf<Int>()
        opt.add(targetCount)
        while (opt.size < 3) {
            val wrong = (1..25).random()
            if (wrong != targetCount) opt.add(wrong)
        }
        options = opt.toList().shuffled()
        selectedOption = null
        isCorrect = null
    }

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer.release()
            tts?.stop()
            tts?.shutdown()
        }
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Let's count the objects",
            fontFamily = FontFamily(Font(R.font.balootwomediam)),
            fontSize = 22.sp,
            color = Color(0xFF4B1B92),
            modifier = Modifier.padding(top = 26.dp, bottom = 0.dp)
        )

        // --- MAIN GAME BOX (F0EBF7) ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFFF0EBF7)) // User specified color
        ) {
            Column(modifier = Modifier.fillMaxSize()) {

                // --- TOP AREA: SCALABLE IMAGES ---
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())//fix 1
                        .padding(15.dp),//15
                    contentAlignment = Alignment.Center
                ) {
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalArrangement = Arrangement.Center,
                        // Responsive row count
                        maxItemsInEachRow = when {
                            targetCount > 15 -> 6
                            targetCount > 8 -> 4
                            else -> 3
                        }
                    ) {
                        // Responsive Image Size
                        val imageSize = when {
                            targetCount <= 4 -> 110.dp
                            targetCount <= 9 -> 80.dp
                            targetCount <= 16 -> 60.dp
                            else -> 48.dp
                        }

                        repeat(targetCount) {
                            Image(
                                painter = painterResource(id = selectedImage),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(imageSize)
                                    .padding(4.dp)
                            )
                        }
                    }
                }
                // --- BOTTOM AREA: OPTIONS (Slightly Darker Purple) ---
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFD0B0FF))
                        .padding(vertical = 14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        options.forEach { option ->
                            val isThisSelected = selectedOption == option

                            val btnBgColor by animateColorAsState(
                                targetValue = when {
                                    isThisSelected && isCorrect == true -> Color(0xFF1F9856) // Correct: Green
                                    isThisSelected && isCorrect == false -> Color(0xFFED1C2A) // Wrong: Red
                                    else -> Color(0xFFF8F7F7)
                                },
                                animationSpec = tween(400)
                            )

                            Card(
                                modifier = Modifier
                                    .width(80.dp)//90
                                    .height(50.dp)//55
                                    .clickable(
                                        indication = null,
                                        interactionSource = remember { MutableInteractionSource() }
                                    ) {
                                        isHandGuideVisible = false
                                        selectedOption = option
                                        if (option == targetCount) {
                                            isCorrect = true
                                            // 1. Play Success Sound
                                            mediaPlayer.start()
                                            showSuccessLottie = true
                                            Handler(Looper.getMainLooper()).postDelayed(
                                                {
                                                    tts?.speak(
                                                        option.toString(),
                                                        TextToSpeech.QUEUE_FLUSH,
                                                        null,
                                                        null
                                                    )
                                                }, 500
                                            )
                                            CoroutineScope(Dispatchers.Main).launch {
                                                delay(2000)
                                                showSuccessLottie = false

                                                Handler(Looper.getMainLooper())
                                                    .postDelayed({
                                                        levelTrigger++
                                                    }, 500)
                                            }


                                        } else {
                                            isCorrect = false
                                            // Vibration logic
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                vibrator.vibrate(
                                                    VibrationEffect.createOneShot(
                                                        200,
                                                        VibrationEffect.DEFAULT_AMPLITUDE
                                                    )
                                                )
                                            } else {
                                                vibrator.vibrate(200)
                                            }
                                            // Reset to white
                                            Handler(Looper.getMainLooper())
                                                .postDelayed({
                                                    selectedOption = null
                                                    isCorrect = null
                                                }, 500)
                                        }
                                    },
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = btnBgColor),
                                elevation = CardDefaults.cardElevation(4.dp)
                            ) {
                                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Text(
                                        text = option.toString(),
                                        fontSize = 30.sp,
                                        fontFamily = FontFamily(Font(R.font.balootwomediam)),
                                        color = if (isThisSelected && isCorrect != null) Color.White else Color.Black
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
    }
    if (showSuccessLottie) {
        LottieAnimation(
            composition = composition,
            iterations = 1,
            modifier = Modifier
                .fillMaxSize()
                .scale(1.5f)
                .zIndex(10f)
        )
    }
    if (isHandGuideVisible && options.isNotEmpty()) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val screenWidth = constraints.maxWidth.toFloat()
            val screenHeight = constraints.maxHeight.toFloat()
            val density = LocalDensity.current.density

            val correctIndex = options.indexOf(targetCount)

            val sectionWidth = screenWidth / 3
            val handX = (sectionWidth * correctIndex) + (sectionWidth / 2) - (15 * density)

            val handY = screenHeight - (85 * density)

            // Animation for tapping
            val animatedY = handY + (handAnim.value * 25f)

            Image(
                painter = painterResource(id = R.drawable.guidehandimage),
                contentDescription = null,
                modifier = Modifier
                    .size(50.dp)
                    .offset { IntOffset(handX.toInt(), animatedY.toInt()) }
                    .zIndex(100f)
            )
        }
    }

}

