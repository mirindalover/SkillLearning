
### Android6.0(M-23)

####	运行时权限

	[官网链接](https://developer.android.google.cn/training/permissions/requesting)
	
	```java
		//表示没有权限
		ContextCompat.checkSelfPermission(mActivity, permission) != PackageManager.PERMISSION_GRANTED
		//如果申请过权限,且拒绝了,返回true
		ActivityCompat.shouldShowRequestPermissionRationale();
		//请求权限
		ActivityCompat.requestPermissions()
	```
	
	示例
	
	```java
	
		private boolean isNeedCheck = true;
		
		private static final String[] PERMISSIONS=new String[]{};
	
		@Override
		protected void onResume() {
			super.onResume();
			if (Build.VERSION.SDK_INT >= 23) {
				if (isNeedCheck) {
					checkPermissions();
				}
			}
		}
		
		private void checkPermissions(){
			List<String> unGenPer = new ArrayList<>();
			for (String permission : PERMISSIONS) {
				if (ContextCompat.checkSelfPermission(mActivity, permission) != PackageManager.PERMISSION_GRANTED) {
					unGenPer.add(permission);
				}
			}		
			if (unGen.length != 0) {
                ActivityCompat.requestPermissions(mActivity, unGen, REQUEST_PERMISSION);
			}
		}
		
		public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
			super.onRequestPermissionsResult(requestCode, permissions, grantResults);
			List<String> unGenPermission = new ArrayList<>();
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {					
                    boolean showRequestPermission = ActivityCompat.shouldShowRequestPermissionRationale(mActivity, permissions[i]);
                    if (showRequestPermission) {
						//拒绝后 需要提示用户 再继续申请
                        showRequest = false;
                    }
                }
            }
					
			
			if (!showRequest) {
				showPermissionTip();
			}            
		}
	```
	
#####	shouldShowRequestPermissionRationale返回值

1，没有申请过权限，申请就是了，所以返回false； 
2，申请了用户拒绝了，那你就要提示用户了，所以返回true； 
3，用户选择了拒绝并且不再提示，那你也不要申请了，也不要提示用户了，所以返回false； 
4，已经允许了，不需要申请也不需要提示，所以返回false

##### 外部存储权限获取失败后的策略

1.大数据使用 context.getExternalFilesDir()存储在外部sd卡的路径

2. 

####	WindowManager的类型适配

	```java
	public static int getWindowParamType(){
		int type = 0;
		//25使用Toast类型会崩溃，8.0+系统使用Toast会自动消失
		if (Build.VERSION.SDK_INT >= 26) {//8.0新特性
			type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
		} else if (Build.VERSION.SDK_INT == 25) {
			type = WindowManager.LayoutParams.TYPE_PHONE;
		} else if (Build.VERSION.SDK_INT > 18) {
			type = WindowManager.LayoutParams.TYPE_TOAST;
		} else {
			type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
		}

		return type;
	}
	```
	
####	指纹


