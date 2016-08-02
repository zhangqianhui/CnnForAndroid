LOCAL_PATH := $(call my-dir)  
 
include $(CLEAR_VARS)    
OpenCV_INSTALL_MODULES:=on
OPENCV_CAMERA_MODULES:=off
OPENCV_LIB_TYPE:=SHARE
LOCAL_CPP_EXTENSION := .cc
include D:\OpenCV-3.0.0-android-sdk-1\OpenCV-android-sdk\sdk\native\jni\OpenCV.mk

LOCAL_MODULE  := caffe_p
LOCAL_SRC_FILES  := cnnInterface.cpp , caffe.pb.cc , common.cpp
LOCAL_LDLIBS += -lm -llog 

#指定头文件路径
#LOCAL_C_INCLUDES = $(LOCAL_PATH)/src
#指定静态库
LOCAL_LDFLAGS += $(LOCAL_PATH)/libprotobuf.a

include $(BUILD_SHARED_LIBRARY)

 