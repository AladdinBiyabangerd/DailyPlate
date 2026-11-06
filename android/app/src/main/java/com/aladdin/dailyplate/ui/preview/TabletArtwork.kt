package com.aladdin.dailyplate.ui.preview

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.size
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import java.util.concurrent.ConcurrentHashMap

/** Transparent delivery has larger empty margins than the low-resolution matte design art.
 * Fit its alpha subject (with breathing room) into the approved image box, without stretching,
 * generating or rewriting an asset. The original phone painter is intentionally unchanged. */
private val tabletArtBounds = ConcurrentHashMap<Int, Pair<IntOffset, IntSize>>()
@Composable internal fun TabletArt(@DrawableRes id: Int, width: Dp, height: Dp, modifier: Modifier = Modifier) {
    val image = ImageBitmap.imageResource(id)
    val painter = remember(image, id) {
        val (offset, size) = tabletArtBounds.getOrPut(id) {
            val bitmap = image.asAndroidBitmap()
            val pixels = IntArray(bitmap.width * bitmap.height)
            bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
            var left = bitmap.width; var top = bitmap.height; var right = -1; var bottom = -1
            pixels.forEachIndexed { index, pixel -> if (pixel ushr 24 > 30) {
                val x = index % bitmap.width; val y = index / bitmap.width
                left = minOf(left, x); right = maxOf(right, x); top = minOf(top, y); bottom = maxOf(bottom, y)
            } }
            if (right < left) IntOffset.Zero to IntSize(image.width, image.height) else {
                val padding = (maxOf(image.width, image.height) * .02f).toInt()
                left = (left - padding).coerceAtLeast(0); top = (top - padding).coerceAtLeast(0)
                right = (right + padding + 1).coerceAtMost(image.width); bottom = (bottom + padding + 1).coerceAtMost(image.height)
                IntOffset(left, top) to IntSize(right - left, bottom - top)
            }
        }
        BitmapPainter(image, srcOffset = offset, srcSize = size)
    }
    Image(painter, contentDescription = null, contentScale = ContentScale.Fit, modifier = modifier.size(width, height))
}

@Composable internal fun TabletArt(@DrawableRes id: Int, width: Dp, modifier: Modifier = Modifier) = TabletArt(id, width, width, modifier)
