#pragma once

#include <opencv2/core.hpp>
#include <opencv2/imgproc.hpp>
#include <chrono>
#include <android/log.h>

using namespace std::chrono;

using namespace cv;

void lightChange(Mat& src, float exposureVal, float contrastVal, float gamma);
void colorTransferIntensity(Mat& source, Mat& color, float intensity, float exposureCompensation);
void colorEdit(Mat& source, float saturation);
void colorTransfer(Mat& source, Mat color_image);
std::tuple<double, double, double, double, double, double> image_stats(Mat image);