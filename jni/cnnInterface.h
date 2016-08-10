#include <iostream>
#include <memory>
#define CNN_USE_CAFFE_CONVERTER

#include "tiny_cnn/tiny_cnn.h"

using namespace tiny_cnn;
using namespace tiny_cnn::activation;
using namespace std;


#include <opencv2/imgcodecs.hpp>
#include <opencv2/core/core.hpp>
#include <opencv2/highgui/highgui.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <jni.h>
#include "caffe.pb.h"
#include "common.h"


typedef void *PredictorHandle;

#ifndef _Included_jni_cnn_H
#define _Included_jni_cnn_H

#ifdef __cplusplus

extern "C" {
#endif
/*
 * Class:     com_example_jniandroidtest1_imageProc
 * Method:    grayproc
 * Signature: ([III)[I
 */
//JNIEXPORT jint JNICALL Java_jni_testCnn_jniPredict2(JNIEnv *env , jclass obj , jlong addr , jstring cmdIn , jstring proto);
JNIEXPORT jlong JNICALL Java_jni_Predictor_createPredictor(JNIEnv *env , jclass obj , jstring symbol);

JNIEXPORT jlong JNICALL Java_jni_Predictor_createPredictorforCaffe(JNIEnv *env , jclass obj , jstring symbol , jstring params);

JNIEXPORT jfloat* JNICALL Java_jni_Predoctor_getFeacture(JNIEnv *env , jclass obj , jlong handle , jlong addr);

JNIEXPORT jint JNICALL Java_jni_Predictor_getPredict(JNIEnv *env , jclass obj , jlong handle ,  jlong addr , jint type , jstring caffeMean);


#ifdef __cplusplus
}

#endif
#endif
