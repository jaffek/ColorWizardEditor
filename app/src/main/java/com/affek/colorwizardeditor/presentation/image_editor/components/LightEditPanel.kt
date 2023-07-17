package com.affek.colorwizardeditor.presentation.image_editor.components

import androidx.annotation.DrawableRes
import androidx.annotation.IntegerRes
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderColors
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.affek.colorwizardeditor.R
import com.affek.colorwizardeditor.domain.model.ImageParams
import com.affek.colorwizardeditor.presentation.image_editor.subcomponents.CustomSlider
import com.affek.colorwizardeditor.presentation.util.modifiers.conditional
import java.math.RoundingMode
import java.text.DecimalFormat
import kotlin.math.roundToInt

@Composable
fun LightEditPanel(
    params: ImageParams,
    onChange : (value: Float, index: Int) -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.3f),
        contentAlignment = Alignment.BottomStart
    )
    {
        Column(
            modifier = Modifier
                .alpha(0.9f)
                .background(MaterialTheme.colorScheme.background)
                .clickable(enabled = false, onClick = {})
                .wrapContentSize(Alignment.BottomStart)
                .verticalScroll(state = rememberScrollState())
        ) {

            enumValues<LightEditPanelSliders>().forEachIndexed() {loopIndex, item ->
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
                            val df = DecimalFormat("#.00")
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
                        .conditional(loopIndex == enumValues<LightEditPanelSliders>().size - 1) {
                            padding(bottom = 10.dp)
                        },
                    basePoint = item.basePoint
                )
            }
            
        }
    }
}



enum class LightEditPanelSliders(@StringRes val labelResourceId: Int, val bottomBound: Float, val upperBound: Float, val basePoint: Float, val index: Int) {

    ExposureSlider(R.string.exposure, -150f, 150f, 0f,0),
    ContrastSlider(R.string.contrast, -60f, 60f,0f, 1),
    GammaSlider(R.string.gamma, 0.01f, 3f,1f, 2),

}