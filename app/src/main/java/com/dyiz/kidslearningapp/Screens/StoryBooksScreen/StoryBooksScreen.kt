package com.dyiz.kidslearningapp.Screens.StoryBooksScreen

import android.app.Activity
import android.media.MediaPlayer
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.view.WindowManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.dyiz.kidslearningapp.Database.ViewModel.MainViewModel
import com.dyiz.kidslearningapp.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun StoryBooksScreen(navController: NavHostController,mainViewModel: MainViewModel) {
    val context = LocalContext.current

    var selectedStory by remember { mutableStateOf("Barnaby’s Big Mistake") }
    var isPlaying by remember { mutableStateOf(false) }
    var isAutoReadingMode by remember { mutableStateOf(false) }
    var activeStoryName by remember { mutableStateOf("") }
    val activity = context as? Activity
    val window = activity?.window
    var isSeeAllMode by remember{mutableStateOf(false)}
    var isSearchVisible by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    //try these cards animation
    var startSpreading by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(300)
        startSpreading = true
    }



    // Title Images Mapping
    val titleImageRes = remember(selectedStory) {
        when (selectedStory) {
            "Music of the Moon Seed" -> R.drawable.musicofmoonseedtitileimage
            "Barnaby’s Big Mistake" -> R.drawable.barnaydognewcoverimage
            "Magic of the SunBerry" -> R.drawable.magicofsungberrytitleimage
            "The Land of Soft Whispers" -> R.drawable.landofsoftwhispertitleimage
            "Secret Garden" -> R.drawable.silvercover
            "The Heavy Blue Pebble" -> R.drawable.heavybluepebbletitleimage
            "Tembo’s Big Day" -> R.drawable.tembotitleiamge
            "Strength" -> R.drawable.strengthtitleimage
            "Garden Day" -> R.drawable.gardendaytitleimage
            "Yellow Hat" -> R.drawable.yellowhattitleimage
            else -> R.drawable.musicofmoonseedtitileimage
        }
    }
    val stories = listOf(
        StoryItem("Music of the Moon Seed", R.drawable.musicofmoonseed),
        StoryItem("Barnaby’s Big Mistake", R.drawable.barnebys),
        StoryItem("Magic of the SunBerry", R.drawable.magicofsunberry),
        StoryItem("The Land of Soft Whispers", R.drawable.landofsoftwhisper),
        StoryItem("Secret Garden", R.drawable.secretgarden),
        StoryItem("The Heavy Blue Pebble", R.drawable.heavybluepebble),
        StoryItem("Tembo’s Big Day", R.drawable.tembosbigday),
        StoryItem("Strength", R.drawable.strength),
        StoryItem("Garden Day", R.drawable.gardenday),
        StoryItem("Yellow Hat", R.drawable.yellowhat)
    )


    LaunchedEffect(isPlaying, isAutoReadingMode) {
        if (isPlaying && isAutoReadingMode) {
            mainViewModel.setGlobalMusicMuted(true)
        } else {
            mainViewModel.setGlobalMusicMuted(false)
        }
    }

    //screen timeout case
    DisposableEffect(Unit) {
        window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        onDispose {
            window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            mainViewModel.setGlobalMusicMuted(false)
        }
    }

    Box(modifier = Modifier.fillMaxSize()){
        CompositionLocalProvider(
            LocalDensity provides Density(
                LocalDensity.current.density,
                fontScale = 1f
            )
        ) {
            Image(
                painter = painterResource(id = R.drawable.storybookbg),
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
                    .then(if (!isSeeAllMode) Modifier.verticalScroll(rememberScrollState()) else Modifier)
            ) {
                if (isSeeAllMode) {
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
                                ) {
                                    if (isSearchVisible) {
                                        isSearchVisible = false
                                        searchQuery = ""
                                    } else if (isSeeAllMode) {
                                        isSeeAllMode = false
                                    } else {
                                        navController.navigateUp()
                                    }
                                }
                        )
                        Text(
                            text = "Story Books",
                            fontSize = 22.sp,
                            fontFamily = FontFamily(Font(R.font.balootwomediam)),
                            color = Color.Black,
                            modifier = Modifier
                                .padding(start = 16.dp)
                                .weight(1f)
                        )
                        Card(
                            modifier = Modifier
                                .size(38.dp)
                                .clickable(
                                    indication = null,
                                    interactionSource = remember { MutableInteractionSource() }
                                ) {
                                    isSearchVisible = !isSearchVisible
                                },
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isSearchVisible) Icons.Default.Close else ImageVector.vectorResource(id = R.drawable.storyseachicon),
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp),
                                    tint = Color.Unspecified
                                )
                            }
                        }
                    }
                    AnimatedVisibility(
                        visible = isSearchVisible,
                        enter = expandVertically() + fadeIn(),
                        exit = shrinkVertically() + fadeOut()
                    ) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            placeholder = { Text("Search stories...", color = Color.Gray) },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedBorderColor = Color(0xFFFB923C),
                                unfocusedBorderColor = Color.LightGray
                            )
                        )
                    }
                }
                else{
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
                                    ) {
                                        if (isPlaying) {
                                            isPlaying = false
                                        } else {
                                            navController.navigateUp()
                                        }
                                    }
                            )
                            Text(
                                text =if (selectedStory == "Music of the Moon Seed") {
                                    "Music of the Moon Seed"
                                } else {
                                    selectedStory
                                },
                                fontSize = 22.sp,
                                fontFamily = FontFamily(Font(R.font.balootwomediam)),
                                color = Color.Black,
                                modifier = Modifier.padding(start = 16.dp)
                            )
                    }
                }

                if(isSeeAllMode){
                    val stories = listOf(
                        StoryItem("Music of the Moon Seed", R.drawable.storymusicofmoonseed),
                        StoryItem("Barnaby’s Big Mistake", R.drawable.storybarnaby),
                        StoryItem("Magic of the SunBerry", R.drawable.storymagic),
                        StoryItem("The Land of Soft Whispers", R.drawable.storyland),
                        StoryItem("Secret Garden", R.drawable.storysecret),
                        StoryItem("The Heavy Blue Pebble", R.drawable.storyheavy),
                        StoryItem("Tembo’s Big Day", R.drawable.storytembos),
                        StoryItem("Strength", R.drawable.storystrength),
                        StoryItem("Garden Day", R.drawable.storygarden),
                        StoryItem("Yellow Hat", R.drawable.storyyellow)
                    )
                    val filteredStories = stories.filter{
                        it.name.contains(searchQuery, ignoreCase = true)
                    }
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        items(filteredStories) { story ->
//                            val story = stories[index]
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable(
                                        indication = null,
                                        interactionSource = remember { MutableInteractionSource() }
                                    ) {
                                        selectedStory = story.name
                                        isSeeAllMode = false
                                        isPlaying = false
                                        searchQuery = ""
                                        isSearchVisible = false
                                    }
                            ) {
                                Box(
                                    modifier = Modifier
                                        .aspectRatio(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                ) {
                                    Image(
                                        painter = painterResource(id = story.image),
                                        contentDescription = null,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Fit
                                    )
                                }
                                Text(
                                    text = story.name,
                                    fontSize = 14.sp,
                                    textAlign = TextAlign.Center,
                                    fontFamily = FontFamily(Font(R.font.balootworegular)),
                                    modifier = Modifier.padding(top = 8.dp),
                                    maxLines = 2,
                                    minLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }

                else {
                    // White Main Card
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp, start = 16.dp, end = 16.dp, bottom = 12.dp)
                            .height(450.dp)
                    ) {
                        Crossfade(
                            targetState = selectedStory,
                            animationSpec = tween(durationMillis = 500),
                            label = "Game transition"
                        ) { targetStory ->
                            val currentStoryPages = remember(targetStory) { getStoryPages(targetStory) }
                            Card(
                                modifier = Modifier.fillMaxSize(),
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Box(modifier = Modifier.fillMaxSize()) {

                                    if (isPlaying && activeStoryName == targetStory) {
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
                                        BarnabyStoryContent(
                                            isAutoPlaying = isAutoReadingMode,
                                            pages = currentStoryPages,
                                            onNextStory = {
                                                // Find next story index
                                                val currentIndex = stories.indexOfFirst { it.name == selectedStory }
                                                val nextIndex = (currentIndex + 1) % stories.size
                                                selectedStory = stories[nextIndex].name

                                                // Reset flags to show title screen for new story
                                                isPlaying = false
                                                activeStoryName = ""
                                            }
                                        )
                                    } else {
                                        // Title Image
                                        Image(
                                            painter = painterResource(id = titleImageRes),
                                            contentDescription = null,
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.FillBounds
                                        )

                                        // Buttons aur Progress Overlays
                                        TitleOverlays(
                                            onPlay = {
                                                activeStoryName = targetStory
                                                isPlaying = true
                                                isAutoReadingMode = true
                                            },
                                            onRead = {
                                                activeStoryName = targetStory
                                                isPlaying = true
                                                isAutoReadingMode = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Try these",
                            fontSize = 16.sp,
                            fontFamily = FontFamily(Font(R.font.balootwomediam)),
                            color = Color.Black,
                            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                        )
                        Text(
                            text = "See all",
                            fontSize = 14.sp,
                            fontFamily = FontFamily(Font(R.font.balootwosemibold)),
                            color = Color(0xFF7E7C7C),
                            modifier = Modifier
                                .padding(end = 16.dp)
                                .clickable(
                                    indication = null,
                                    interactionSource = remember { MutableInteractionSource() }) {
                                    isSeeAllMode = true
                                }
                        )
                    }

                    val stories = listOf(
                        StoryItem("Music of the Moon Seed", R.drawable.musicofmoonseed),
                        StoryItem("Barnaby’s Big Mistake", R.drawable.barnebys),
                        StoryItem("Magic of the SunBerry", R.drawable.magicofsunberry),
                        StoryItem("The Land of Soft Whispers", R.drawable.landofsoftwhisper),
                        StoryItem("Secret Garden", R.drawable.secretgarden),
                        StoryItem("The Heavy Blue Pebble", R.drawable.heavybluepebble),
                        StoryItem("Tembo’s Big Day", R.drawable.tembosbigday),
                        StoryItem("Strength", R.drawable.strength),
                        StoryItem("Garden Day", R.drawable.gardenday),
                        StoryItem("Yellow Hat", R.drawable.yellowhat)
                    )
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        itemsIndexed(
                            items = stories.filter { it.name != selectedStory },
                            key= { _, story -> story.name}
                        ) { index,story ->
                            AnimatedVisibility(
                                visible = startSpreading,
                                enter = slideInHorizontally(
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioLowBouncy,
                                        stiffness = Spring.StiffnessLow
                                    ),
                                    initialOffsetX = { -100 }//500
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
                                            selectedStory = story.name
                                        }
                                ) {
                                    Image(
                                        painter = painterResource(id = story.image),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(100.dp)
                                            .clip(RoundedCornerShape(6.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                    Text(
                                        text = story.name,
                                        fontSize = 13.sp,
                                        lineHeight = 16.sp,
                                        fontFamily = FontFamily(Font(R.font.balootworegular)),
                                        color = Color(0xFF464646),
                                        modifier = Modifier.padding(
                                            top = 8.dp,
                                            start = 4.dp,
                                            end = 4.dp
                                        ), textAlign = TextAlign.Center,
                                        maxLines = 2, minLines = 2, overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }

                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}
@Composable
fun TitleOverlays(
    onPlay: () -> Unit,
    onRead: () -> Unit
) {

    var timeLeft by remember { mutableIntStateOf(5) }
    var progress by remember { mutableFloatStateOf(0f) }

    // 2. Timer Logic
    LaunchedEffect(Unit) {
        val totalTime = 5000L
        val interval = 50L
        var elapsed = 0L

        while (elapsed < totalTime) {
            delay(interval)
            elapsed += interval
            progress = elapsed.toFloat() / totalTime
            timeLeft = 5 - (elapsed / 1000).toInt()
        }
        onPlay()
    }
    Box(modifier = Modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Horizontal Progress Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .height(7.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Color.White.copy(alpha = 0.4f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress) // Static progress for now
                        .fillMaxHeight()
                        .background(Color(0XFFFB923C))
                )
            }
            Text(
                text = "Auto starts in $timeLeft secs",
                color = Color.White,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 8.dp),
                style = TextStyle(
                    shadow = Shadow(
                        color = Color.Black.copy(alpha = 0.8f),
                        offset = Offset(2f, 2f),
                        blurRadius = 4f
                    )
                ),
                fontFamily = FontFamily(Font(R.font.balootwosemibold))
            )
        }

        // 2. RIGHT SECTION: Play Button and Read Myself (Vertical Stack)
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 20.dp, top = 130.dp), // Adjusted for vertical center-ish look
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // PLAY BUTTON (Using your reference code design)
            Card(
                shape = RoundedCornerShape(50.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier.size(75.dp)
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
                                startY = 0f, endY = 100f
                            )
                        )
                        .border(
                            width = 1.dp,
                            brush = Brush.verticalGradient(
                                listOf(Color.White.copy(0.4f), Color.Transparent)
                            ),
                            shape = RoundedCornerShape(50.dp)
                        )
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) { onPlay() },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.playicon),
                        contentDescription = "Play",
                        modifier = Modifier
                            .size(38.dp)
                            .offset(x = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // READ MYSELF BUTTON
            Row(
                modifier = Modifier
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) { onRead() },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.readmyselficon),
                    contentDescription = null,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Read Myself",
                    color = Color.White,
                    fontSize = 17.sp,
                    fontFamily = FontFamily(Font(R.font.balootwosemibold)),
                    style = TextStyle(
                        shadow = Shadow(
                            color = Color.Black,
                            offset = Offset(2f, 2f),
                            blurRadius = 4f
                        )
                    )
                )
            }
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BarnabyStoryContent(
    isAutoPlaying: Boolean,
    pages: List<StoryPage>,
    onNextStory: () -> Unit
) {
    var isPaused by remember { mutableStateOf(false) }
    var showPageDialog by remember { mutableStateOf(false) }
    var isStoryFinished by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()


    val tts = remember { mutableStateOf<TextToSpeech?>(null) }
    var isTtsReady by remember { mutableStateOf(false) } //
    var hasStartedSpeaking by remember { mutableStateOf(false) }

    var highlightStart by remember { mutableIntStateOf(0) }
    var highlightEnd by remember { mutableIntStateOf(0) }
    var resumeOffset by remember { mutableIntStateOf(0) }

    val currentTextLength = pages[pagerState.currentPage].text.length
    val actualPosition = (resumeOffset + highlightEnd).coerceIn(0, currentTextLength)
    val totalSpoken = actualPosition.toFloat()
    val completionSound = remember { MediaPlayer.create(context, R.raw.tracingonesidesound) }

    val progress by animateFloatAsState(
        targetValue = if (currentTextLength > 0) totalSpoken / currentTextLength else 0f,
        animationSpec = tween(durationMillis = 300), label = ""
    )
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_PAUSE || event == Lifecycle.Event.ON_STOP) {
                tts.value?.stop()
                isPaused = true
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    DisposableEffect(Unit) {
        val speech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts.value?.let { engine ->
                    engine.language = java.util.Locale.US
                    engine.setSpeechRate(0.7f)
                    isTtsReady = true
                }
            }
        }

        speech.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                hasStartedSpeaking = true
            }

            override fun onDone(utteranceId: String?) {
                // it goes to next page when content gets completed
                if (isAutoPlaying && !isPaused && utteranceId == "id_${pagerState.currentPage}") {
                    scope.launch {
                        if (pagerState.currentPage < pages.size - 1) {
                            hasStartedSpeaking = false
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }else{
                            isStoryFinished = true
                        }
                    }
                }
            }

            override fun onError(utteranceId: String?) {
                hasStartedSpeaking = true
            }

            override fun onRangeStart(utteranceId: String?, start: Int, end: Int, frame: Int) {
                highlightStart = start
                highlightEnd = end
            }
        })

        tts.value = speech
        onDispose {
            speech.stop()
            speech.shutdown()
            completionSound.release()
        }
    }

    LaunchedEffect(pagerState.currentPage, isAutoPlaying, isTtsReady, isPaused) {
        if (isAutoPlaying && isTtsReady) {
            if (isPaused) {
                tts.value?.stop()
            } else {
                val fullText = pages[pagerState.currentPage].text
                val safeOffset = if (resumeOffset >= fullText.length) 0 else resumeOffset
                val textToSpeak = fullText.substring(safeOffset)

                val params = Bundle()
                params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "id_${pagerState.currentPage}")

                tts.value?.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, params, "id_${pagerState.currentPage}")
            }
        }
    }

    // Reset offset on page change
    LaunchedEffect(pagerState.currentPage) {
        resumeOffset = 0
        highlightStart = 0
        highlightEnd = 0
        hasStartedSpeaking = false
        if(pagerState.currentPage==0) isStoryFinished = false
    }

    LaunchedEffect(pagerState.targetPage, pagerState.isScrollInProgress) {
        if (!isAutoPlaying &&
            pagerState.currentPage == pages.size - 1 &&
            pagerState.isScrollInProgress &&
            pagerState.targetPage == pagerState.currentPage
        ) {
            isStoryFinished = true
        }
    }
    LaunchedEffect(isStoryFinished) {
        if (isStoryFinished) {
            completionSound.seekTo(0)
            completionSound.start()
        }
    }
    Box(modifier = Modifier.fillMaxSize()) {

        if(isStoryFinished){
            StoryCompletionUI(
                onRestart = {
                    isStoryFinished = false
                    scope.launch {
                        pagerState.scrollToPage(0)
                        isPaused = false
                    }
                },
                onNext = onNextStory
            )
        }else {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                userScrollEnabled = !isAutoPlaying
            ) { pageIndex ->
                val currentPage = pages[pageIndex]
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(25.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Image Section
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1.1f)
                            .clip(RoundedCornerShape(18.dp))
                    ) {
                        Image(
                            painter = painterResource(id = currentPage.imageRes),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.FillBounds
                        )

                        StoryControlIcons(
                            isAutoPlaying = isAutoPlaying,
                            isPaused = isPaused,
                            progress = progress,
                            onPauseToggle = {
                                if (!isPaused) {

                                    tts.value?.stop()
                                    resumeOffset += highlightStart
                                    highlightStart = 0
                                    highlightEnd = 0

                                }
                                isPaused = !isPaused
                            },
                            onOpenPageClick = {
                                tts.value?.stop()
                                isPaused = true
                                showPageDialog = true
                            }
                        )
                    }

                    // Text with Highlighter
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(top = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val fullText = currentPage.text
                        val annotatedText = buildAnnotatedString {
                            // HIGHLIGHTER LOGIC WITH OFFSET
                            val actualStart =
                                (resumeOffset + highlightStart).coerceIn(0, fullText.length)
                            val actualEnd =
                                (resumeOffset + highlightEnd).coerceIn(0, fullText.length)

                            if (isAutoPlaying && actualStart < actualEnd) {
                                append(fullText.substring(0, actualStart))
                                withStyle(
                                    style = SpanStyle(
                                        background = Color(0xFFC1F3D8),
                                        color = Color.Black
                                    )
                                ) {
                                    append(fullText.substring(actualStart, actualEnd))
                                }
                                append(fullText.substring(actualEnd))
                            } else {
                                append(fullText)
                            }
                        }
                        Text(
                            text = annotatedText,
                            fontSize = 16.sp,
                            lineHeight = 26.sp,
                            textAlign = TextAlign.Center,
                            color = Color.Black,
                            fontFamily = FontFamily(Font(R.font.balootwomediam))
                        )
                    }
                }
            }
        }

        // Page Number
        if (!isStoryFinished) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(22.dp)
                    .size(35.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFFDCD8)),
                contentAlignment = Alignment.Center
            ) {
                Text("${pagerState.currentPage + 1}", fontSize = 15.sp, color = Color(0xFF7E7C7C))
            }
        }

    }
    // circular progress bar when tts engine get start speak
    if (!hasStartedSpeaking && isAutoPlaying) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White.copy(alpha = 0.5f))
                .clickable(enabled = false) {},
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator(
                    color = Color(0XFF19A963),
                    modifier = Modifier.size(50.dp)
                )
            }
        }
    }


    if (showPageDialog) {
        PageSelectionDialog(
            totalPages = pages.size,
            onPageSelected = { selectedPageIndex ->    scope.launch {
                tts.value?.stop()

                // RESET EVERYTHING FOR NEW PAGE
                resumeOffset = 0
                highlightStart = 0
                highlightEnd = 0

                pagerState.scrollToPage(selectedPageIndex)
                showPageDialog = false
                isPaused = false
            }
            },
            onDismiss = {
                showPageDialog = false
            }
        )
    }
}

@Composable
fun StoryControlIcons(
    isAutoPlaying: Boolean,
    isPaused: Boolean,
    progress: Float,
    onPauseToggle: () -> Unit,
    onOpenPageClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(12.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.End
    ) {
        // page open Icon
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(Color.White.copy(0.9f))
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }) {
                    onOpenPageClick()
                }
            ,
            contentAlignment = Alignment.Center
        ) {
            Image(painter = painterResource(id = R.drawable.storycontenticon), contentDescription = null, modifier = Modifier.size(20.dp))
        }

        if (isAutoPlaying) {
            Spacer(modifier = Modifier.height(10.dp))
            // Pause Button with Circular Progress
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    onPauseToggle()
                }
            ) {
                CircularProgressIndicator(
                    progress = progress,
                    modifier = Modifier.size(42.dp),
                    color = Color(0xFFFB923C),
                    strokeWidth = 3.dp,
                    trackColor = Color.White.copy(alpha = 0.3f)
                )
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF19A963)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = if (isPaused) R.drawable.playicon else R.drawable.pauseicon),
                        contentDescription = null,
                        modifier = Modifier
                            .size(if (isPaused) 16.dp else 12.dp)
                            .offset(x = if (isPaused) 1.dp else 0.dp)
                    )
                }
            }
        }
    }
}
@Composable
fun PageSelectionDialog(
    totalPages: Int,
    onPageSelected: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            contentAlignment = Alignment.TopEnd
        ) {
            // Main Card
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(8.dp),
                modifier = Modifier.padding(top = 12.dp, end = 12.dp)
            ) {
                // Dashed Border Container
                Box(
                    modifier = Modifier
                        .padding(12.dp)
                        .fillMaxWidth()
                ) {
                    // --- CANVAS FOR DASHED BOX ---
                    Canvas(modifier = Modifier.matchParentSize()) {
                        val stroke = Stroke(
                            width = 2.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                        )
                        drawRoundRect(
                            color = Color.LightGray.copy(alpha = 0.5f), // Dotted line ka color
                            cornerRadius = CornerRadius(15.dp.toPx()),
                            style = stroke
                        )
                    }

                    // Content inside dashed box
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Select Page Number",
                            fontSize = 18.sp,
                            fontFamily = FontFamily(Font(R.font.balootwomediam)),
                            color = Color.Black
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        LazyVerticalGrid(
                            columns = GridCells.Fixed(5),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.heightIn(max = 200.dp)
                        ) {
                            items(totalPages) { index ->
                                Box(
                                    modifier = Modifier
                                        .aspectRatio(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .border(1.dp, Color(0xFFF9A499), RoundedCornerShape(10.dp))
                                        .background(Color.White)
                                        .clickable { onPageSelected(index) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${index + 1}",
                                        fontSize = 18.sp,
                                        fontFamily = FontFamily(Font(R.font.balootwomediam)),
                                        color = Color.Black
                                    )
                                }
                            }
                        }
                    }
                }
            }


            Image(
                painter = painterResource(id = R.drawable.crossimagestory),
                contentDescription = "Close",
                modifier = Modifier
                    .size(28.dp)
                    .offset(x = (-6).dp, y = (0).dp)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) { onDismiss() }
            )
        }
    }
}

@Composable
fun StoryCompletionUI(onRestart: () -> Unit, onNext: () -> Unit) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.storycompletion))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = Modifier.size(150.dp)
        )
        Text(
            text = "Awesome!",
            fontSize = 20.sp,
            fontFamily = FontFamily(Font(R.font.balootwomediam)),
            color = Color.Black,
            modifier = Modifier.padding(top = 16.dp)
        )
        Text(
            text = "You just completed the story",
            fontSize = 15.sp,
            fontFamily = FontFamily(Font(R.font.balootworegular)),
            color = Color(0xFF7E7C7C),
            modifier = Modifier.padding(top = 8.dp)
        )

        Row(
            modifier = Modifier.padding(top = 30.dp),
            horizontalArrangement = Arrangement.spacedBy(25.dp)
        ) {
            // RESTART BUTTON (Orange)
            CompletionButton(
                icon = R.drawable.restart,
                label = "Restart",
                color = Color(0xFFFB923C),
                onClick = onRestart
            )
            // NEXT BUTTON (Green)
            CompletionButton(
                icon = R.drawable.next,
                label = "Next",
                color = Color(0xFF19A963),
                onClick = onNext
            )
        }
    }
}

@Composable
fun CompletionButton(icon: Int, label: String, color: Color, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Card(
            shape = CircleShape,
            elevation = CardDefaults.cardElevation(6.dp),
            modifier = Modifier.size(45.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color)
                    .clickable { onClick() },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = icon),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    colorFilter = ColorFilter.tint(Color.White)
                )
            }
        }
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color(0xFF7E7C7C),
            modifier = Modifier.padding(top = 8.dp),
            fontFamily = FontFamily(Font(R.font.balootwomediam))
        )
    }
}
fun getStoryPages(storyName:String):List<StoryPage>{
    return when (storyName) {
        "Barnaby’s Big Mistake" -> listOf(
            StoryPage(R.drawable.page1barberny, "In a town full of delicious food smells, there lived a hungry dog named Barnaby. While other dogs ate small crumbs, Barnaby always dreamed of finding a giant feast."),
            StoryPage(R.drawable.page2barberny, "One afternoon, Barnaby found a huge, juicy bone on a silver tray outside Otto the butcher's shop. It was the best bone he had ever seen. Otto was busy, so Barnaby saw his chance."),
            StoryPage(R.drawable.page3barberny, "Barnaby quickly grabbed the bone and ran away. He raced to find a hidden spot where he could eat his prize alone."),
            StoryPage(R.drawable.page4barberny, "He ran into the quiet meadows, but he did not stop to play with the birds. He held the bone tightly because he was afraid other dogs might try to take it."),
            StoryPage(R.drawable.page5barberny, "Soon reached a wooden bridge over a clear, quiet stream. As he walked to the middle, his claws clicked on the wood and he looked down into the water."),
            StoryPage(R.drawable.page6barberyn, "In the middle of the bridge, Barnaby looked down into the water. To his surprise, he saw another dog staring back at him. That dog had a bone that looked even bigger than his own."),
            StoryPage(R.drawable.page7barberny, "Barnaby grew greedy and wanted both bones instead of just one. He glared at the dog in the water, and the other dog glared back at him."),
            StoryPage(R.drawable.page8barberny, "“That bone should be mine!” Barnaby thought. He decided to scare the other dog to take his treasure. He opened his mouth wide and let out a loud bark to frighten the stranger."),
            StoryPage(R.drawable.page9barberny, "Splash! Barnaby barked, and his bone fell into the water, sinking forever. The \"other dog\" also lost his bone as the water rippled away."),
            StoryPage(R.drawable.page10barberny, "Barnaby stood on the bridge, realizing there was no other dog or second bone. By being greedy, he had lost what he already had. Sad and hungry, he began his long walk home.")

        )
        "Music of the Moon Seed" -> listOf(
            StoryPage(R.drawable.moonseed1, "In the Sands of Someday, young Zephyr found a humming silver seed. The wise Kavi told him it was a Moon-Seed. She explained it only grows when the world is still, so he must learn the art of wait."),
            StoryPage(R.drawable.moonseed2, "Zephyr found it hard to be still. He sang loud songs, danced in circles, and tried to make the sun move faster. He wanted the flower right away, but the seed stayed small and quiet in the dirt."),
            StoryPage(R.drawable.moonseed3, "\"Why won't it grow?\" Zephyr asked, feeling sad. Kavi sat beside him and explained, \"The seed needs peace, Zephyr. It cannot hear the moon's music while you are making so much noise.\""),
            StoryPage(R.drawable.moonseed4, "That night, Zephyr sat perfectly still. He closed his eyes and listened to the desert wind. Instead of wishing for tomorrow, he watched the stars. Suddenly, he felt the ground shake under his feet."),
            StoryPage(R.drawable.moonseed5, "By dawn, a beautiful flower bloomed like stardust. Kavi whispered that the best things happen when we stop rushing and just learn to be. Zephyr smiled, finally understanding how to wait.")
        )

        "The Land of Soft Whispers" -> listOf(
            StoryPage(R.drawable.softw1, "The sun set and stars began to blink. Inside a cozy, quiet room, Nodd sat up in bed with wide eyes. Watching the curtains dance in the breeze, Nodd whispered to the shadows, \"I'm not sleepy yet\"."),
            StoryPage(R.drawable.softw2, "Suddenly, a golden light hummed from the bed. It was Bubble, stretching his fuzzy arms, he spoke \"The night is a soft blanket for the world, not for worrying. Shall we go for a quiet walk?\""),
            StoryPage(R.drawable.softw3, "Bubble pointed to the window, where a moonlight path stretched into the sky. Nodd took his hand and stepped onto the beam. It felt like a velvet ribbon, and the air smelled like lavender"),
            StoryPage(R.drawable.softw4, "They reached the Forest of Whispers, where trees sighed and silver silk leaves hummed a low tune. Walking on the soft moss, Nodd felt his feet tingle."),
            StoryPage(R.drawable.softw5, "On a large mushroom, they met Mica, a snail with a shell like a polished pearl. Moving so slowly he seemed almost still, Mica breathed, \"Hello. I am practicing stillness. Would you like to try?\""),
            StoryPage(R.drawable.softw6, "Nodd sat beside Mica and closed his eyes. They breathed in the cool air together, then let out a long, slow breath. Nodd’s shoulders relaxed as the forest wrapped them in a quiet hug of silence."),
            StoryPage(R.drawable.softw7, "Bubble led Nodd to the Sea of Stars, where glowing bubbles drifted instead of water. As waves whispered \"shhhhh,\" Nodd dipped his toe in, feeling the tickle of a soft cloud."),
            StoryPage(R.drawable.softw8, "From the glowing water rose Crystan, a starlight whale. Gliding through the air, he blew a shimmering mist over Nodd. It felt as warm and cozy as a heavy winter coat."),
            StoryPage(R.drawable.softw9, "\"It's time to head back,\" Bubble whispered, noticing Nodd’s heavy eyelids. They stepped onto the moonbeam path, which felt like a soft moving walkway."),
            StoryPage(R.drawable.softw10, "Back in the cozy room, the bed felt fluffier than ever. Bubble tucked the duvet to Nodd’s chin. The room felt just right. With one last deep breath, Nodd’s eyes closed as he fell into a deep, peaceful sleep.")
        )
        "Magic of the SunBerry" -> listOf(
            StoryPage(R.drawable.sunbery1, "Deep in the Whispering Woods, Pip found a Sun-Berry, the forest's brightest fruit. Clutching the warm, glowing prize, he whispered, \"This is all for me. I won't have to search for food for a week now!\""),
            StoryPage(R.drawable.sunbery2, "Pip hid the Sun-Berry inside a hollow oak tree, feeling safe in the shadows. He thought that by keeping it secret, he would never be hungry and would never have to share a bite."),
            StoryPage(R.drawable.sunbery3, "A soft flutter of wings echoed as Flit landed nearby, weary and weak. He asked Pip for any spare scraps. Pip felt a prick of guilt as the hidden berry glowed behind him, but he looked away."),
            StoryPage(R.drawable.sunbery4, "Pip looked at Flit, then at the berry. With a sigh, he pulled it out and broke it in half to share. As the fruit snapped, golden sparkles filled the air, and both pieces magically grew back to their original size."),
            StoryPage(R.drawable.sunbery5, "Pip realized the berry tasted far better shared than it would have alone. The woods had revealed a secret: when you share with kindness, happiness doesn't divide, it doubles.")
        )
        // story 5
        "The Heavy Blue Pebble" -> listOf(
            StoryPage(R.drawable.pebble1, "In the Whispering Woods, Elara lived in a cottage smelling of cinnamon and rain. She helped Silas, the clockmaker, polish the Great Town Clock's gears. Silas believed every tiny piece must be perfectly placed."),
            StoryPage(R.drawable.pebble2, "One afternoon, Silas pulled a glowing crystal from a velvet box. \"This is the Heart-Spark, Elara,\" he whispered. \"The clock needs it for the midnight festival. I must get my specialized oil, so please, do not touch it. It is very fragile.\""),
            StoryPage(R.drawable.pebble3, "Elara stared at the Heart-Spark, which shimmered like a captured star. Reaching out to feel its warmth, she accidentally knocked it to the floor. It chipped with a sharp clink, turning dull and lopsided."),
            StoryPage(R.drawable.pebble4, "Elara’s heart raced. She hid the chip under a rug and put the crystal back, turning the broken side away. She hoped if she stayed quiet, the clock would still work and no one would know."),
            StoryPage(R.drawable.pebble5, "As soon as the secret was hidden, Elara felt a heavy blue pebble in her pocket. She didn't know how it got there. Every time she thought about the broken crystal, the pebble grew heavier"),
            StoryPage(R.drawable.pebble6, "Silas returned smiling, but he didn't see the damage. \"Ready for the festival?\" he asked. Elara nodded without a smile. The stone now felt as heavy as a brick, making it hard for her to walk"),
            StoryPage(R.drawable.pebble7, "That evening, the village gathered at the tower. Elara stood in the shadows, the stone dragging her down like a mountain in her pocket. She knew that if she didn't speak, the weight would never go away."),
            StoryPage(R.drawable.pebble8, "Just as Silas reached for the lever, Elara fell forward. \"Wait!\" she cried. With tears in her eyes, she pulled out the heavy blue stone. \"I broke the Heart-Spark, Silas. I was scared, so I hid the piece under the rug\""),
            StoryPage(R.drawable.pebble9, "Elara told the truth, and the heavy stone turned into mist. Silas smiled and said, \"A lie is heavy, but the truth is light.\" Elara felt happy and free again."),
            StoryPage(R.drawable.pebble10, "Silas fixed the crystal with glue. The clock started to tick and hum. It made a loud, happy sound! Elara felt light and happy. She danced while the bells rang for everyone.")
        )
        //story 6
        "Strength" -> listOf(
            StoryPage(R.drawable.stre1, "Bramble was in a big rush. \"Out of my way!\" he yelled. He tripped over Argo, who was walking slowly. Bramble didn't even look back to see if Argo was okay; he just kept running fast."),
            StoryPage(R.drawable.stre2, "Bramble reached the water and landed with a loud thump. Midge was holding a tiny dew pearl for her garden. \"Move away!\" Bramble shouted. He splashed the water and ran away."),
            StoryPage(R.drawable.stre3, "Bramble reached the party, but the big gate was stuck. He pushed and pushed, but it would not move. Then Argo arrived. Even though Bramble was mean, Argo used his strong shell to help him push."),
            StoryPage(R.drawable.stre4, "The gate moved, but a tiny latch held it shut. Bramble and Argo were too big. Midge crawled inside and opened it. Bramble realized that being small is also a big strength"),
            StoryPage(R.drawable.stre5, "The gate opened. Bramble apologized, \"I was selfish; thank you for your help.\" He learned that every creature deserves respect, no matter their size or speed.")
        )
        //story 7
        "Secret Garden" -> listOf(
            StoryPage(R.drawable.sg1, "Liron, a tiny bird, found a shimmering silver seed in a big meadow. \"What a treasure!\" he chirped, excited by its brightness."),
            StoryPage(R.drawable.sg2, "Liron took the seed to Zenon, the wise tortoise. \"I found a magic seed!\" he chirped. \"Will it grow into a mountain of berries by lunch?\""),
            StoryPage(R.drawable.sg3, "Zenon looked at the seed. She explained that it was a Heart-Bloom seed. It only grows for those with a patient and kind heart, not just from sun or rain."),
            StoryPage(R.drawable.sg4, "Liron didn't want to wait. He buried the seed and shouted, \"Grow now!\" while flapping his wings hard. He wanted his berries immediately, not patience."),
            StoryPage(R.drawable.sg5, "Nothing happened after hours of waiting. Grumpy and tired, Liron sighed by sunset with no sprout in sight. He whispered, \"Maybe the seed is broken.\""),
            StoryPage(R.drawable.sg6, "Zenon brought water, explaining that patience requires care even when results are hidden. Liron listened, gently watering the soil with a softer heart."),
            StoryPage(R.drawable.sg7, "Just then, Zora the ladybug landed on a nearby leaf looking exhausted and thirsty. Liron looked at his seed but chose to give the last few drops of water to Zora instead of saving it for the dirt."),
            StoryPage(R.drawable.sg8, "That afternoon, dark clouds brought a cold rain. Liron wanted to fly to his cozy nest but stayed under a sturdy leaf to keep his seed company through the storm."),
            StoryPage(R.drawable.sg9, "At sunrise, a magical rainbow flower bloomed. Zenon smiled and said, \"Your kindness made it grow.\" Liron learned that patience and a helping heart are the real keys to magic."),
            StoryPage(R.drawable.sg10, "Zora joined them, enjoying the flower's scent. Liron smiled, realizing that while the bloom was beautiful, the patience he had practiced and the friends he had helped were his truest treasures.")
        )
        // story 8
        "Tembo’s Big Day" -> listOf(
            StoryPage(R.drawable.tembo1, "Tembo opens his big eyes and stretches his long trunk toward the sky. \"Hello, Sun!\" he trumpets happily."),
            StoryPage(R.drawable.tembo2, "Tembo notices a bright yellow hat resting on the grass. His eyes light up as he admires the cheerful color glowing in the sunlight."),
            StoryPage(R.drawable.tembo3, "Tembo spots a colorful butterfly named Mariposa. He smiles as his new friend dances through the air to say hello."),
            StoryPage(R.drawable.tembo4, "Tembo peeks into the garden and finds Mariposa hidden among the bright petals."),
            StoryPage(R.drawable.tembo5, "\"I found you!\" Tembo laughs. Mariposa lands softly right on his nose."),
            StoryPage(R.drawable.tembo6, "Tembo and Mariposa find a sweet snack to share. \"Yum, yum!\" they say together."),
            StoryPage(R.drawable.tembo7, "The stars come out as Tembo and Mariposa close their eyes. \"Goodnight!\" they whisper, drifting off to sleep.")
        )
        "Garden Day" -> listOf(
            StoryPage(R.drawable.gd1, "Arvid opens his eyes and feels the golden sunlight. The cozy green nest is the perfect place to start a bright day."),
            StoryPage(R.drawable.gd2, "Arvid spots a little red ladybug resting on a green leaf. \"Hello, Maika!\" he chirps with a happy flutter."),
            StoryPage(R.drawable.gd3, "Maika zips across the leaf, and Arvid hops quickly after her . They are racing toward a secret spot in the garden!"),
            StoryPage(R.drawable.gd4, "Arvid lands on the edge of the giant, golden flower. Its sweet scent fills the air, smelling just like a warm summer day."),
            StoryPage(R.drawable.gd5, "Arvid and Maika take a sip of the cool, sparkly dewdrop. \"Splish!\" it tastes fresh and sweet."),
            StoryPage(R.drawable.gd6, "The sky turns orange as the sun begins to hide. Arvid whispers, \"Goodnight, Maika,\" and tucks his head into his wings for a long sleep.")
        )
        // story 10
        "Yellow Hat" -> listOf(
            StoryPage(R.drawable.hat1, "Pip sees a bright yellow hat lying in the soft grass. He smiles at the cheerful color glowing in the sun."),
            StoryPage(R.drawable.hat2, "Twitch hops over quickly to see the bright hat. He looks at the golden treasure with big, excited eyes."),
            StoryPage(R.drawable.hat3, "\"Let's share!\" says Pip. Pip places the sunny yellow hat right on Twitch's head. \"Now it's your turn!\" Pip says with a happy cheer."),
            StoryPage(R.drawable.hat4, "The wind sweeps the hat up, and Pip watches it fly into the air. \"Oh no!\" he shouts as the yellow hat sails higher and higher into the sky."),
            StoryPage(R.drawable.hat5, "Twitch sprints across the grass, reaching up high as she tries to grab the hat before it floats away."),
            StoryPage(R.drawable.hat6, "The yellow hat falls from the sky and lands right on Pebble's head. \"Look!\" Pip yells, pointing at the little turtle wearing their treasure."),
            StoryPage(R.drawable.hat7, "The yellow hat lands right on Pebble’s tiny head, fitting him perfectly. They both cheer, happy to see their new friend looking so bright!")
        )

        else -> emptyList()
    }
}
data class StoryItem(val name: String, val image: Int)
data class StoryPage(val imageRes: Int, val text: String)