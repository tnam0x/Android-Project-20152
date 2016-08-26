package com.namtran.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.namtran.exchange.ExpExchangeFragment;
import com.namtran.exchange.RevExchangeFragment;

/**
 * Created by namtr on 22/08/2016.
 */
public class ExchangeViewPagerAdapter extends FragmentPagerAdapter {
    public ExchangeViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new RevExchangeFragment();
            case 1:
                return new ExpExchangeFragment();

        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Danh Sách Thu";
            case 1:
                return "Danh Sách Chi";
        }
        return null;
    }
}
