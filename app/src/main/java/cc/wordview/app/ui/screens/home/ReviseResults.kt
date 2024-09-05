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

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import cc.wordview.app.extensions.goBack
import cc.wordview.app.ui.components.BackTopAppBar

@Composable
fun ReviseResults(navHostController: NavHostController) {
    fun leave() {
        navHostController.goBack()
    }

    Scaffold(topBar = {
        BackHandler { leave() }
        BackTopAppBar(text = "Results") { leave() }
    }) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {

        }
    }
}