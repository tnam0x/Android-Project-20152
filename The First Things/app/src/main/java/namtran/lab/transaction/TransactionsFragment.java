package namtran.lab.transaction;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import namtran.lab.adapter.TransactionsViewPagerAdapter;
import namtran.lab.revexpmanager.R;

/**
 * Created by namtr on 19/08/2016.
 */
public class TransactionsFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transactions_statistics, container, false);
        final TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tab_layout);
        final ViewPager pager = (ViewPager) view.findViewById(R.id.pager);
        pager.setAdapter(new TransactionsViewPagerAdapter(getChildFragmentManager()));
        pager.addOnPageChangeListener(new PageListener());
        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                tabLayout.setupWithViewPager(pager, true);
            }
        });
        return view;
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
