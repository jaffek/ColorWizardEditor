package com.affek.colorwizardeditor.presentation.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.splineBasedDecay
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculateCentroid
import androidx.compose.foundation.gestures.calculateCentroidSize
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateRotation
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChanged
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.util.fastAny
import androidx.compose.ui.util.fastForEach
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.abs


@Composable
fun ZoomableImage(
    bitmap: ImageBitmap,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null,
    filterQuality: FilterQuality = DrawScope.DefaultFilterQuality
) {

    val scope = rememberCoroutineScope()

    val zoomAnimated = remember { Animatable(1f) }
    val panXAnimated = remember { Animatable(0f) }
    val panYAnimated = remember { Animatable(0f) }
    var maxX by remember { mutableStateOf(0f) }
    var maxY by remember { mutableStateOf(0f) }

    val bitmapRatio = bitmap.height.toFloat() / bitmap.width

    val imageModifier = Modifier
        .fillMaxSize()
        .pointerInput(Unit) {
            val decay = splineBasedDecay<Float>(this)
            val imageContainerRatio = size.height.toFloat() / size.width

            customDetectTransformGestures(
                onGesture = { gestureCentroid, gesturePan, gestureZoom, _ ->
                    val oldScale = zoomAnimated.value
                    val newScale = (zoomAnimated.value * gestureZoom).coerceIn(0.5f..5f)
                    val centroidX = gestureCentroid.x-size.width/2f
                    val centroidY = gestureCentroid.y-size.height/2f
                    var newOffsetX = ((panXAnimated.value - centroidX / oldScale) +
                            (centroidX / newScale + gesturePan.x / oldScale)) * newScale
                    var newOffsetY = ((panYAnimated.value - centroidY / oldScale) +
                            (centroidY / newScale + gesturePan.y / oldScale)) * newScale

                    maxX = if (bitmapRatio > imageContainerRatio)
                        (size.width * ((newScale * imageContainerRatio / bitmapRatio - 1).coerceAtLeast(0f)) / 2f)
                    else
                        (size.width * ((newScale - 1).coerceAtLeast(0f)) / 2f)

                    maxY = if (bitmapRatio < imageContainerRatio)
                        (size.height * ((newScale * bitmapRatio / imageContainerRatio - 1).coerceAtLeast(0f)) / 2f)
                    else
                        (size.height * ((newScale - 1).coerceAtLeast(0f)) / 2f)

                    newOffsetX = (newOffsetX).coerceIn(-maxX, maxX) / newScale
                    newOffsetY = (newOffsetY).coerceIn(-maxY, maxY) / newScale

                    panXAnimated.updateBounds(-maxX, maxX)
                    panYAnimated.updateBounds(-maxY, maxY)

                    scope.launch {
                        zoomAnimated.snapTo(newScale)
                        panXAnimated.snapTo(newOffsetX)
                        panYAnimated.snapTo(newOffsetY)
                    }
                },
                onFling = { velocity ->
                    scope.launch {
                        panXAnimated.updateBounds(-maxX / zoomAnimated.value, maxX / zoomAnimated.value)
                        panXAnimated.animateDecay(velocity.x / zoomAnimated.value, decay)
                    }
                    scope.launch {
                        panYAnimated.updateBounds(-maxY / zoomAnimated.value, maxY / zoomAnimated.value)
                        panYAnimated.animateDecay(velocity.y / zoomAnimated.value, decay)
                    }
                }
            )
        }
        .pointerInput(Unit) {
            val imageContainerRatio = size.height.toFloat() / size.width
            detectTapGestures(
                onDoubleTap = {
                    if (zoomAnimated.value != 1f) {

                        panXAnimated.updateBounds(0f, 0f)
                        panYAnimated.updateBounds(0f, 0f)

                        scope.launch {
                            zoomAnimated.animateTo(1f)
                        }
                        scope.launch {
                            panXAnimated.snapTo(0f)
                        }
                        scope.launch {
                            panYAnimated.snapTo(0f)
                        }
                    }
                    else {
                        val oldScale = zoomAnimated.value
                        val newScale = 3f
                        val centroidX = it.x-size.width/2f
                        val centroidY = it.y-size.height/2f
                        var newOffsetX = (- centroidX / oldScale + centroidX / newScale) * newScale
                        var newOffsetY = (- centroidY / oldScale + centroidY / newScale) * newScale

                        maxX = if (bitmapRatio > imageContainerRatio)
                            (size.width * ((newScale * imageContainerRatio / bitmapRatio - 1).coerceAtLeast(0f)) / 2f)
                        else
                            (size.width * ((newScale - 1).coerceAtLeast(0f)) / 2f)

                        maxY = if (bitmapRatio < imageContainerRatio)
                            (size.height * ((newScale * bitmapRatio / imageContainerRatio - 1).coerceAtLeast(0f)) / 2f)
                        else
                            (size.height * ((newScale - 1).coerceAtLeast(0f)) / 2f)

                        newOffsetX = (newOffsetX).coerceIn(-maxX, maxX) / newScale
                        newOffsetY = (newOffsetY).coerceIn(-maxY, maxY) / newScale

                        panXAnimated.updateBounds(-maxX, maxX)
                        panYAnimated.updateBounds(-maxY, maxY)

                        scope.launch {
                            zoomAnimated.animateTo(newScale)
                        }
                        scope.launch {
                            panXAnimated.animateTo(newOffsetX)
                        }
                        scope.launch {
                            panYAnimated.animateTo(newOffsetY)
                        }
                    }
                }
            )
        }
        .graphicsLayer {
            translationX = panXAnimated.value * zoomAnimated.value
            translationY = panYAnimated.value * zoomAnimated.value
            scaleX = zoomAnimated.value
            scaleY = zoomAnimated.value
        }

    Image(
        bitmap = bitmap,
        contentDescription = contentDescription,
        modifier = modifier.then(imageModifier),
        alignment = alignment,
        contentScale = contentScale,
        alpha = alpha,
        colorFilter = colorFilter,
        filterQuality = filterQuality
    )
}

suspend fun PointerInputScope.customDetectTransformGestures(
    panZoomLock: Boolean = false,
    onGesture: (centroid: Offset, pan: Offset, zoom: Float, rotation: Float) -> Unit,
    onFling: (velocity: Velocity) -> Unit = {}
) {
    awaitEachGesture {
        var rotation = 0f
        var zoom = 1f
        var pan = Offset.Zero
        var pastTouchSlop = false
        val touchSlop = viewConfiguration.touchSlop
        var lockedToPanZoom = false
        val velocityTracker = VelocityTracker()
        var fling = false

        awaitFirstDown(requireUnconsumed = false)
        do {
            val event = awaitPointerEvent()
            val canceled = event.changes.fastAny { it.isConsumed }
            if (!canceled) {
                val zoomChange = event.calculateZoom()
                val rotationChange = event.calculateRotation()
                val panChange = event.calculatePan()

                if (!pastTouchSlop) {
                    zoom *= zoomChange
                    rotation += rotationChange
                    pan += panChange

                    val centroidSize = event.calculateCentroidSize(useCurrent = false)
                    val zoomMotion = abs(1 - zoom) * centroidSize
                    val rotationMotion = abs(rotation * PI.toFloat() * centroidSize / 180f)
                    val panMotion = pan.getDistance()

                    if (zoomMotion > touchSlop ||
                        rotationMotion > touchSlop ||
                        panMotion > touchSlop
                    ) {
                        pastTouchSlop = true
                        lockedToPanZoom = panZoomLock && rotationMotion < touchSlop
                    }
                    fling = false
                }
                if (pastTouchSlop) {
                    val centroid = event.calculateCentroid(useCurrent = false)
                    val effectiveRotation = if (lockedToPanZoom) 0f else rotationChange

                    if (event.changes.size >= 2) {
                        velocityTracker.resetTracking()
                    } else if (centroid.isSpecified) {
                        val change = event.changes.firstOrNull()
                        if (change?.pressed == true) {
                            velocityTracker.addPosition(
                                change.uptimeMillis,
                                centroid
                            )
                        }
                    }
                    if (effectiveRotation != 0f ||
                        zoomChange != 1f ||
                        panChange != Offset.Zero
                    ) {
                        onGesture(centroid, panChange, zoomChange, effectiveRotation)
                        fling = zoomChange == 1f
                    }
                    event.changes.fastForEach {
                        if (it.positionChanged()) {
                            it.consume()
                        }
                    }
                }
            }
        } while (!canceled && event.changes.fastAny { it.pressed })
            if (fling) {
                val velocity = velocityTracker.calculateVelocity()
                onFling(velocity)
            }
    }
}

