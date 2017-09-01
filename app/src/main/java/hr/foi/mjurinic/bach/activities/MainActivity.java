package hr.foi.mjurinic.bach.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import hr.foi.mjurinic.bach.R;
import hr.foi.mjurinic.bach.fragments.HomeFragment;
import hr.foi.mjurinic.bach.fragments.stream.StreamContainerFragment;
import hr.foi.mjurinic.bach.fragments.watch.QrScannerFragment;
import hr.foi.mjurinic.bach.fragments.watch.WatchContainerFragment;
import hr.foi.mjurinic.bach.utils.adapters.ViewPagerAdapter;

public class MainActivity extends BaseActivity implements TabLayout.OnTabSelectedListener {

    private static final int PERMISSION_REQUEST_CAMERA = 0;

    @BindView(R.id.main_view_pager)
    ViewPager mainViewPager;

    @BindView(R.id.main_tab_layout)
    TabLayout mainTabLayout;

    private List<Fragment> fragments;

    protected boolean hasCameraPermission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        hasPermissions();

        if (hasNavBar()) {
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(mainTabLayout.getLayoutParams());
            layoutParams.setMargins(0, 0, 0, convertDpToPx(48));
            mainTabLayout.setLayoutParams(layoutParams);
        }

        mainTabLayout.addTab(mainTabLayout.newTab().setIcon(R.drawable.watch_tab_selector));
        mainTabLayout.addTab(mainTabLayout.newTab().setIcon(R.drawable.home_tab_selector));
        mainTabLayout.addTab(mainTabLayout.newTab().setIcon(R.drawable.stream_tab_selector));

        fragments = new ArrayList<>();
        fragments.add(new WatchContainerFragment());
        fragments.add(new HomeFragment());
        fragments.add(new StreamContainerFragment());

        mainViewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager(), fragments));
        mainViewPager.setOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mainTabLayout));
        mainTabLayout.setOnTabSelectedListener(this);
        mainViewPager.setCurrentItem(1);

        mainViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    QrScannerFragment qrScannerFragment =
                            ((QrScannerFragment) ((WatchContainerFragment) fragments.get(0)).getNthFragment(0));

                    if (qrScannerFragment != null) {
                        qrScannerFragment.init();
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PERMISSION_REQUEST_CAMERA: {
                hasCameraPermission = grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
            }
        }
    }

    public void jumpToHomeFragment() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mainViewPager.setCurrentItem(1);
            }
        });
    }

    public boolean hasPermissions() {
        hasCameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;

        if (!hasCameraPermission) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
        }

        return hasCameraPermission;
    }

    public void hideTabLayout() {
        mainTabLayout.setVisibility(View.GONE);
    }

    public void showTabLayout() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mainTabLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    public void changeActiveFragment(int position) {
        mainViewPager.setCurrentItem(position);
    }
}
