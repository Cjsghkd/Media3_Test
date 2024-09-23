package com.example.media3_test.music

import android.net.Uri
import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.animation.with
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.example.media3_test.R
import com.example.media3_test.music.component.MusicCoverAnimation
import com.example.media3_test.music.component.MusicSlider
import com.example.media3_test.music.extension.convertToText
import com.example.media3_test.music.model.Music
import kotlinx.coroutines.delay

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MusicScreen() {
    val context = LocalContext.current
    val player = remember { ExoPlayer.Builder(context).build() }
    val playList = listOf(
        Music(
            name = "Master Of Puppets",
            artist = "Metallica",
            cover = R.drawable.master_of_puppets_album_cover,
            music = R.raw.master_of_puppets
        ),
        Music(
            name = "Everyday Normal Guy 2",
            artist = "Jon Lajoie",
            cover = R.drawable.everyday_normal_guy_2_album_cover,
            music = R.raw.everyday_normal_guy_2
        ),
        Music(
            name = "Lose Yourself",
            artist = "Eminem",
            cover = R.drawable.lose_yourself_album_cover,
            music = R.raw.lose_yourself
        ),
        Music(
            name = "Crazy",
            artist = "Gnarls Barkley",
            cover = R.drawable.crazy_album_cover,
            music = R.raw.crazy
        ),
        Music(
            name = "Till I Collapse",
            artist = "Eminem",
            cover = R.drawable.till_i_collapse_album_cover,
            music = R.raw.till_i_collapse
        ),
    )

    val pagerState = rememberPagerState(pageCount = { playList.count() })
    val playingSongIndex = remember { mutableIntStateOf(0) }

    LaunchedEffect(pagerState.currentPage) {
        playingSongIndex.intValue = pagerState.currentPage
        player.seekTo(pagerState.currentPage, 0)
    }

    LaunchedEffect(player.currentMediaItemIndex) {
        playingSongIndex.intValue = player.currentMediaItemIndex
        pagerState.animateScrollToPage(
            playingSongIndex.intValue,
            animationSpec = tween(500)
        )
    }

    LaunchedEffect(Unit) {
        playList.forEach {
            val path = "android.resource://" + "com.example.media3_test" + "/" + it.music
            val mediaItem = MediaItem.fromUri(Uri.parse(path))
            player.addMediaItem(mediaItem)
        }

        player.prepare()
    }

    DisposableEffect(Unit) {
        onDispose { player.release() }
    }

    val isPlaying = remember {
        mutableStateOf(false)
    }

    val currentPosition = remember {
        mutableLongStateOf(0)
    }

    val sliderPosition = remember {
        mutableLongStateOf(0)
    }

    val totalDuration = remember {
        mutableLongStateOf(0)
    }


    LaunchedEffect(key1 = player.currentPosition, key2 = player.isPlaying) {
        delay(1000)
        currentPosition.longValue = player.currentPosition
    }

    LaunchedEffect(currentPosition.longValue) {
        sliderPosition.longValue = currentPosition.longValue
    }

    LaunchedEffect(player.duration) {
        Log.d("test123", player.duration.toString())
        if (player.duration >= 0) {
            Log.d("test123", currentPosition.longValue.toString())
            totalDuration.longValue = player.duration
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize(), contentAlignment = Alignment.Center
    ) {
        val configuration = LocalConfiguration.current

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            AnimatedContent(
                targetState = playingSongIndex.intValue,
                transitionSpec = {
                    (scaleIn() + fadeIn()) togetherWith (scaleOut() + fadeOut())
                },
                label = "AnimatedContent",
            ) {
                Text(
                    text = playList[it].name, fontSize = 24.sp,
                    color = Color.Black,
                    style = TextStyle(fontWeight = FontWeight.ExtraBold)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            AnimatedContent(
                targetState = playingSongIndex.intValue,
                transitionSpec = {
                    (scaleIn() + fadeIn()) togetherWith (scaleOut() + fadeOut())
                },
                label = "AnimatedContent",
            ) {
                Text(
                    text = playList[it].artist, fontSize = 12.sp, color = Color.Black,
                    style = TextStyle(fontWeight = FontWeight.Bold)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            HorizontalPager(
                modifier = Modifier.fillMaxWidth(),
                state = pagerState,
                pageSize = PageSize.Fixed((configuration.screenWidthDp / (1.7)).dp),
                contentPadding = PaddingValues(horizontal = 85.dp)
            ) { page ->
                val painter = painterResource(id = playList[page].cover)

                if (page == pagerState.currentPage) {
                    MusicCoverAnimation(isSongPlaying = isPlaying.value, painter = painter)
                } else {
                    MusicCoverAnimation(isSongPlaying = false, painter = painter)
                }
            }

            Spacer(modifier = Modifier.height(54.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
            ) {
                MusicSlider(
                    value = sliderPosition.longValue.toFloat(),
                    onValueChange = {
                        sliderPosition.longValue = it.toLong()
                    },
                    onValueChangeFinished = {
                        currentPosition.longValue = sliderPosition.longValue
                        player.seekTo(sliderPosition.longValue)
                    },
                    musicDuration = totalDuration.longValue.toFloat()
                )

                Row(modifier = Modifier.fillMaxWidth(),) {
                    Text(
                        text = (currentPosition.longValue).convertToText(),
                        modifier = Modifier
                            .weight(1f)
                            .padding(8.dp),
                        color = Color.Black,
                        style = TextStyle(fontWeight = FontWeight.Bold)
                    )

                    val remainTime = totalDuration.longValue - currentPosition.longValue
                    Text(
                        text = if (remainTime >= 0) remainTime.convertToText() else "",
                        modifier = Modifier
                            .padding(8.dp),
                        color = Color.Black,
                        style = TextStyle(fontWeight = FontWeight.Bold)
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .clickable { player.seekToPreviousMediaItem() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        modifier = Modifier.size(40.dp / 1.5f),
                        painter = painterResource(id = R.drawable.ic_previous),
                        tint = Color.Black,
                        contentDescription = null
                    )
                }
                Spacer(modifier = Modifier.width(20.dp))
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .clickable {
                            if (isPlaying.value) {
                                player.pause()
                            } else {
                                player.play()
                            }
                            isPlaying.value = player.isPlaying
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        modifier = Modifier.size(100.dp / 1.5f),
                        painter = painterResource(id = if (isPlaying.value) R.drawable.ic_pause else R.drawable.ic_play),
                        tint = Color.Black,
                        contentDescription = null
                    )
                }
                Spacer(modifier = Modifier.width(20.dp))
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .clickable { player.seekToNextMediaItem() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        modifier = Modifier.size(40.dp / 1.5f),
                        painter = painterResource(id = R.drawable.ic_next),
                        tint = Color.Black,
                        contentDescription = null
                    )
                }
            }
        }
    }
}