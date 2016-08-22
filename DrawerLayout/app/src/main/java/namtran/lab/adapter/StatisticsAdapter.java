package namtran.lab.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import namtran.lab.statistics.TodayStatsFragment;
import namtran.lab.statistics.YearStatsFragment;

/**
 * Created by namtr on 20/08/2016.
 */
public class StatisticsAdapter extends FragmentPagerAdapter {
    public StatisticsAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new TodayStatsFragment();
            case 1:
                return new YearStatsFragment();
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
                return "Hiện Tại";
            case 1:
                return "Theo Năm";
        }
        return null;
    }
}
