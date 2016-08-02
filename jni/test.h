#include <iostream>
#include <opencv2/opencv.hpp>
/*#include <opencv2/imgproc.hpp>
#include <opencv2/imgcodecs.hpp>
#include <opencv2/highgui.hpp>*/
#include "./tiny_cnn/tiny_cnn.h"
#include "common.h"
#include <jni.h>

using namespace tiny_cnn;
using namespace tiny_cnn::activation;
using namespace std;
using namespace cv;


#ifndef _Included_jni_TEST_H
#define _Included_jni_TEST_H
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_example_jniandroidtest1_imageProc
 * Method:    grayproc
 * Signature: ([III)[I
 */
JNIEXPORT jint JNICALL Java_jni_testCnn_jniPredict(JNIEnv *env , jclass obj , jlong addr , jstring cmdIn);

#ifdef __cplusplus
}
#endif
#endif
