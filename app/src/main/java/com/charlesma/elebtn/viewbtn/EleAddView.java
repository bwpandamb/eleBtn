package com.charlesma.elebtn.viewbtn;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.charlesma.elebtn.R;

import java.util.ArrayList;
import java.util.Collection;


public class EleAddView extends View {

    private final static int STATE_NONE = 0;
    private final static int STATE_MOVE = 1;
    private final static int STATE_MOVE_OVER = 2;
    private final static int STATE_ROTATE = 3;
    private final static int STATE_ROTATE_OVER = 4;

    private final static int STATE_NORMAL = 1001;
    private final static int STATE_CLASSICAL = 1002;
    private int TYPE_STATE = STATE_CLASSICAL;

    private final static int DEFAULT_DURATION = 500;
    private final static String DEFAULT_SHOPPING_TEXT = "加入购物车";
    private int MAX_WIDTH;
    private int MAX_HEIGHT;

    //是否是向前状态（= = 名字不好取，意思就是区分向前和回退状态）
    private boolean mIsForward = true;
    //购买数量
    private int mNum = 0;
    //展示文案
    private String mShoppingText;
    //当前状态
    private int mState = STATE_NONE;


    //属性值
    private int mWidth = 0;
    private int mAngle = 0;
    private int mTextPosition = 0;
    private int mMinusBtnPosition = 0;
    private int mAlpha = 0;

    private Paint mPaintBg;
    private Paint mPaintMinus;
    private Paint mPaintText;
    private Paint mPaintNum;
    private int mDuration;

    private EleAddView.ShoppingClickListener mShoppingClickListener;

    public EleAddView(Context context) {
        this(context, null);
    }

    public EleAddView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EleAddView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(MAX_WIDTH, MAX_HEIGHT);    //根据上面计算的尺寸，定义这个view的尺寸
    }

    private void init(Context context, AttributeSet attrs) {
        //定义了四个参数。分别为动画时间，文字内容，文字大小，背景色
        TypedArray typeArray = getContext().obtainStyledAttributes(attrs, R.styleable.EleAddView);
        mDuration = typeArray.getInt(R.styleable.EleAddView_duration, DEFAULT_DURATION);
        mShoppingText = TextUtils.isEmpty(typeArray.getString(R.styleable.EleAddView_text_content)) ? DEFAULT_SHOPPING_TEXT : typeArray.getString(R.styleable.EleAddView_text_content);
        //展示文案大小
        int textSize = (int) typeArray.getDimension(R.styleable.EleAddView_text_size, sp2px(16));
        //背景色
        int bgColor = typeArray.getColor(R.styleable.EleAddView_bg_color, ContextCompat.getColor(getContext(), R.color.ele_blue));
        boolean typeModel = typeArray.getBoolean(R.styleable.EleAddView_circle_model, true);
        typeArray.recycle();

        if (typeModel) {
            TYPE_STATE = STATE_NORMAL;
        } else {
            TYPE_STATE = STATE_CLASSICAL;  //包含展开加入购物车的模式
        }

        initPaint(textSize, bgColor);
        initSize(textSize);

    }

    private void initSize(int textSize) {
        MAX_WIDTH = getTextWidth(mPaintText, mShoppingText) / 5 * 8;    //根据文字长度确定控件的最大宽度
        MAX_HEIGHT = textSize * 2;                                      //根据文字的size确定最大高度

        if (MAX_WIDTH / (float) MAX_HEIGHT < 3.5) {                     //尽量保证宽高比为3.5
            MAX_WIDTH = (int) (MAX_HEIGHT * 3.5);
        }

        mTextPosition = MAX_WIDTH / 2;                                  //文字位置？在最大宽度一半处
        mMinusBtnPosition = MAX_HEIGHT / 2;                             //减号按钮位置在最大高度的一半
    }

    /**
     * 设置购买数量
     *
     * @param num 购买数量
     */
    public void setTextNum(int num) {
        if (TYPE_STATE == STATE_NORMAL) {
            if (num == 0) {
                mAlpha = 0;
                mNum = num;
                invalidate();
            } else if (num > 0) {
                mNum = num;
                mAlpha = 255;
                invalidate();
            }

        } else {
            if (num == 0) {
                mNum = num;
                mState = STATE_NONE;
                invalidate();
            } else if (num > 0) {
                mNum = num;
                mState = STATE_ROTATE_OVER;
                invalidate();
            }
        }
    }

    public void setOnShoppingClickListener(EleAddView.ShoppingClickListener shoppingClickListener) {
        this.mShoppingClickListener = shoppingClickListener;
    }

    public interface ShoppingClickListener {
        void onAddClick(int num);

        void onMinusClick(int num);
    }

    private void initPaint(int textSize, int bgColor) {
        mPaintBg = new Paint();                     //背景笔
        mPaintBg.setColor(bgColor);
        mPaintBg.setStyle(Paint.Style.FILL);
        mPaintBg.setAntiAlias(true);
        mPaintMinus = new Paint();                  //减号笔
        mPaintMinus.setColor(bgColor);
        mPaintMinus.setStyle(Paint.Style.STROKE);
        mPaintMinus.setAntiAlias(true);
        mPaintMinus.setStrokeWidth(textSize / 6);
        mPaintText = new Paint();                   //文字笔
        mPaintText.setColor(Color.WHITE);
        mPaintText.setStrokeWidth(textSize / 6);
        mPaintText.setTextSize(textSize);
        mPaintText.setAntiAlias(true);
        mPaintNum = new Paint();                    //数字笔
        mPaintNum.setColor(Color.BLACK);
        mPaintNum.setTextSize(textSize / 3 * 4);
        mPaintNum.setStrokeWidth(textSize / 6);
        mPaintNum.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (TYPE_STATE == STATE_NORMAL) {
            mPaintMinus.setAlpha(mAlpha);
            mPaintNum.setAlpha(mAlpha);
            drawMinusBtn(canvas, mAngle);
            drawNumText(canvas);
            drawAddBtn(canvas);
        } else if (TYPE_STATE == STATE_CLASSICAL) {
            if (mState == STATE_NONE) {                 //无状态，
                drawBgMove(canvas);
                drawShoppingText(canvas);
            } else if (mState == STATE_MOVE) {          //移动状态
                drawBgMove(canvas);
            } else if (mState == STATE_MOVE_OVER) {     //移动结束状态
                mState = STATE_ROTATE;
                if (mIsForward) {
                    drawAddBtn(canvas);
                    startRotateAnim();
                } else {
                    drawBgMove(canvas);
                    drawShoppingText(canvas);
                    mState = STATE_NONE;
                    mIsForward = true;
                    mNum = 0;
                }
            } else if (mState == STATE_ROTATE) {        //旋转状态
                mPaintMinus.setAlpha(mAlpha);
                mPaintNum.setAlpha(mAlpha);
                drawMinusBtn(canvas, mAngle);
                drawNumText(canvas);
                drawAddBtn(canvas);
            } else if (mState == STATE_ROTATE_OVER) {   //旋转结束状态
                drawMinusBtn(canvas, mAngle);
                drawNumText(canvas);
                drawAddBtn(canvas);
                if (!mIsForward) {
                    startMoveAnim();
                }
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (TYPE_STATE == STATE_NORMAL) {
                    if (isPointInCircle(new PointF(event.getX(), event.getY()), new PointF(MAX_HEIGHT / 2, MAX_HEIGHT / 2), MAX_HEIGHT / 2)) {
                        if (mNum == 1) {
                            mIsForward = false;
                            startRotateAnim();
                        } else {
                            invalidate();
                        }

                        if (mNum > 0) {
                            mNum--;
                        }

                        if (mShoppingClickListener != null) {
                            mShoppingClickListener.onMinusClick(mNum);
                        }
                    } else if (isPointInCircle(new PointF(event.getX(), event.getY()), new PointF(MAX_WIDTH - MAX_HEIGHT / 2, MAX_HEIGHT / 2), MAX_HEIGHT / 2)) {
                        if (mNum == 0) {
                            mIsForward = true;
                            startRotateAnim();
                        } else {
                            invalidate();
                        }

                        mNum++;
                        if (mShoppingClickListener != null) {
                            mShoppingClickListener.onAddClick(mNum);
                        }
                    }

                } else if (TYPE_STATE == STATE_CLASSICAL) {
                    if (mState == STATE_NONE) {
                        mNum++;
                        startMoveAnim();
                        if (mShoppingClickListener != null) {
                            mShoppingClickListener.onAddClick(mNum);
                        }
                    } else if (mState == STATE_ROTATE_OVER) {
                        if (isPointInCircle(new PointF(event.getX(), event.getY()), new PointF(MAX_WIDTH - MAX_HEIGHT / 2, MAX_HEIGHT / 2), MAX_HEIGHT / 2)) {
                            if (mNum > 0) {
                                mNum++;
                                mIsForward = true;
                                if (mShoppingClickListener != null) {
                                    mShoppingClickListener.onAddClick(mNum);
                                }
                            }
                            invalidate();
                        } else if (isPointInCircle(new PointF(event.getX(), event.getY()), new PointF(MAX_HEIGHT / 2, MAX_HEIGHT / 2), MAX_HEIGHT / 2)) {
                            if (mNum > 1) {
                                mNum--;
                                if (mShoppingClickListener != null) {
                                    mShoppingClickListener.onMinusClick(mNum);
                                }
                                invalidate();
                            } else {
                                if (mShoppingClickListener != null) {
                                    mShoppingClickListener.onMinusClick(0);
                                }
                                mState = STATE_ROTATE;
                                mIsForward = false;
                                startRotateAnim();
                            }
                        }
                    }
                }

                break;
        }
        return super.onTouchEvent(event);

    }

    /**
     * 开始移动动画
     */
    private void startMoveAnim() {
        mState = STATE_MOVE;                    //标示一下进入了移动动画的状态
        ValueAnimator valueAnimator;
        if (mIsForward) {//判断是否展开状态
            valueAnimator = ValueAnimator.ofInt(0, MAX_WIDTH - MAX_HEIGHT);   //数值由  0 -> 最大宽度 - 最大高度
        } else {
            valueAnimator = ValueAnimator.ofInt(MAX_WIDTH - MAX_HEIGHT, 0);
        }
        valueAnimator.setDuration(mDuration);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {

                mWidth = (Integer) valueAnimator.getAnimatedValue();
                if (mIsForward) {
                    if (mWidth == MAX_WIDTH - MAX_HEIGHT) {
                        mState = STATE_MOVE_OVER;
                    }
                } else {
                    if (mWidth == 0) {
                        mState = STATE_MOVE_OVER;
                    }
                }

                invalidate();
            }
        });
        valueAnimator.start();
    }

    /**
     * 开始旋转动画
     */
    private void startRotateAnim() {

        Collection<Animator> animatorList = new ArrayList<>();

        ValueAnimator animatorTextRotate;
        if (mIsForward) {
            animatorTextRotate = ValueAnimator.ofInt(0, 360);
        } else {
            animatorTextRotate = ValueAnimator.ofInt(360, 0);
        }
        animatorTextRotate.setDuration(mDuration);
        animatorTextRotate.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {

                mAngle = (Integer) valueAnimator.getAnimatedValue();

                if (mIsForward) {
                    if (mAngle == 360) {
                        mState = STATE_ROTATE_OVER;
                    }
                } else {
                    if (mAngle == 0) {
                        mState = STATE_ROTATE_OVER;
                    }
                }

            }
        });

        animatorList.add(animatorTextRotate);

        ValueAnimator animatorAlpha;
        if (mIsForward) {
            animatorAlpha = ValueAnimator.ofInt(0, 255);
        } else {
            animatorAlpha = ValueAnimator.ofInt(255, 0);
        }
        animatorAlpha.setDuration(mDuration);
        animatorAlpha.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {

                mAlpha = (Integer) valueAnimator.getAnimatedValue();

                if (mIsForward) {
                    if (mAlpha == 255) {
                        mState = STATE_ROTATE_OVER;
                    }
                } else {
                    if (mAlpha == 0) {
                        mState = STATE_ROTATE_OVER;
                    }
                }

            }
        });

        animatorList.add(animatorAlpha);

        ValueAnimator animatorTextMove;
        if (mIsForward) {
            animatorTextMove = ValueAnimator.ofInt(MAX_WIDTH - MAX_HEIGHT / 2, MAX_WIDTH / 2);
        } else {
            animatorTextMove = ValueAnimator.ofInt(MAX_WIDTH / 2, MAX_WIDTH - MAX_HEIGHT / 2);
        }
        animatorTextMove.setDuration(mDuration);
        animatorTextMove.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {

                mTextPosition = (Integer) valueAnimator.getAnimatedValue();

                if (mIsForward) {
                    if (mTextPosition == MAX_WIDTH / 2) {
                        mState = STATE_ROTATE_OVER;
                    }
                } else {
                    if (mTextPosition == MAX_WIDTH - MAX_HEIGHT / 2) {
                        mState = STATE_ROTATE_OVER;
                    }
                }

            }
        });

        animatorList.add(animatorTextMove);

        ValueAnimator animatorBtnMove;
        if (mIsForward) {
            animatorBtnMove = ValueAnimator.ofInt(MAX_WIDTH - MAX_HEIGHT / 2, MAX_HEIGHT / 2);
        } else {
            animatorBtnMove = ValueAnimator.ofInt(MAX_HEIGHT / 2, MAX_WIDTH - MAX_HEIGHT / 2);
        }
        animatorBtnMove.setDuration(mDuration);
        animatorBtnMove.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {

                mMinusBtnPosition = (Integer) valueAnimator.getAnimatedValue();

                if (mIsForward) {
                    if (mMinusBtnPosition == MAX_HEIGHT / 2) {
                        mState = STATE_ROTATE_OVER;
                    }
                } else {
                    if (mMinusBtnPosition == MAX_WIDTH - MAX_HEIGHT / 2) {
                        mState = STATE_ROTATE_OVER;
                    }
                }

                invalidate();
            }
        });

        animatorList.add(animatorBtnMove);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(mDuration);
        animatorSet.playTogether(animatorList);
        animatorSet.start();
    }

    /**
     * 绘制加号按钮
     *
     * @param canvas 画板
     */
    private void drawAddBtn(Canvas canvas) {
        //画圆，圆心位置（x =（最右边位置 + 高度的1/2），y = (高度的1/2)），半径 = 高度的1/2
        canvas.drawCircle(MAX_WIDTH - MAX_HEIGHT / 2, MAX_HEIGHT / 2, MAX_HEIGHT / 2, mPaintBg);
        //绘制竖线 , x 保持中点，y 1/4高度绘制到3/4高度
        canvas.drawLine(MAX_WIDTH - MAX_HEIGHT / 2, MAX_HEIGHT / 4, MAX_WIDTH - MAX_HEIGHT / 2, MAX_HEIGHT / 4 * 3, mPaintText);
        //绘制横线 , y 保持中点，x 1/4长度绘制到3/4长度
        canvas.drawLine(MAX_WIDTH - MAX_HEIGHT / 2 - MAX_HEIGHT / 4, MAX_HEIGHT / 2, MAX_WIDTH - MAX_HEIGHT / 4, MAX_HEIGHT / 2, mPaintText);
    }

    /**
     * 绘制减号按钮
     *
     * @param canvas 画板
     * @param angle  旋转角度
     */
    private void drawMinusBtn(Canvas canvas, float angle) {
        if (angle != 0) {
            canvas.rotate(angle * 2, mMinusBtnPosition, MAX_HEIGHT / 2);
        }
        canvas.drawCircle(mMinusBtnPosition, MAX_HEIGHT / 2, MAX_HEIGHT / 2 - MAX_HEIGHT / 20, mPaintMinus);
        canvas.drawLine(mMinusBtnPosition - MAX_HEIGHT / 4, MAX_HEIGHT / 2, mMinusBtnPosition + MAX_HEIGHT / 4, MAX_HEIGHT / 2, mPaintMinus);
        if (angle != 0) {
            canvas.rotate(-angle * 2, mMinusBtnPosition, MAX_HEIGHT / 2);
        }
    }

    /**
     * 绘制移动的背景
     *
     * @param canvas 画板
     */
    private void drawBgMove(Canvas canvas) {
        //绘制半圆，第一个RectF代表外轮廓的矩形区域，90代表起始角度，180是扫过的角度，true表示要绘制圆心（这里因为是填充的，所以用不用都可以）
        //mWidth是一个变动的值，后面根据属性动画改变这个值来实现动画，第一个半圆的位置和矩形的大小是要改变的，第二个半圆一直不移动
        //背景收缩就是：1、第一个半圆右边移动到第二个半圆的左边点；2、矩形长度由（总长 - 总高）的长度逐渐变为0，3、前两步骤完成后两个半圆合并
        canvas.drawArc(new RectF(mWidth, 0, mWidth + MAX_HEIGHT, MAX_HEIGHT), 90, 180, true, mPaintBg);
        canvas.drawRect(new RectF(mWidth + MAX_HEIGHT / 2, 0, MAX_WIDTH - MAX_HEIGHT / 2, MAX_HEIGHT), mPaintBg);
        canvas.drawArc(new RectF(MAX_WIDTH - MAX_HEIGHT, 0, MAX_WIDTH, MAX_HEIGHT), 90, -180, true, mPaintBg);
    }

    /**
     * 绘制购物车文案
     *
     * @param canvas 画板
     */
    private void drawShoppingText(Canvas canvas) {
        //绘制文字，重点在要保证文字处于背景的中心
        canvas.drawText(mShoppingText, MAX_WIDTH / 2 - getTextWidth(mPaintText, mShoppingText) / 2f, MAX_HEIGHT / 2 + getTextHeight(mShoppingText, mPaintText) / 2f, mPaintText);
    }

    /**
     * 绘制购买数量
     *
     * @param canvas 画板
     */
    private void drawNumText(Canvas canvas) {
        drawText(canvas, String.valueOf(mNum), mTextPosition - getTextWidth(mPaintNum, String.valueOf(mNum)) / 2f, MAX_HEIGHT / 2 + getTextHeight(String.valueOf(mNum), mPaintNum) / 2f, mPaintNum, mAngle);
    }

    /**
     * 绘制Text带角度
     *
     * @param canvas 画板
     * @param text   文案
     * @param x      x坐标
     * @param y      y坐标
     * @param paint  画笔
     * @param angle  旋转角度
     */
    private void drawText(Canvas canvas, String text, float x, float y, Paint paint, float angle) {
        if (angle != 0) {
            canvas.rotate(angle * 2, mTextPosition, MAX_HEIGHT / 2);
        }
        canvas.drawText(text, x, y, paint);
        if (angle != 0) {
            canvas.rotate(-angle * 2, mTextPosition, MAX_HEIGHT / 2);
        }
    }


    /**
     * 判断点是否在圆内
     *
     * @param pointF 待确定点
     * @param circle 圆心
     * @param radius 半径
     * @return true在圆内  根据圆距离圆心的距离判断，这里比较的距离的平方
     */
    private boolean isPointInCircle(PointF pointF, PointF circle, float radius) {
        return Math.pow((pointF.x - circle.x), 2) + Math.pow((pointF.y - circle.y), 2) <= Math.pow(radius, 2);
    }

    private int sp2px(float spValue) {
        final float fontScale = getContext().getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    //获取Text高度
    private int getTextHeight(String str, Paint paint) {
        Rect rect = new Rect();
        paint.getTextBounds(str, 0, str.length(), rect);
        return (int) (rect.height() / 33f * 29);
    }

    //获取Text宽度
    private int getTextWidth(Paint paint, String str) {
        int iRet = 0;
        if (str != null && str.length() > 0) {
            int len = str.length();
            float[] widths = new float[len];
            paint.getTextWidths(str, widths);
            for (int j = 0; j < len; j++) {
                iRet += (int) Math.ceil(widths[j]);
            }
        }
        return iRet;
    }
}
