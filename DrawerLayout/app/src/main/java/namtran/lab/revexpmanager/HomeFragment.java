package namtran.lab.revexpmanager;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import namtran.lab.adapter.HomeAdapter;

/**
 * Created by namtr on 19/08/2016.
 */
public class HomeFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        final TabLayout tabLayout = (TabLayout) rootView.findViewById(R.id.tab_layout);
        final ViewPager pager = (ViewPager) rootView.findViewById(R.id.pager);
        pager.setAdapter(new HomeAdapter(getChildFragmentManager()));
        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                tabLayout.setupWithViewPager(pager, true);
            }
        });
        return rootView;
    }
}
