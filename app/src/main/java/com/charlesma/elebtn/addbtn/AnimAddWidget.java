package com.charlesma.elebtn.addbtn;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.charlesma.elebtn.R;
import com.github.florent37.viewanimator.AnimationListener;
import com.github.florent37.viewanimator.ViewAnimator;

public class AnimAddWidget extends FrameLayout {

    private AddButton addButton;
    private MinusButton subButton;
    private TextView tvCount;
    private int count = 0;

    private onOyoAddViewClickLisenter lisenter;
    private int mainColor;
    private boolean showAddShoppingCar;
    private Integer countFromXml;
    private boolean showState;

    public interface onOyoAddViewClickLisenter {

        void onAddClick(int count);

        void onSubClick(int count);
    }

    public void setAddViewLisenter(onOyoAddViewClickLisenter lisenter) {
        this.lisenter = lisenter;
    }

    public AnimAddWidget(@NonNull Context context) {
        super(context);
    }

    public AnimAddWidget(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public AnimAddWidget(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(final Context context, AttributeSet attrs) {
        View inflate = inflate(context, R.layout.view_anim_add_widget, this);//整个布局内容填充
        addButton = inflate.findViewById(R.id.add_button);                 //加号view
        subButton = inflate.findViewById(R.id.sub_button);                    //减号view
        tvCount = inflate.findViewById(R.id.tv_count);                    //数量view

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AnimAddWidget);
        mainColor = a.getColor(R.styleable.AnimAddWidget_main_color, getResources().getColor(R.color.colorPrimary));
        showAddShoppingCar = a.getBoolean(R.styleable.AnimAddWidget_show_add_shopping_car, true);
        countFromXml = a.getInteger(R.styleable.AnimAddWidget_show_number, 0);
        showState = a.getBoolean(R.styleable.AnimAddWidget_state_show_add_shopping, false);
        a.recycle(); //以上都是自定义属性，AddWidget_circle_anim控制是否在数量为1时圆形加号变形为圆角矩形并显示"加入购物车"，AddWidget_sub_anim控制是否有减法伸缩动画

        initSetting();
        addButton.setAnimListner(new AddButton.AnimListner() {
            @Override
            public void onCollapseAnimStop() {
                if (count == 0) {
                    addWidgetExpendAnim();
                }
                count++;
                tvCount.setText(String.valueOf(count));
                if (lisenter != null) {
                    lisenter.onAddClick(count);
                }
            }
        });

        subButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (count <= 0) return; //防止连续点击出现小于0的情况
                if (count == 1) {
                    count--;
                    addWidgetCollapseAnim();
                } else {
                    count--;
                    tvCount.setText(String.valueOf(count));
                }
                if (lisenter != null) {
                    lisenter.onSubClick(count);
                }
            }
        });

    }

    private void initSetting() {
        setCount(countFromXml);
        setState(showState);
        addButton.setMainColor(mainColor);
        subButton.setMainColor(mainColor);
    }

    private void setState(boolean state) {
        if (state) {
            addButton.setCircle(state);
//            tvCount.setAlpha(0);
//            subButton.setAlpha(0);
//            addButton.setCircle(false);
        }

    }

    public void setCount(int count) {
        this.count = count;
        if (count > 0) {
            tvCount.setText(String.valueOf(count));
            subButton.setAlpha(1);
            tvCount.setAlpha(1);
        }
    }

    public void addWidgetCollapseAnim() {
        addButton.setClickable(false);
        subButton.setClickable(false);

        ViewAnimator.animate(subButton)
                .translationX(0, addButton.getLeft() - subButton.getLeft())
                .rotation(-360)
                .alpha(255, 0)
                .duration(300)
                .interpolator(new AccelerateInterpolator())
                .andAnimate(tvCount)
                .translationX(0, addButton.getLeft() - tvCount.getLeft())
                .rotation(-360)
                .alpha(255, 0)
                .interpolator(new AccelerateInterpolator())
                .duration(300)
                .onStop(new AnimationListener.Stop() {
                    @Override
                    public void onStop() {
                        addButton.setClickable(true);
                        subButton.setClickable(true);
                        if (showAddShoppingCar) {
                            addButton.expendAnim();
                        }
                    }
                })
                .start()
        ;
    }

    public void addWidgetExpendAnim() {
        addButton.setClickable(false);
        subButton.setClickable(false);
        ViewAnimator.animate(subButton)
                .translationX(addButton.getLeft() - subButton.getLeft(), 0)
                .rotation(360)
                .alpha(0, 255)
                .duration(300)
                .interpolator(new DecelerateInterpolator())
                .andAnimate(tvCount)
                .translationX(addButton.getLeft() - tvCount.getLeft(), 0)
                .rotation(360)
                .alpha(0, 255)
                .interpolator(new DecelerateInterpolator())
                .duration(300)
                .onStop(new AnimationListener.Stop() {
                    @Override
                    public void onStop() {
                        addButton.setClickable(true);
                        subButton.setClickable(true);
                    }
                })
                .start();
    }

}
