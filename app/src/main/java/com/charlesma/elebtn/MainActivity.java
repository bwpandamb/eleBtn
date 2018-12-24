package com.charlesma.elebtn;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Path;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.charlesma.elebtn.viewbtn.EleAddView;
import com.github.florent37.viewanimator.AnimationListener;
import com.github.florent37.viewanimator.ViewAnimator;

public class MainActivity extends AppCompatActivity {

    private EleAddView btn1;
    private EleAddView btn2;
    private EleAddView btn3;
    private EleAddView btn4;

    int[] addbtnLoc1 = new int[2];
    int[] addbtnLoc2 = new int[2];
    int[] targetLoc = new int[2];
    private RelativeLayout rootView;
    private TextView tvTarget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rootView = (RelativeLayout) findViewById(R.id.rootView);
        btn1 = (EleAddView) findViewById(R.id.btn1);
        btn2 = (EleAddView) findViewById(R.id.btn2);
        btn3 = (EleAddView) findViewById(R.id.btn3);
        btn4 = (EleAddView) findViewById(R.id.btn4);
        tvTarget = (TextView) findViewById(R.id.tv_target);
        btn3.setTextNum(1);
        btn4.setTextNum(0);





        tvTarget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ListActivity.class));
            }
        });


        btn1.setOnShoppingClickListener(new EleAddView.ShoppingClickListener() {
            @Override
            public void onAddClick(int num) {
                Path path = new Path();
                path.moveTo(addbtnLoc1[0] + btn1.getWidth() * 0.8f, addbtnLoc1[1] - btn1.getHeight() * 2.3f);
                path.quadTo(targetLoc[0], addbtnLoc1[1] - 200, targetLoc[0], targetLoc[1]);

                final TextView textView = new TextView(MainActivity.this);
                textView.setBackgroundResource(R.drawable.circle_cahres);
                textView.setText("1");
                textView.setTextColor(Color.WHITE);
                textView.setGravity(Gravity.CENTER);

                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(50, 50);
                rootView.addView(textView, layoutParams);
                ViewAnimator.animate(textView).path(path).accelerate().duration(3000).onStop(new AnimationListener.Stop() {
                    @Override
                    public void onStop() {
                        rootView.removeView(textView);
                    }
                }).start();
            }

            @Override
            public void onMinusClick(int num) {

            }
        });

        btn2.setOnShoppingClickListener(new EleAddView.ShoppingClickListener() {
            @Override
            public void onAddClick(int num) {
                Path path = new Path();
                path.moveTo(addbtnLoc2[0] + btn2.getWidth() * 0.8f, addbtnLoc2[1] - btn2.getHeight() * 2.3f);
                path.quadTo(targetLoc[0], addbtnLoc2[1] - 200, targetLoc[0], targetLoc[1]);

                final TextView textView = new TextView(MainActivity.this);
                textView.setBackgroundResource(R.drawable.circle_cahres);
                textView.setText("1");
                textView.setTextColor(Color.WHITE);
                textView.setGravity(Gravity.CENTER);

                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(60, 60);
                rootView.addView(textView, layoutParams);
                ViewAnimator.animate(textView).path(path).accelerate().duration(3000).onStop(new AnimationListener.Stop() {
                    @Override
                    public void onStop() {
                        rootView.removeView(textView);
                    }
                }).start();
            }

            @Override
            public void onMinusClick(int num) {

            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        tvTarget.getLocationInWindow(targetLoc);
        btn2.getLocationInWindow(addbtnLoc2);
        btn1.getLocationInWindow(addbtnLoc1);
    }
}
