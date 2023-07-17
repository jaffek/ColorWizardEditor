package com.affek.colorwizardeditor.presentation.image_editor.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.affek.colorwizardeditor.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageEditTopBar(
    backClick : () -> Unit,
    undoClick : () ->Unit,
    clickTopMenu : () -> Unit,
    isDropDownMenuExpanded : Boolean,
    visible : Boolean = true,
    isUndoButtonEnabled: Boolean = false,
    title : @Composable () -> Unit = {}
) {
    if(visible) {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
        ) {
            TopAppBar(
                navigationIcon = {
                    IconButton(
                        onClick = { backClick() }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_arrow_back_24),
                            contentDescription = stringResource(id = R.string.back),
                        )
                    }
                },
                title = title,
                actions = {
                    IconButton(
                        onClick = { undoClick() },
                        enabled = isUndoButtonEnabled
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_undo_24),
                            contentDescription = stringResource(id = R.string.undo),
                        )
                    }
                    IconButton(
                        onClick = { clickTopMenu() }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_more_vert_24),
                            contentDescription = stringResource(id = R.string.additional_options),
                        )
                    }
                    DropdownMenu(
                        modifier = Modifier
                            .background(color = MaterialTheme.colorScheme.surface)
                            .border(
                                width = dimensionResource(id = R.dimen.dropdown_menu_order_width),
                                color = MaterialTheme.colorScheme.secondary,
                                shape = RoundedCornerShape(dimensionResource(id = R.dimen.dropdown_menu_corner_radius))
                            ),
                        expanded = isDropDownMenuExpanded,
                        offset = DpOffset(dimensionResource(id = R.dimen.dropdown_menu_horizontal_offset), dimensionResource(
                            id = R.dimen.dropdown_menu_vertical_offset
                        )),
                        onDismissRequest = { clickTopMenu() }
                    ) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = "TEST"
                                )
                            },
                            onClick = { /*TODO*/ }
                        )
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = "TEST"
                                )
                            },
                            onClick = { /*TODO*/ }
                        )
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = "TEST"
                                )
                            },
                            onClick = { /*TODO*/ }
                        )
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = "TEST"
                                )
                            },
                            onClick = { /*TODO*/ }
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    scrolledContainerColor = MaterialTheme.colorScheme.surface,
                    navigationIconContentColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                    actionIconContentColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier
                    .statusBarsPadding()
                )
        }
    }
}