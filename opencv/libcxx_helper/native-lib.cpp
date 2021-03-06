#include <jni.h>
#include <string>
#include <vector>

#include <opencv2/opencv.hpp>
#include <jni.h>
#include <jni.h>

using namespace cv;
// empty

extern "C"
JNIEXPORT void JNICALL
Java_com_example_ae_android_ui_coloring_ColorOnPicFragment_detectEdgeJNI(JNIEnv *env, jobject thiz, jlong input_image,
    jlong output_image, jint th1, jint th2) {
// TODO: implement detectEdgeJNI()
cv::Mat &inputMat = *(cv::Mat *) input_image;
cv::Mat &outputMat = *(cv::Mat *) output_image;

cvtColor(inputMat, outputMat, cv::COLOR_RGB2GRAY);
Canny(outputMat, outputMat, th1, th2);
bitwise_not(outputMat, outputMat);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_ae_android_ui_coloring_ColorOnPicFragment_MeanShiftFilteringJNI(JNIEnv
* env,
jobject thiz, jlong
input_image,
jlong output_image, jdouble
sp,
jdouble sr
) {
// TODO: implement MeanShiftFilteringJNI()
cv::Mat &inputMat = *(cv::Mat *) input_image;
cv::Mat &outputMat = *(cv::Mat *) output_image;

cvtColor(inputMat, outputMat,cv::COLOR_BGR2Luv); // output = input 의 8비트 3채널 변환사진

cv::pyrMeanShiftFiltering(outputMat,outputMat,sp,sr);

cvtColor(outputMat, outputMat,cv::COLOR_Luv2BGR);
}
