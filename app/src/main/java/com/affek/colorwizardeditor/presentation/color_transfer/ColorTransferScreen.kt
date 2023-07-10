package com.affek.colorwizardeditor.presentation.color_transfer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.ramcosta.composedestinations.annotation.Destination
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.asImageBitmap
import androidx.hilt.navigation.compose.hiltViewModel
import com.affek.colorwizardeditor.navigation.ColorTransferScreenNavArgs
import com.affek.colorwizardeditor.presentation.components.ZoomableImage
import com.ramcosta.composedestinations.navigation.DestinationsNavigator


@Destination(
    navArgsDelegate = ColorTransferScreenNavArgs::class
)
@Composable
fun ColorTransferScreen(
    navigator: DestinationsNavigator,
    viewModel: ColorTransferViewModel = hiltViewModel()
) {
    val state : ColorTransferState by viewModel.state.collectAsState()

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
                ZoomableImage(
                    bitmap = state.calculatedImage!!.asImageBitmap(),
                    contentDescription = "Edited image"
                )
            }

        }
    }
}

