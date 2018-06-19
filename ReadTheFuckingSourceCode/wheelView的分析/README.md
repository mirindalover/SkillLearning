
##	滚动控件的简介

Android的日期选择：DatePicker,原理是通过3个NumberPicker组合而成

## NumberPicker分析

集成LinearLayout

NumberPicker是通过设置设置最大值、最小值。通过 ondraw的时候drawText来进行绘制文字

通过2个Scroller来进行滑动

github上开源的滚动控件--WheelView

### WheelView分析

方案一、 继承SrcollView

通过添加TextView来addView()进去

滑动时通过up-->设置handler进行判断，比较滑动的位置后，通过smoothScrollTo()进行


















