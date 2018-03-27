# Clean_Architecture

## GoogleSample

## todo_mvp：使用fragment充当view层

![mvp模型][1]	

	Presenter:	activity中创建，在构造方法中绑定view和model，在view的onResume中调用mPresenter.start()，开启运作
	View:		由activity创建，在Presenter构造中绑定Presenter
	Model:		创建Presenter时创建，即随Presenter创建
	
## todo_clean：添加了DomainLayer

![mvp_clean模型][2]

	Presenter 不直接操作data，通过UseCase来进行
		
## todo_databinding 使用databinding：添加依赖，xml中使用<data>将view与数据绑定
	使用mvvvm推荐使用此方法
	
	
	
	
[1]:https://github.com/mirindalover/SummaryOfProgrammingLearning/blob/master/architecture/resource/mvp.png
[2]:https://github.com/mirindalover/SummaryOfProgrammingLearning/blob/master/architecture/resource/clean_mvp.png