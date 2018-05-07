
Linux通信机制：Socket、管道等

Binder是通过Linux的可动态加载模块机制，被链接到了内核作为内核的一部分。

Binder的优势：安全、高效。安全：对通信双方做了身份校验，而Socket通信ip可以进行伪造


Binder的原理：
上图

![链接图片](https://github.com/mirindalover/SummaryOfProgrammingLearning/blob/master/android/resource/Binder原理.png "Binder原理")

	Binder通信都是一个Server对应多个Client的形式，Client获取proxy来通过Binder调用Server的stub的方法
	ServiceManager是用来注册Binder的，也是一个Binder。我们获取的也只是proxy来向ServiceManager添加注册服务
	service端注册Binder到ServiceManager的查找表中，




















