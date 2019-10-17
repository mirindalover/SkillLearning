
#### AndroidStudio关联源码
在studiox.x->config->jdk.table.xml中的sourcepath需要写明

#### AndroidStudio窗口问题
tool中显示更多的工具：view-toolbar勾选

#### AndroidStudio3.1.2 Message窗口不显示
 message属于 toolBar。3.1.2可在build窗口点击按钮切换到文字来显示

#### AndroidStudio 预览问题
Theme需要时Base.XXXX


#### AndroidStudio 布局查看工具地址、模拟器文件查看
SDK\tools\monitor.bat

#### 引导时，屏蔽引导入的某个依赖库

		compile ('com.github.JakeWharton:ViewPagerIndicator:2.4.1') {
            exclude module: 'support-v4'
        }



