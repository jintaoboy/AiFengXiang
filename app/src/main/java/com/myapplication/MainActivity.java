package com.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.myapplication.adapters.MyFragmentPagerAdpater;
import com.myapplication.fragments.HomeFragment;
import com.myapplication.fragments.MessageFragment;
import com.myapplication.fragments.MyFragment;

public class MainActivity extends FragmentActivity implements RadioGroup.OnCheckedChangeListener, ViewPager.OnPageChangeListener {

    public static final int PAGE_ONE = 0;
    public static final int PAGE_TWO = 1;
    public static final int PAGE_THREE = 2;

    private RadioGroup rg_nav;
    private RadioButton rb_home,rb_message,rb_my;
    private ViewPager vp_content;
    private MyFragmentPagerAdpater mFPAdpater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();

        mFPAdpater = new MyFragmentPagerAdpater(getSupportFragmentManager());
        vp_content.setOffscreenPageLimit(4);

        vp_content.setAdapter(mFPAdpater);
        vp_content.setCurrentItem(0);
    }

    public void initViews(){
        rg_nav = findViewById(R.id.rg_nav);
        rb_home = findViewById(R.id.rb_home);
        rb_message = findViewById(R.id.rb_message);
        rb_my = findViewById(R.id.rb_my);
        vp_content = findViewById(R.id.vp_content);

        rg_nav.setOnCheckedChangeListener(this);
        vp_content.addOnPageChangeListener(this);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        switch (checkedId){
            case R.id.rb_home:
                vp_content.setCurrentItem(PAGE_ONE,false);
                break;
            case R.id.rb_message:
                vp_content.setCurrentItem(PAGE_TWO,false);
                break;
            case R.id.rb_my:
                vp_content.setCurrentItem(PAGE_THREE,false);
                break;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (state == 2){
            switch (vp_content.getCurrentItem()){
                case PAGE_ONE:
                    rb_home.setChecked(true);
                    break;
                case PAGE_TWO:
                    rb_message.setChecked(true);
                    break;
                case PAGE_THREE:
                    rb_my.setChecked(true);
                    break;
            }
        }
    }
}
