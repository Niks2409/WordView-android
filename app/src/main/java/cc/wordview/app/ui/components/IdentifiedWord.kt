/*
 * Copyright (c) 2025 Arthur Araujo
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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import cc.wordview.app.ImageCacheManager
import cc.wordview.app.extensions.capitalize
import cc.wordview.gengolex.word.Word
import coil.compose.AsyncImage

@Composable
fun IdentifiedWord(word: Word, text: String, langtag: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.width(IntrinsicSize.Max),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (word.representable) {
            val image = ImageCacheManager.getCachedImage(word.parent)

            if (image != null) AsyncImage(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f),
                model = image,
                contentDescription = null
            )
        }
        Row(
            Modifier.background(
                if (word.representable)
                    MaterialTheme.colorScheme.primaryContainer
                else
                    MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            for (char in word.word) {
                Text(
                    modifier = Modifier
                        .testTag("text-cue-plain"),
                    text = char.toString(),
                    fontSize = getFontSize(text, langtag),
                    color = MaterialTheme.colorScheme.inverseSurface
                )
            }
        }
        Row(
            modifier = Modifier.height(20.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            @Suppress("SENSELESS_COMPARISON")
            if (word.type != null) {
                TraitText(
                    text = word.type.capitalize(),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            if (word.time != null) {
                Spacer(Modifier.size(8.dp))
                TraitText(
                    text = word.time!!.capitalize(),
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
        }
    }
}