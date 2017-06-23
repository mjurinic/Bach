package hr.foi.mjurinic.bach.fragments.watch;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import hr.foi.mjurinic.bach.R;
import hr.foi.mjurinic.bach.dagger.components.DaggerWatchComponent;
import hr.foi.mjurinic.bach.dagger.components.WatchComponent;
import hr.foi.mjurinic.bach.dagger.modules.WatchModule;
import hr.foi.mjurinic.bach.fragments.BaseFragment;
import hr.foi.mjurinic.bach.mvp.presenters.WatchPresenter;
import hr.foi.mjurinic.bach.mvp.views.WatchView;
import hr.foi.mjurinic.bach.utils.adapters.ViewPagerAdapter;

public class WatchContainerFragment extends BaseFragment implements WatchView {

    @BindView(R.id.watch_view_pager)
    ViewPager viewPager;

    @Inject
    WatchPresenter watchPresenter;

    private List<Fragment> fragments;
    private WatchComponent watchComponent;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_watch, container, false);
        ButterKnife.bind(this, view);

        watchComponent = DaggerWatchComponent.builder()
                .watchModule(new WatchModule(this))
                .build();

        watchComponent.inject(this);

        fragments = new ArrayList<>();
        fragments.add(new QrScannerFragment());
        fragments.add(new ConnectionFragment());

        viewPager.setAdapter(new ViewPagerAdapter(getChildFragmentManager(), fragments));
        viewPager.setCurrentItem(0);

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        watchPresenter.disconnectWifi();
    }

    public void changeActiveFragment(int position) {
        viewPager.setCurrentItem(position);
    }

    public WatchComponent getWatchComponent() {
        return watchComponent;
    }

    @Override
    public void updateFrame() {

    }
}
