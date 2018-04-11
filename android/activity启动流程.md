


问题：
	点击应用图标开启应用的流程
	startActivity的流程
	Binder之间通信多多少次
	设置启动模式后的启动流程(如果使用fragment模仿类似的流程)
	
重点：
	启动过程中，原来的onPause()方法执行后，才开始创建新的activity。新activity的onResume()执行后，旧的才onStop()
		引出问题：onPause()方法不能做重量级操作，那样会延迟新的activity的展示。
		
	Instrumentation：管理activity的工具，创建和启动activity，activity的生命周期都由它控制。
	
	ActivityThread：用来描述一个应用程序进程，应用每启动一个应用进程，会加载一个ActivityThread实例，app的入口，在此进行looper.loop()循环
			每一个activity的父成员变量mMainThread都保存它
	
	ApplicationThread (extends ApplicationThreadNative)：Binder，来接受服务端的响应，控制本地的四大组件
	
	ActivityClientRecord：应用进程中保存每个启动的activity的 堆栈信息
	
	
	========================Binder=======================
			
	ActivityRecord：ActivityManagerService中用来描述组件堆栈信息，每个activity的mToken变量就是指向它的一个本地Binder变量
	
	ProcessRecord：描述每个应用进程
		
	ActivityManagerService: ActivityRecord 来保存每个activity的运行状态和信息
	
	ActivityStack：activity的堆栈信息，通过ActivityRecord来保存栈中的所有activity的状态


1、







































