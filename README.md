# android-serialport-api

* This is a serial port demo on Android, which includes the JNI source code and documents. 
  It can be compilered and also can be used directly.

> Repair the bug for google [android-serialport-api](https://code.google.com/archive/p/android-serialport-api) lost byte

* The real bug is caused by the mInputStream.read() blocking when the Activity be closed, 
  this also made the GC can not be released, that the unreleased Activity will get the 
  next data which is belong to the new Activity.

* The complete code for ReadThread in SerialPortActivity.java:

```java
    private class ReadThread extends Thread {
        @Override
        public void run() {
            super.run();
            while(!isInterrupted()) {
                int size;
                try {
                    byte[] buffer = new byte[64];
                    if (mInputStream == null) return;
                    if (mInputStream.available() > 0) {
                        size = mInputStream.read(buffer);
                        if (size > 0) {
                            onDataReceived(buffer, size);
                        }
                    }
                    SystemClock.sleep(100);
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }
```
