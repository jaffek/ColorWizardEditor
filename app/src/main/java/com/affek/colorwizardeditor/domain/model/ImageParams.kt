package com.affek.colorwizardeditor.domain.model

import com.affek.colorwizardeditor.presentation.image_editor.components.ColorTransferPanelSliders
import com.affek.colorwizardeditor.presentation.image_editor.components.LightEditPanelSliders

data class ImageParams(
    private val exposure: Float,
    private val contrast: Float,
    private val gamma: Float,
    private val colorTransferIntensity: Float,
    var indexOfLastEditedSlider : Int,
    var indexOfLastEditedPanel: Int
) : Cloneable{
    //constructor(params: ImageParams) : this(params.exposure, params.contrast, params.gamma, params.indexOfLastEdited)

    var indexedParams = mutableMapOf<Int, Float>(
        LightEditPanelSliders.ExposureSlider.index to exposure,
        LightEditPanelSliders.ContrastSlider.index to contrast,
        LightEditPanelSliders.GammaSlider.index to gamma,
        ColorTransferPanelSliders.ColorTransferIntensity.index to colorTransferIntensity
    )
    public override fun clone() : ImageParams {
        val params = ImageParams(this.exposure, this.contrast, this.gamma, this.colorTransferIntensity, this.indexOfLastEditedSlider, this.indexOfLastEditedPanel)
        params.indexedParams = this.indexedParams.toMutableMap()
        return params
    }
}


