#include "opencv-utils.h"

void lightChange(Mat& src, float exposureVal, float contrastVal, float gamma) {
    if(exposureVal != 0 || contrastVal != 0) {
        if (exposureVal != 0) {
            src.convertTo(src, -1, 1, exposureVal);
        }
        if (contrastVal != 0) {
            double f = 131 * (contrastVal + 127) / (127 * (131 - contrastVal));
            double alpha_c = f;
            double gamma_c = 127 * (1 - f);
            addWeighted(src, alpha_c, src, 0, gamma_c, src);
        }
    }
    if(gamma != 0){
        Mat lookUpTable(1, 256, CV_8U);
        uchar* p = lookUpTable.ptr();
        for( int i = 0; i < 256; ++i)
            p[i] = saturate_cast<uchar>(pow(i / 255.0, gamma) * 255.0);
        LUT(src, lookUpTable, src);
    }
}

void colorTransferIntensity(Mat& source, Mat& color, float intensity) {
    source.convertTo(source, CV_16S);
    color.convertTo(color, CV_16S);
    Mat diff;
    subtract(color, source, diff);
    addWeighted(source, 1, diff, intensity, 0, source);
    source.convertTo(source, CV_8UC4);

}


void colorTransfer(Mat& source, Mat color_image) {
        cvtColor(source, source, COLOR_RGB2Lab);
        cvtColor(color_image, color_image, COLOR_RGB2Lab);
        source.convertTo(source,CV_32F);
        color_image.convertTo(color_image,CV_32F);

        auto [l_mean_src, l_std_src, a_mean_src, a_std_src, b_mean_src, b_std_src] = image_stats(source);
        auto [l_mean_color, l_std_color, a_mean_color, a_std_color, b_mean_color, b_std_color] = image_stats(color_image);

        Mat channels_src[3];
        split(source, channels_src);

        channels_src[0] -= l_mean_src;
        channels_src[1] -= a_mean_src;
        channels_src[2] -= b_mean_src;

        channels_src[0] = (l_std_color / l_std_src) * channels_src[0];
        channels_src[1] = (a_std_color / a_std_src) * channels_src[1];
        channels_src[2] = (b_std_color / b_std_src) * channels_src[2];

        channels_src[0] += l_mean_color;
        channels_src[1] += a_mean_color;
        channels_src[2] += b_mean_color;

        merge(channels_src, 3, source);
        source.setTo(Scalar(0, 0, 0), source < 0);
        source.setTo(Scalar(255, 255, 255), source > 255);
        source.convertTo(source, CV_8UC4);
        cvtColor(source, source, COLOR_Lab2RGB);

}

std::tuple<double, double, double, double, double, double> image_stats(Mat image) {
    Mat channels[3];
    split(image, channels);

    Scalar mean_l, std_l, mean_a, std_a, mean_b, std_b;
    meanStdDev(channels[0], mean_l, std_l);
    meanStdDev(channels[1], mean_a, std_a);
    meanStdDev(channels[2], mean_b, std_b);

    return std::make_tuple(mean_l[0], std_l[0], mean_a[0], std_a[0], mean_b[0], std_b[0]);
}

