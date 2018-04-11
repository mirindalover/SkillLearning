

对于Android调用JS代码的方法有2种： 
1. 通过WebView的loadUrl() 
2. 通过WebView的evaluateJavascript()  api4.4以上

注：JS代码调用一定要在 onPageFinished() 回调之后才能调用，否则不会调用。


对于JS调用Android代码的方法有3种： 
1. 通过WebView的addJavascriptInterface()进行对象映射 
2. 通过 WebViewClient 的shouldOverrideUrlLoading ()方法回调拦截 url 或者onLoadResource()进行拦截
3. 通过 WebChromeClient 的onJsAlert()、onJsConfirm()、onJsPrompt()方法回调拦截JS对话框alert()、confirm()、prompt() 消息


公司webview调用android的原理：

使用nanoHTTPD作为手机服务端

	1、保存变量到sp中
		点击保存设置按钮，发送ajax请求到手机服务端，解析请求根据地址进行处理
	2、跳转页面：
		点击按钮后，通过js中的 window.onJsOverrideUrlLoading = function(str){}
		android中对应 addJavascriptInterface()中统一用onJsOverrideUrlLoading()进行处理
		















