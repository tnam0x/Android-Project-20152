package namtran.lab.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import namtran.lab.transaction.ExpenditureFragment;
import namtran.lab.transaction.RevenuesFragment;

/**
 * Created by namtr on 19/08/2016.
 */
public class TransactionsViewPagerAdapter extends FragmentPagerAdapter {
    public TransactionsViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new RevenuesFragment();
            case 1:
                return new ExpenditureFragment();
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
