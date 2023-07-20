package com.affek.colorwizardeditor.presentation.image_editor

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.ramcosta.composedestinations.annotation.Destination
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalView
import androidx.hilt.navigation.compose.hiltViewModel
import com.affek.colorwizardeditor.navigation.ColorTransferScreenNavArgs
import com.affek.colorwizardeditor.presentation.image_editor.components.ColorTransferPanel
import com.affek.colorwizardeditor.presentation.image_editor.components.ImageEditBottomBar
import com.affek.colorwizardeditor.presentation.image_editor.components.ImageEditTopBar
import com.affek.colorwizardeditor.presentation.image_editor.components.LightEditPanel
import com.affek.colorwizardeditor.presentation.util.modifiers.conditional_modifier.conditional
import com.affek.colorwizardeditor.presentation.util.modifiers.conditional_modifier.longPressAction
import com.affek.colorwizardeditor.presentation.util.modifiers.zoomable_modifier.rememberZoomState
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.affek.colorwizardeditor.presentation.util.modifiers.zoomable_modifier.zoomable


@Destination(
    navArgsDelegate = ColorTransferScreenNavArgs::class
)
@Composable
fun ImageEditorScreen(
    navigator: DestinationsNavigator,
    viewModel: ImageEditorViewModel = hiltViewModel()
) {
    val state: ImageEditorState by viewModel.state.collectAsState()
    val zoomState = rememberZoomState()
    val bottomAppBarItems = viewModel.bottomAppItems
    val view = LocalView.current

    BackHandler() {
        navigator.navigateUp()
        if(state.isFullScreen)
            viewModel.onEvent(ImageEditorEvent.FullScreen(view))
    }
    Scaffold(
        topBar = {
            ImageEditTopBar(
                modifier = Modifier
                    .conditional(state.isShowingBeforeEdit){ alpha(0f) },
                backClick = {
                    navigator.navigateUp()
                },
                undoClick = { viewModel.onEvent(ImageEditorEvent.UndoEdit) },
                redoClick = { viewModel.onEvent(ImageEditorEvent.RedoEdit) },
                resetClick = { viewModel.onEvent(ImageEditorEvent.ResetEdit) },
                clickTopMenu = { viewModel.onEvent(ImageEditorEvent.ExpandDropDownMenu) },
                isDropDownMenuExpanded = state.isDropDownMenuExpanded,
                visible = !state.isFullScreen,
                isUndoButtonEnabled = state.isUndoButtonEnabled,
                isResetButtonEnabled = state.isResetButtonEnabled,
                isRedoButtonEnabled = state.isRedoButtonEnabled
            )
        },
        bottomBar = {
            ImageEditBottomBar(
                modifier = Modifier
                    .conditional(state.isShowingBeforeEdit){ alpha(0f) },
                items = bottomAppBarItems,
                selectedIndex = state.bottomBarSelectedItem,
                isSelected = state.isBottomBarItemSelected,
                visible = !state.isFullScreen,
                onClick = { index, isSelected ->
                    viewModel.onEvent(ImageEditorEvent.SelectTab(index, isSelected))
                }
            )
        },
        modifier = Modifier
            .fillMaxSize()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .conditional(!state.isFullScreen) {padding(paddingValues)}
                .background(MaterialTheme.colorScheme.surface)
        ) {
            Box(
                modifier = Modifier
                    .conditional(!state.isBottomBarItemSelected) { fillMaxSize() }
                    .fillMaxWidth()
                    .conditional(state.isBottomBarItemSelected) { weight(1f) }
            ) {
                androidx.compose.animation.AnimatedVisibility(
                    visible = state.isLoading,
                    modifier = Modifier
                        .align(Alignment.Center)
                ) {
                    CircularProgressIndicator()
                }
                androidx.compose.animation.AnimatedVisibility(
                    visible = !state.isLoading,
                    modifier = Modifier
                        .align(Alignment.Center)
                ) {
                    zoomState.setContentSize(
                        Size(
                            state.calculatedImage!!.width.toFloat(),
                            state.calculatedImage!!.height.toFloat()
                        )
                    )
                    Image(
                        bitmap = state.calculatedImage!!.asImageBitmap(),
                        contentDescription = "Edited image",
                        modifier = Modifier
                            .fillMaxSize()
                            .zoomable(
                                zoomState,
                                onTap = {
                                    viewModel.onEvent(ImageEditorEvent.FullScreen(view))
                                }
                            )
                            .longPressAction(onPressAction = {
                                viewModel.onEvent(ImageEditorEvent.ImagePress(it))
                            })
                    )
                }
            }
            if(!state.isFullScreen) {
                AnimatedVisibility(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally)
                        .conditional(state.isShowingBeforeEdit){ alpha(0f) },
                    visible = state.isBottomBarItemSelected,
                    enter = slideInVertically(initialOffsetY = { height -> height / 2 }) + fadeIn(),
                    exit = slideOutVertically(targetOffsetY = { height -> height / 2 }) + fadeOut()

                ) {
                    when {
                        (state.isColorTransfer && state.bottomBarSelectedItem == 0) ->
                            ColorTransferPanel(
                                onChange = { value, index ->
                                    viewModel.onEvent(ImageEditorEvent.ColorTransferPanelSliders(value, index))
                                },
                                params = state.imageParams[state.currentIndexOfEditChanges]
                            )
                        (state.isColorTransfer && state.bottomBarSelectedItem == 1) || (!state.isColorTransfer && state.bottomBarSelectedItem == 0) ->
                            LightEditPanel(
                                onChange = { value, index ->
                                    viewModel.onEvent(ImageEditorEvent.BasicPanelSliders(value, index))
                                },
                                params = state.imageParams[state.currentIndexOfEditChanges]
                            )
                    }
                }
            }
        }
    }
}

