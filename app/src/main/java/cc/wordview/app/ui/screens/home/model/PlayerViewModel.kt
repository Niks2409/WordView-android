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

package cc.wordview.app.ui.screens.home.model

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.lifecycle.ViewModel
import cc.wordview.app.audio.AudioPlayer
import cc.wordview.app.subtitle.Lyrics
import cc.wordview.app.subtitle.WordViewCue
import cc.wordview.app.ui.screens.home.PlayerStatus
import cc.wordview.gengolex.Language
import cc.wordview.gengolex.Parser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

object PlayerViewModel : ViewModel() {
    private val TAG = PlayerViewModel::class.java.simpleName

    private val _cues = MutableStateFlow(ArrayList<WordViewCue>())
    private val _playIcon = MutableStateFlow(Icons.Filled.PlayArrow)
    private val _lyrics = MutableStateFlow(Lyrics())
    private val _parser = MutableStateFlow(Parser(Language.ENGLISH))
    private val _player = MutableStateFlow(AudioPlayer())
    private val _currentCue = MutableStateFlow(WordViewCue())
    private val _finalized = MutableStateFlow(false)
    private val _playerStatus = MutableStateFlow(PlayerStatus.LOADING)
    private val _filterRomanizations = MutableStateFlow(true)

    val cues = _cues.asStateFlow()
    val playIcon = _playIcon.asStateFlow()
    val lyrics = _lyrics.asStateFlow()
    val parser = _parser.asStateFlow()
    val player = _player.asStateFlow()
    val currentCue = _currentCue.asStateFlow()
    val filterRomanizations = _filterRomanizations.asStateFlow()
    val finalized = _finalized.asStateFlow()
    val playerStatus = _playerStatus.asStateFlow()

    fun setCues(cues: ArrayList<WordViewCue>) {
        _cues.update { cues }
    }

    fun clearCues() {
        _cues.update { ArrayList() }
    }

    fun playIconPause() {
        _playIcon.update { Icons.Filled.PlayArrow }
    }

    fun playIconPlay() {
        _playIcon.update { Icons.Filled.Pause }
    }

    fun lyricsParse(res: String) {
        val newLyrics = Lyrics()
        newLyrics.parse(res)
        _lyrics.update { newLyrics }
    }

    fun initParser(language: Language) {
        _parser.update { Parser(language) }
    }

    fun addDictionary(name: String, dictionary: String) {
        _parser.value.addDictionary(name, dictionary)
    }

    fun setCurrentCue(cue: WordViewCue) {
        _currentCue.update { cue }
    }

    fun setFilterRomanizations(filter: Boolean) {
        _filterRomanizations.update { filter }
    }

    fun finalize() {
        _finalized.update { true }
    }

    fun unFinalize() {
        _finalized.update { false }
    }

    fun setPlayerStatus(playerStatus: PlayerStatus) {
        _playerStatus.update { playerStatus }
    }

    fun reset() {
        this.player.value.stop()
        this.setPlayerStatus(PlayerStatus.LOADING)
        this.clearCues()
        this.setCurrentCue(WordViewCue())

        Log.v(
            TAG,
            "reset: playerStatus=${playerStatus.value} cues.size=${cues.value.size} currentCue.text=${currentCue.value.text}"
        )
    }
}