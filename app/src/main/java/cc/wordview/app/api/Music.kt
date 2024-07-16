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

package cc.wordview.app.api

import android.content.Context
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import java.lang.reflect.Method
import java.net.URLEncoder

interface APICallback {
    fun onSuccessResponse(response: String?)
    fun onErrorResponse(response: VolleyError)
}

fun getHistory(callback: APICallback, context: Context) {
    val queue = Volley.newRequestQueue(context)
    val url = "$apiURL/music/history"

    val request = JsonAcceptingRequest(
        Request.Method.GET,
        url,
        { res -> callback.onSuccessResponse(res) },
        { err -> callback.onErrorResponse(err) })

    queue.add(request)
}

fun search(query: String, callback: APICallback, context: Context) {
    val queue = Volley.newRequestQueue(context)
    val url = "$apiURL/music/search?q=${URLEncoder.encode(query)}"

    val request = JsonAcceptingRequest(
        Request.Method.GET,
        url,
        { res -> callback.onSuccessResponse(res) },
        { err -> callback.onErrorResponse(err) })

    // Little hack to deal with the search taking too much due to using ytdl
    // TODO: Remove this retry policy when the API starts using the NewPipeExtractor
    request.setRetryPolicy(
        DefaultRetryPolicy(
            20000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
    )

    queue.add(request)
}

fun getLyrics(id: String, lang: String, callback: APICallback, context: Context) {
    val queue = Volley.newRequestQueue(context)
    val url = "$apiURL/music/lyrics?id=$id&lang=$lang"

    val stringRequest =
        StringRequest(Request.Method.GET, url, { response ->
            callback.onSuccessResponse(response)
        },
            { err -> callback.onErrorResponse(err) })

    queue.add(stringRequest)
}