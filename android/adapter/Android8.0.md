
### Android8.0(0-26)  


####	activity设置方向的崩溃问题

	```java
	//Activity的源码
	if (getApplicationInfo().targetSdkVersion >= O_MR1 && mActivityInfo.isFixedOrientation()) {
		final TypedArray ta = obtainStyledAttributes(com.android.internal.R.styleable.Window);
		final boolean isTranslucentOrFloating = ActivityInfo.isTranslucentOrFloating(ta);
		ta.recycle();

		if (isTranslucentOrFloating) {
			throw new IllegalStateException(
					"Only fullscreen opaque activities can request orientation");
		}
	}	
	```
	
	API26的手机会出现问题,27和27以后的手机已经修复
	
	需要把透明的主题去掉
	
	
####	后台服务

	startService时回崩溃
	使用startForegroundService()5s不调用startForeground()也会崩溃
	直接使用JobScheduler来进程后台服务
	
####	