
#	activity的启动过程

activity的启动，主要是客户端(可能是多个)和服务端通讯的过程，
	
## 重点：

*	客户端：

	Instrumentation：管理activity的工具，创建和启动activity，activity的生命周期都由它控制。
	
	ActivityThread：用来描述一个应用程序进程，应用每启动一个应用进程，会加载一个ActivityThread实例，app的入口，在此进行looper.loop()循环
			每一个activity的父成员变量mMainThread都保存它
	
	ApplicationThread (extends ApplicationThreadNative)：Binder，来接受服务端的响应，控制本地的四大组件
	
	ActivityClientRecord：应用进程中保存每个启动的activity的 堆栈信息
	
	
*	服务端
			
	ActivityRecord：ActivityManagerService中用来描述组件堆栈信息，每个activity的mToken变量就是指向它的一个本地Binder变量
	
	ProcessRecord：描述每个应用进程
		
	ActivityManagerService: ActivityRecord 来保存每个activity的运行状态和信息
	
	ActivityStack：activity的堆栈信息，通过ActivityRecord来保存栈中的所有activity的状态

*	注意点：
	
	启动过程中，原来的onPause()方法执行后，才开始创建新的activity。新activity的onResume()执行后，旧的才onStop()
		引出问题：onPause()方法不能做重量级操作，那样会延迟新的activity的展示。并且有一个超时消息会报错。
		
直接上图：

![链接图片](https://github.com/mirindalover/SummaryOfProgrammingLearning/blob/master/android/resource/activity的启动过程.png "activity的启动过程") 

省略的步骤3，为Activity的其他方法和生命周期

下面跟着源码来进行分析：


启动activity可以从startActivity调起，也可以从桌面图标调起

##	第一遍通信：从旧Activity startActivity() 到 onPause掉旧Activity

点击桌面图标,从Launcher最终调到Activity.startActivity()

```Java
	Launcher.java
	public void onClick(View v) {
        ...
        if (tag instanceof ShortcutInfo) {
            final Intent intent = ((ShortcutInfo) tag).intent;
            boolean success = startActivitySafely(v, intent, tag);
			...
        } 
		...
    }	
```
	
	intent:是我们在安装应用的时候，PackageManagerService会对AndroidManifest.xml进行解析
	把图标和具有<category android:name="android.intent.category.LAUNCHER" />的联系起来

```Java	
	boolean startActivitySafely(View v, Intent intent, Object tag) {
        boolean success = false;
        try {
            success = startActivity(v, intent, tag);
        } catch (ActivityNotFoundException e) {
        }
        return success;
    }	
	
	boolean startActivity(View v, Intent intent, Object tag) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            ...
           startActivity(intent, opts.toBundle());
        } catch (SecurityException e) {
            ...
        }
        return false;
    }
```

	intent添加了NEW_TASK开启新的任务
	
```Java
Activity.java

	public void startActivity(Intent intent, @Nullable Bundle options) {
        if (options != null) {
            startActivityForResult(intent, -1, options);
        } else {
            startActivityForResult(intent, -1);
        }
    }	

	public void startActivityForResult(Intent intent, int requestCode, @Nullable Bundle options) {
        if (mParent == null) {
            Instrumentation.ActivityResult ar =
                mInstrumentation.execStartActivity(
                    this, mMainThread.getApplicationThread(), mToken, this,
                    intent, requestCode, options);      
			...
        } else {
			...
        }
    }
```
	
	Instrumentation是用来监控应用程序和系统之间的交互。
	mMainThread.getApplicationThread()，Binder用来和 ActivityManagerService 通信的
	mToken指向了ActivityManagerService的一个ActivityRecord对象，是ActivityManagerService用来维护每一个activity的运行状态和信息的

```Java	
Instrumentation.java
	
	public ActivityResult execStartActivity(
            Context who, IBinder contextThread, IBinder token, Activity target,
            Intent intent, int requestCode, Bundle options) {
        ...        
        try {
            
            int result = ActivityManagerNative.getDefault()
                .startActivity(whoThread, who.getBasePackageName(), intent,
                        intent.resolveTypeIfNeeded(who.getContentResolver()),
                        token, target != null ? target.mEmbeddedID : null,
                        requestCode, 0, null, options);
            checkStartActivityResult(result, intent);
        } catch (RemoteException e) {
            throw new RuntimeException("Failure from system", e);
        }
        return null;
    }
	
ActivityManagerNative.java
	
	 private static final Singleton<IActivityManager> gDefault = new Singleton<IActivityManager>() {
        protected IActivityManager create() {
            IBinder b = ServiceManager.getService("activity");            
            IActivityManager am = asInterface(b);           
            return am;
        }
    };
	static public IActivityManager asInterface(IBinder obj) {
        if (obj == null) {
            return null;
        }
        IActivityManager in =
            (IActivityManager)obj.queryLocalInterface(descriptor);
        if (in != null) {
            return in;
        }
        return new ActivityManagerProxy(obj);
    }
```
	
	获取ActivityManagerService的一个代理对象,封装成ActivityManagerProxy，调用startActivity();

```Java
ActivityManagerProxy.java

	public int startActivity(IApplicationThread caller, String callingPackage, Intent intent,
            String resolvedType, IBinder resultTo, String resultWho, int requestCode,
            int startFlags, ProfilerInfo profilerInfo, Bundle options) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        ...
        mRemote.transact(START_ACTIVITY_TRANSACTION, data, reply, 0);
        reply.readException();
        int result = reply.readInt();
        reply.recycle();
        data.recycle();
        return result;
    }

ActivityManagerService extends ActivityManagerNative
	
	public boolean onTransact(int code, Parcel data, Parcel reply, int flags)
            throws RemoteException {
        switch (code) {
        case START_ACTIVITY_TRANSACTION:
        {
            ...
            int result = startActivity(app, callingPackage, intent, resolvedType,
                    resultTo, resultWho, requestCode, startFlags, profilerInfo, options);
            reply.writeNoException();
            reply.writeInt(result);
            return true;
        }
```

	把参数写入Parecel 中，向ActivityManagerService发送一个START_ACTIVITY_TRANSACTION进程间通信

```Java
ActivityManagerService extends ActivityManagerNative.java

	public boolean onTransact(int code, Parcel data, Parcel reply, int flags)
            throws RemoteException {
        switch (code) {
        case START_ACTIVITY_TRANSACTION:
        {
            ...读取数据
            int result = startActivity(app, callingPackage, intent, resolvedType,
                    resultTo, resultWho, requestCode, startFlags, profilerInfo, options);
            ...
            return true;
        }

	public final int startActivity(IApplicationThread caller, String callingPackage,
            Intent intent, String resolvedType, IBinder resultTo, String resultWho, int requestCode,
            int startFlags, ProfilerInfo profilerInfo, Bundle options) {
        return startActivityAsUser(caller, callingPackage, intent, resolvedType, resultTo,
            resultWho, requestCode, startFlags, profilerInfo, options,
            UserHandle.getCallingUserId());
    }

	public final int startActivityAsUser(IApplicationThread caller, String callingPackage,
            ...
        return mStackSupervisor.startActivityMayWait(caller, -1, callingPackage, intent,
                resolvedType, null, null, resultTo, resultWho, requestCode, startFlags,
                profilerInfo, null, null, options, false, userId, null, null);
    }
```
	
使用了ActivityStackSupervisor，由于多屏功能的出现，就需要ActivityStackSupervisor这么一个类来管理ActivityStack。	

```Java
startActivityMayWait()-->resolveActivity()-->PackageManagerService.resolveIntent()-->

						-->startActivityLocked()-->startActivityUncheckedLocked()-->ActivityStack.startActivityLocked()
-->ActivityStackSupervisor.resumeTopActivitiesLocked()-->ActivityStack.resumeTopActivityLocked()-->ActivityStack.resumeTopActivityInnerLocked()
-->ActivityStack.startPausingLocked()

ActivityStackSupervisor.startActivityLocked()
//获取到调用者的进程信息。 通过 Intent.FLAG_ACTIVITY_FORWARD_RESULT 判断是否需要进行 startActivityForResult 处理。 
//检查调用者是否有权限来调用指定的 Activity。 
//创建 ActivityRecord 对象，并检查是否运行 App 切换。

ActivityStackSupervisor.startActivityUncheckedLocked()
//进行对 launchMode 的处理[可参考 Activity 启动模式]，创建 Task 等操作。
//启动 Activity 所在进程，已存在则直接 onResume()，不存在则创建 Activity 并处理是否触发 onNewIntent()。

ActivityStack.resumeTopActivityInnerLocked()

	if (mResumedActivity != null) {
		if (DEBUG_STATES) Slog.d(TAG_STATES,
				"resumeTopActivityLocked: Pausing " + mResumedActivity);
		pausing |= startPausingLocked(userLeaving, false, true, dontWaitForPause);
	}
	if (pausing) {
		...
		return true;
	}
//先要把前一个activity pause掉
	
```

```Java
ActivityStack.java

	final boolean startPausingLocked(boolean userLeaving, boolean uiSleeping, boolean resuming,
            boolean dontWait) {
        ...
        ActivityRecord prev = mResumedActivity;
        ...
        mResumedActivity = null;
        mPausingActivity = prev;
        mLastPausedActivity = prev;
        mLastNoHistoryActivity = (prev.intent.getFlags() & Intent.FLAG_ACTIVITY_NO_HISTORY) != 0
                || (prev.info.flags & ActivityInfo.FLAG_NO_HISTORY) != 0 ? prev : null;
        prev.state = ActivityState.PAUSING;
        prev.task.touchActiveTime();
        ...
        if (prev.app != null && prev.app.thread != null) {           
            try {
               ...
                prev.app.thread.schedulePauseActivity(prev.appToken, prev.finishing,
                        userLeaving, prev.configChangeFlags, dontWait);
            } catch (Exception e) {
                ...
            }
        } else {
            ...
        }
		...
        if (mPausingActivity != null) {
            ...
			Message msg = mHandler.obtainMessage(PAUSE_TIMEOUT_MSG);
			msg.obj = prev;
			prev.pauseTime = SystemClock.uptimeMillis();
			mHandler.sendMessageDelayed(msg, PAUSE_TIMEOUT);
			if (DEBUG_PAUSE) Slog.v(TAG_PAUSE, "Waiting for pause to complete...");
			return true;
        } 
		...
    }
```
	
	prev.app.thread：prev是ActivityRecord，成员变量app为ProcessRecord，其中thread成员变量为ApplicationThreadProxy的Binder对象
	通过ApplicationThreadProxy发送一个进程间通讯到 preActivity 的中止(pause)，完成后还需要给ActivityManagerService发送一个启动的通知
	后面发送了一个延时消息判断500ms preActivity完成 中止

```Java
ApplicationThreadProxy.java

	public final void schedulePauseActivity(IBinder token, boolean finished,
            boolean userLeaving, int configChanges, boolean dontReport) throws RemoteException {
        ...
        mRemote.transact(SCHEDULE_PAUSE_ACTIVITY_TRANSACTION, data, null,
                IBinder.FLAG_ONEWAY);
        data.recycle();
    }
```
	
	发送一个进程间通讯

```Java
ApplicationThread extends ApplicationThreadNative

	public boolean onTransact(int code, Parcel data, Parcel reply, int flags)
            throws RemoteException {
        switch (code) {
        case SCHEDULE_PAUSE_ACTIVITY_TRANSACTION:
        {
            data.enforceInterface(IApplicationThread.descriptor);
            IBinder b = data.readStrongBinder();
            boolean finished = data.readInt() != 0;
            boolean userLeaving = data.readInt() != 0;
            int configChanges = data.readInt();
            boolean dontReport = data.readInt() != 0;
            schedulePauseActivity(b, finished, userLeaving, configChanges, dontReport);
            return true;
        }

	public final void schedulePauseActivity(IBinder token, boolean finished,
                boolean userLeaving, int configChanges, boolean dontReport) {
            sendMessage(
                    finished ? H.PAUSE_ACTIVITY_FINISHING : H.PAUSE_ACTIVITY,
                    token,
                    (userLeaving ? 1 : 0) | (dontReport ? 2 : 0),
                    configChanges);
    }
	
	private void sendMessage(int what, Object obj, int arg1, int arg2) {
        sendMessage(what, obj, arg1, arg2, false);
    }

    private void sendMessage(int what, Object obj, int arg1, int arg2, boolean async) {
        ...
        mH.sendMessage(msg);
    }
```

	由于ApplicationThread是ActivityThread的内部类，mH为ActivityThread的成员变量

```Java
H.java(ActivityThrea内部类)

	public void handleMessage(Message msg) {
            ...
			case PAUSE_ACTIVITY:				
				handlePauseActivity((IBinder)msg.obj, false, (msg.arg1&1) != 0, msg.arg2,
						(msg.arg1&2) != 0);								
				break;
	}
	
ActivityThread.java

	private void handlePauseActivity(IBinder token, boolean finished,
            boolean userLeaving, int configChanges, boolean dontReport) {
        ActivityClientRecord r = mActivities.get(token);
        if (r != null) {            
            if (userLeaving) {
                performUserLeavingActivity(r);
            }

            r.activity.mConfigChangeFlags |= configChanges;
            performPauseActivity(token, finished, r.isPreHoneycomb());

            // Make sure any pending writes are now committed.
            if (r.isPreHoneycomb()) {
                QueuedWork.waitToFinish();
            }
            // Tell the activity manager we have paused.
            if (!dontReport) {
                try {
                    ActivityManagerNative.getDefault().activityPaused(token);
                } catch (RemoteException ex) {
                }
            }
            mSomeActivitiesChanged = true;
        }
    }
```

	根据ActivityManagerService中的ActivityRecord即token参数，来获取本地对应的ActivityClientRecord
	如果是用户主动离开 preActivity，执行 performUserLeavingActivity(r);-->Activity.onUserLeaveHint();
	performPauseActivity()-->Activity.onPause()
	QueuedWork.waitToFinish();保证一些读写操作完成
	最后使用ActivityManagerProxy来发送进程间通讯
	
##	第二次通讯，旧Activity 通知activityPaused完成，准备resume新的Activity

```Java
ActivityManagerProxy.java

	public void activityPaused(IBinder token) throws RemoteException
    {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeStrongBinder(token);
        mRemote.transact(ACTIVITY_PAUSED_TRANSACTION, data, reply, 0);
        reply.readException();
        data.recycle();
        reply.recycle();
    }

ActivityManagerService.java

	public final void activityPaused(IBinder token) {
        final long origId = Binder.clearCallingIdentity();
        synchronized(this) {
            ActivityStack stack = ActivityRecord.getStackLocked(token);
            if (stack != null) {
                stack.activityPausedLocked(token, false);
            }
        }
        Binder.restoreCallingIdentity(origId);
    }

ActivityStack.java
	
	final void activityPausedLocked(IBinder token, boolean timeout) {     
		final ActivityRecord r = isInStackLocked(token);	
        if (r != null) {
            mHandler.removeMessages(PAUSE_TIMEOUT_MSG, r);
            if (mPausingActivity == r) {
                if (DEBUG_STATES) Slog.v(TAG_STATES, "Moving to PAUSED: " + r
                        + (timeout ? " (due to timeout)" : " (pause complete)"));
                completePauseLocked(true);
            } else {
                ...
            }
        }
    }
```

	删除Pause超时的消息

```Java
completePauseLocked()-->ActivityStackSupervisor.resumeTopActivitiesLocked()-->ActivityStack.resumeTopActivityLocked()
-->ActivityStack.resumeTopActivityInnerLocked()-->

ActivityStack.resumeTopActivityInnerLocked()

	if (next.app != null && next.app.thread != null) {
		...
		next.app.thread.scheduleResumeActivity(next.appToken, next.app.repProcState,
                        mService.isNextTransitionForward(), resumeAnimOptions);
	}else{
		mStackSupervisor.startSpecificActivityLocked(next, true, true);
	}
```

	如果activity已经启动过，就调到前台
	没有启动就创建并调到前台

```Java
ActivityStackSupervisor.java
	
	void startSpecificActivityLocked(ActivityRecord r,
            boolean andResume, boolean checkConfig) {
        // Is this activity's application already running?
        ProcessRecord app = mService.getProcessRecordLocked(r.processName,
                r.info.applicationInfo.uid, true);
		...
        if (app != null && app.thread != null) {
            try {
                ...
                realStartActivityLocked(r, app, andResume, checkConfig);
                return;
            } catch (RemoteException e) {
                ...
            }

            // If a dead object exception was thrown -- fall through to
            // restart the application.
        }

        mService.startProcessLocked(r.processName, r.info.applicationInfo, true, 0,
                "activity", r.intent.getComponent(), false, false, true);
    }
```

 	进程是否存在，存在直接调用realStartActivityLocked()
	进程不存在，调用mService.startProcessLocked()开启进程

```Java
	final boolean realStartActivityLocked(ActivityRecord r,
            ProcessRecord app, boolean andResume, boolean checkConfig)
            throws RemoteException {
		...
		app.thread.scheduleLaunchActivity(new Intent(r.intent), r.appToken,
				System.identityHashCode(r), r.info, new Configuration(mService.mConfiguration),
				new Configuration(stack.mOverrideConfig), r.compat, r.launchedFromPackage,
				task.voiceInteractor, app.repProcState, r.icicle, r.persistentState, results,
				newIntents, !andResume, mService.isNextTransitionForward(), profilerInfo);
		...
	}
```

	调用了ApplicationThreadProxy的scheduleLaunchActivity()通过进程间通信，调用到ActivityThread的scheduleLaunchActivity

```Java
发送消息给H-->ActivityThread.handleLaunchActivity()

	private void handleLaunchActivity(ActivityClientRecord r, Intent customIntent) {
			...
			Activity a = performLaunchActivity(r, customIntent);

			if (a != null) {
				r.createdConfig = new Configuration(mConfiguration);
				Bundle oldState = r.state;
				handleResumeActivity(r.token, false, r.isForward,
						!r.activity.mFinished && !r.startsNotResumed);
				...
			}
	}
```

	调用performLaunchActivity()来创建activity,执行onCreate()和onStart()方法
	创建完成后，调用handleResumeActivity()来让activity可见

```Java
ActivityThread.java

	private Activity performLaunchActivity(ActivityClientRecord r, Intent customIntent) {
		...
		activity = mInstrumentation.newActivity(
                    cl, component.getClassName(), r.intent);					
		...			
	}
```
	
	通过 Instrumentation来创建activity，然后执行一系列生命周期
	
##	后续流程

	本文只分析到 新Activity的onResume方法执行，后续还有新Activity通过Binder告诉resume执行完、旧activity的onStop方法执行等
	
	没有分析进程的创建过程 mService.startProcessLocked()
		其实是通过Socket通信，写入"--runtime-args" "--setuid=" + uid等。fork进程后，返回进程id
		
	
##	总结

	第一步：Activity通过Instrumentation发起startActivity，Instrumentation通过客户端的Binder对象 ActivityManagerProxy 通知 ActivityManagerService需要开启Activity	

	服务端 接到任务后，先让 旧Activity onPause, 通过 Binder对象 ApplicationThreadProxy 通知 客户端的 ApplicationThread(ActivityThread内部类)
			
	第二步：旧Activity onPause完成后，ActivityThread 调用 客户端的Binder对象ActivityManagerProxy 通知 ActivityManagerService，完成 onPause操作了
			
	服务端 接到任务后，开始resume 新Activity，通过 Binder对象 ApplicationThreadProxy 通知 客户端的 ApplicationThread(ActivityThread内部类)


































