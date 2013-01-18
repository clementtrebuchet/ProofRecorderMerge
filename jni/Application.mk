APP_ABI := armeabi armeabi-v7a
APP_PROJECT_PATH := ../ProofRecorderMerge
APP_STL := stlport_static
APP_MODULES := mpg
APP_PLATFORM := android-8
# If APP_PIE isn't defined, set it to true for android-16 and above
<<<<<<< HEAD
#
=======
>>>>>>> master
APP_PIE := $(strip $(APP_PIE))
ifndef APP_PIE
    ifneq (,$(call gte,$(APP_PLATFORM_LEVEL),16))
        APP_PLATFORM := android-14
        APP_PIE := true
    else
        APP_PIE := false
    endif
endif
<<<<<<< HEAD
=======

>>>>>>> master
