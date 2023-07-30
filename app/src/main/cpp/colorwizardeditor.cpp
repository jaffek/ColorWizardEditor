#include "opencv-utils.h"
#include "mat-bitmap-conversion.h"


extern "C" JNIEXPORT jstring JNICALL
Java_com_affek_colorwizardeditor_MainActivity_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}


extern "C"
JNIEXPORT void JNICALL
Java_com_affek_colorwizardeditor_data_image_1processing_LightEditingKt_exposureContrast(JNIEnv *env,
                                jclass clazz,
                                jobject source_bitmap,
                                jobject final_bitmap,
                                jfloat exposure_val,
                                jfloat contrast_val,
                                jfloat gamma) {

    Mat src;
    bitmapToMat(env, source_bitmap, src, false);
    lightChange(src, exposure_val, contrast_val, gamma);
    matToBitmap(env, src, final_bitmap, false);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_affek_colorwizardeditor_data_image_1processing_ColorTransferKt_colorTransferC(JNIEnv *env,
                                                                                       jclass clazz,
                                                                                       jobject source_bitmap,
                                                                                       jobject color_bitmap,
                                                                                       jobject final_bitmap) {
    Mat src, color;
    bitmapToMat(env, source_bitmap, src, false);
    bitmapToMat(env, color_bitmap, color, false);
    colorTransfer(src, color);
    matToBitmap(env, src, final_bitmap, false);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_affek_colorwizardeditor_data_image_1processing_ColorTransferKt_colorTransferIntensity(
        JNIEnv *env,
        jclass clazz,
        jobject source_bitmap,
        jobject color_bitmap,
        jobject final_bitmap,
        jfloat color_intensity,
        jfloat exposure_compensation) {



    Mat src, color;
    bitmapToMat(env, source_bitmap, src, false);
    bitmapToMat(env, color_bitmap, color, false);
    colorTransferIntensity(src, color, color_intensity, exposure_compensation);
    matToBitmap(env, src, final_bitmap, false);
}
extern "C"
JNIEXPORT void JNICALL
Java_com_affek_colorwizardeditor_data_image_1processing_ColorEditKt_colorEdit(JNIEnv *env,
                                                                              jclass clazz,
                                                                              jobject source_bitmap,
                                                                              jobject final_bitmap,
                                                                              jfloat saturation) {
    Mat src;
    bitmapToMat(env, source_bitmap, src, false);
    colorEdit(src, saturation);
    matToBitmap(env, src, final_bitmap, false);
}