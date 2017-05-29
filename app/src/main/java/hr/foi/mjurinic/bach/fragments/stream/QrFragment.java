package hr.foi.mjurinic.bach.fragments.stream;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import hr.foi.mjurinic.bach.R;
import hr.foi.mjurinic.bach.fragments.BaseFragment;
import hr.foi.mjurinic.bach.mvp.presenters.Impl.StreamPresenterImpl;
import hr.foi.mjurinic.bach.mvp.presenters.StreamPresenter;
import hr.foi.mjurinic.bach.mvp.views.StreamView;

public class QrFragment extends BaseFragment implements StreamView {

    private static final String TAG = "QrFragment";

    @BindView(R.id.toolbar_primary_color)
    Toolbar toolbar;

    @BindView(R.id.iv_qr_code)
    ImageView ivQrCode;

    @BindView(R.id.qr_progress)
    ProgressBar progressBar;

    StreamPresenter streamPresenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_stream_qr, container, false);
        streamPresenter = new StreamPresenterImpl(this, getBaseActivity().getApplicationContext());

        ButterKnife.bind(this, view);
        toolbar.setTitle("Waiting for Connection");

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        streamPresenter.removeWifiP2PGroup();
    }

    @Override
    public void showQrCode(Bitmap qrCode) {
        progressBar.setVisibility(View.GONE);

        ivQrCode.setVisibility(View.VISIBLE);
        ivQrCode.setImageBitmap(qrCode);
    }

    public void initWifiDirect() {
        streamPresenter.initWifiDirect();
        streamPresenter.createWifiP2PGroup();
    }
}
