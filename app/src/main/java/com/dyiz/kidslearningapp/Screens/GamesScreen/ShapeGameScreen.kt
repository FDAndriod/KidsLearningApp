package com.dyiz.kidslearningapp.Screens.GamesScreen

import android.content.Context
import android.media.MediaPlayer
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.speech.tts.TextToSpeech
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
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
import kotlin.math.pow
import kotlin.math.sqrt



object ShapeDataf {
    private fun interpolate(segment: List<Offset>): List<Offset> {
        val result = mutableListOf<Offset>()
        if (segment.isEmpty()) return result
        result.add(segment.first())
        for (i in 0 until segment.size - 1) {
            val p1 = segment[i]; val p2 = segment[i + 1]
            val dist = sqrt((p1.x - p2.x).pow(2) + (p1.y - p2.y).pow(2))
            val numPoints = (dist / 0.02f).toInt().coerceAtLeast(1) // Har 0.02 distance par point
            for (j in 1..numPoints) {
                val fraction = j.toFloat() / numPoints
                result.add(Offset(p1.x + (p2.x - p1.x) * fraction, p1.y + (p2.y - p1.y) * fraction))
            }
        }
        return result
    }

    fun getPoints(shape: String): List<List<Offset>> {
        val raw = when (shape) {
            "Circle" -> listOf(listOf(Offset(0.5f, 0.15f), Offset(0.72f, 0.22f), Offset(0.82f, 0.5f), Offset(0.72f, 0.78f), Offset(0.5f, 0.85f), Offset(0.28f, 0.78f), Offset(0.18f, 0.5f), Offset(0.28f, 0.22f), Offset(0.5f, 0.15f)))
            "Square" -> listOf(listOf(Offset(0.2f, 0.2f), Offset(0.8f, 0.2f)), listOf(Offset(0.8f, 0.2f), Offset(0.8f, 0.8f)), listOf(Offset(0.8f, 0.8f), Offset(0.2f, 0.8f)), listOf(Offset(0.2f, 0.8f), Offset(0.2f, 0.2f)))
            "Triangle" -> listOf(listOf(Offset(0.5f, 0.15f), Offset(0.15f, 0.85f)), listOf(Offset(0.15f, 0.85f), Offset(0.85f, 0.85f)), listOf(Offset(0.85f, 0.85f), Offset(0.5f, 0.15f)))
            "Rectangle" -> listOf(listOf(Offset(0.15f, 0.35f), Offset(0.85f, 0.35f)), listOf(Offset(0.85f, 0.35f), Offset(0.85f, 0.65f)), listOf(Offset(0.85f, 0.65f), Offset(0.15f, 0.65f)), listOf(Offset(0.15f, 0.65f), Offset(0.15f, 0.35f)))
            "Pentagon" -> listOf(listOf(Offset(0.5f, 0.1f), Offset(0.9f, 0.4f)), listOf(Offset(0.9f, 0.4f), Offset(0.75f, 0.85f)), listOf(Offset(0.75f, 0.85f), Offset(0.25f, 0.85f)), listOf(Offset(0.25f, 0.85f), Offset(0.1f, 0.4f)), listOf(Offset(0.1f, 0.4f), Offset(0.5f, 0.1f)))
            // --- OVAL SHAPE ADDED ---
            "Oval" -> listOf(
                listOf(
                    Offset(0.5f, 0.1f),    // Top Center
                    Offset(0.68f, 0.2f),   // Top Right Curve
                    Offset(0.75f, 0.5f),   // Right Center (Narrower than Circle)
                    Offset(0.68f, 0.8f),   // Bottom Right Curve
                    Offset(0.5f, 0.9f),    // Bottom Center
                    Offset(0.32f, 0.8f),   // Bottom Left Curve
                    Offset(0.25f, 0.5f),   // Left Center
                    Offset(0.32f, 0.2f),   // Top Left Curve
                    Offset(0.5f, 0.1f)     // Back to Top
                )
            )

            else -> listOf(listOf(Offset(0.5f, 0.15f), Offset(0.5f, 0.85f)))
        }
        return raw.map { interpolate(it) }
    }
}

@Composable
fun ShapeGameScreen(
    navController: NavHostController,mainViewModel: MainViewModel
){
    var selectedGame by remember { mutableStateOf("Match Up") }

    val isLocked by mainViewModel.isLocked.collectAsState()
    LaunchedEffect(isLocked) {
        if (isLocked) {
            navController.navigate(NavRoutes.HOME) {
                popUpTo(NavRoutes.HOME) { inclusive = true }
            }
        }
    }

    var showBadgeDialog by remember { mutableStateOf<Int?>(null) }
    LaunchedEffect(Unit) {
        mainViewModel.badgeGameProgress.newlyUnlockedBadge.collect { badgeId ->
            showBadgeDialog = badgeId
        }
    }

    //try these cards animation
    var startSpreading by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(300)
        startSpreading = true
    }

    showBadgeDialog?.let { id ->
        BadgeUnlockedDialog(badgeIndex = id) {
            showBadgeDialog = null
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        TrackGameBadgeProgress(mainViewModel, BadgeGameType.Shape)
        CompositionLocalProvider(
            LocalDensity provides Density(
                LocalDensity.current.density,
                fontScale = 1f
            )
        ) {
            Image(
                painter = painterResource(id = R.drawable.shapegamebg),
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
                            .clickable { navController.navigateUp() })
                    Text(
                        text = if (selectedGame == "Match Up") "Match Up" else selectedGame,
                        fontSize = 22.sp,
                        fontFamily = FontFamily(Font(R.font.balootwomediam)),
                        color = Color.Black,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }

                // White Card
                // White Main Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top=4.dp, start = 16.dp, end = 16.dp, bottom = 12.dp)
                        .height(450.dp)
                ) {

                    Crossfade(
                        targetState = selectedGame,
                        animationSpec = tween(durationMillis = 500),
                        label = "Game Transistion"
                    ) { targetGame ->
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
                                    /*Dash Rect*/
                                    drawRoundRect(
                                        color = Color(0xFF79D5F5),
                                        cornerRadius = CornerRadius(15.dp.toPx()),
                                        style = Stroke(
                                            width = 2.dp.toPx(),
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
                                    "Match Up" -> ShapeMatchUpUI()
                                    "Shape Fun" -> ShapeFunUI()
                                    "Magic Trace" -> MagicTraceUI()
                                    "Shape Hunt" -> ShapeHuntUI()
                                }
                            }
                        }
                    }
                }
                // Try These Block
                Text(
                    text = "Try these",
                    fontSize = 16.sp,
                    fontFamily = FontFamily(Font(R.font.balootwomediam)),
                    color = Color.Black,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )

                val games = listOf(
                    GameItem("Match Up", R.drawable.matchupgameimage),
                    GameItem("Shape Fun", R.drawable.shapefungameimg),
                    GameItem("Magic Trace", R.drawable.magictracegameimage),
                    GameItem("Shape Hunt", R.drawable.shapehuntgameimg)
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
                                        interactionSource = remember { MutableInteractionSource() }
                                    ) {
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



// Magic trace UI

@Composable
fun MagicTraceUI() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // States
    val shapes = remember { listOf("Circle", "Square", "Triangle", "Rectangle", "Pentagon","Oval") }
    var currentShapeIndex by remember { mutableIntStateOf(0) }
    var tracingColor by remember { mutableStateOf(Color(0xFF1F9856)) }
    val reachedPoints = remember { mutableStateListOf<Offset>() }
    var redrawTrigger by remember { mutableIntStateOf(0) }
    var showLottie by remember { mutableStateOf(false) }
    var isTransitioning by remember { mutableStateOf(false) }

    val currentShape = shapes[currentShapeIndex]

    // TTS Setup
    val tts = remember {
        var ttsInstance: TextToSpeech? = null
        ttsInstance = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                ttsInstance?.language = java.util.Locale.US
                ttsInstance?.speak("Let's Trace the $currentShape", TextToSpeech.QUEUE_FLUSH, null, null)
            }
        }
        ttsInstance
    }

    LaunchedEffect(currentShapeIndex) {
        tts.speak("Let's trace the $currentShape", TextToSpeech.QUEUE_FLUSH, null, null)
    }
    DisposableEffect(Unit) {
        onDispose {
            tts?.stop()
            tts?.shutdown()
        }
    }
    // Reuse TracingGameUI Logic
    Box(modifier = Modifier.fillMaxSize()) {
        ShapeTracingUI(
            shapeName = currentShape,
            color = tracingColor,
            reachedPoints = reachedPoints,
            trigger = redrawTrigger,
            showLottie = showLottie,
            onUpdate = {
                if (isTransitioning) return@ShapeTracingUI

                val allPoints = ShapeDataf.getPoints(currentShape).flatten()
                val totalPoints = allPoints.size

                if (totalPoints > 0) {
                    val currentReachedUnique = reachedPoints.distinct().size
                    val progress = currentReachedUnique.toFloat() / totalPoints

                    val lastPoint = allPoints.lastOrNull()
                    val isAtEnd = lastPoint != null && reachedPoints.contains(lastPoint)

                    // 93% completion or reached last point
                    if (progress >= 1.0f || (progress >= 0.95f && isAtEnd)) {
                        isTransitioning = true
                        showLottie = true

                        scope.launch {
                            delay(2000)
                            reachedPoints.clear()
                            currentShapeIndex = (currentShapeIndex + 1) % shapes.size
                            showLottie = false
                            redrawTrigger++
                            isTransitioning = false
                        }
                    } else {
                        redrawTrigger++
                    }
                }
            },
            onColorChange = { tracingColor = it }
        )
    }
}
@Composable
fun ShapeTracingUI(
    shapeName: String,color: Color,
    reachedPoints: MutableList<Offset>,
    trigger: Int,
    showLottie: Boolean,
    onUpdate: () -> Unit,
    onColorChange: (Color) -> Unit
) {
    val context = LocalContext.current
    val guideSegments = remember(shapeName) { ShapeDataf.getPoints(shapeName) }
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.tracinglottie))
    // --- SUCCESS SOUND SETUP ---
    val completionSound = remember(shapeName) {
        MediaPlayer.create(context, R.raw.tracingonesidesound)
    }


    LaunchedEffect(showLottie) {
        if (showLottie) {
            completionSound.start()
        }
    }

    DisposableEffect(shapeName) {
        onDispose {
            completionSound.release()
        }
    }

    // Animation for pulse guide
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = 1.3f,
        animationSpec = infiniteRepeatable(tween(600), RepeatMode.Reverse), label = ""
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f, targetValue = 0.9f,
        animationSpec = infiniteRepeatable(tween(600), RepeatMode.Reverse), label = ""
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.smallstars), // Use your star resource
            contentDescription = null,
            modifier = Modifier
                .size(60.dp)
                .offset(x = 20.dp, y = 30.dp)
                .alpha(0.6f)
        )
        Image(
            painter = painterResource(id = R.drawable.smallstars),
            contentDescription = null,
            modifier = Modifier
                .size(40.dp)
                .align(Alignment.CenterEnd)
                .offset(x = (-30).dp, y = 20.dp)
                .alpha(0.6f)
        )
        Image(
            painter = painterResource(id = R.drawable.smallstars),
            contentDescription = null,
            modifier = Modifier
                .size(45.dp)
                .align(Alignment.BottomStart)
                .offset(x = 30.dp, y = (-180).dp)
                .alpha(0.6f)
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text= "Let’s trace the $shapeName",
                modifier = Modifier.padding(top = 16.dp),
                fontSize = 22.sp, fontFamily = FontFamily(Font(R.font.balootwomediam)),
                color = Color(0xFF07698B)
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(40.dp)
                        .pointerInput(shapeName) {
                            if (showLottie) return@pointerInput
                            detectDragGestures { change, _ ->
                                change.consume()
                                guideSegments.forEachIndexed { sIdx, segment ->
                                    val isPrevSegmentDone =
                                        if (sIdx > 0) reachedPoints.contains(guideSegments[sIdx - 1].last()) else true
                                    if (isPrevSegmentDone) {
                                        segment.forEachIndexed { pIdx, pt ->
                                            val isPrevPtDone =
                                                if (pIdx > 0) reachedPoints.contains(segment[pIdx - 1]) else true
                                            if (isPrevPtDone) {
                                                val target =
                                                    Offset(pt.x * size.width, pt.y * size.height)
                                                if (change.position.distanceTo(target) < 80f) {
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
                        }
                ) {
                    val w = size.width
                    val h = size.height

                    // 1. Background Outline (White with Orange Border)
                    val bgPath = Path()
                    guideSegments.forEach { seg ->
                        seg.forEachIndexed { i, pt ->
                            if (i == 0) bgPath.moveTo(
                                pt.x * w,
                                pt.y * h
                            ) else bgPath.lineTo(pt.x * w, pt.y * h)
                        }
                    }
                    drawPath(
                        bgPath,
                        Color(0xFF79D5F5),
                        style = Stroke(100f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                    )
                    drawPath(
                        bgPath,
                        Color.White,
                        style = Stroke(85f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                    )

                    // 2. User Progress Tracing
                    guideSegments.forEach { seg ->
                        for (i in 0 until seg.size - 1) {
                            if (reachedPoints.contains(seg[i]) && reachedPoints.contains(seg[i + 1])) {
                                drawLine(
                                    color,
                                    Offset(seg[i].x * w, seg[i].y * h),
                                    Offset(seg[i + 1].x * w, seg[i + 1].y * h),
                                    85f,
                                    cap = StrokeCap.Round
                                )
                            }
                        }
                    }

                    // 3. Pulse Guide (Arrow/Dot)
                    guideSegments.forEachIndexed { index, seg ->
                        val isPrevDone =
                            if (index > 0) reachedPoints.contains(guideSegments[index - 1].last()) else true
                        val isCurrDone = reachedPoints.contains(seg.last())
                        if (isPrevDone && !isCurrDone) {
                            drawStaticGuide(
                                start = Offset(seg[0].x * w, seg[0].y * h),
                                next = Offset(seg[1].x * w, seg[1].y * h),
                                color = color, scale = pulseScale, alpha = pulseAlpha
                            )
                        }
                    }
                }

                if (showLottie) {
                    LottieAnimation(
                        composition, iterations = 1, modifier = Modifier
                            .fillMaxSize()
                            .scale(1.5f)
                    )
                }
            }

            // Color Picker Row
            Row(
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier.padding(bottom = 20.dp)
            ) {
                listOf(
                    Color(0xFF1F9856),
                    Color(0xFFED1C2A),
                    Color(0xFF1DB3DB),
                ).forEach { c ->
                    val isSelected = color == c
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

                    Box(
                        modifier = Modifier
                            .offset(y = offsetBy)
                            .graphicsLayer(scaleX = scale, scaleY = scale)
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(c)
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ) { onColorChange(c) })
                }
            }
        }
    }
}
//shape hunt Ui
@Composable
fun ShapeHuntUI() {
    val context = LocalContext.current
    // vibraton on wrong answer
    val vibrator = remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    }

    var wrongOption by remember { mutableStateOf<String?>(null) } // for Wrong tracking

    var selectedOption by remember { mutableStateOf<String?>(null) }
    // 1. Shapes Data
    val allShapes = remember {
        listOf(
            ShapeHuntItem("Circle", R.drawable.bigcircleshape),
            ShapeHuntItem("Triangle", R.drawable.bigtrianlgeshape),
            ShapeHuntItem("Square", R.drawable.bigsquareshape),
            ShapeHuntItem("Rhombus", R.drawable.bigrombasshape),
            ShapeHuntItem("Rectangle", R.drawable.bigrectangleshape),
            ShapeHuntItem("Oval", R.drawable.bigovalshape),
            ShapeHuntItem("Pentagon", R.drawable.bigpolygoneshape)
        )
    }

    // 2. States
    var currentShapeIndex by remember { mutableStateOf(0) }
    val currentShape = allShapes[currentShapeIndex]


    val options = remember(currentShapeIndex) {
        val wrongOption = allShapes.filter { it.name != currentShape.name }.random()
        listOf(currentShape.name, wrongOption.name).shuffled()
    }
    var showSuccessLottie by remember { mutableStateOf(false) }
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.tracinglottie))
    val successSound = remember { MediaPlayer.create(context, R.raw.tracingonesidesound) }


    // TTS Engine
    val tts = remember {
        var ttsInstance: TextToSpeech? = null
        ttsInstance = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                ttsInstance?.language = java.util.Locale.US
                ttsInstance?.speak("Guess the shape name", TextToSpeech.QUEUE_FLUSH, null, null)
            }
        }
        ttsInstance
    }


    LaunchedEffect(currentShapeIndex) {
        selectedOption = null
        wrongOption = null
    }
    LaunchedEffect(showSuccessLottie) {
        if (!showSuccessLottie) {
            selectedOption = null // Reset selection for next shape
        }
    }

    // Clean up TTS when leaving screen
    DisposableEffect(Unit) {
        onDispose {
            successSound.release()
            tts?.stop()
            tts?.shutdown()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        Image(
            painter = painterResource(id = R.drawable.smallstars), // Use your star resource
            contentDescription = null,
            modifier = Modifier
                .size(60.dp)
                .offset(x = 20.dp, y = 30.dp)
                .alpha(0.6f)
        )
        Image(
            painter = painterResource(id = R.drawable.smallstars),
            contentDescription = null,
            modifier = Modifier
                .size(40.dp)
                .align(Alignment.CenterEnd)
                .offset(x = (-30).dp, y = 20.dp)
                .alpha(0.6f)
        )
        Image(
            painter = painterResource(id = R.drawable.smallstars),
            contentDescription = null,
            modifier = Modifier
                .size(45.dp)
                .align(Alignment.BottomStart)
                .offset(x = 30.dp, y = (-180).dp)
                .alpha(0.6f)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Guess the shape name",
                fontFamily = FontFamily(Font(R.font.balootwomediam)),
                fontSize = 22.sp,
                color = Color(0xFF07698B),
                modifier = Modifier.padding(top = 10.dp)
            )

            Spacer(modifier = Modifier.weight(0.5f))

            // --- MAIN BIG SHAPE ---
            Image(
                painter = painterResource(id = currentShape.imageRes),
                contentDescription = null,
                modifier = Modifier
                    .size(220.dp)
                    .animateContentSize(),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.weight(1f))

            // --- BUTTONS ROW (WITH SHADOW) ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 30.dp, start = 6.dp, end = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                options.forEach { optionName ->
                    val isCorrectSelection = selectedOption == optionName && optionName == currentShape.name
                    val isWrongSelection = wrongOption == optionName
                    ShapeOptionButton(
                        text = optionName,
                        isCorrect = isCorrectSelection,
                        isWrong = isWrongSelection,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            selectedOption = optionName
                            if (optionName == currentShape.name) {
                                // Correct Answer
                                wrongOption = null
                                successSound.seekTo(0)
                                successSound.start()
                                showSuccessLottie = true
                                CoroutineScope(Dispatchers.Main).launch {
                                    delay(2000)
                                    showSuccessLottie = false
                                    // Next Shape
                                    currentShapeIndex = (currentShapeIndex + 1) % allShapes.size
                                }
                            }else{
                                wrongOption = optionName
                                // Vibration alert
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
                                } else {
                                    vibrator.vibrate(200)
                                }
                                CoroutineScope(Dispatchers.Main).launch {
                                    delay(1000)
                                    wrongOption = null
                                }

                            }
                        }
                    )
                }
            }
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
}
@Composable
fun ShapeOptionButton(
    text: String,
    isCorrect: Boolean,
    isWrong: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val topGradientColors = when {
        isCorrect -> listOf(Color(0xFF66BB6A), Color(0xFF43A047))
        isWrong -> listOf(Color(0xFFED1C2A), Color(0xFFED1C2A))
        else -> listOf(Color(0xFF42C7F1), Color(0xFF2EB4E2))
    }
    val depthColor = when{
        isCorrect -> Color(0xFF2E7D32)
        isWrong -> Color(0xFFED1C2A)
        else -> Color(0xFF1B8BB5)
    }

    Box(
        modifier = modifier
            .height(70.dp)
            .padding(horizontal = 4.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
    ) {
        // --- 1. Outer Shadow bottom
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset(y = 4.dp)
                .background(
                    color = Color.Black.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(50.dp)
                )
                .blur(4.dp)
        )

        // --- 2. DEPTH LAYER
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = depthColor, // Solid Dark Blue
                    shape = RoundedCornerShape(50.dp)
                )
        )

        // --- 3. TOP LAYER
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 8.dp)
                .clip(RoundedCornerShape(50.dp))
                .background(brush = Brush.verticalGradient(colors = topGradientColors)),
            contentAlignment = Alignment.Center
        ) {
            // Text with Shadow
            Text(
                text = text,
                color = Color.White,
                fontSize = 20.sp,
                fontFamily = FontFamily(Font(R.font.balootwomediam)),
                style = TextStyle(
                    shadow = Shadow(
                        color = Color.Black.copy(alpha = 0.25f),
                        offset = Offset(0f, 4f),
                        blurRadius = 5f
                    )
                )
            )
        }
    }
}
data class ShapeHuntItem(val name: String, val imageRes: Int)

//shape fun ui
@Composable
fun ShapeFunUI() {
    val context = LocalContext.current

// --- TUTORIAL STATES ---
    var isTutorialActive by remember { mutableStateOf(true) }
    val tutorialAnim = remember {Animatable(0f) }


    // Game State
    val shapesList = remember {
        listOf(
            ShapeDragItem("Square", R.drawable.squarshape, R.drawable.squareoutline),
            ShapeDragItem("Triangle", R.drawable.triangleshape, R.drawable.triangleouline),
            ShapeDragItem("Circle", R.drawable.circleshape, R.drawable.circleoutline),
            ShapeDragItem("Star", R.drawable.starshape, R.drawable.starshapeoutline),
            ShapeDragItem("Rhombus", R.drawable.rombasshape, R.drawable.rombasoutline),
            ShapeDragItem("Rectangle", R.drawable.rectshape, R.drawable.rectoutline),
            ShapeDragItem("Pentagon", R.drawable.polygoneshape, R.drawable.polygoneoutline),
            ShapeDragItem("Oval", R.drawable.ovalshape, R.drawable.ovalshapeoutline),
        )
    }

    var batchIndex by remember { mutableStateOf(0) }
    // 3. Current 3 shapes to show (Shuffle logic included)
    val currentBatch = remember(batchIndex) {
        shapesList.shuffled().take(3)
    }
    // Positions tracking
    val targetPositions = remember { mutableStateMapOf<String, Offset>() }
    val solvedShapes = remember { mutableStateListOf<String>() }
    var showSuccessLottie by remember { mutableStateOf(false) }
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.tracinglottie))
    val successSound = remember { MediaPlayer.create(context, R.raw.tracingonesidesound) }


    // --- TTS Engine ---
    val tts = remember {
        var ttsInstance: TextToSpeech? = null
        ttsInstance = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                ttsInstance?.language = java.util.Locale.US
                ttsInstance?.speak("Let's sort the shapes", TextToSpeech.QUEUE_FLUSH, null, null)
            }
        }
        ttsInstance
    }

    // Tutorial loop: 0f se 1f
    LaunchedEffect(isTutorialActive) {
        if (isTutorialActive) {
            while (true) {
                tutorialAnim.snapTo(0f)
                tutorialAnim.animateTo(
                    targetValue = 1f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(2000, easing = FastOutSlowInEasing),
                        repeatMode = RepeatMode.Restart
                    )
                )
                delay(500)
            }
        }
    }

    // Level Complete Check
    LaunchedEffect(solvedShapes.size) {
        if (solvedShapes.size ==3) {
            successSound.start()
            showSuccessLottie = true
            delay(2500)
            showSuccessLottie = false
            solvedShapes.clear()
            targetPositions.clear()
            batchIndex++
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            tts?.stop()
            tts?.shutdown()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        Image(
            painter = painterResource(id = R.drawable.smallstars), // Use your star resource
            contentDescription = null,
            modifier = Modifier
                .size(60.dp)
                .offset(x = 20.dp, y = 30.dp)
                .alpha(0.6f)
        )
        Image(
            painter = painterResource(id = R.drawable.smallstars),
            contentDescription = null,
            modifier = Modifier
                .size(40.dp)
                .align(Alignment.CenterEnd)
                .offset(x = (-30).dp, y = 20.dp)
                .alpha(0.6f)
        )
        Image(
            painter = painterResource(id = R.drawable.smallstars),
            contentDescription = null,
            modifier = Modifier
                .size(45.dp)
                .align(Alignment.BottomStart)
                .offset(x = 30.dp, y = (-180).dp)
                .alpha(0.6f)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Let's sort the shapes",
                fontFamily = FontFamily(Font(R.font.balootwomediam)),
                fontSize = 22.sp,
                color = Color(0xFF07698B),
                modifier = Modifier.padding(top = 20.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            ) {
                val width = constraints.maxWidth.toFloat()
                val height = constraints.maxHeight.toFloat()

                currentBatch.forEachIndexed { index, item ->
                    if (!solvedShapes.contains(item.id)) {
                        // Percentage based positions (0.0f to 1.0f)
                        val (relX, relY, rotation) = when (index) {
                            // Top Left (approx 15% from left)
                            0 -> Triple(0.15f, 0.1f, 0f)
                            // Top Right (approx 65% from left)
                            1 -> Triple(0.65f, 0.15f, 20f)
                            // Center Bottom (approx 40% from left, 50% from top)
                            2 -> Triple(0.40f, 0.55f, -15f)
                            else -> Triple(0.5f, 0.5f, 0f)
                        }

                        // Density use karke pixels ko offset mein convert karna
                        val density = LocalDensity.current
                        val offsetX = with(density) { (relX * width).toDp() }
                        val offsetY = with(density) { (relY * height).toDp() }

                        Box(modifier = Modifier.offset(x = offsetX, y = offsetY)) {
                            DraggableShape(item, rotation) { finalPos ->
                                if (isTutorialActive) isTutorialActive = false
                                val targetPos = targetPositions[item.id]
                                if (targetPos != null) {
                                    val detectionRadius = with(density) { 60.dp.toPx() }
                                    val distance = (finalPos - targetPos).getDistance()

                                    if (distance < detectionRadius) {
                                        solvedShapes.add(item.id)
                                        tts?.speak(item.id, TextToSpeech.QUEUE_FLUSH, null, null)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // --- 2. TARGET SHAPES AREA (Bottom Outlines) ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {

                currentBatch.forEach { item ->
                    Image(
                        painter = if (solvedShapes.contains(item.id))
                            painterResource(id = item.shapeRes)
                        else
                            painterResource(id = item.outlineRes),
                        contentDescription = null,
                        modifier = Modifier
                            .wrapContentSize()
                            .onGloballyPositioned { coords ->
                                targetPositions[item.id] = coords.positionInRoot()
                            }
                            .graphicsLayer {
                                rotationZ = 0f
                            }
                    )
                }
            }
        }

        // --- HAND GUIDE OVERLAY ---
        if (isTutorialActive && currentBatch.isNotEmpty()) {
            val firstShapeId = currentBatch[0].id
            val targetPos = targetPositions[firstShapeId]
            if (targetPos != null) {
                val startX = 60.dp
                val startY = 150.dp
                val currentX = lerp(startX, 60.dp, tutorialAnim.value)
                val currentY = lerp(startY, 550.dp, tutorialAnim.value)

                Image(
                    painter = painterResource(id = R.drawable.guidehandimage),
                    contentDescription = null,
                    modifier = Modifier
                        .size(50.dp)
                        .offset(x = currentX, y = currentY)
                        .zIndex(20f)
                )
            }
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
}

@Composable
fun DraggableShape(
    item: ShapeDragItem,
    initialRotation: Float,
    onDrop: (Offset) -> Unit
) {
    var dragOffset by remember { mutableStateOf(Offset.Zero) }
    var initialPosition by remember { mutableStateOf(Offset.Zero) }
    var isDragging by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .onGloballyPositioned { coords ->
                if (!isDragging) initialPosition = coords.positionInRoot()
            }
            .offset { IntOffset(dragOffset.x.toInt(), dragOffset.y.toInt()) }
            .size(85.dp)
            .zIndex(if (isDragging) 10f else 1f)
            .graphicsLayer {
                rotationZ = initialRotation
                scaleX = if (isDragging) 1.1f else 1f
                scaleY = if (isDragging) 1.1f else 1f
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { isDragging = true },
                    onDragEnd = {
                        isDragging = false
                        onDrop(initialPosition + dragOffset)
                        dragOffset = Offset.Zero
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        dragOffset += dragAmount
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = item.shapeRes),
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )
    }
}

data class ShapeDragItem(
    val id: String,
    val shapeRes: Int,
    val outlineRes: Int
)

// shape matched game

data class ShapeCard(
    val id: Int,
    val shapeRes: Int,
    var isFlipped: Boolean = false,
    var isMatched: Boolean = false
)

@Composable
fun ShapeMatchUpUI() {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()


    // Tutorial and Animation States di hai
    // --- TUTORIAL & ANIMATION STATES ---
    var isTutorialActive by remember { mutableStateOf(true) }
    val infiniteTransition = rememberInfiniteTransition(label = "HandGuide")
    val handOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 15f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "HandMove"
    )


    var showSuccessLottie by remember { mutableStateOf(false) }
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.tracinglottie))
    val completionSound = remember { MediaPlayer.create(context, R.raw.tracingonesidesound) }
    val flipSound = remember { MediaPlayer.create(context, R.raw.flipnewsound) } //

    // 1. Shapes ki list
    val shapes = remember {
        listOf(
            R.drawable.circlematchup, R.drawable.squarematchup,
            R.drawable.trianglematchup, R.drawable.rectanglematchup,
            R.drawable.polygonmatchup, R.drawable.starmatchup
        )
    }

    // 2. Game State
    var cards by remember {
        mutableStateOf((shapes + shapes).shuffled().mapIndexed { index, res ->
            ShapeCard(id = index, shapeRes = res)
        })
    }

    // Selection tracking
    var firstSelectedIdx by remember { mutableStateOf<Int?>(null) }
    var secondSelectedIdx by remember { mutableStateOf<Int?>(null) }
    var matchedCount by remember { mutableIntStateOf(0) }

    // TTS Engine
    val tts = remember {
        var ttsInstance: TextToSpeech? = null
        ttsInstance = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                ttsInstance?.language = java.util.Locale.US
                ttsInstance?.speak("Let's find the matching pairs", TextToSpeech.QUEUE_FLUSH, null, null)
            }
        }
        ttsInstance
    }

    // Level Completion Logic
    LaunchedEffect(matchedCount) {
        if (matchedCount == 6){
            delay(300)
           completionSound.start()
            showSuccessLottie = true
            delay(1500)
            showSuccessLottie = false
            matchedCount = 0
            cards  = (shapes + shapes).shuffled().mapIndexed { index, res ->
                ShapeCard(id = index, shapeRes = res)
            }
            firstSelectedIdx = null
            secondSelectedIdx = null
            delay(200)
            tts?.speak("Let's find the matching pairs again", TextToSpeech.QUEUE_ADD, null, null)
        }
    }



    DisposableEffect(Unit) {
        onDispose {
            tts?.stop()
            tts?.shutdown()
            completionSound.release()
            flipSound.release()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp, start = 12.dp, bottom = 16.dp, end = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Matched : $matchedCount",
            fontFamily = FontFamily(Font(R.font.balootwomediam)),
            fontSize = 18.sp,modifier = Modifier.padding(top = 4.dp),
            color = Color(0xFF7E7C7C)
        )
        Text(
            text = "Let's find the matching pairs",
            fontFamily = FontFamily(Font(R.font.balootwomediam)),
            fontSize = 20.sp,
            color = Color(0xFF07698B),
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // 3. 3x4 Grid for 12 Cards
        val columns = 3
        Column(verticalArrangement = Arrangement.spacedBy(17.dp)) {
            for (i in 0 until 4) { // 4 rows
                Row(horizontalArrangement = Arrangement.spacedBy(17.dp)) {
                    for (j in 0 until columns) {
                        val index = i * columns + j
                        val card = cards[index]

                        Box(
                            modifier = Modifier
                                .size(70.dp)
                                .clickable(
                                    enabled = !card.isFlipped && !card.isMatched && secondSelectedIdx == null,
                                    indication = null,
                                    interactionSource = remember { MutableInteractionSource() }
                                ) {
                                    // Tutorial Dismiss

                                    if (isTutorialActive) isTutorialActive = false

                                    if (flipSound.isPlaying) {
                                        flipSound.pause()
                                        flipSound.seekTo(0)
                                    }
                                    flipSound.start()

                                    // Flip Logic
                                    cards = cards.mapIndexed { idx, c ->
                                        if (idx == index) c.copy(isFlipped = true) else c
                                    }

                                    if (firstSelectedIdx == null) {
                                        firstSelectedIdx = index
                                    } else {
                                        secondSelectedIdx = index
                                        // Check Match
                                        scope.launch {
                                            delay(800)
                                            if (cards[firstSelectedIdx!!].shapeRes == cards[secondSelectedIdx!!].shapeRes) {
                                                // MATCHED!
                                                cards = cards.mapIndexed { idx, c ->
                                                    if (idx == firstSelectedIdx || idx == secondSelectedIdx)
                                                        c.copy(isMatched = true) else c
                                                }
                                                matchedCount++
                                                val shapeName =
                                                    getShapeName(cards[firstSelectedIdx!!].shapeRes)
                                                tts?.speak(
                                                    "$shapeName shapes matched",
                                                    TextToSpeech.QUEUE_FLUSH,
                                                    null,
                                                    null
                                                )

                                            } else {
                                                // NOT MATCHED - Flip back
                                                cards = cards.mapIndexed { idx, c ->
                                                    if (idx == firstSelectedIdx || idx == secondSelectedIdx)
                                                        c.copy(isFlipped = false) else c
                                                }
                                            }
                                            firstSelectedIdx = null
                                            secondSelectedIdx = null
                                        }
                                    }
                                }
                        ) {
                            CardView(card)
                            if (isTutorialActive && index == 0) {
                                Image(
                                    painter = painterResource(id = R.drawable.guidehandimage),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(50.dp)
                                        .offset(y = 30.dp + handOffset.dp, x = 20.dp)
                                        .zIndex(5f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
    // --- SUCCESS LOTTIE OVERLAY ---
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

// Helper function for getting shape name
fun getShapeName(resId: Int): String {
    return when (resId) {
        R.drawable.circlematchup -> "Circle"
        R.drawable.squarematchup -> "Square"
        R.drawable.trianglematchup -> "Triangle"
        R.drawable.rectanglematchup -> "Rectangle"
        R.drawable.polygonmatchup -> "Rhombus"
        R.drawable.starmatchup -> "Star"
        else -> ""
    }
}
@Composable
fun CardView(card: ShapeCard) {

    val rotation by animateFloatAsState(
        targetValue = if (card.isFlipped || card.isMatched) 180f else 0f,
        animationSpec = tween(durationMillis = 400),
        label = "cardFlip"
    )

    val isBackVisible = rotation > 90f

    Card(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 12f * density
            },
        shape = RoundedCornerShape(15.dp),
        colors = CardDefaults.cardColors(containerColor = if (isBackVisible) Color.White else Color(0xFF79D5F5)),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isBackVisible) 0.dp else 4.dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (!isBackVisible) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(6.dp)
                ) {
                    drawRoundRect(
                        color = Color.White,
                        cornerRadius = CornerRadius(10.dp.toPx()),
                        style = Stroke(
                            width = 1.5.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f))
                        )
                    )
                }
                Image(
                    painter = painterResource(id = R.drawable.questionmarkicon),
                    contentDescription = null,
                    modifier = Modifier.size(30.dp)
                )
            }
            else{
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer { rotationY = 180f },
                    contentAlignment = Alignment.Center
                ) {
                    // Dashed inner border effect
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(6.dp)
                    ) {
                        drawRoundRect(
                            color = Color(0xFF79D5F5),
                            cornerRadius = CornerRadius(10.dp.toPx()),
                            style = Stroke(
                                width = 1.dp.toPx(),
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f))
                            )
                        )
                    }
                    Image(
                        painter = painterResource(id = card.shapeRes),
                        contentDescription = null,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
        }
    }
}

