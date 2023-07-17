#include "opencv-utils.h"
#include <opencv2/imgproc.hpp>
#include <opencv2/core.hpp>

void lightChange(Mat src, float exposureVal, float contrastVal, float gamma) {

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