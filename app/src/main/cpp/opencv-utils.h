#pragma once

#include <opencv2/core.hpp>
#include <opencv2/imgproc.hpp>

using namespace cv;

void lightChange(Mat& src, float exposureVal, float contrastVal, float gamma);
void colorTransfer(Mat& source, Mat color_image);
std::tuple<double, double, double, double, double, double> image_stats(Mat image);