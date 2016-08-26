package com.namtran.statistics;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.namtran.adapter.StatisticsViewPagerAdapter;
import com.namtran.main.R;

/**
 * Created by namtr on 20/08/2016.
 */
public class StatisticsFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_transactions_statistics, container, false);
        final TabLayout tabLayout = (TabLayout) rootView.findViewById(R.id.tab_layout);
        final ViewPager pager = (ViewPager) rootView.findViewById(R.id.pager);
        pager.setAdapter(new StatisticsViewPagerAdapter(getChildFragmentManager()));
        pager.addOnPageChangeListener(new PageListener());
        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                tabLayout.setupWithViewPager(pager, true);
            }
        });
        return rootView;
    }

    public static int mCurrentPage = 0;

    private class PageListener extends ViewPager.SimpleOnPageChangeListener {
        @Override
        public void onPageSelected(int position) {
            Log.i("Page change", "page selected " + position);
            mCurrentPage = position;
        }
    }
}
