/*
 * Copyright (c) 2024 Arthur Araujo
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package cc.wordview.app.ui.screens.home

import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import cc.wordview.app.R
import cc.wordview.app.SongViewModel
import cc.wordview.app.api.handler.PlayerRequestHandler
import cc.wordview.app.extensions.goBack
import cc.wordview.app.extractor.getSubtitleFor
import cc.wordview.app.subtitle.WordViewCue
import cc.wordview.app.ui.components.AsyncComposable
import cc.wordview.app.ui.components.FadeOutBox
import cc.wordview.app.ui.components.PlayerButton
import cc.wordview.app.ui.components.TextCue
import cc.wordview.app.ui.screens.home.model.PlayerViewModel
import cc.wordview.app.ui.theme.Typography
import cc.wordview.gengolex.Language
import coil.compose.AsyncImage
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import me.zhanghai.compose.preference.LocalPreferenceFlow
import kotlin.concurrent.thread

@SuppressLint("SourceLockedOrientationActivity")
@Composable
fun Player(
    navHostController: NavHostController,
    viewModel: PlayerViewModel = PlayerViewModel,
    requestHandler: PlayerRequestHandler = PlayerRequestHandler,
    autoplay: Boolean = true
) {
    val song by SongViewModel.video.collectAsStateWithLifecycle()
    val cues by viewModel.cues.collectAsStateWithLifecycle()
    val lyrics by viewModel.lyrics.collectAsStateWithLifecycle()
    val playIcon by viewModel.playIcon.collectAsStateWithLifecycle()
    val initialized by viewModel.initialized.collectAsStateWithLifecycle()
    val audioPlayer by viewModel.player.collectAsStateWithLifecycle()

    val systemUiController = rememberSystemUiController()

    var controlsLocked by remember { mutableStateOf(true) }
    var audioInitFailed by remember { mutableStateOf(false) }

    var currentCue by remember { mutableStateOf(WordViewCue()) }

    val context = LocalContext.current

    val preferences by LocalPreferenceFlow.current.collectAsStateWithLifecycle()

    fun leave() {
        audioPlayer.stop()
        viewModel.clearCues()
        viewModel.deinitialize()
        navHostController.goBack()
    }

    fun initAudio() {
        var endpoint: String? = preferences["api_endpoint"]
        if (endpoint == null) endpoint = "10.0.2.2"

        audioPlayer.initialize("http://$endpoint:8080/api/v1/music/download?id=${song.id}")
    }

    fun fetchLyrics() {
        val url = getSubtitleFor(song.id, "ja")

        if (!url.isNullOrEmpty()) {
            requestHandler.getLyrics(url)
        } else {
            requestHandler.getLyricsWordFind(song.searchQuery)
        }
    }

    BackHandler { leave() }

    LaunchedEffect(Unit) {
        requestHandler.apply {
            endpoint = preferences["api_endpoint"]!!

            onLyricsSucceed = {
                controlsLocked = false

                if (autoplay) audioPlayer.togglePlay()
                requestHandler.getDictionary("kanji")
            }

            init(context)
        }

        audioPlayer.apply {
            onPositionChange = {
                currentCue = lyrics.getCueAt(it)
            }

            onInitializeFail = {
                audioInitFailed = true
                controlsLocked = true
            }
        }

        // This is only threaded to make clear for the user that the app is not frozen
        // while it is downloading the necessary resources.
        // TODO: thread doesn't seem right to be placed here, delegate it elsewhere.
        thread {
            if (!initialized) {
                viewModel.initialize()
                viewModel.initParser(Language.JAPANESE)
                initAudio()
                fetchLyrics()
            }
        }
    }

    DisposableEffect(Unit) {
        systemUiController.isSystemBarsVisible = false
        systemUiController.systemBarsBehavior = 2

        (context as? Activity)?.requestedOrientation =
            ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE

        onDispose {
            (context as? Activity)?.requestedOrientation =
                ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            systemUiController.isSystemBarsVisible = true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(),
        verticalArrangement = Arrangement.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(1f)
        ) {
            if (audioInitFailed) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("error-message"),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        modifier = Modifier.size(180.dp),
                        painter = painterResource(id = R.drawable.radio),
                        contentDescription = ""
                    )
                    Spacer(Modifier.size(15.dp))
                    Text(
                        text = "An error has occurred \nand the audio could not be played.",
                        textAlign = TextAlign.Center,
                        style = Typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            } else {
                AsyncComposable(condition = (cues.size > 0)) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        AsyncImage(
                            model = song.cover,
                            contentDescription = "${song.title} cover",
                            modifier = Modifier
                                .fillMaxSize()
                                .alpha(0.15f),
                            contentScale = ContentScale.FillWidth,
                        )
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Bottom
                        ) {
                            TextCue(modifier = Modifier.zIndex(1f), cue = currentCue)
                        }
                        // Box Controls overlay
                        FadeOutBox(duration = 250, stagnationTime = 2000) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(64.dp),
                                contentAlignment = Alignment.TopStart,
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = { leave() },
                                        colors = IconButtonDefaults.iconButtonColors(contentColor = MaterialTheme.colorScheme.inverseSurface)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.ArrowBack,
                                            contentDescription = "Back"
                                        )
                                    }
                                    Text(
                                        text = song.title,
                                        color = MaterialTheme.colorScheme.inverseSurface,
                                        fontSize = 18.sp
                                    )
                                }
                            }
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    Modifier.fillMaxWidth(0.5f),
                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    PlayerButton(icon = Icons.Filled.SkipPrevious, size = 72.dp) {
                                        audioPlayer.skipBackward()
                                    }
                                    PlayerButton(icon = playIcon, size = 80.dp) {
                                        audioPlayer.togglePlay()
                                    }
                                    PlayerButton(icon = Icons.Filled.SkipNext, size = 72.dp) {
                                        audioPlayer.skipForward()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
