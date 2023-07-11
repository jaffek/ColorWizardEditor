package com.affek.colorwizardeditor.presentation.color_transfer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.ramcosta.composedestinations.annotation.Destination
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.asImageBitmap
import androidx.hilt.navigation.compose.hiltViewModel
import com.affek.colorwizardeditor.navigation.ColorTransferScreenNavArgs
import com.affek.colorwizardeditor.presentation.components.zoomable.rememberZoomState
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import zoomable


@Destination(
    navArgsDelegate = ColorTransferScreenNavArgs::class
)
@Composable
fun ColorTransferScreen(
    navigator: DestinationsNavigator,
    viewModel: ColorTransferViewModel = hiltViewModel()
) {
    val state : ColorTransferState by viewModel.state.collectAsState()
    val zoomState = rememberZoomState()

    Box(
        modifier = Modifier
        .fillMaxSize())
    {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {

            AnimatedVisibility(
                visible = state.isLoading,
                modifier = Modifier
                        .align(Alignment.Center)
            ) {
                CircularProgressIndicator()
            }
            AnimatedVisibility(
                visible = !state.isLoading,
                modifier = Modifier.fillMaxSize()
            ) {
                zoomState.setContentSize(Size(state.calculatedImage!!.width.toFloat(), state.calculatedImage!!.height.toFloat()))
                Image(
                    bitmap = state.calculatedImage!!.asImageBitmap(),
                    contentDescription = "Edited image",
                    modifier = Modifier
                        .fillMaxSize()
                        .zoomable(zoomState)

                )
            }

        }
    }
}

