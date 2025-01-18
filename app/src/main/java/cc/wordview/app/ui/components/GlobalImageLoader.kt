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

import android.content.Context
import android.graphics.Bitmap
import coil.ImageLoader
import coil.memory.MemoryCache
import coil.request.ErrorResult
import coil.request.ImageRequest
import coil.request.SuccessResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Globally handles the preloading of images
 */
object GlobalImageLoader {
    private lateinit var loader: ImageLoader
    private val globalImageLoaderScope = CoroutineScope(Dispatchers.IO)

    private val imagesStatus = HashMap<String, ImageLoaderStatus>()
    private val imageRequestQueue = ArrayList<ImageRequest>()

    fun init(context: Context) {
        loader = ImageLoader(context)
    }

    /**
     * Will add the request to a queue. The queue items will be executed by calling `executeAllInQueue()`
     */
    fun enqueue(request: ImageRequest.Builder) {
        globalImageLoaderScope.launch {
            request.listener(object : ImageRequest.Listener {
                override fun onStart(request: ImageRequest) {
                    val key = request.memoryCacheKey?.key!!
                    imagesStatus[key] = ImageLoaderStatus.LOADING
                }

                override fun onError(request: ImageRequest, result: ErrorResult) {
                    Timber.e("Failed to download image: ${result.throwable.message}")

                    val key = request.memoryCacheKey?.key!!
                    imagesStatus[key] = ImageLoaderStatus.ERROR
                }

                override fun onSuccess(request: ImageRequest, result: SuccessResult) {
                    val key = request.memoryCacheKey?.key!!
                    imagesStatus[key] = ImageLoaderStatus.SUCCESS
                }
            })
            imageRequestQueue.add(request.build())
        }
    }

    suspend fun executeAllInQueue() {
        for (item in imageRequestQueue) {
            loader.execute(item)
        }
    }

    fun getCachedImage(key: String): Bitmap? {
        val currentStatus = imagesStatus[key]

        when (currentStatus) {
            ImageLoaderStatus.LOADING -> {
                Timber.w("The image associated with key=$key has not yet been resolved")
                return null
            }
            ImageLoaderStatus.ERROR -> {
                Timber.w("The image associated with key=$key is not available due to an error")
                return null
            }
            ImageLoaderStatus.SUCCESS -> {
                val cachedValue = loader.memoryCache?.get(MemoryCache.Key(key))
                return cachedValue?.bitmap
            }

            null -> {
                Timber.w("The image associated with key=$key is not present")
                return null
            }
        }
    }

    enum class ImageLoaderStatus {
        LOADING,
        ERROR,
        SUCCESS
    }
}