package com.charlesma.elebtn.addbtn;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.charlesma.elebtn.R;
import com.github.florent37.viewanimator.AnimationListener;
import com.github.florent37.viewanimator.ViewAnimator;

public class AddButton extends View implements View.OnClickListener {


    private Paint paint;
    private Paint addPaint;
    private Rect textRect = new Rect();
    private RectF rectf = new RectF();
    private Path addPath = new Path();
    private Paint textPaint;

    private AddButton.AnimListner animListner;

    interface AnimListner {
        void onCollapseAnimStop();
    }

    void setAnimListner(AddButton.AnimListner animListner) {
        this.animListner = animListner;
    }

    public AddButton(Context context) {
        super(context);
        setOnClickListener(this);
        initPaint();
    }

    public AddButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setOnClickListener(this);
        initPaint();
    }

    public AddButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOnClickListener(this);
        initPaint();
    }

    private void initPaint() {
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        paint.setColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(ViewUtils.sp2px(getContext(), 12));
        textPaint.setColor(Color.WHITE);
        textPaint.setAntiAlias(true);
        initAdd();
    }

    public void setMainColor(@ColorInt int color) {
        paint.setColor(color);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        initAdd();
    }

    private void initAdd() {
        addPaint = new Paint();
        addPaint.setStrokeWidth(getHeight() / 18);
        addPaint.setStyle(Paint.Style.STROKE);
        addPaint.setAntiAlias(true);
        addPaint.setColor(Color.WHITE);

        int height = getHeight();
        int padding = getHeight() / 4;
        addPath.moveTo(padding, height / 2);
        addPath.lineTo(height - padding, height / 2);
        addPath.moveTo(height / 2, padding);
        addPath.lineTo(height / 2, height - padding);
    }

    boolean isCircle = true;

    public void setCircle(boolean circle) {
        isCircle = circle;
        expendAnim();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        draw_o2c(canvas); //绘制圆形（实际使用了圆角矩形）
        if (isCircle) {
            canvas.drawPath(addPath, addPaint);
        } else {
            drawText(canvas);
        }
    }

    private void drawText(Canvas canvas) {
        textRect.left = 0;
        textRect.top = 0;
        textRect.right = getWidth();
        textRect.bottom = getHeight();
        Paint.FontMetricsInt fontMetrics = textPaint.getFontMetricsInt();
        int baseline = (textRect.bottom + textRect.top - fontMetrics.bottom - fontMetrics.top) / 2;
        canvas.drawText("加入购物车", textRect.centerX(), baseline, textPaint);
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
        if (isCircle) { //如果需要直接设置加入购物车这个状态，默认的宽度要够
            mWidth = (int) (ViewUtils.dip2px(getContext(), 22) * 3.5f);
        }
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
        int minRule = (getHeight() >= getWidth()) ? getWidth() : getHeight();
        int maxDimen = (getHeight() <= getWidth()) ? getWidth() : getHeight();
        rectf.left = minRule / 40;
        rectf.top = minRule / 40;
        rectf.right = getWidth() - minRule / 40;
        rectf.bottom = getHeight() - minRule / 40;
        canvas.drawRoundRect(rectf, maxDimen, maxDimen, paint); //rx和ry分别代表形成圆角所需要的椭圆的x和y轴半径,保证rx ry大于宽高可以保证绘制出圆形
    }


    @Override
    public void onClick(View v) {
        if (animListner != null) { //这个加号控件的点击监听
            if (!isCircle) {       //当前非圆形
                collapseAnim();
            } else {               //本来就是圆的
                animListner.onCollapseAnimStop();    //动画监听停止
            }
        }
    }


    public void expendAnim() {
        isCircle = false;
        setClickable(false);
        ViewAnimator.animate(this)
                .width(getWidth(), ViewUtils.dip2px(getContext(), 75))
                .duration(300)
                .onStop(new AnimationListener.Stop() {
                    @Override
                    public void onStop() {
                        setClickable(true);
                        invalidate();
                    }
                })
                .start();
    }

    public void collapseAnim() {
        setClickable(false);
        ViewAnimator.animate(this)
                .width(getWidth(), getHeight())
                .duration(300)
                .onStop(new AnimationListener.Stop() {
                    @Override
                    public void onStop() {
                        isCircle = true;                //变为圆形了
                        invalidate();                   //重新渲染一下
                        setClickable(true);             //可以点击了
                        if (animListner != null) {
                            animListner.onCollapseAnimStop();    //动画监听停止
                        }
                    }
                })
                .start();
    }
}
