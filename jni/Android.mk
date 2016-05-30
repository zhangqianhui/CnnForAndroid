LOCAL_PATH := $(call my-dir)  
 
include $(CLEAR_VARS)    
OpenCV_INSTALL_MODULES:=on
OPENCV_CAMERA_MODULES:=off
OPENCV_LIB_TYPE:=SHARE
include D:\OpenCV-2.4.9-android-sdk\sdk\native\jni\OpenCV.mk
LOCAL_MODULE     := tinycnn 
LOCAL_SRC_FILES  := common.cpp , test.cpp 
LOCAL_LDLIBS += -lm -llog 
 include $(BUILD_SHARED_LIBRARY) 