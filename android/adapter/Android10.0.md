
### Android10.0(Q-29)  

https://developer.android.google.cn/preview/privacy/scoped-storage

####	范围化存储

>	外部存储受到沙盒隔离
	
	1. targetSDK为Q(29)
	
	2. targetSDK低于Q,清单文件设置
	
	```xml
		 <manifest ... >
		  <application android:allowLegacyExternalStorage="true" ... >
			...
		  </application>
		</manifest>
	```
	
	targetSDK其他情况会开启兼容模式,表示不使用沙盒隔离
	
>	访问媒体需要权限READ_MEDIA_IMAGES,READ_MEDIA_VIDEO,READ_MEDIA_AUDIO	

>	访问其他应用的沙盒需要使用 [存储访问框架](https://developer.android.google.cn/guide/topics/providers/document-provider)

>	应用卸载后,会删除沙盒的内容,如果要保存 需保存到MediaStore的某个目录
	
####	后台启动activity的限制

	此变化无论targetSDK是多少都会受到影响
	
	不允许应用不可见的时候开启Activity
	
	log中会显示
	
		This background activity start from package-name will be blocked in future Q builds	
	
	
####	位置权限

	新添加ACCESS_BACKGROUND_LOCATION权限
	
	targetSDK在Q以下，可自动添加对应的权限，申请原来的权限时同时申请该权限
	
####	设备标识符变更

	访问IMEI和序列号需要READ_PRIVILEGED_PHONE_STATE权限
	
	1. targetSDK为Q,没有申请改权限,发生SecurityException
	
	2. targetSDK低于Q,具有READ_PHONE_STATE权限,返回null.没有READ_PHONE_STATE权限发生SecurityException


