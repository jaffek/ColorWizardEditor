package com.affek.colorwizardeditor.presentation.image_editor.components

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.affek.colorwizardeditor.R
import com.affek.colorwizardeditor.domain.model.ImageParams
import com.affek.colorwizardeditor.presentation.image_editor.subcomponents.CustomSlider
import com.affek.colorwizardeditor.presentation.util.modifiers.conditional_modifier.conditional
import java.math.RoundingMode
import java.text.DecimalFormat
import kotlin.math.roundToInt

@Composable
fun ColorTransferPanel(
    params: ImageParams,
    onChange : (value: Float, index: Int) -> Unit
) {


    Column(
        modifier = Modifier
            .alpha(0.9f)
            .background(MaterialTheme.colorScheme.primaryContainer)
            .clickable(enabled = false, onClick = {})
            .verticalScroll(state = rememberScrollState())
    ) {

        enumValues<ColorTransferPanelSliders>().forEachIndexed() {loopIndex, item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = dimensionResource(id = R.dimen.bottom_bar_slider_description_left_padding),
                        end = dimensionResource(id = R.dimen.bottom_bar_slider_description_right_padding),
                        top = dimensionResource(id = R.dimen.bottom_bar_slider_description_top_padding),
                        bottom = dimensionResource(id = R.dimen.bottom_bar_slider_description_bottom_padding)
                    )
            ) {
                Text(
                    text = stringResource(item.labelResourceId),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Start,
                )
                Spacer(Modifier.weight(1f))
                Text(
                    text = if(item.index == LightEditPanelSliders.GammaSlider.index) {
                        val df = DecimalFormat("0.00")
                        df.roundingMode = RoundingMode.HALF_UP
                        val roundoff = df.format(params.indexedParams[item.index])
                        roundoff
                    }
                    else
                        params.indexedParams[item.index]!!.roundToInt().toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Start,
                    modifier = Modifier
                )
            }

            CustomSlider(
                value = params.indexedParams[item.index]!!,
                onValueChange = {
                    onChange(it, item.index)
                },
                valueRange = item.bottomBound.rangeTo(item.upperBound),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = dimensionResource(id = R.dimen.bottom_bar_slider_horizontal_padding))
                    .conditional(loopIndex == enumValues<ColorTransferPanelSliders>().size - 1) {
                        padding(bottom = 10.dp)
                    },
                basePoint = item.basePoint
            )
        }

    }

}



enum class ColorTransferPanelSliders(@StringRes val labelResourceId: Int, val bottomBound: Float, val upperBound: Float, val basePoint: Float, val index: Int) {

    ColorTransferIntensity(R.string.intensity, 0f, 2f, 1f,0),

}