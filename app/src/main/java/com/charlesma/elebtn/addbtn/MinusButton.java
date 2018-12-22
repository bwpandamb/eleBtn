package com.charlesma.elebtn.addbtn;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.charlesma.elebtn.R;

public class MinusButton extends View {

    private Paint paint;
    private Paint addPaint;
    private RectF rectf = new RectF();
    private Path addPath = new Path();
    private int dimenRule;

    public MinusButton(Context context) {
        super(context);
        initPaint();
    }

    public MinusButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initPaint();
    }

    public MinusButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
    }

    private void initPaint() {
        paint = new Paint();
        addPaint = new Paint();

        initPaintCommonSet();
    }

    private void initPaintCommonSet() {
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        paint.setColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        addPaint.setStyle(Paint.Style.STROKE);
        addPaint.setAntiAlias(true);
        addPaint.setColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
    }

    public void setMainColor(@ColorInt int color) {
        paint.setColor(color);
        addPaint.setColor(color);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        initPaintSize(); //因为要根据设置的尺寸来配置一些大小，因此在这里调用才能保证拿到getHeight() 和 getWidth() 不为零
    }


    private void initPaintSize() {
        dimenRule = (getHeight() > getWidth()) ? getWidth() : getHeight();
        paint.setStrokeWidth(dimenRule / 20);
        addPaint.setStrokeWidth(dimenRule / 20);

        int padding = dimenRule / 4;
        addPath.moveTo(padding, dimenRule / 2);
        addPath.lineTo(dimenRule - padding, dimenRule / 2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        draw_o2c(canvas); //绘制圆形（实际使用了圆角矩形）
        canvas.drawPath(addPath, addPaint);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // 重写下面的原因是，自定义View的宽高会占全屏，按如下处理即可，但是需要设定一个默认大小
        // 获取宽-测量规则的模式和大小
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        // 获取高-测量规则的模式和大小
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        // 设置wrap_content的默认宽 / 高值
        // 默认宽/高的设定并无固定依据,根据需要灵活设置
        int mWidth = ViewUtils.dip2px(getContext(), 22);
        int mHeight = ViewUtils.dip2px(getContext(), 22);

        // 当布局参数设置为wrap_content时，设置默认值
        if (getLayoutParams().width == ViewGroup.LayoutParams.WRAP_CONTENT && getLayoutParams().height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            setMeasuredDimension(mWidth, mHeight);
            // 宽 / 高任意一个布局参数为= wrap_content时，都设置默认值
        } else if (getLayoutParams().width == ViewGroup.LayoutParams.WRAP_CONTENT) {
            setMeasuredDimension(mWidth, heightSize);
        } else if (getLayoutParams().height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            setMeasuredDimension(widthSize, mHeight);
        }
    }

    private void draw_o2c(Canvas canvas) {
        rectf.left = 0;
        rectf.top = 0;
        rectf.right = dimenRule;
        rectf.bottom = dimenRule;
        canvas.drawCircle(dimenRule / 2, dimenRule / 2, dimenRule / 2 - dimenRule / 20, paint);
    }


}
