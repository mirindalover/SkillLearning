
#	TextView的右对齐问题

>会保证最后一个显示的不是符号,从而会进行折行处理


## 源码跟踪

```Java

#TextView
	@Override
    protected void onDraw(Canvas canvas) {
        //...
		//直接追踪canvas的draw操作即可找到重点部分
		//省略代码是先画 textView的Drawables
        
        if (mEditor != null) {
            mEditor.onDraw(canvas, layout, highlight, mHighlightPaint, cursorOffsetVertical);
        } else {
            layout.draw(canvas, highlight, mHighlightPaint, cursorOffsetVertical);
			//使用Layout.draw方法
        }

        if (mMarquee != null && mMarquee.shouldDrawGhost()) {
            final float dx = mMarquee.getGhostOffset();
            canvas.translate(layout.getParagraphDirection(0) * dx, 0.0f);
            layout.draw(canvas, highlight, mHighlightPaint, cursorOffsetVertical);
        }

        canvas.restore();
    }

```

```Java

#Layout
	/**
     * @hide
     */
    public void drawText(Canvas canvas, int firstLine, int lastLine) {
        
        for (int lineNum = firstLine; lineNum <= lastLine; lineNum++) {
            //...
			//跟踪canvas的draw方法
            if (directions == DIRS_ALL_LEFT_TO_RIGHT && !mSpannedText && !hasTab) {
                //通过drawText来画的，
                canvas.drawText(buf, start, end, x, lbaseline, paint);
            } else {
                tl.set(paint, buf, start, end, dir, directions, hasTab, tabStops);
                tl.draw(canvas, x, ltop, lbaseline, lbottom);
            }
            paint.setHyphenEdit(0);
        }
    }

```


> 总结：TextView通过onMeasure确定行数(mLayout)、再通过onDraw交给mLayout来进行绘制

## 解决思路

> 通过TextView的onMeasure进行测量后，通过自定义onDraw()来进行绘制

### 实现

*	判断是否需要增大间隙
	*	考虑第一行的tab
	*	考虑最后一行(即有\n)
	*	考虑空行

####	需要的api
	
>	StaticLayout.getDesiredWidth()	一行文字需要的宽度
>
>	layout.getLineStart(i)(非静态)	第i行文字在text的开始index
>
>	layout.getLineEnd(i)(非静态)	第i行文字在text的结尾index


详见 JustifyTextView.java
























