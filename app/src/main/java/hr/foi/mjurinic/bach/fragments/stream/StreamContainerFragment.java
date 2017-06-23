package hr.foi.mjurinic.bach.fragments.stream;

import android.graphics.Bitmap;
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
import hr.foi.mjurinic.bach.dagger.components.DaggerStreamComponent;
import hr.foi.mjurinic.bach.dagger.components.StreamComponent;
import hr.foi.mjurinic.bach.dagger.modules.StreamModule;
import hr.foi.mjurinic.bach.fragments.BaseFragment;
import hr.foi.mjurinic.bach.mvp.presenters.StreamPresenter;
import hr.foi.mjurinic.bach.mvp.views.StreamView;
import hr.foi.mjurinic.bach.utils.adapters.ViewPagerAdapter;

public class StreamContainerFragment extends BaseFragment implements StreamView {

    @BindView(R.id.stream_view_pager)
    ViewPager viewPager;

    @Inject
    StreamPresenter streamPresenter;

    private List<Fragment> fragments;
    private StreamComponent streamComponent;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stream, container, false);
        ButterKnife.bind(this, view);

        streamComponent = DaggerStreamComponent.builder()
                .streamModule(new StreamModule(this))
                .build();

        streamComponent.inject(this);

        fragments = new ArrayList<>();
        fragments.add(new ConnectionTypeFragment());
        fragments.add(new QrFragment());

        viewPager.setAdapter(new ViewPagerAdapter(getChildFragmentManager(), fragments));
        viewPager.setCurrentItem(0);

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        streamPresenter.closeOpenConnections();
    }

    public void changeActiveFragment(int position) {
        viewPager.setCurrentItem(position);

        switch (position) {
            case 1:
                streamPresenter.createWifiP2PGroup();
                break;

            default:
                break;
        }
    }

    @Override
    public void showQrCode(Bitmap qrCode) {
    }

    public StreamComponent getStreamComponent() {
        return streamComponent;
    }
}
