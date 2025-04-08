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

package cc.wordview.app.extensions

import cc.wordview.gengolex.Language
import java.net.URLEncoder

fun String.asURLEncoded(): String {
    @Suppress("DEPRECATION")
    return URLEncoder.encode(this) ?: this
}

fun String.capitalize(): String {
    return this[0].uppercase() + this.lowercase().substring(1)
}

// convert language tag to language name in smaller to camel case
fun String.languageDisplayName(): String {
    return Language.byTag(this).name
        .lowercase()
        .replaceFirstChar { it.uppercase() }
}