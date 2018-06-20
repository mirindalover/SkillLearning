
##	lifecycle-compent


*	AppcompatActivity通知生命周期的原理：

AppcompatActivity中通过在SpportActivity的onCreate中添加一个ReportFragment。

```Java

#ReportFragment

	@Override
    public void onStart() {
        super.onStart();
        dispatchStart(mProcessListener);
        dispatch(Lifecycle.Event.ON_START);
    }

```

即 都是通过fragment的生命周期来通知

```Java

#ReportFragment
	private void dispatch(Lifecycle.Event event) {
        Activity activity = getActivity();
        if (activity instanceof LifecycleRegistryOwner) {
            ((LifecycleRegistryOwner) activity).getLifecycle().handleLifecycleEvent(event);
            return;
        }

        if (activity instanceof LifecycleOwner) {
            Lifecycle lifecycle = ((LifecycleOwner) activity).getLifecycle();
            if (lifecycle instanceof LifecycleRegistry) {
                ((LifecycleRegistry) lifecycle).handleLifecycleEvent(event);
            }
        }
    }

```

通过获取activity中的lifecycleRegistry来进行通知


*	Fragment(v4)通知生命周期的原理

```Java

v4.Fragment

	void performCreate(Bundle savedInstanceState) {
        //...
        mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE);
    }
	
```
直接通过修改performXXX方法来进行通知

具体见下图

![fragment的调用][1]


[1]:https://github.com/mirindalover/SummaryOfProgrammingLearning/blob/master/architecture/resource/fragment_lifecycle.png



















