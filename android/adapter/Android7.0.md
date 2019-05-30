
### Android7.0(N-24)


####	CONNECTIVITY_ACTION广播不能静态注册

####	禁止file://类型的URI离开应用(如:用intent传递出去.如拍照,安装apk)

	[博客链接](https://blog.csdn.net/lmj623565791/article/details/72859156)	
	
	```java
		if (Build.VERSION.SDK_INT >= 24) {
			fileUri = FileProvider.getUriForFile(this, "com.zhy.android7.fileprovider", file);
		} else {
			fileUri = Uri.fromFile(file);
		}	
	```
	
	清单文件注册FileProvider
	
	权限问题:
		1. Intent.addFlags，针对intent.setData，setDataAndType以及setClipData相关方式传递uri的
		2. context.grantUriPermission。给符合的package权限
		
####	WebView

	1. 部分机型 https对证书有验证
	
		```java
			@Override
			public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
				if (error.getPrimaryError() == SslError.SSL_DATE_INVALID
						|| error.getPrimaryError() == SslError.SSL_EXPIRED
						|| error.getPrimaryError() == SslError.SSL_INVALID
						|| error.getPrimaryError() == SslError.SSL_UNTRUSTED) {
					handler.proceed();
				} else {
					handler.cancel();
				}

				super.onReceivedSslError(view, handler, error);
			}					
		```
	2. 部分webview二级页面加载不出来
	
		```java
			@Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    view.loadUrl(request.getUrl().toString());
                } else {
                    view.loadUrl(request.toString());
                }
                return true;
            }		
		```
		
		
		
		
		
		
		
		
		
		