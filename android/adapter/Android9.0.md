
### Android9.0(P-28)  

https://developer.android.google.cn/about/versions/pie/android-9.0-changes-28

####	WebView

>	应用只能在一个进程使用android.webkit中的类

	```java
		//指定目录后缀
		 WebView.setDataDirectorySuffix()	
	```
	
####	删除了Apache HTTP API(Android6.0移除了支持)

	使用HttpURLConnection类
	
####	默认开启TLS,即http请求会失败

		
1.		创建xml文件,写明不开启TLS
		```xml
			<?xml version="1.0" encoding="utf-8"?>
			<network-security-config>
				<domain-config cleartextTrafficPermitted="true"/>
			</network-security-config>    
		```
2.		在application中声明		
		```xml
			<application android:networkSecurityConfig="@xml/network_security_config"
                        ... >
            ...
			</application>
		```
	
####	挖孔屏

>	对于有状态栏的页面，不会受到挖孔屏特性的影响；
>
>	全屏显示的页面，系统挖孔屏方案会对应用界面做下移避开挖孔区显示；
>
>	已经适配的P的应用的全屏页面可以通过谷歌提供的


