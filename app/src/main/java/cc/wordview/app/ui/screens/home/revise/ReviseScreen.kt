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

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import cc.wordview.gengolex.languages.Word

sealed class ReviseScreen(val route: String) {
    @Composable
    open fun Composable(navHostController: NavHostController) {}

    data object Presenter : ReviseScreen("presenter") {
        @Composable
        override fun Composable(navHostController: NavHostController) {
            Presenter()
        }
    }

    data object DragAndDrop : ReviseScreen("d-a-d") {
        @Composable
        override fun Composable(navHostController: NavHostController) {
            DragAndDrop(navHostController)
        }
    }

    companion object {
        val screens = listOf(Presenter, DragAndDrop)

        fun getByRoute(route: String): ReviseScreen? {
            for (screen in screens) {
                if (screen.route == route) return screen
            }

            return null
        }
    }
}