
## Google官方MVP

通过Activity中添加Fragment(MVP中的View)，创建presenter

如图：

![Google-MVP][1]

存在的问题：presenter需要重写所有的fragment对应的生命周期

##	添加lifecycle-component

在presenter中添加lifecycle组件，[组件原理](https://github.com/mirindalover/SummaryOfProgrammingLearning/blob/master/ReadTheFuckingSourceCode\lifecycle-compent原理\README.md)

如图：

![添加lifecycle-component][2]

存在的问题；开发中很多公用的view，如果一对一对应的话，有很多重复的代码

##	多个view结合

通过在Fragment中添加View结合的代码来实现

如图：

![多view结合][3]

存在的问题：presenter之间的交互麻烦

##	使用presenter来管理多个presenter

>	Activity/Fragment 通过present拿到view(一个或者多个组合)
>
>	presenter通过lifecycle来进行生命周期的调用
>
>	一个preseter来管理多个presenter。可以通过重写方法的形式来进行presenter之间的调用(本质是通过总的presenter)

如图：

![使用presenter来管理多个presenter][4]




[1]:https://github.com/mirindalover/SummaryOfProgrammingLearning/blob/master/android/resource/google-mvp.png
[2]:https://github.com/mirindalover/SummaryOfProgrammingLearning/blob/master/android/resource/添加lifecycle-component.png
[3]:https://github.com/mirindalover/SummaryOfProgrammingLearning/blob/master/android/resource/多个view结合.png
[4]:https://github.com/mirindalover/SummaryOfProgrammingLearning/blob/master/android/resource/使用presenter管理.png