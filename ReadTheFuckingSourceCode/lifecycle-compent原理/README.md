
##	lifecycle-compent


*	通知生命周期的原理：

AppcompatActivity中通过在SpportActivity的onCreate中添加一个ReportFragment。

即 都是通过fragment的生命周期来通知

通过在修改fragment来完成

```Java

v4.Fragment

	void performCreate(Bundle savedInstanceState) {
        //...
        mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE);
    }
	
```

具体见下图

![fragment的调用][1]


[1]:https://github.com/mirindalover/SummaryOfProgrammingLearning/blob/master/architecture/resource/fragment_lifecycle.png



















