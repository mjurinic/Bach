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
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import hr.foi.mjurinic.bach.BachApp;
import hr.foi.mjurinic.bach.R;
import hr.foi.mjurinic.bach.fragments.BaseFragment;
import hr.foi.mjurinic.bach.mvp.presenters.StreamPresenter;
import hr.foi.mjurinic.bach.mvp.views.BaseStreamView;

public class QrFragment extends BaseFragment implements BaseStreamView {

    @BindView(R.id.toolbar_primary_color)
    Toolbar toolbar;

    @BindView(R.id.iv_qr_code)
    ImageView ivQrCode;

    @BindView(R.id.qr_progress)
    ProgressBar progressBar;

    @BindView(R.id.tv_stream_progress_text)
    TextView tvProgressText;

    @Inject
    StreamPresenter streamPresenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_stream_qr, container, false);

        ButterKnife.bind(this, view);
        ((StreamContainerFragment) getParentFragment()).getStreamComponent().inject(this);

        toolbar.setTitle("Waiting for Connection");
        streamPresenter.updateView(this);

        BachApp.getInstance().getWifiDirectBroadcastReceiver().setStreamPresenter(streamPresenter);

        return view;
    }

    @Override
    public void showQrCode(Bitmap qrCode) {
        progressBar.setVisibility(View.GONE);
        tvProgressText.setVisibility(View.GONE);
        ivQrCode.setVisibility(View.VISIBLE);
        ivQrCode.setImageBitmap(qrCode);
    }

    @Override
    public void updateProgressText(String message) {
        tvProgressText.setText(message);
    }

    @Override
    public void nextFragment() {
        ((StreamContainerFragment) getParentFragment()).changeActiveFragment(3);
    }
}
