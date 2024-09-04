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

package cc.wordview.app.ui.screens.home.revise

import android.annotation.SuppressLint
import android.os.CountDownTimer
import android.util.Log
import cc.wordview.app.ui.screens.home.model.WordReviseViewModel

object ReviseTimer : CountDownTimer(300000, 1000) {
    private val TAG = ReviseTimer::class.java.simpleName
    private val viewModel = WordReviseViewModel

    override fun onTick(millisUntilFinished: Long) {
        // TODO: communicate the timer progress through a websocket
        viewModel.setFormattedTime(formatMillisecondsToMS(millisUntilFinished))
    }

    @SuppressLint("DefaultLocale")
    private fun formatMillisecondsToMS(milliseconds: Long): String {
        val totalSeconds = milliseconds / 1000
        val minutes= totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%d:%02d", minutes, seconds)
    }


    override fun onFinish() {
        Log.i(TAG, "Timer finished!")
    }
}