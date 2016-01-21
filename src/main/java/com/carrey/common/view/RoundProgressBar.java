package com.carrey.common.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.carrey.common.R;
import com.carrey.common.util.UIUtil;


/**
 * 圆形百分比进度条
 * RoundProgressBar
 * ningyaoyun
 * 2014年10月29日 下午2:20:51
 * @version 3.5
 */
public class RoundProgressBar extends View {
	/**
	 * 画笔对象的引用
	 */
	private Paint paint;
	
	/**
	 * 圆环的颜色
	 */
	private int roundColor;
	
	/**
	 * 圆环进度的颜色
	 */
	private int roundProgressColor;
	
	/**
	 * 中间进度百分比的字符串的颜色
	 */
	private int textColor;
	
	/**
	 * 中间进度百分比的字符串的字体
	 */
	private float textSize;
	
	/**
	 * 圆环的宽度
	 */
	private float roundWidth; 	
	/**
	 * 最大进度
	 */
	private int max;
	
	/**
	 * 当前进度
	 */
	private int progress;
	/**
	 * 中间文本，优先级高于进度
	 */
	private String textThanProgress;
	/**
	 * 是否显示中间的进度
	 */
	private boolean textIsDisplayable;
	
	/**
	 * 进度的风格，实心或者空心
	 */
	private int style;
	/**
	 * 进度方向
	 */
	private boolean isForward;//ture代表正向，false代表逆向
	
	public static final int STROKE = 0;
	public static final int FILL = 1;

	private boolean isNeedPercent = true;
	
	RectF oval = new RectF();  //用于定义的圆弧的形状和大小的界限
	
	public RoundProgressBar(Context context) {
		this(context, null);
	}

	public RoundProgressBar(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public RoundProgressBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		paint = new Paint();

		
		TypedArray mTypedArray = context.obtainStyledAttributes(attrs,
				R.styleable.RoundProgressBar);
		
		//获取自定义属性和默认值
		roundColor = mTypedArray.getColor(R.styleable.RoundProgressBar_roundColor, 0xffdbeaf0);
		roundProgressColor = mTypedArray.getColor(R.styleable.RoundProgressBar_roundProgressColor, 0xff4bc8f5);
		textColor = mTypedArray.getColor(R.styleable.RoundProgressBar_textColor, 0xff4bc8f5);
		textSize = mTypedArray.getDimension(R.styleable.RoundProgressBar_textSize, UIUtil.dip2px(16));
		roundWidth = mTypedArray.getDimension(R.styleable.RoundProgressBar_roundWidth, UIUtil.dip2px(5)); 
		max = mTypedArray.getInteger(R.styleable.RoundProgressBar_max, 100);
		textIsDisplayable = mTypedArray.getBoolean(R.styleable.RoundProgressBar_textIsDisplayable, true);
		style = mTypedArray.getInt(R.styleable.RoundProgressBar_style, 0);
		
		mTypedArray.recycle();
	}
	

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		/**
		 * 画最外层的大圆环
		 */
		int centre = getWidth()/2; //获取圆心的x坐标
		int radius = (int) (centre - roundWidth/2); //圆环的半径
		paint.setColor(roundColor); //设置圆环的颜色
		paint.setStyle(Paint.Style.STROKE); //设置空心
        paint.setAntiAlias(true);  //消除锯齿
        paint.setStrokeWidth(roundWidth); //设置圆环的宽度
		canvas.drawCircle(centre, centre, radius, paint); //画出圆环
		
		/**
		 * 画进度百分比
		 */
		paint.setStrokeWidth(0); 
		paint.setColor(textColor);
		paint.setTextSize(textSize);
		paint.setTypeface(Typeface.DEFAULT_BOLD); //设置字体
		int percent = progress;  //中间的进度百分比，先转换成float在进行除法运算，不然都为0

		String percentText = (TextUtils.isEmpty(textThanProgress) ? percent : textThanProgress) + (isNeedPercent ? "%" : "");
		float textWidth = paint.measureText(percentText);   //测量字体宽度，我们需要根据字体的宽度设置在圆环中间

		Paint.FontMetricsInt fontMetrics = paint.getFontMetricsInt();
		int baseline = (centre * 2 - fontMetrics.bottom - fontMetrics.top) / 2;

		if(textIsDisplayable && !TextUtils.isEmpty(percentText) && style == STROKE){
			canvas.drawText(percentText, centre - textWidth / 2, baseline, paint); //画出进度百分比
		}

		/**
		 * 画圆弧 ，画圆环的进度
		 */
		
		//设置进度是实心还是空心
		paint.setStrokeWidth(roundWidth); //设置圆环的宽度
		paint.setColor(roundProgressColor);  //设置进度的颜色

//        oval.set(centre - radius, centre - radius, centre + radius, centre + radius);
//		canvas.drawArc(oval, -90, 360 * progress/100, false, paint);  //根据进度画圆弧

        oval.left = centre - radius;
        oval.top = centre - radius;
        oval.right = centre + radius;
        oval.bottom = centre + radius;
        switch (style) {
			case STROKE: {
				paint.setStyle(Paint.Style.STROKE);
				canvas.drawArc(oval, -90, (isForward ? 1 : -1) * 360 * progress / max, false, paint);  //根据进度画圆弧
				break;
			}
			case FILL: {
				paint.setStyle(Paint.Style.FILL_AND_STROKE);
				if (progress != 0) {
					canvas.drawArc(oval, -90, (isForward ? 1 : -1) * 360 * progress / max, true, paint);  //根据进度画圆弧
				}
				break;
			}
		}
	}
	
	
	public synchronized int getMax() {
		return max;
	}

	/**
	 * 设置进度的最大值
	 * @param max
	 */
	public synchronized void setMax(int max) {
		if(max < 0){
			throw new IllegalArgumentException("max not less than 0");
		}
		this.max = max;
	}

	/**
	 * 获取进度.需要同步
	 * @return
	 */
	public synchronized int getProgress() {
		return progress;
	}

	/**
	 * 设置进度，此为线程安全控件，由于考虑多线的问题，需要同步
	 * 刷新界面调用postInvalidate()能在非UI线程刷新
	 * @param progress
	 */
	public synchronized void setProgress(int progress) {
		if(progress < 0){
			throw new IllegalArgumentException("progress not less than 0");
		}
		if(progress > max){
			progress = max;
		}
		if(progress <= max){
			this.progress = progress;
			postInvalidate();
		}
		
	}

    private ValueAnimator mSmoothAnim;

    public synchronized void setSmoothedProgress(int start, int progress, final long duration) {
        if(progress < 0){
            throw new IllegalArgumentException("progress not less than 0");
        }
        if(progress > max){
            progress = max;
        }
        if(progress <= max){
            int nowProgress = start == -1 ? this.progress : start;
            if (mSmoothAnim != null) {
                mSmoothAnim.cancel();
            }
            mSmoothAnim = ValueAnimator.ofInt(nowProgress, progress);
            mSmoothAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    RoundProgressBar.this.progress = (int) animation.getAnimatedValue();
                    invalidate();
                }
            });
            mSmoothAnim.setDuration(duration);
            mSmoothAnim.setInterpolator(new AccelerateDecelerateInterpolator());
            mSmoothAnim.start();
        }
    }

	public synchronized void setSmoothedProgress(int progress, final long duration) {
        setSmoothedProgress(-1, progress, duration);
	}
	
	
	public int getCricleColor() {
		return roundColor;
	}

	public void setCricleColor(int cricleColor) {
		this.roundColor = cricleColor;
	}

	public int getCricleProgressColor() {
		return roundProgressColor;
	}

	public void setCricleProgressColor(int cricleProgressColor) {
		this.roundProgressColor = cricleProgressColor;
	}

	public int getTextColor() {
		return textColor;
	}

	public void setTextColor(int textColor) {
		this.textColor = textColor;
	}

	public float getTextSize() {
		return textSize;
	}

	public void setTextSize(float textSize) {
		this.textSize = textSize;
	}

	public float getRoundWidth() {
		return roundWidth;
	}

	public void setRoundWidth(float roundWidth) {
		this.roundWidth = roundWidth;
	}

	public boolean isForward() {
		return isForward;
	}

	public void setIsForward(boolean isForward) {
		this.isForward = isForward;
	}

	public boolean isNeedPercent() {
		return isNeedPercent;
	}

	public void setIsNeedPercent(boolean isNeedPercent) {
		this.isNeedPercent = isNeedPercent;
	}

	public String getTextThanProgress() {
		return textThanProgress;
	}

	public void setTextThanProgress(String textThanProgress) {
		this.textThanProgress = textThanProgress;
	}
}
