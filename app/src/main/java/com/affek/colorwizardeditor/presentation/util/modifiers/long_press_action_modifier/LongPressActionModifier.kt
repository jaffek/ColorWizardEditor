package com.affek.colorwizardeditor.presentation.util.modifiers.conditional_modifier

import android.util.Log
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun Modifier.longPressAction(
    onPressAction: (pressed : Boolean) -> Unit
) = composed {
    val scope = rememberCoroutineScope()
    pointerInput(Unit) {
        awaitEachGesture {
            awaitFirstDown(requireUnconsumed = false)
            var enoughTime = false
            val pressJob = scope.launch {
                delay(viewConfiguration.longPressTimeoutMillis)
                enoughTime = true
                onPressAction(true)
            }
            waitForUpOrCancellation()
            pressJob.cancel()
            if(enoughTime)
                onPressAction(false)
        }
    }
}