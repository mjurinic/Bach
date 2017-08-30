package hr.foi.mjurinic.bach.fragments.watch;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import hr.foi.mjurinic.bach.R;
import hr.foi.mjurinic.bach.activities.MainActivity;
import hr.foi.mjurinic.bach.fragments.BaseFragment;
import hr.foi.mjurinic.bach.mvp.interactors.SocketInteractor;
import hr.foi.mjurinic.bach.mvp.interactors.impl.SocketInteractorImpl;
import hr.foi.mjurinic.bach.utils.adapters.ViewPagerAdapter;
import timber.log.Timber;

public class WatchContainerFragment extends BaseFragment {

    private ViewPager viewPager;
    private List<Fragment> fragments;
    private SocketInteractor socketInteractor;

    @Override
    protected int getViewStubLayoutResource() {
        return R.layout.fragment_watch_container;
    }

    @Override
    protected void onCreateViewAfterViewStubInflated(View inflatedView, Bundle savedInstanceState) {
        Timber.d("Watch container inflated!");

        initFragments();

        socketInteractor = new SocketInteractorImpl();

        viewPager = (ViewPager) inflatedView.findViewById(R.id.view_pager_watch);
        viewPager.setAdapter(new ViewPagerAdapter(getChildFragmentManager(), fragments));

        changeActiveFragment(0);
    }

    private void initFragments() {
        fragments = new ArrayList<>();
        fragments.add(new QrScannerFragment());
        fragments.add(new WatchFragment());
    }

    public void changeActiveFragment(int position) {
        viewPager.setCurrentItem(position);

        if (position == 1) {
            ((MainActivity) getBaseActivity()).hideTabLayout();
        }
    }

    public SocketInteractor getSocketInteractor() {
        return socketInteractor;
    }

    public BaseFragment getNthFragment(int position) {
        return fragments != null ? (BaseFragment) fragments.get(position) : null;
    }
}
