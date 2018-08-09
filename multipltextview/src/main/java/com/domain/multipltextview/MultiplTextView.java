package com.domain.multipltextview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;


/**
 * Created by domain on 2018-08-06.
 * Email:sunlongyue@foxmail.com
 * describe:
 */

public class MultiplTextView extends AppCompatTextView {

    /*
    自定义view的几个方法的执行顺序:
        构造方法->measure->onSizeChanged->onDraw
    * */
    int width = 0;
    int height = 0;
    private Paint mPaint;
    private Rect mBounds = new Rect();

    private LinearGradient mLinearGradient;//渐变渲染器
    private Matrix mGradientMatrix;
    private int mTranslate;
    Paint.FontMetricsInt fontMetricsInt;

    Boolean bRunText = false;
    Boolean bRemoveDefaultPadding = false;
    String fontPath = null;
    Boolean bGradient = false;
    int gradientStartColor;
    int gradientCenterColor;
    int gradietendColor;
    Typeface typeface;

    public MultiplTextView(Context context) {
        super(context);
    }

    public MultiplTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttributes(context, attrs);
    }

    public MultiplTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttributes(context, attrs);
    }

    //java 代码块 ，先于构造函数执行
    {
        mPaint = getPaint(); //获取textview的画笔 获取画笔属性

    }

    @Override
    protected void onDraw(Canvas canvas) {
        init();
        option(canvas);
        drawText(canvas);
//        super.onDraw(canvas);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);
        if (bRemoveDefaultPadding) {
            calculateTextParams();
            setMeasuredDimension(mBounds.right - mBounds.left, -mBounds.top + mBounds.bottom); //设置view的宽高为text的宽高
        }
    }

    private void init() {
        mLinearGradient = new LinearGradient(0, 0, getMeasuredWidth(), 0,
                new int[]{gradientStartColor, gradientCenterColor, gradietendColor},
                null, Shader.TileMode.MIRROR);
        mGradientMatrix = new Matrix();
        Log.e("sss", "gradientStartColor:" + gradientStartColor + "gradietendColor:" + gradietendColor);
    }

    //初始化属性
    private void initAttributes(Context context, AttributeSet attrs) {
        //从xml的属性中获取到字体颜色与string
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.MultiplTextView);
        bRunText = ta.getBoolean(R.styleable.MultiplTextView_runText, false);
        bGradient = ta.getBoolean(R.styleable.MultiplTextView_gradient, false);
        bRemoveDefaultPadding = ta.getBoolean(R.styleable.MultiplTextView_removeDefaultPadding, false);
        gradientStartColor = ta.getColor(R.styleable.MultiplTextView_gradientStartColor, getCurrentTextColor());//默认为当前颜色
        gradientCenterColor = ta.getColor(R.styleable.MultiplTextView_gradientCenterColor, getCurrentTextColor());//默认为当前颜色
        gradietendColor = ta.getColor(R.styleable.MultiplTextView_gradientEndColor, Color.BLACK);
        fontPath = ta.getString(R.styleable.MultiplTextView_textFont);
        if (!TextUtils.isEmpty(fontPath)) {
            typeface = Typeface.createFromAsset(getResources().getAssets(), fontPath); //获取字体
            mPaint.setTypeface(typeface);
        }
        ta.recycle();
    }

    //选择效果 执行对应方法
    private void option(Canvas canvas) {
        if (bGradient) {
            mPaint.setShader(mLinearGradient);
        }
        if (bRunText) {
            runText();
        }
    }

    //文字颜色滚动
    public void runText() {
        if (mGradientMatrix != null) {
            mTranslate += width / 5;
            if (mTranslate > 2 * width) {
                mTranslate = -width;
            }
            mGradientMatrix.setTranslate(mTranslate, 0);
            mLinearGradient.setLocalMatrix(mGradientMatrix);
            postInvalidateDelayed(100);
        }
    }


    //获取text宽高
    private String calculateTextParams() {
        final String text = getText().toString();
        final int textLength = text.length();
//        mPaint.setTextSize(getTextSize());
        mPaint.getTextBounds(text, 0, textLength, mBounds);
        if (textLength == 0) {
            mBounds.right = mBounds.left;
        }
        return text;
    }

    //用drawText方法画text
    private void drawText(Canvas canvas) {
        final String text = calculateTextParams();
        final int left = mBounds.left;
        final int bottom = mBounds.bottom;
        mBounds.offset(-mBounds.left, -mBounds.top);
        mPaint.setAntiAlias(true);
        mPaint.setColor(getCurrentTextColor());
        canvas.drawText(text, -left, mBounds.bottom - bottom, mPaint);
    }

}

