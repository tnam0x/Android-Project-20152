package namtran.lab.transaction;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Locale;

import namtran.lab.adapter.TransactionsViewPagerAdapter;
import namtran.lab.revexpmanager.R;

/**
 * Created by namtr on 19/08/2016.
 */
public class TransactionsFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_transactions_statistics, container, false);
        final TabLayout tabLayout = (TabLayout) rootView.findViewById(R.id.tab_layout);
        final ViewPager pager = (ViewPager) rootView.findViewById(R.id.pager);
        pager.setAdapter(new TransactionsViewPagerAdapter(getChildFragmentManager()));
        pager.addOnPageChangeListener(new PageListener());
        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                tabLayout.setupWithViewPager(pager, true);
            }
        });
        return rootView;
    }

    public static int currentPage = 0;

    private class PageListener extends ViewPager.SimpleOnPageChangeListener {
        @Override
        public void onPageSelected(int position) {
            Log.i("Page change", "page selected " + position);
            currentPage = position;
        }
    }

}
