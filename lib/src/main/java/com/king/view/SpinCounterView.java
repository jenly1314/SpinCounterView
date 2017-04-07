package com.king.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
/**
 * @author Jenly <a href="mailto:jenly1314@gmail.com">Jenly</a>
 * @since 2017/4/5
 */

public class SpinCounterView extends View {

    public static final String DEFAULT_FORMAT = getDecimalFormat(2);
    public static final String RMB_FORMAT = "￥%1$.2f";
    public static final String DOLLAR_FORMAT = "$%1$.2f";

    private Paint mPaint;

    private int mNormalColor = Color.parseColor("#c8c8c8");
    private int mProgressColor = Color.parseColor("#f97203");

    private int mLabelTextColor = Color.parseColor("#c8c8c8");
    private int mTextColor = Color.parseColor("#f97203");

    /**
     * 笔画的宽度
     */
    private float mStrokeWidth = 20;

    /**
     * 开始角度
     */
    private int mStartAngle = 145;
    /**
     * 扫描角度
     */
    private int mSweepAngle =  250;

    /**
     * 刻度 类似于时钟的刻度
     */
    private int mScaleAngle = 5;

    /**
     * 圆心
     */
    private float mCircleCenterX;
    private float mCircleCenterY;

    /**
     * 半径
     */
    private float mRadius = 300;

    /**
     * 最大进度
     */
    private float mMax = 100;

    /**
     * 当前进度
     */
    private float mProgress = 0;

    /**
     * 圆弧间的间距
     */
    private float mSpace = 30;

    /**
     * 箭头与刻度的距离
     */
    private float mArrow = 20;

    /**
     * 动画持续的时间
     */
    private int mDuration = 500;

    /**
     * 标签内容
     */
    private String mLabelText;

    /**
     * 值
     */
    private float mValue;

    /**
     * 最大值
     */
    private float mMaxValue = 100;

    /**
     * 值与进度的比率 mValueRatio = mValue / mProgress
     */
    private float mValueRatio = 1;

    /**
     * 字体大小
     */
    private float mTextSize = 60;
    private float mLabelTextSize = 46;


    /**
     * 文本里圆心的Y轴偏移量
     */
    private float mTextOffsetY = 40;

    private String mFormat = DEFAULT_FORMAT;

    private boolean isShowLabel = true;

    private boolean isShowText = true;

    private OnUpdateListener mOnUpdateListener;

    public interface OnUpdateListener{
        boolean onUpdate(float progress,float curValue,String format);
    }

    public SpinCounterView(Context context) {
        this(context,null);
    }

    public SpinCounterView(Context context,AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SpinCounterView(Context context,AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }

    private void init(Context context,AttributeSet attrs){

        mPaint = new Paint();

        TypedArray a = context.obtainStyledAttributes(attrs,R.styleable.SpinCounterView);


        mLabelTextSize = a.getDimension(R.styleable.SpinCounterView_android_textSize,TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,16,getDisplayMetrics()));
        mLabelTextColor = a.getColor(R.styleable.SpinCounterView_android_textColor,mLabelTextColor);
        mLabelText = a.getString(R.styleable.SpinCounterView_labelText);

        mTextSize = a.getDimension(R.styleable.SpinCounterView_android_textSize,TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,22,getDisplayMetrics()));
        mTextColor = a.getColor(R.styleable.SpinCounterView_android_textColor,mTextColor);

        mNormalColor = a.getColor(R.styleable.SpinCounterView_normalColor,mNormalColor);
        mProgressColor = a.getColor(R.styleable.SpinCounterView_progressColor,mProgressColor);

        mStrokeWidth = a.getDimension(R.styleable.SpinCounterView_strokeWidth,mStrokeWidth);
        mStartAngle = a.getInt(R.styleable.SpinCounterView_startAngle,mStartAngle);
        mSweepAngle = a.getInt(R.styleable.SpinCounterView_sweepAngle,mSweepAngle);
        mScaleAngle = a.getInt(R.styleable.SpinCounterView_scaleAngle,mScaleAngle);

        mMax = a.getFloat(R.styleable.SpinCounterView_max,mMax);
        mProgress = a.getFloat(R.styleable.SpinCounterView_progress,0);
        mMaxValue = mMax;
        mMaxValue = a.getFloat(R.styleable.SpinCounterView_maxValue,mMaxValue);

        mValueRatio = mMaxValue / mMax;

        mSpace = a.getDimension(R.styleable.SpinCounterView_space,TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,10,getDisplayMetrics()));
        mArrow = a.getDimension(R.styleable.SpinCounterView_space,TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,6.7f,getDisplayMetrics()));
        mDuration = a.getInt(R.styleable.SpinCounterView_duration,mDuration);
        mTextOffsetY = a.getDimension(R.styleable.SpinCounterView_duration,TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,13,getDisplayMetrics()));
        isShowLabel = a.getBoolean(R.styleable.SpinCounterView_showLabelText,true);
        isShowText = a.getBoolean(R.styleable.SpinCounterView_showText,true);

        a.recycle();
    }

    private DisplayMetrics getDisplayMetrics(){
        return getResources().getDisplayMetrics();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int defaultValue = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,200,getDisplayMetrics());

        int defaultWidth = defaultValue + getPaddingLeft() + getPaddingRight();
        int defaultHeight = defaultValue + getPaddingTop() + getPaddingBottom();

        int width = measureHandler(widthMeasureSpec,defaultWidth);
        int height = measureHandler(heightMeasureSpec,defaultHeight);

        setMeasuredDimension(width,height);
        //圆心坐标
        mCircleCenterX = (getWidth() + getPaddingLeft() - getPaddingRight())/ 2.0f;
        mCircleCenterY = (getHeight() + getPaddingTop() - getPaddingBottom())/ 2.0f;

        //半径
        mRadius = (getWidth()- getPaddingLeft() - getPaddingRight()) / 2.0f - mSpace;

    }

    private int measureHandler(int measureSpec,int defaultSize){

        int result = defaultSize;
        int measureMode = MeasureSpec.getMode(measureSpec);
        int measureSize = MeasureSpec.getSize(measureSpec);
        if(measureMode == MeasureSpec.EXACTLY){
            result = measureSize;
        }else if(measureMode == MeasureSpec.AT_MOST){
            result = Math.min(defaultSize,measureSize);
        }
        return result;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawArc(canvas);
        drawCenterText(canvas);
    }

    private void drawArc(Canvas canvas){

        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);

        mPaint.setStrokeWidth(mStrokeWidth);
        mPaint.setColor(mNormalColor);

        float diameter = mRadius * 2;
        float startX = mCircleCenterX - mRadius;
        float startY = mCircleCenterY - mRadius;
        RectF rectF = new RectF(startX,startY,startX + diameter,startY + diameter);
        //画最外层的弧形
        canvas.drawArc(rectF,mStartAngle,mSweepAngle,false,mPaint);


        mPaint.setColor(mProgressColor);
        //画最外层的弧形（当前进度）
        canvas.drawArc(rectF,mStartAngle,mSweepAngle * getRatio(),false,mPaint);

        //-------------------

        mPaint.setStrokeWidth(mStrokeWidth/2);

        float radius = mRadius - mSpace - mStrokeWidth;
        float ratioAngle = (float)(2 * Math.PI /360) * (mStartAngle + Math.round(mSweepAngle * getRatio()/mScaleAngle) * mScaleAngle);
        int size = mSweepAngle / mScaleAngle;
        //算出弧形上的点坐标并画点
        for (int i = 0; i <= size; i++) {
            float angle = (float)(2 * Math.PI /360) * (mStartAngle + i * mScaleAngle);

            float x = (float)(mCircleCenterX + Math.cos(angle) * radius);
            float y = (float)(mCircleCenterY + Math.sin(angle) * radius);
            if(ratioAngle>=angle){
                mPaint.setColor(mProgressColor);
            }else{
                mPaint.setColor(mNormalColor);
            }
            canvas.drawPoint(x,y,mPaint);
        }


        //-------------------
        //弧形上指示器的坐标点（当前进度）
        float x = (float)(mCircleCenterX + Math.cos(ratioAngle) * radius);
        float y = (float)(mCircleCenterY + Math.sin(ratioAngle) * radius);

        mPaint.setStrokeWidth(mStrokeWidth/3);
        mPaint.setColor(mProgressColor);
        //指示器的圆的半径
        float indicatorRadius = mStrokeWidth/2;
        //画进度指示器的圆
        canvas.drawCircle(x,y,indicatorRadius,mPaint);
        //指示器箭头的坐标
        float x1 = (float)(mCircleCenterX + Math.cos(ratioAngle) * (radius + mArrow));
        float y1 = (float)(mCircleCenterY + Math.sin(ratioAngle) * (radius + mArrow));

        //算出指示器到指示器圆心的直线与指示器切线的夹角
        float a = (float) Math.sin(radius/(radius + mArrow));
        //指示器与指示器圆的切线坐标角度
        float angle2 = ratioAngle + a;
        //指示器与指示器圆的切线坐标1
        float x2 = (float)(x + Math.cos(angle2) * indicatorRadius);
        float y2 = (float)(y + Math.sin(angle2) * indicatorRadius);

        //指示器与指示器圆的切线坐标角度
        float angle3 = ratioAngle - a;
        //指示器与指示器圆的切线坐标2
        float x3 = (float)(x + Math.cos(angle3) * indicatorRadius);
        float y3 = (float)(y + Math.sin(angle3) * indicatorRadius);

        Path path = new Path();
        path.moveTo(x2,y2);
        path.lineTo(x1,y1);
        path.lineTo(x3,y3);
        //画指示器的箭头
        canvas.drawPath(path,mPaint);
//        canvas.drawPoint(x1,y1,mPaint);

        //-------------------
        mPaint.setColor(mNormalColor);

        float innerDiameter = (mRadius - mStrokeWidth * 1.5f - mSpace - mSpace) * 2;
        float innerStartX = mCircleCenterX - mRadius + mStrokeWidth * 1.5f + mSpace + mSpace;
        float innerStartY = mCircleCenterY - mRadius + mStrokeWidth * 1.5f + mSpace + mSpace;
        RectF innerRectF = new RectF(innerStartX,innerStartY,innerStartX + innerDiameter,innerStartY + innerDiameter);
        //画内层的弧形
        canvas.drawArc(innerRectF,mStartAngle,mSweepAngle,false,mPaint);
    }

    private void drawCenterText(Canvas canvas){
        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setTypeface(Typeface.DEFAULT);
        mPaint.setTextAlign(Paint.Align.CENTER);

        if(isShowLabel && mLabelText != null){//画标签文本逻辑
            mPaint.setColor(mLabelTextColor);
            mPaint.setTextSize(mLabelTextSize);
            float x = mCircleCenterX;
            float y = mCircleCenterY - mTextOffsetY;
            canvas.drawText(mLabelText,x,y,mPaint);
        }

        if(isShowText){//画文本逻辑
            mPaint.setColor(mTextColor);
            mPaint.setTextSize(mTextSize);
            mPaint.setFakeBoldText(true);
            String text = getFormatText(mValue,mFormat);
            float x = mCircleCenterX;
            float y = mCircleCenterY + mTextOffsetY;
            canvas.drawText(text,x,y,mPaint);
        }
    }

    private String getFormatText(float value,String format){
        return String.format(format,value);
    }

    public void showAppendAnimation(float progress){
        showAnimation(mProgress,progress,mDuration);
    }


    public void showAnimation(float progress){
        showAnimation(progress,mDuration);
    }

    public void showAnimation(float progress,int duration){
        showAnimation(0,progress,duration);
    }

    public void showAnimation(float from,float to,int duration){
        showAnimation(from,to,duration,mFormat);
    }

    public void showAnimation(float from,float to,int duration,String format){
        this.mDuration = duration;
        this.mFormat = (format == null ? DEFAULT_FORMAT : format);
        setProgress(to);
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(from,mProgress);
        valueAnimator.setDuration(duration);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mProgress = (float)animation.getAnimatedValue();
                mValue = mProgress * mValueRatio;
                if(mOnUpdateListener==null || !mOnUpdateListener.onUpdate(mProgress,mValue,mFormat)){
                    invalidate();
                }
            }
        });

        valueAnimator.start();
    }

    public void setText(float value,String format){
        this.mValue = value;
        this.mFormat = (format == null ? DEFAULT_FORMAT : format);
        invalidate();
    }

    private float getRatio(){
        return mProgress/mMax;
    }

    public void setProgress(int progress){
        if(progress<mMax){
            this.mProgress = progress;
        }else{
            this.mProgress = mMax;
        }
        mValue = mProgress * mValueRatio;
        invalidate();
    }


    //-------------------


    public int getNormalColor() {
        return mNormalColor;
    }

    public void setNormalColor(int normalColor) {
        this.mNormalColor = normalColor;
    }

    public int getProgressColor() {
        return mProgressColor;
    }

    public void setProgressColor(int progressColor) {
        this.mProgressColor = progressColor;
    }

    public int getLabelTextColor() {
        return mLabelTextColor;
    }

    public void setLabelTextColor(int labelTextColor) {
        this.mLabelTextColor = labelTextColor;
    }

    public int getTextColor() {
        return mTextColor;
    }

    public void setTextColor(int textColor) {
        this.mTextColor = textColor;
    }

    public float getStrokeWidth() {
        return mStrokeWidth;
    }

    public void setStrokeWidth(float strokeWidth) {
        this.mStrokeWidth = strokeWidth;
    }

    public int getStartAngle() {
        return mStartAngle;
    }

    public void setStartAngle(int startAngle) {
        this.mStartAngle = startAngle;
    }

    public int getSweepAngle() {
        return mSweepAngle;
    }

    public void setSweepAngle(int sweepAngle) {
        this.mSweepAngle = sweepAngle;
    }

    public float getMax() {
        return mMax;
    }

    public void setMax(float max) {
        setMaxAndValue(max,max);
    }

    public void setMaxAndValue(float max ,float maxValue){
        this.mMax = max;
        this.mMaxValue = maxValue;
        this.mValueRatio = maxValue / max;
    }

    public float getProgress() {
        return mProgress;
    }

    public void setProgress(float progress) {
        if(progress<mMax){
            this.mProgress = progress;
        }else{
            this.mProgress = mMax;
        }

    }

    public float getSpace() {
        return mSpace;
    }

    public void setSpace(float space) {
        this.mSpace = space;
    }

    public float getArrow() {
        return mArrow;
    }

    public void setArrow(float arrow) {
        this.mArrow = arrow;
    }

    public int getDuration() {
        return mDuration;
    }

    public void setDuration(int duration) {
        this.mDuration = duration;
    }

    public String getLabelText() {
        return mLabelText;
    }

    public void setLabelText(String labelText) {
        this.mLabelText = labelText;
    }

    public void setLabelText(int resId) {
        this.mLabelText = getResources().getString(resId);
    }

    public float getValue(){
        return mValue;
    }

    public float getTextSize() {
        return mTextSize;
    }

    public void setTextSize(float textSize) {
        this.mTextSize = textSize;
    }

    public float getLabelTextSize() {
        return mLabelTextSize;
    }

    public void setLabelTextSize(float labelTextSize) {
        this.mLabelTextSize = labelTextSize;
    }

    public float getTextOffsetY() {
        return mTextOffsetY;
    }

    public void setTextOffsetY(float textOffsetY) {
        this.mTextOffsetY = textOffsetY;
    }

    public String getFormat() {
        return mFormat;
    }

    public void setFormat(String format) {
        this.mFormat = format;
    }

    public boolean isShowLabel() {
        return isShowLabel;
    }

    public void setShowLabel(boolean showLabel) {
        isShowLabel = showLabel;
    }

    public boolean isShowText() {
        return isShowText;
    }

    public void setShowText(boolean showText) {
        isShowText = showText;
    }

    public void setOnUpdateListener(OnUpdateListener onUpdateListener) {
        this.mOnUpdateListener = onUpdateListener;
    }

    /**
     *
     * @param decimals
     * @return  "%1$." + decimals + "f"
     */
    public static String getDecimalFormat(int decimals){
        return "%1$." + decimals + "f";
    }

}
