package hr.foi.mjurinic.bach.fragments.stream;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import hr.foi.mjurinic.bach.R;
import hr.foi.mjurinic.bach.dagger.components.DaggerStreamComponent;
import hr.foi.mjurinic.bach.dagger.modules.StreamModule;
import hr.foi.mjurinic.bach.fragments.BaseFragment;
import hr.foi.mjurinic.bach.mvp.presenters.StreamPresenter;
import hr.foi.mjurinic.bach.mvp.views.StreamView;

public class QrFragment extends BaseFragment implements StreamView {

    private static final String TAG = "QrFragment";

    @BindView(R.id.toolbar_primary_color)
    Toolbar toolbar;

    @Inject
    StreamPresenter streamPresenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_stream_qr, container, false);

        ButterKnife.bind(this, view);

        DaggerStreamComponent.builder()
                .streamModule(new StreamModule(this))
                .build()
                .inject(this);

        toolbar.setTitle("Waiting for Connection");
        streamPresenter.initWifiDirect();
        streamPresenter.createWifiP2PGroup();

        Log.d(TAG, "onCreateView");

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        streamPresenter.removeWifiP2PGroup();
    }

    @Override
    public void showQrCode(Bitmap qrCode) {

    }
}
