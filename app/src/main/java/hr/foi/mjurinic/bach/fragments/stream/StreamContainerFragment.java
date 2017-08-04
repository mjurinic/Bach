package hr.foi.mjurinic.bach.fragments.stream;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import hr.foi.mjurinic.bach.R;
import hr.foi.mjurinic.bach.fragments.BaseFragment;
import hr.foi.mjurinic.bach.mvp.interactors.SocketInteractor;
import hr.foi.mjurinic.bach.mvp.interactors.impl.SocketInteractorImpl;
import hr.foi.mjurinic.bach.utils.adapters.ViewPagerAdapter;
import timber.log.Timber;

public class StreamContainerFragment extends BaseFragment {

    private ViewPager viewPager;
    private List<Fragment> fragments;
    private SocketInteractor socketInteractor;

    @Override
    protected int getViewStubLayoutResource() {
        return R.layout.fragment_stream;
    }

    @Override
    protected void onCreateViewAfterViewStubInflated(View inflatedView, Bundle savedInstanceState) {
        Timber.d("Stream container inflated!");

        initFragments();

        socketInteractor = new SocketInteractorImpl();

        viewPager = (ViewPager) inflatedView.findViewById(R.id.view_pager_stream);
        viewPager.setAdapter(new ViewPagerAdapter(getChildFragmentManager(), fragments));

        changeActiveFragment(0);
    }

    @Override
    public void onPause() {
        super.onPause();

        if (socketInteractor != null) {
            socketInteractor.stopReceiver();
            socketInteractor.stopSender();
        }
    }

    private void initFragments() {
        fragments = new ArrayList<>();
        fragments.add(new ConnectionTypeFragment());
        fragments.add(new StreamFragment());
    }

    public void changeActiveFragment(int position) {
        viewPager.setCurrentItem(position);
    }

    public SocketInteractor getSocketInteractor() {
        return socketInteractor;
    }
}
