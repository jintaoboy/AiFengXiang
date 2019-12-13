package com.myapplication.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.myapplication.MainActivity;
import com.myapplication.fragments.HomeFragment;
import com.myapplication.fragments.MessageFragment;
import com.myapplication.fragments.MyFragment;

/**
 * Created by Lin on 2019/6/8.
 */

public class MyFragmentPagerAdpater extends FragmentPagerAdapter {

    private final int PAGER_COUNT = 3;
    private HomeFragment homeFragment;
    private MessageFragment messageFragment;
    private MyFragment myFragment;

    public MyFragmentPagerAdpater(FragmentManager fm) {
        super(fm);
        homeFragment = new HomeFragment();
        messageFragment = new MessageFragment();
        myFragment = new MyFragment();
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position){
            case MainActivity.PAGE_ONE:
                fragment = homeFragment;
                break;
            case MainActivity.PAGE_TWO:
                fragment = messageFragment;
                break;
            case MainActivity.PAGE_THREE:
                fragment = myFragment;
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return PAGER_COUNT;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        return super.instantiateItem(container, position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        //super.destroyItem(container, position, object);
    }

}
