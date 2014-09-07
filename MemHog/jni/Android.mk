LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := memhog
LOCAL_SRC_FILES := memhog.c

include $(BUILD_SHARED_LIBRARY)
