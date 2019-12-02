
javah -d jni -classpath build/intermediates/classes/debug me.android.library.utils.base.serialport.SerialPort
javah -d jni -classpath . me.android.library.utils.base.serialport.SerialPort

ndk-build