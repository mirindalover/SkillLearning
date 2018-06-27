
#	Binder的介绍

##	binder的一些小知识：

Linux通信机制：Socket、管道等

Binder是通过Linux的可动态加载模块机制，被链接到了内核作为内核的一部分。

Binder通过用户空间中 binder_open(), binder_mmap(), binder_ioctl() 这些方法通过 system call 来调用内核空间 Binder 驱动中的方法。

内核空间与用户空间共享内存通过 copy_from_user(), copy_to_user() 内核方法来完成用户空间与内核空间内存的数据传输

Binder的优势：安全、高效。安全：对通信双方做了身份校验，而Socket通信ip可以进行伪造

##	重要的类

ServiceManager是用来注册Binder的，也是一个Binder。我们获取的也只是proxy来向ServiceManager添加注册服务

我们将通过下面几点来进行介绍:

*	Service的添加
*	Service的使用
*	AIDL的介绍
*	ServiceManager的作用

##	Service的注册

###	先获取ServiceManagerProxy

```Java

#ServiceManager
	//添加服务
	public static void addService(String name, IBinder service) {
        try {
            getIServiceManager().addService(name, service, false);
        } catch (RemoteException e) {
            Log.e(TAG, "error in addService", e);
        }
    }
	
	//获取ServiceManagerProxy
	private static IServiceManager getIServiceManager() {
        if (sServiceManager != null) {
            return sServiceManager;
        }

        // Find the service manager
        sServiceManager = ServiceManagerNative
                .asInterface(Binder.allowBlocking(BinderInternal.getContextObject()));
        return sServiceManager;
    }	

```

```Java

#ServiceManagerNative
	static public IServiceManager asInterface(IBinder obj)
    {
        if (obj == null) {
            return null;
        }
        IServiceManager in =
            (IServiceManager)obj.queryLocalInterface(descriptor);
        if (in != null) {
            return in;
        }
        
        return new ServiceManagerProxy(obj);
    }
	
```

获取封装的ServiceManagerProxy来使用

```Java
#BinderInternal
	
	public static final native IBinder getContextObject();

```

getContextObject()获取了一个BinderProxy对象，对象跟Native的BpBinder挂钩，此时BpBinder通讯目标是ServiceManager

可以参考最后的图，来进行理解

###	把service添加到Binder驱动

回到前面，调用了 ServiceManagerProxy.addService()

```Java

#ServiceManagerProxy
	public void addService(String name, IBinder service, boolean allowIsolated)
            throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IServiceManager.descriptor);
        data.writeString(name);
        data.writeStrongBinder(service);
        data.writeInt(allowIsolated ? 1 : 0);
        mRemote.transact(ADD_SERVICE_TRANSACTION, data, reply, 0);//mRemote是BinderProxy
        reply.recycle();
        data.recycle();
    }

```

```Java
#BinderProxy(Binder.java的内部类)
	public boolean transact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
        //...
        try {
            return transactNative(code, data, reply, flags);
        } finally {
            if (tracingEnabled) {
                Trace.traceEnd(Trace.TRACE_TAG_ALWAYS);
            }
        }
    }
	
	public native boolean transactNative(int code, Parcel data, Parcel reply,
            int flags) throws RemoteException;

```

native代码是通过BpBinder通过transact()把数据发送到Binder驱动

-	那么我们传递的数据是什么呢？

```Java

#ServiceManagerProxy

	public final void writeStrongBinder(IBinder val) {
        nativeWriteStrongBinder(mNativePtr, val);
    }

	private static native void nativeWriteStrongBinder(long nativePtr, IBinder val);

```

Native代码做的事情：

向包裹中 写入一个	JavaBBinder(变量mObject指向了Binder对象)
JavaBBinder 来自	一个JavaBBinderHolder的mBinder变量
JavaBBinderHolder来自	Binder初始化时init()，放在了mObject变量中

总结一波：

>	service的注册，比如ActivityManagerService，首先拿到ServiceManager的proxy
>
>	通过proxy->Native 的BpBinder。通过trasact()把数据Parcel发送到Binder驱动中进行服务的添加(Binder是如何添加到ServiceManager中的 见下面ServiceManager的具体分析)
>
>	Parcel数据是通过Binder初始化时的mObject(JavaBBinderHolder)->mBinder(JavaBBinder)得到的JavaBBinder
>
>	而JavaBBinder的mObject引用着Binder对象。

##	service的使用

>	客户端通过Proxy->Native 的BpBinder。通过trasact()把数据Parcel发送到Binder驱动中(Binder驱动如何调用到JavaBBinder见 下面Binder的解析)
>
>	Binder驱动调用到JavaBBinder(ActivityManagerService对应的)的onTransact()，调用Binder的execTransact()
>
>	对应调用到ActivityManagerService的onTransact()

上图：

![Service的使用](https://github.com/mirindalover/SummaryOfProgrammingLearning/blob/master/android/resource/service的使用.png "service的使用")

##	与Binder驱动的通信

经过以上的介绍，我们知道proxy通过BpBinder发送数据到Binder，Binder再调到服务的JavaBBinder

-	那么Binder期间做了什么操作来调用到Service端的

其实BpProxy是通过IPCthreadState来与Binder交互的。维护mIn、mOut来与Binder进行数据交换

再通过ioctl()与Binder交互。

同时BpBinder有一个mHandle来指向要通信的Service(0表示ServiceManager)

*	Binder有关的几个重要操作：

>	每个进程开启时，ProcessState会进行open(打开/dev/binder	打开Binder的通道)
>
>	mmap(映射内存)	在Binder驱动分配内存，进行数据交互

>	IPCthreadState是直接与Binder进行
>
>	通过ioctl()来进行

>	交互的目标Service是通过mhandle来确定

##	ServiceManager的作用

上面说到，添加Service通过ServiceManagerProxy把数据发送到Binder，ServiceManager把服务进行注册

那么服务是如何注册的呢，我们查找的时候又是如何进行的呢？

-	ServiceManager 的启动是系统在开机时，init 进程解析 init.rc 文件调用 service_manager.c 中的 main() 方法入口启动的。native 层有一个 binder.c 封装了一些与 Binder 驱动交互的方法

-	ServiceManager 的启动分为三步，首先打开驱动创建全局链表 binder_procs，然后将自己当前进程信息保存到 binder_procs 链表，最后开启 loop 不断的处理共享内存中的数据

*	添加服务

>	BpBinder通过ioctl()向Binder发送数据
>
>	Binder驱动收到该Binder请求，生成BR_TRANSACTION命令，选择目标处理该请求的线程，插入到目标线程的todo队列,等待处理
>
>	注册的过程就是向 Binder 驱动的全局链表 binder_procs 中插入服务端的信息（binder_proc 结构体，每个 binder_proc 结构体中都有 todo 任务队列），
>
>	然后向 ServiceManager 的 svcinfo 列表中缓存一下注册的服务

*	查询服务

>	通过ServiceManagerProxy->Binder->ServiceManager
>
>	执行do_find_service()在svcinfo中查找


*	查询的服务，属于不同进程，返回BpProxy。属于同以进程时 返回的是BBinder

![ServiceManager管理](https://github.com/mirindalover/SummaryOfProgrammingLearning/blob/master/android/resource/IPC-Binder.png "ServiceManager")


##	AIDL的介绍

使用：在main->aidl->创建xxx.aidl文件

会生成一个同名的.java文件，内容如下：

>	一个interface类，包含了aidl声明的函数

>	一个抽象的Stub类 extends Binder 具体函数实现需要子类进行
>
>	提供asInterface()函数来获取proxy来进行与Service的通讯
>
>	重写了onTransact()，定义了数据读写的顺序。保证了一致性

>	一个私有的Proxy类，保证只能通过Stub.asInterface()来获取
>
>	重写了函数，保证数据的读写的顺序

使用AIDL。

*	服务端

需要继承Service，在onBinder()返回 Stub的子类(实现类)

通过startService启动服务。

*	客户端

通过bindService()获取服务的binder引用

通过Stub.adInterface()来进行获取proxy

	Binder通信都是一个Server对应多个Client的形式，Client获取proxy来通过Binder调用Server的stub的方法
	
	service端注册Binder到ServiceManager的查找表中，




















