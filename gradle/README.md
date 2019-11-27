
##	Gradle学习

###	如何查看Gradle源码

    在项目的build.gradle中添加gralde的依赖
    gradle版本和项目的build.gradle的classpath相同即可
    ```Gradle
    
    dependencies {
    implementation fileTree(dir: 'libs', include: ['*.jar'])

    implementation 'com.android.tools.build:gradle:3.0.1'//这样就可以查看gradle源码
    }
    
    ```
### Gradle介绍

[Gradle官网](https://docs.gradle.org/current/dsl/index.html)

#### Project
	
	每个build.gradle文件解析后生成Project对象
	
#### Task

	执行构建的自小单元
 

###	自定义插件
    
    1. 新建module，只保留src/main和build.gradle
    2. 在main新建groovy文件来存放groovy文件
    3. 在build.gradle中删除所有，添加如下
	
    ```Java
    apply plugin: 'groovy'

    apply plugin: 'maven'

    dependencies{

        compile gradleApi()

        compile localGroovy()
    }
    ```
	
    4. 创建groovy文件继承Plugin ,实现接口的方法
    5. 在main中创建resources/META_INF/gradle-plugin文件夹，创建properties文件，名称即为 使用时 的插件名称
    6. 在build.gradle中添加插件生成的代码
	
    ```Java
    //使用时的环境，用于buildscript
    group='com.jd.mlc.plugin'
    version='1.0.0' 

    uploadArchives {
         repositories {
            mavenDeployer {
            //提交到远程服务器：
            // repository(url: "http://www.xxx.com/repos") {
            //    authentication(userName: "admin", password: "admin")
            // }
            //本地的Maven地址设置为D:/repos
            repository(url: uri('D:/repos'))
            }
        }
    }
    ```
	
    7. 引用：
        在Project的build.gradle中添加插件的环境
		
	```Java
	buildscript {

		repositories {
			maven{
				url uri('D:/repos')
			}
		}
		dependencies {
			classpath 'com.jd.mlc.plugin:plugin:1.0.0'
		
		}
	}
	```
	
	在app的build.gradle中使用插件
	apply plugin: 'com.jd.mlc.test'
 
###	Gradle的配置属性、字段

####	Project的build.gradle
    
		* buildscript
        
        gradle脚本自身需要的资源包括：依赖、插件、maven

        * repositories
        
        依赖的仓库maven、google()、jcenter()
        
        * dependencies
        
        依赖的plugin,使用classpath配置
        
        * allprojects/subprojects(子module)
        
        所有项目module的设置
        
        * ext
        
        自定义的属性，比如使用rootProject.ext.xxxx，同时Project的build依赖(apply from)了其他的gradle文件(其中声明了ext)

####	Module的build.gradle
    
       * apply plugin
       
       引用的插件，后面是插件的名称
       
       * apply from
       
       引用的脚本，就是把其他文件加进来，方便拆分
	   
	   * dependencies
        
        依赖的第三方库

####	Extension(扩展配置)
    
        通过Extension，我们可以向目标对象添加DSL扩展，这一过程通过project中的ExtensionContainer来add，通过project.getExtensions()
                .create获取，并与一个对应的委托类关联起来（即新建一个DSL域，并委托给一个具体类）
				
				可通过findByType，来查找存在的DSL域
                
####	Task
    
        Task是有生命周期的：
        初始化->配置->执行
        
        * 执行task时会把所有的task走到配置阶段
        
        * doFirst doLast 分别是在任务前添加、任务后添加
        
        * Task的基类是DefaultTask 通过@TaskAction来表示执行的方法
        
        * dependsOn task的执行依赖谁，表示来要后于谁执行
 
###	Gradle问题

####	Studio版本不同导致无法运行的问题

	[各个版本对应的gradle](https://developer.android.google.cn/studio/releases/gradle-plugin.html#updating-plugin)
	
	解决方式：通过降低gradle-wrapper.properties、Project的build.gradle的tools.build
        
        
        
        
        



