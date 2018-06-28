
#	线程池

线程池的创建可以参考[多线程](https://github.com/mirindalover/SummaryOfProgrammingLearning/blob/master/java/%E5%A4%9A%E7%BA%BF%E7%A8%8B.md)

本文只探讨一个问题：

线程池是如何复用的，我们常见的Thread都是直接 start()，任务执行完就直接死亡

而且一个Thread是不能多次调用start()的。那么线程池是如何进行线程复用的呢

##	源码剖析

我们从线程的执行开始

```Java

#ThreadPoolExecutor
	public void execute(Runnable command) {
        if (command == null)
            throw new NullPointerException();       
        int c = ctl.get();
		//如果线程数小于核心线程：直接新建线程执行
        if (workerCountOf(c) < corePoolSize) {
            if (addWorker(command, true))//创建线程是根据核心线程比较--参数true
                return;
            c = ctl.get();
        }
		//加入队列中，并且使用double-check
		//double-check原因是，可能检查后线程死亡。或者线程池直接停止
        if (isRunning(c) && workQueue.offer(command)) {
            int recheck = ctl.get();
			//如果线程池不运行了，需要移除
            if (! isRunning(recheck) && remove(command))
                reject(command);
			//如果线程的数量为0，需要添加一个线程
            else if (workerCountOf(recheck) == 0)
                addWorker(null, false);//创建线程是根据max比较--参数false
        }
        else if (!addWorker(command, false))//创建线程是根据max比较--参数false
            reject(command);
    }

```

前面核心就是addWorker()了，我们看下源码

```Java
	private boolean addWorker(Runnable firstTask, boolean core) {
        //通过CAS来增加线程的线程个数
        //...
        try {
            w = new Worker(firstTask);
            final Thread t = w.thread;
            if (t != null) {
                final ReentrantLock mainLock = this.mainLock;
                mainLock.lock();
                try {
                    //...
                        workers.add(w);                        
                    }
                } finally {
                    mainLock.unlock();
                }
                if (workerAdded) {
                    t.start();
                    workerStarted = true;
                }
            }
        } finally {
            if (! workerStarted)
                addWorkerFailed(w);
        }
        return workerStarted;
    }

```

上面做的事情是创建Worker对象，启动thread。

*	注意的是，我们firstTask可能为空，也可能为有值的。通过回到上一个方法。我们发现：不向队列中添加的时候firstTask为command，添加到队列后即是null

下面我们分析 Worker和他的run()

```Java
#Worker
	Worker(Runnable firstTask) {
		setState(-1); // inhibit interrupts until runWorker
		this.firstTask = firstTask;
		this.thread = getThreadFactory().newThread(this);//通过ThreadPool的工厂创建Thread
	}

	public void run() {
		runWorker(this);
	}

	final void runWorker(Worker w) {
        Thread wt = Thread.currentThread();
        Runnable task = w.firstTask;
        w.firstTask = null;
        w.unlock(); // allow interrupts
        boolean completedAbruptly = true;
        try {
			//使用firstTask或者在队列中取出
            while (task != null || (task = getTask()) != null) {
                w.lock();
                
                try {
                    beforeExecute(wt, task);//没有任何操作，提供给子类扩展的
                    Throwable thrown = null;
                    try {
                        task.run();//核心
                    } catch (RuntimeException x) {
                        thrown = x; throw x;
                    } catch (Error x) {
                        thrown = x; throw x;
                    } catch (Throwable x) {
                        thrown = x; throw new Error(x);
                    } finally {
                        afterExecute(task, thrown);//没有任何操作，提供给子类扩展的
                    }
                } finally {
                    task = null;
                    w.completedTasks++;
                    w.unlock();
                }
            }
            completedAbruptly = false;
        } finally {
            processWorkerExit(w, completedAbruptly);
        }
    }

```

我们通过runWorker()发现了真相：我们在新建的线程中，主动调用了runnable的run()

总结：Worker通过使用FirstTask或者从队列中取消息，并且执行run()来循环运行。这样就达到了复用的目的

我们不妨看看getTask是如何操作的

```Java
	private Runnable getTask() {
		//...
		boolean timed = allowCoreThreadTimeOut || wc > corePoolSize;
        for (;;) {
            //...
            try {
                Runnable r = timed ?
                    workQueue.poll(keepAliveTime, TimeUnit.NANOSECONDS) :
                    workQueue.take();
                if (r != null)
                    return r;
                timedOut = true;
            } catch (InterruptedException retry) {
                timedOut = false;
            }
        }
    }
```

果不其然，通过workQueue.poll()或者workQueue.take()这跟timed有关

分析timed：

-	当allowCoreThreadTimeOut(允许核心线程超时死亡)或者工作线程>核心线程数 为true

workQueue.poll()会根据	keepAliveTime 时间，没有task返回null。Worker的循环停止。进行回收

-	当不允许核心线程超时死亡，并且 工作线程<= 核心线程的时候  为false

workQueue.take()会一直等待新task


最后上一张线程池的图，便于理解

![线程池](https://github.com/mirindalover/SummaryOfProgrammingLearning/blob/master/ReadTheFuckingSourceCode/ThreadPool/resource/线程池运行情况.png "线程池")


1.	如果当前运行的线程少于corePoolSize，则创建新线程来执行任务（注意，执行这一步骤需要获取全局锁）。
2.	如果运行的线程等于或多于corePoolSize，则将任务加入BlockingQueue。
3.	如果无法将任务加入BlockingQueue（队列已满），则创建新的线程来处理任务（注意，执行这一步骤需要获取全局锁）。
4.	如果创建新线程将使当前运行的线程超出maximumPoolSize，任务将被拒绝，并调用RejectedExecutionHandler.rejectedExecution()方法。


