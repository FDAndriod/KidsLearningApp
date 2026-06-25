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
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.fontscaling.MathUtils.lerp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavHostController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
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

@Composable
fun ColorGameScreen(
    navController: NavHostController, mainViewModel: MainViewModel
) {
    var selectedGame by remember { mutableStateOf("Learn Color") }
    //try these cards animation
    var startSpreading by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(300)
        startSpreading = true
    }


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
    Box(modifier = Modifier.fillMaxSize()) {
        TrackGameBadgeProgress(mainViewModel, BadgeGameType.Color)
        CompositionLocalProvider(
            LocalDensity provides Density(
                LocalDensity.current.density,
                fontScale = 1f
            )
        ) {
            // Screen Background
            Image(
                painter = painterResource(id = R.drawable.colorscreenbg),
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
                                indication = null, interactionSource = remember { MutableInteractionSource() }
                            ) { navController.navigateUp() })
                    Text(
                        text = if (selectedGame == "Color Pop") "Color Pop" else selectedGame,
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
                        if (targetGame != "Color Pop") {
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
                                            color = Color(0xFF36EBB4),
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
                                    when (targetGame) {
                                        "Color Match" -> ColorMatchUI()
                                        "Learn Color" -> LearnColorUI()
                                        "Color Fun" -> ColorFunUI()
                                    }
                                }
                            }
                        } else {
                            ColorPopUI()
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
                    GameItem("Learn Color", R.drawable.learncolorgame),
                    GameItem("Color Match", R.drawable.colormatchgame),
                    GameItem("Color Fun", R.drawable.colorfungame),
                    GameItem("Color Pop", R.drawable.colorpopgame)
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
// Learn Color game UI
@Composable
fun LearnColorUI() {
    val context = LocalContext.current
    val learnColors = remember {
        listOf(
            LearnColorItem("Red", Color(0xFFED1C2A), R.drawable.redflower, R.drawable.redpaint),
            LearnColorItem("Blue", Color(0xFF007AFF), R.drawable.blueflower, R.drawable.bluepaint),
            LearnColorItem(
                "Green",
                Color(0xFF34C759),
                R.drawable.greenflower,
                R.drawable.greenpaint
            ),
            LearnColorItem("Pink", Color(0xFFF14A6A), R.drawable.pinkflower, R.drawable.pinkpaint),
            LearnColorItem(
                "Yellow",
                Color(0xFFFFCC00),
                R.drawable.yellowflower,
                R.drawable.yellowpaint
            ),
            LearnColorItem(
                "Orange",
                Color(0xFFFF9500),
                R.drawable.oranageflower,
                R.drawable.orangepaint
            ),
            LearnColorItem(
                "Purple",
                Color(0xFF67419F),
                R.drawable.purpleflower,
                R.drawable.purplepaint
            ),
            LearnColorItem(
                "Brown",
                Color(0xFF8C5209),
                R.drawable.brownflower,
                R.drawable.brownpaint
            ),
            LearnColorItem(
                "White",
                Color(0xFFFFFFFF),
                R.drawable.whiteflower,
                R.drawable.whitepaint
            ),
            LearnColorItem(
                "Black",
                Color(0xFF000000),
                R.drawable.blackflower,
                R.drawable.blackpaint
            )
        )
    }
    // 2. State Initialization (From SharedPreferences)
    var selectedColorData by remember {
        val savedName = getSavedColor(context)
        mutableStateOf(learnColors.find { it.name == savedName } ?: learnColors[0])
    }

    val tts = remember {
        var ttsInstance: TextToSpeech? = null
        ttsInstance = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                ttsInstance?.language = java.util.Locale.US
                ttsInstance?.speak("Let's Learn the colors", TextToSpeech.QUEUE_FLUSH, null, null)
            }
        }
        ttsInstance
    }
    var isTutorialActive by remember { mutableStateOf(true) }
    val tutorialAnim = remember { Animatable(0f) }
// Hand guide animation loop
    LaunchedEffect(isTutorialActive) {
        if (isTutorialActive) {
            while (true) {
                tutorialAnim.snapTo(0f)
                tutorialAnim.animateTo(
                    targetValue = 1f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1500, easing = FastOutSlowInEasing),
                        repeatMode = RepeatMode.Restart
                    )
                )
                delay(500)
            }
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
                .padding(start = 12.dp, end = 12.dp, top = 12.dp, bottom = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Let's learn the colors",
                fontFamily = FontFamily(Font(R.font.balootwomediam)),
                fontSize = 20.sp,
                color = Color(0xFF07698B),
                modifier = Modifier.padding(top = 8.dp)
            )

            // Selected Color Text
            Text(
                text = selectedColorData.name.uppercase(),
                fontFamily = FontFamily(Font(R.font.balootwobold)),
                fontSize = 32.sp,
                color = if (selectedColorData.name == "White") Color.Gray else selectedColorData.color,
                modifier = Modifier.padding(vertical = 6.dp)
            )
            // --- FLOWER DISPLAY ---
            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                // Layer 1: Petals
                Image(
                    painter = painterResource(id = selectedColorData.flowerRes), //
                    contentDescription = null,
                    modifier = Modifier.size(160.dp)
                )
            }

            Spacer(modifier = Modifier.height(26.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 3.dp, end = 3.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // First Row (5 Cards)
                ColorRow(learnColors.take(5), selectedColorData) { colorItem ->
                    isTutorialActive = false // Tutorial stop
                    selectedColorData = colorItem
                    saveSelectedColor(context, colorItem.name)
                    tts?.speak(
                        colorItem.name,
                        TextToSpeech.QUEUE_FLUSH,
                        null,
                        null
                    )
                }

                // Second Row (5 Cards)
                ColorRow(learnColors.drop(5), selectedColorData) { colorItem ->
                    isTutorialActive = false // Tutorial stop
                    selectedColorData = colorItem
                    saveSelectedColor(context, colorItem.name)
                    tts?.speak(
                        colorItem.name,
                        TextToSpeech.QUEUE_FLUSH,
                        null,
                        null
                    )
                }
            }
        }
    }
    // --- HAND GUIDE OVERLAY ---
    if (isTutorialActive) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val maxWidth = constraints.maxWidth.toFloat()
            val maxHeight = constraints.maxHeight.toFloat()
            val density = LocalDensity.current

            val handX = with(density) { (0.15f * maxWidth) }
            val handY = with(density) { maxHeight - 110.dp.toPx() }

            val animatedY = lerp(handY, handY + 30f, tutorialAnim.value)

            Image(
                painter = painterResource(id = R.drawable.guidehandimage),
                contentDescription = null,
                modifier = Modifier
                    .size(50.dp)
                    .offset {
                        IntOffset(
                            (handX).toInt(),
                            (animatedY).toInt()
                        )
                    }
                    .zIndex(200f)
            )
        }
    }
}
// SharedPreferences Helpers
fun saveSelectedColor(context: Context, colorName: String) {
    val prefs = context.getSharedPreferences("color_game_prefs", Context.MODE_PRIVATE)
    prefs.edit().putString("last_color", colorName).apply()
}

fun getSavedColor(context: Context): String {
    val prefs = context.getSharedPreferences("color_game_prefs", Context.MODE_PRIVATE)
    return prefs.getString("last_color", "Red") ?: "Red"
}
data class LearnColorItem(
    val name: String,
    val color: Color,
    val flowerRes: Int,
    val paintRes: Int
)

@Composable
fun ColorRow(
    colors: List<LearnColorItem>,
    selectedColor: LearnColorItem,
    onColorClick: (LearnColorItem) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 2.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        colors.forEach { colorItem ->
            val isSelected = selectedColor.name == colorItem.name

            Card(
                modifier = Modifier
                    .weight(1f) // Responsive width
                    .aspectRatio(1.1f)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) { onColorClick(colorItem) },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) Color(0xFFF0FBFF) else Color.White
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = if (isSelected) 2.dp else 2.dp
                ),
                border = if (isSelected) BorderStroke(2.dp, Color.Transparent) else null
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = colorItem.paintRes),
                        contentDescription = null,
                        modifier = Modifier
                            .size(40.dp)
                            .graphicsLayer {

                                scaleX = if (isSelected) 1.1f else 1f
                                scaleY = if (isSelected) 1.1f else 1f
                            },
                        )
                }
            }
        }
    }
}

// Color Match Ui
@Composable
fun ColorMatchUI() {
    val context = LocalContext.current
    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

    val matchColors = remember {
        listOf(
            ColorData(Color(0xFF007AFF), "Blue"),
            ColorData(Color(0xFFFF3B30), "Red"),
            ColorData(Color(0xFF34C759), "Green"),
            ColorData(Color(0xFFF14A6A), "Pink"),
            ColorData(Color(0xFF000000), "Black"),
            ColorData(Color(0xFFEF711F), "Orange"),
            ColorData(Color(0xFF67419F), "Purple"),
            ColorData(Color(0xFF8C5209), "Brown"),
            ColorData(Color(0xFF7E7C7C), "Grey"),
            ColorData(Color(0xFFFFCC00), "Yellow")
        )
    }

    // Game States
    var levelTrigger by remember { mutableIntStateOf(0) }
    var targetColor by remember { mutableStateOf(matchColors[0]) }
    var options by remember { mutableStateOf(listOf<ColorData>()) }
    var selectedOption by remember { mutableStateOf<String?>(null) }
    var isCorrect by remember { mutableStateOf<Boolean?>(null) }

    var showSuccessLottie by remember { mutableStateOf(false) }
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.tracinglottie))
    val successSound = remember { MediaPlayer.create(context, R.raw.tracingonesidesound) }
    val tts = remember {
        var ttsInstance: TextToSpeech? = null
        ttsInstance = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                ttsInstance?.language = java.util.Locale.US
                ttsInstance?.speak("Which color is this?", TextToSpeech.QUEUE_FLUSH, null, null)
            }
        }
        ttsInstance
    }
    // Level Generation
    LaunchedEffect(levelTrigger) {
        val shuffled = matchColors.shuffled()
        targetColor = shuffled[0]
        options = listOf(shuffled[0], shuffled[1]).shuffled()
        selectedOption = null
        isCorrect = null
        tts?.speak("Which color is this?", TextToSpeech.QUEUE_FLUSH, null, null)
    }
    LaunchedEffect(showSuccessLottie) {
        if (!showSuccessLottie) {
            selectedOption = null // Reset selection for next shape
        }
    }

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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Which color is this?",
                fontFamily = FontFamily(Font(R.font.balootwomediam)),
                fontSize = 22.sp,
                color = Color(0xFF07698B)
            )

            Spacer(modifier = Modifier.height(30.dp))

            // Center Color Box
            Card(
                modifier = Modifier.size(180.dp),
                shape = RoundedCornerShape(25.dp),
                colors = CardDefaults.cardColors(containerColor = targetColor.color),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {}

            Spacer(modifier = Modifier.height(60.dp))

            // Options Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                options.forEach { option ->
                    val isThisSelected = selectedOption == option.name
                    val buttonBgColor by animateColorAsState(
                        targetValue = when {
                            isThisSelected && isCorrect == true -> Color(0xFF34C759) // Green on success
                            isThisSelected && isCorrect == false -> Color(0xFFED1C2A) // Red on wrong
                            else -> Color.White
                        },
                        animationSpec = tween(durationMillis = 300)
                    )

                    val contentColor by animateColorAsState(
                        targetValue = if (isThisSelected && isCorrect != null) Color.White else option.color
                    )

                    val borderColor =
                        if (isThisSelected && isCorrect != null) buttonBgColor else option.color

                    Box(
                        modifier = Modifier
                            .width(140.dp)
                            .height(55.dp)
                            .background(buttonBgColor, RoundedCornerShape(25.dp))
                            .border(2.dp, borderColor, RoundedCornerShape(25.dp))
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ) {
                                selectedOption = option.name
                                if (option.name == targetColor.name) {
                                    isCorrect = true
                                    successSound.seekTo(0)
                                    successSound.start()
                                    showSuccessLottie = true
                                    tts?.speak(option.name, TextToSpeech.QUEUE_FLUSH, null, null)
                                    // Next Level delay
                                    CoroutineScope(Dispatchers.Main).launch {
                                        delay(2000)
                                        showSuccessLottie = false
                                        delay(500)
                                        levelTrigger++
                                    }
                                } else {
                                    isCorrect = false
                                    // Vibrate
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
                                    // Reset after small delay to let user try again
                                    Handler(Looper.getMainLooper())
                                        .postDelayed({
                                            selectedOption = null
                                            isCorrect = null
                                        }, 800)
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = option.name,
                            fontSize = 21.sp,
                            fontFamily = FontFamily(Font(R.font.balooregular)),
                            color = contentColor
                        )
                    }

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



// Color Fun Game
@Composable
fun ColorFunUI() {
    val context = LocalContext.current
    val gameColors = remember {
        listOf(
            ColorData(Color(0xFFED1C2A), "Red"),
            ColorData(Color(0xFF34C759), "Green"),
            ColorData(Color(0xFFEF711F), "Orange"),
            ColorData(Color(0xFF007AFF), "Blue"),
            ColorData(Color(0xFFF14A6A), "Pink"),
            ColorData(Color(0xFF67419F), "Purple"),
            ColorData(Color(0xFF8C5209), "Brown"),
            ColorData(Color(0xFF000000), "Black"),
            ColorData(Color(0xFFFFCC00), "Yellow")
        )
    }

    // 2. Game States
    var levelTrigger by remember { mutableIntStateOf(0) }
    val solvedItems = remember { mutableStateListOf<String>() }
    val basketPositions = remember { mutableStateMapOf<String, Offset>() }


    val currentBaskets = remember(levelTrigger) { gameColors.shuffled().take(3).map { BasketTarget(it) } }
    val currentShapes = remember(levelTrigger) {
        val baseShapes = listOf(
            Triple("Circle", R.drawable.colorcircle, "circle"),
            Triple("Triangle", R.drawable.colortriangle, "triangle"),
            Triple("Star", R.drawable.colorstar, "star"),
            Triple("Pentagon", R.drawable.colorpentagon, "pentagon"),
            Triple("Square", R.drawable.colorsquare, "square")
        ).shuffled()
        baseShapes.mapIndexed { index, (name, res, id) ->
            val colorData = if(index < currentBaskets.size){
                currentBaskets[index].colorData
            }else{
                currentBaskets.random().colorData
            }
            ColorFunItem(id,name,res,colorData)
        }.shuffled()
    }

    // TTS Setup
    val tts = remember {
        var ttsInstance: TextToSpeech? = null
        ttsInstance = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                ttsInstance?.language = java.util.Locale.US
                ttsInstance?.speak("Drag the items to baskets", TextToSpeech.QUEUE_FLUSH, null, null)
            }
        }
        ttsInstance
    }

    val successSound = remember { MediaPlayer.create(context, R.raw.tracingonesidesound) }
    var showSuccessLottie by remember { mutableStateOf(false) }


    /*Tutorial State*/
    var isTutorialActive by remember { mutableStateOf(true) }
    val tutorialAnim = remember { Animatable(0f) }

    // Tutorial Loop
    LaunchedEffect(isTutorialActive, levelTrigger) {
        if (isTutorialActive) {
            while (true) {
                tutorialAnim.snapTo(0f)
                tutorialAnim.animateTo(
                    targetValue = 1f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(2500, easing = FastOutSlowInEasing),
                        repeatMode = RepeatMode.Restart
                    )
                )
                delay(500)
            }
        }
    }

    // Level Complete Check
    LaunchedEffect(solvedItems.size) {
        if (solvedItems.size == currentShapes.size && currentShapes.isNotEmpty()) {
            showSuccessLottie = true
            successSound.start()
            delay(2500)
            showSuccessLottie = false
            solvedItems.clear()
            levelTrigger++
        }
    }

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
                .size(50.dp)
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
                text = "Drag the Items to basket",
                fontFamily = FontFamily(Font(R.font.balootwomediam)),
                fontSize = 22.sp,
                color = Color(0xFF07698B),
                modifier = Modifier.padding(top = 10.dp)
            )

            // --- AREA 1: DRAGGABLE SHAPES (Top) ---
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(210.dp)
            ) {
                val maxWidth = constraints.maxWidth.toFloat()
                val maxHeight = constraints.maxHeight.toFloat()
                val density = LocalDensity.current

                currentShapes.forEachIndexed { index, item ->
                    if (!solvedItems.contains(item.id)) {

                        // Percentage-based Responsive Positions (0.0f to 1.0f)
                        val (relX, relY) = when (index) {
                            0 -> 0.15f to 0.1f   // Top Left (15% width, 10% height)
                            1 -> 0.70f to 0.1f   // Top Right (70% width, 10% height)
                            2 -> 0.42f to 0.4f   // Middle (42% width, 40% height)
                            3 -> 0.15f to 0.65f  // Bottom Left (15% width, 65% height)
                            4 -> 0.70f to 0.65f  // Bottom Right (70% width, 65% height)
                            else -> 0f to 0f
                        }

                        val posX = with(density) { (relX * maxWidth).toDp() }
                        val posY = with(density) { (relY * maxHeight).toDp() }

                        Box(modifier = Modifier.offset(x = posX, y = posY)) {
                            DraggableColorShape(
                                item=item,
                                onDragStart={isTutorialActive = false},
                                onDrop= { dropCenter ->
                                    currentBaskets.forEach { basket ->
                                        val basketCenter = basketPositions[basket.colorData.name]
                                        if (basketCenter != null) {
                                            val dist = (dropCenter - basketCenter).getDistance()

                                            // Detection threshold ko bhi thora responsive rakhen
                                            val responsiveThreshold = with(density) { 70.dp.toPx() }

                                            if (dist < responsiveThreshold && item.colorData == basket.colorData) {
                                                solvedItems.add(item.id)
                                                tts?.speak(
                                                    "${item.colorData.name} ${item.name}",
                                                    TextToSpeech.QUEUE_FLUSH,
                                                    null,
                                                    null
                                                )
                                            }
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }

            // Divider
            Canvas(modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .padding(horizontal = 20.dp)) {
                drawLine(Color(0xFFE8E8E8), Offset(0f, 0f), Offset(size.width, 0f), strokeWidth = 2f)
            }

            Spacer(modifier = Modifier.weight(1f))

            // --- AREA 2: BASKETS (Bottom) ---
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 0.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    currentBaskets.take(2).forEach { basket ->
                        BasketWithSolvedShapes(basket, currentShapes, solvedItems) { pos ->
                            basketPositions[basket.colorData.name] = pos
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    currentBaskets.drop(2).take(1).forEach { basket ->
                        BasketWithSolvedShapes(basket, currentShapes, solvedItems) { pos ->
                            basketPositions[basket.colorData.name] = pos
                        }
                    }
                }
            }


        }

        // --- HAND GUIDE OVERLAY ---
        if (isTutorialActive && solvedItems.size < currentShapes.size) {
            val firstUnsolved = currentShapes.firstOrNull { it.id !in solvedItems }
            if (firstUnsolved != null) {
                val targetBasketPos = basketPositions[firstUnsolved.colorData.name]
                val index = currentShapes.indexOf(firstUnsolved)

                val (relX, relY) = when (index) {
                    0 -> 0.15f to 0.1f
                    1 -> 0.70f to 0.1f
                    2 -> 0.42f to 0.4f
                    3 -> 0.15f to 0.65f
                    4 -> 0.70f to 0.65f
                    else -> 0.5f to 0.5f
                }

                BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                    val maxWidth = constraints.maxWidth.toFloat()
                    val density = LocalDensity.current

                    val startX = relX * maxWidth + with(density){ 25.dp.toPx() } // Center of shape
                    val startY = (relY * with(density){ 250.dp.toPx() }) + with(density){ 90.dp.toPx() }

                    if (targetBasketPos != null) {
                        val currentX = lerp(startX, targetBasketPos.x, tutorialAnim.value)
                        val currentY = lerp(startY, targetBasketPos.y, tutorialAnim.value)

                        Image(
                            painter = painterResource(id = R.drawable.guidehandimage),
                            contentDescription = null,
                            modifier = Modifier
                                .size(50.dp)
                                .offset {
                                    IntOffset(
                                        (currentX - 25.dp.toPx()).toInt(),
                                        (currentY - 25.dp.toPx()).toInt()
                                    )
                                }
                                .zIndex(200f)
                        )
                    }
                }
            }
        }

        if (showSuccessLottie) {
            val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.tracinglottie))
            LottieAnimation(composition, iterations = 1, modifier = Modifier
                .fillMaxSize()
                .scale(1.5f)
                .zIndex(20f))
        }
    }
}

@Composable
fun BasketWithSolvedShapes(
    basket: BasketTarget,
    allShapes: List<ColorFunItem>,
    solvedIds: List<String>,
    onPosition: (Offset) -> Unit
) {
    Box(
        modifier = Modifier
            .onGloballyPositioned {
                val layoutCenter =
                    it.positionInRoot() + Offset(it.size.width / 2f, it.size.height / 2f)
                onPosition(layoutCenter)
            }
            .size(width = 110.dp, height = 75.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        allShapes.filter { it.id in solvedIds && it.colorData == basket.colorData }.forEachIndexed { i, solved ->
            Image(
                painter = painterResource(id = solved.shapeRes),
                contentDescription = null,
                modifier = Modifier
                    .size(35.dp)
                    .offset(x = (i * 8 - 12).dp, y = (-20).dp)
                    .graphicsLayer { rotationZ = -10f * i },
                colorFilter = ColorFilter.tint(solved.colorData.color)
            )
        }

        // 2. Basket Image (Overlay)
        Image(
            painter = painterResource(id = R.drawable.colorbasket),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            colorFilter = ColorFilter.tint(basket.colorData.color)
        )

        // 3. Basket Text
        Text(
            text = basket.colorData.name,
            color = Color.White,
            fontSize = 14.sp,
            fontFamily = FontFamily(Font(R.font.balootwobold)),
            modifier = Modifier
                .padding(bottom = 12.dp)
                .offset(y = (-5).dp)
        )
    }
}
@Composable
fun DraggableColorShape(
    item: ColorFunItem,
    onDragStart: () -> Unit,
    onDrop: (Offset) -> Unit
) {
    var dragOffset by remember { mutableStateOf(Offset.Zero) }
    var isDragging by remember { mutableStateOf(false) }
    var initialPositionInRoot by remember { mutableStateOf(Offset.Zero) }
    val density = LocalDensity.current
    val shapeSizePx = with(density) { 50.dp.toPx() }
    Box(
        modifier = Modifier
            .onGloballyPositioned {
                if (!isDragging) {
                    initialPositionInRoot = it.positionInRoot()
                }
            }
            .offset { IntOffset(dragOffset.x.toInt(), dragOffset.y.toInt()) }
            .size(50.dp)
            .zIndex(if (isDragging) 100f else 1f)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = {
                        isDragging = true
                        onDragStart()
                    },
                    onDragEnd = {
                        isDragging = false
                        val finalCenter = initialPositionInRoot + dragOffset + Offset(
                            shapeSizePx / 2,
                            shapeSizePx / 2
                        )
                        onDrop(finalCenter)
                        dragOffset = Offset.Zero
                    },
                    onDrag = { change, amount ->
                        change.consume()
                        dragOffset += amount
                    }
                )
            }
    ) {
        Image(
            painter = painterResource(id = item.shapeRes),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            colorFilter = ColorFilter.tint(item.colorData.color)
        )
    }
}
data class ColorFunItem(
    val id: String,
    val name: String,
    val shapeRes: Int,
    var colorData: ColorData,
    var isSolved: Boolean = false
)

data class BasketTarget(
    val colorData: ColorData,
    var position: Offset = Offset.Zero
)

// Color Pop Game
@Composable
fun ColorPopUI() {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current

    // States
    var targetColorData by remember { mutableStateOf(gameColors.random()) }
    var popCount by remember { mutableIntStateOf(0) }
    val maxTarget = 7
    var showSuccessLottie by remember { mutableStateOf(false) }
    val activeBalloons = remember { mutableStateListOf<ColorPopBalloonState>() }
    val burstEffects = remember { mutableStateListOf<BurstState>() }
    var isTutorialActive by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    // Sounds
    val popSound = remember { MediaPlayer.create(context, R.raw.popballonaudio) }
    val successSound = remember { MediaPlayer.create(context, R.raw.successsound) }

    // Animations
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
    

    // TTS Setup
    val tts = remember {
        var ttsInstance: TextToSpeech? = null
        ttsInstance = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                ttsInstance?.language = java.util.Locale.US
                ttsInstance?.speak("Pop the ${targetColorData.name} balloon", TextToSpeech.QUEUE_FLUSH, null, null)
            }
        }
        ttsInstance
    }

    DisposableEffect(Unit) {
        onDispose {
            popSound.release()
            successSound.release()
            tts?.stop()
            tts?.shutdown()
        }
    }

    // Balloon Spawning Logic
    LaunchedEffect(targetColorData, showSuccessLottie, isTutorialActive) {
        if (isTutorialActive) return@LaunchedEffect
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            while (true) {
                if (!showSuccessLottie) {
                    val isTarget = (0..10).random() > 7
                    val randomColor = if (isTarget) targetColorData else gameColors.random()
                    val randomX = 0.05f + (Math.random().toFloat() * (0.85f - 0.05f))

                    val newBalloon = ColorPopBalloonState(
                        colorData = randomColor,
                        initialX = randomX
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

    Box(modifier = Modifier.fillMaxSize()) {
        // --- TOP COUNTER UI ---
        Row(
            modifier = Modifier
                .offset(y = (-10).dp)
                .align(Alignment.TopStart)
                .graphicsLayer(
                    scaleX = ringScale,
                    scaleY = ringScale,
                    transformOrigin = TransformOrigin(0f, 0f)
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.zIndex(2f)
            ) {
                //Progress Ring
                Canvas(modifier = Modifier.size(65.dp)) {

                    if(showSuccessLottie){
                        drawCircle(
                            color = Color(0xFF1F9856).copy(alpha = glowAlpha),
                            radius = (size.minDimension / 2) + 2.dp.toPx(),
                            style = Stroke(width = 15.dp.toPx())
                        )
                    }
                    //white background
                    drawCircle(color = Color.White, radius = size.minDimension / 2)
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
                Card(
                    shape = CircleShape,
                    modifier = Modifier
                        .size(50.dp)
                        .fillMaxSize(),
                    colors = CardDefaults.cardColors(containerColor = targetColorData.color),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(
                            text="$popCount/$maxTarget",
                            fontSize = 17.sp,
                            fontFamily = FontFamily(Font(R.font.balootwobold)),
                            color = Color.White,
                        )
                    }
                }
            }
            Surface(
               color = Color.Transparent,
                shape = RoundedCornerShape(topEnd = 20.dp, bottomEnd = 20.dp),
                modifier = Modifier
                    .height(38.dp)
                    .offset(x = (-20).dp)
                    .zIndex(1f)
            ) {
                Box(
                    modifier = Modifier.padding(start = 28.dp, end = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = " ${targetColorData.name}",
                        color = targetColorData.color,
                        fontSize = 23.sp,
                        fontFamily = FontFamily(Font(R.font.balootwobold))
                    )
                }
            }
        }

        // --- GAME AREA ---
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val width = constraints.maxWidth
            val height = constraints.maxHeight

            // Tutorial
            if (isTutorialActive) {
                val tx = width / 2f - 110f // Center position
                val ty = height / 2f - 110f
                Box(modifier = Modifier
                    .offset { IntOffset(tx.toInt(), ty.toInt()) }
                    .size(120.dp)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        isTutorialActive = false
                        popSound.start()
                        tts?.speak(
                            "Pop ${targetColorData.name}",
                            TextToSpeech.QUEUE_FLUSH,
                            null,
                            null
                        )
                        burstEffects.add(BurstState(x = tx, y = ty, isTarget = true))
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
                        painter = painterResource(R.drawable.ballona),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer(scaleX = scale, scaleY = scale),
                        colorFilter = ColorFilter.tint(targetColorData.color)
                    )
                    Image(
                        painter = painterResource(R.drawable.guidehandimage),
                        contentDescription = null,
                        modifier = Modifier
                            .size(60.dp)
                            .offset(y = 60.dp + handOffset.dp, x = 30.dp)
                    )
                }
            }

            // Balloons
            if (!isTutorialActive) {
                activeBalloons.forEach { balloon ->
                    val x = balloon.initialX * width
                    val y = balloon.currentY.value * height
                    Box(modifier = Modifier
                        .offset { IntOffset(x.toInt(), y.toInt()) }
                        .size(80.dp)
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) {
                            popSound.apply {
                                if (isPlaying) {
                                    popSound.pause()
                                    popSound.seekTo(0)
                                }
                                popSound.start()
                            }
                            val isCorrect = balloon.colorData == targetColorData
                            burstEffects.add(BurstState(x = x, y = y, isTarget = isCorrect))

                            if (isCorrect) {

                                popCount++
                                tts?.speak(
                                    balloon.colorData.name,
                                    TextToSpeech.QUEUE_FLUSH,
                                    null,
                                    null
                                )
                                if (popCount >= maxTarget) {
                                    scope.launch {
                                        successSound.start()
                                        showSuccessLottie = true
                                        delay(2500)
                                        showSuccessLottie = false
                                        popCount = 0
                                        targetColorData = gameColors.random()
                                        tts?.speak(
                                            "Now find ${targetColorData.name}",
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
                            painter = painterResource(R.drawable.ballona),
                            contentDescription = null,modifier = Modifier.fillMaxSize(),
                            colorFilter = ColorFilter.tint(balloon.colorData.color)
                        )
                    }
                }
            }

            // Burst Effects (Reuse your existing Burst logic)
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
        // Success Lottie
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
}
data class ColorData(val color: Color, val name: String)

val gameColors = listOf(
    ColorData(Color(0xFFEF711F), "Orange"),
    ColorData(Color(0xFFED1C2A), "Red"),
    ColorData(Color(0xFF8C5209),"Brown"),
    ColorData(Color(0xFFFFCC00), "Yellow"),
    ColorData(Color(0xFF67419F), "Purple"),
    ColorData(Color(0xFFF14A6A), "Pink"),
    ColorData(Color(0xFF007AFF), "Blue"),
    ColorData(Color(0xFF000000), "Black"),
    ColorData(Color(0xFF4CAF50), "Green"),
)

data class ColorPopBalloonState(
    val colorData: ColorData,
    val initialX: Float,
    var currentY: Animatable<Float, AnimationVector1D> = Animatable(0.86f)
)