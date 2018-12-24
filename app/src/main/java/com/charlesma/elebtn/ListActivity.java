package com.charlesma.elebtn;

import android.animation.ValueAnimator;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.PointF;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.charlesma.CharlesMaListAdapter;
import com.charlesma.FoodModel;
import com.charlesma.elebtn.viewbtn.EleAddView;
import com.github.florent37.viewanimator.AnimationListener;
import com.github.florent37.viewanimator.ViewAnimator;

import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {

    private RecyclerView rcList;
    private TextView tvTarget;

    int[] targetLoc = new int[2];

    private RelativeLayout rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        tvTarget = (TextView) findViewById(R.id.tv_target);
        rootView = (RelativeLayout) findViewById(R.id.rootView);
        initList();
    }

    private void initList() {
        rcList = (RecyclerView) findViewById(R.id.rc_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rcList.setLayoutManager(layoutManager);
        CharlesMaListAdapter charlesMaListAdapter = new CharlesMaListAdapter(R.layout.item_list);
//        charlesMaListAdapter.setOnLoadMoreListener(() -> fetchHotelData(false), mRcvHotelList);
        rcList.setAdapter(charlesMaListAdapter);
        ArrayList<FoodModel> listData = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            listData.add(new FoodModel(i % 3, "这是第 " + i + " 个"));
        }
        charlesMaListAdapter.setNewData(listData);
        charlesMaListAdapter.setShopOnClickListtener(new CharlesMaListAdapter.ShopOnClickListtener() {
            @Override
            public void add(View view, int position) {
                //贝塞尔起始数据点
                int[] startPosition = new int[2];
                //贝塞尔结束数据点
                int[] endPosition = new int[2];
                //控制点
                int[] recyclerPosition = new int[2];

                view.getLocationInWindow(startPosition);
                tvTarget.getLocationInWindow(endPosition);
                rcList.getLocationInWindow(recyclerPosition);

                PointF startF = new PointF();
                PointF endF = new PointF();
                PointF controllF = new PointF();

                startF.x = startPosition[0] + view.getWidth() * 0.8f;
                startF.y = startPosition[1] - recyclerPosition[1];
                endF.x = endPosition[0] + tvTarget.getWidth() / 2;
                endF.y = endPosition[1] - recyclerPosition[1];
                controllF.x = endF.x;
                controllF.y = startF.y;

                Path path = new Path();
                path.moveTo(startF.x, startF.y);
                path.quadTo(controllF.x, controllF.y, endF.x, endF.y);

                final TextView textView = new TextView(ListActivity.this);
                textView.setBackgroundResource(R.drawable.circle_cahres);
                textView.setText("1");
                textView.setTextColor(Color.WHITE);
                textView.setGravity(Gravity.CENTER);

                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(50, 50);
                rootView.addView(textView, layoutParams);

                ViewAnimator.animate(textView)
                        .path(path)
                        .duration(500)
                        .interpolator(new AccelerateInterpolator())
                        .onStop(new AnimationListener.Stop() {
                            @Override
                            public void onStop() {
                                rootView.removeView(textView);
                            }
                        })
                        .thenAnimate(tvTarget)
                        .scale(1, 1.5f, 1)
                        .duration(300)
                        .start();
            }

            @Override
            public void minus(View view, int position) {

            }
        });


    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        tvTarget.getLocationInWindow(targetLoc);
    }
}
