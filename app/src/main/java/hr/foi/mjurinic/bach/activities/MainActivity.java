package hr.foi.mjurinic.bach.activities;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import hr.foi.mjurinic.bach.R;
import hr.foi.mjurinic.bach.fragments.HomeFragment;
import hr.foi.mjurinic.bach.fragments.WatchFragment;
import hr.foi.mjurinic.bach.fragments.stream.StreamFragment;
import hr.foi.mjurinic.bach.utils.adapters.ViewPagerAdapter;

public class MainActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener {

    @BindView(R.id.main_view_pager)
    ViewPager mainViewPager;

    @BindView(R.id.main_tab_layout)
    TabLayout mainTabLayout;

    private List<Fragment> fragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mainTabLayout.addTab(mainTabLayout.newTab().setIcon(R.drawable.watch_tab_selector));
        mainTabLayout.addTab(mainTabLayout.newTab().setIcon(R.drawable.home_tab_selector));
        mainTabLayout.addTab(mainTabLayout.newTab().setIcon(R.drawable.stream_tab_selector));

        fragments = new ArrayList<>();
        fragments.add(new WatchFragment());
        fragments.add(new HomeFragment());
        fragments.add(new StreamFragment());

        mainViewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager(), fragments));
        mainViewPager.setOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mainTabLayout));
        mainTabLayout.setOnTabSelectedListener(this);
        mainViewPager.setCurrentItem(1);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        mainViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
}
