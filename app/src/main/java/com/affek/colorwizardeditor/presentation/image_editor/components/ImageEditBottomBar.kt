package com.affek.colorwizardeditor.presentation.image_editor.components

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.fontResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.affek.colorwizardeditor.presentation.image_editor.subcomponents.CustomScrollableTabRow
import com.affek.colorwizardeditor.presentation.image_editor.subcomponents.CustomTab
import com.affek.colorwizardeditor.R

@Composable
fun ImageEditBottomBar(
    modifier : Modifier = Modifier,
    items: List<ImageEditBottomBarItems>,
    selectedIndex: Int,
    isSelected: Boolean,
    isColorTransfer: Boolean,
    visible: Boolean = true,
    onClick: (Int, Boolean) -> Unit,
    divider : @Composable() () -> Unit = {}
) {
    if (visible) {
        Box(
            modifier = modifier
                .background(MaterialTheme.colorScheme.primaryContainer)
        ) {
            CustomScrollableTabRow(
                selectedTabIndex = if (!isSelected) -1 else { if(isColorTransfer) selectedIndex else selectedIndex - 1 },
                minTabWidth = dimensionResource(id = R.dimen.bottom_bar_tab_min_height),
                edgePadding = dimensionResource(id = R.dimen.bottom_bar_edge_padding),
                divider = divider,
                activeIndicatorHeight = dimensionResource(id = R.dimen.bottom_bar_active_indicator_height),
                indicatorWidth = dimensionResource(id = R.dimen.bottom_bar_indicator_width),
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier
                    .navigationBarsPadding()
            ) {
                items.forEach() { item ->
                    CustomTab(
                        selected = item.index == selectedIndex && isSelected,
                        onClick = {
                            if (item.index == selectedIndex && isSelected)
                                onClick(item.index, false)
                            else
                                onClick(item.index, true)
                        },
                        icon = {
                            Icon(
                                painter = painterResource(item.iconResourceId),
                                contentDescription = stringResource(id = item.labelResourceId),
                                modifier = Modifier
                                    .size(dimensionResource(id = R.dimen.bottom_bar_icon_size))
                            )
                        },
                        text = {
                            Text(
                                text = stringResource(item.textResourceId),
                                style = MaterialTheme.typography.bodySmall
                            )
                        },
                        activeIndicatorHeight = dimensionResource(id = R.dimen.bottom_bar_active_indicator_height),
                        singleLineTextBaselineWithIcon = dimensionResource(id = R.dimen.bottom_bar_text_base_line_with_icon),
                        iconDistanceFromBaseline = 15.sp,
                        iconWithTextTabHeight = dimensionResource(id = R.dimen.bottom_bar_icon_with_text_tab_height),
                        horizontalTextPadding = dimensionResource(id = R.dimen.bottom_bar_horizontal_text_padding),
                        unselectedContentColor = MaterialTheme.colorScheme.secondary,
                        selectedContentColor = MaterialTheme.colorScheme.primary

                    )
                }


            }
        }
    }
}

enum class ImageEditBottomBarItems(@StringRes val labelResourceId: Int, @DrawableRes val iconResourceId: Int, @StringRes val textResourceId: Int, val index : Int) {

    ColorTransferEditor(R.string.color_transfer_item_label, R.drawable.baseline_format_color_fill_24, R.string.color_transfer_item_text, 0),
    BasicEditor(R.string.basic_editor_item_label, R.drawable.baseline_brightness_6_24, R.string.basic_editor_item_text,1),
    ColorEditor(R.string.basic_color_editor_item_label, R.drawable.baseline_color_lens_24, R.string.basic_color_editor_item_text,2),
    SharpeningEditor(R.string.details_editor_item_label, R.drawable.baseline_details_24, R.string.details_editor_item_text,3),
    ExportEditor(R.string.export_image_editor_item_label, R.drawable.baseline_save_alt_24, R.string.export_image_editor_item_text,4)
}


