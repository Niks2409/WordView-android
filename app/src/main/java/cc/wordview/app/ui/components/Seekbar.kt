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

package cc.wordview.app.ui.components

import android.annotation.SuppressLint
import androidx.annotation.IntRange
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cc.wordview.app.extensions.fillMaxWidth
import cc.wordview.app.extensions.percentageOf
import cc.wordview.app.ui.theme.Typography

@Composable
fun Seekbar(
    currentPosition: Long,
    duration: Long,
    @IntRange(from = 0, to = 100) bufferingProgress: Int
) {
    val progress = duration.percentageOf(currentPosition)

    Column(Modifier.testTag("seekbar")) {
        Text(
            modifier = Modifier
                .padding(horizontal = 72.dp)
                .padding(bottom = 5.dp),
            text = "${formatTime(currentPosition)} / ${formatTime(duration)}",
            style = Typography.labelMedium,
            fontSize = 14.sp
        )
        Box(
            Modifier
                .fillMaxWidth()
                .height(4.dp)
                .padding(horizontal = 64.dp)
                .background(color = MaterialTheme.colorScheme.surfaceVariant)
                .alpha(0.75f)
        ) {
            Box(
                Modifier
                    .fillMaxWidth(bufferingProgress)
                    .fillMaxHeight()
                    .background(color = MaterialTheme.colorScheme.outline)
                    .testTag("buffer-line")
            )

            Box(
                Modifier
                    .fillMaxWidth((progress / 100f).toFloat())
                    .fillMaxHeight()
                    .background(color = MaterialTheme.colorScheme.primaryContainer)
                    .testTag("progress-line")
            )
        }
    }
}

@SuppressLint("DefaultLocale")
fun formatTime(milliseconds: Long): String {
    if (milliseconds <= 0) return "0:00"

    val minutes = (milliseconds / 1000) / 60
    val seconds = (milliseconds / 1000) % 60
    return String.format("%02d:%02d", minutes, seconds)
}
