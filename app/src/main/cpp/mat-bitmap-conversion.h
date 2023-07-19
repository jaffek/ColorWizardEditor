#pragma once

#include <opencv2/core.hpp>
#include <opencv2/imgproc.hpp>
#include "android/bitmap.h"
#include <jni.h>
#include <string>

using namespace cv;

void bitmapToMat(JNIEnv *env, jobject bitmap, Mat& dst, jboolean needUnPremultiplyAlpha);
void matToBitmap(JNIEnv* env, Mat src, jobject bitmap, jboolean needPremultiplyAlpha);