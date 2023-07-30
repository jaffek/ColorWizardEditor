package com.affek.colorwizardeditor.domain.model

import com.affek.colorwizardeditor.presentation.image_editor.components.ColorEditPanelSliders
import com.affek.colorwizardeditor.presentation.image_editor.components.ColorTransferPanelSliders
import com.affek.colorwizardeditor.presentation.image_editor.components.LightEditPanelSliders

data class ImageParams(
    private val exposure: Float = LightEditPanelSliders.ExposureSlider.basePoint,
    private val contrast: Float = LightEditPanelSliders.ContrastSlider.basePoint,
    private val gamma: Float = LightEditPanelSliders.GammaSlider.basePoint,
    private val colorTransferIntensity: Float = ColorTransferPanelSliders.ColorTransferIntensity.basePoint,
    private val colorTransferExposureCompensation: Float = ColorTransferPanelSliders.ColorTransferExposureCompensation.basePoint,
    private val saturation: Float = ColorEditPanelSliders.SaturationSlider.basePoint,

    var lastEditexTabIndex: Int = -1
) : Cloneable{
    //constructor(params: ImageParams) : this(params.exposure, params.contrast, params.gamma, params.indexOfLastEdited)

    var indexedParams = mutableMapOf<Int, Float>(
        LightEditPanelSliders.ExposureSlider.index to exposure,
        LightEditPanelSliders.ContrastSlider.index to contrast,
        LightEditPanelSliders.GammaSlider.index to gamma,
        ColorTransferPanelSliders.ColorTransferIntensity.index to colorTransferIntensity,
        ColorTransferPanelSliders.ColorTransferExposureCompensation.index to colorTransferExposureCompensation,
        ColorEditPanelSliders.SaturationSlider.index to saturation
    )
    public override fun clone() : ImageParams {
        val params = ImageParams(
            this.exposure,
            this.contrast,
            this.gamma,
            this.colorTransferIntensity,
            this.colorTransferExposureCompensation,
            this.saturation,

            this.lastEditexTabIndex

        )
        params.indexedParams = this.indexedParams.toMutableMap()
        return params
    }
}


