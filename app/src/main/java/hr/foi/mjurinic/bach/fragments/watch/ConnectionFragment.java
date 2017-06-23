package hr.foi.mjurinic.bach.fragments.watch;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import hr.foi.mjurinic.bach.R;
import hr.foi.mjurinic.bach.fragments.BaseFragment;
import hr.foi.mjurinic.bach.models.WifiHostInformation;
import hr.foi.mjurinic.bach.mvp.presenters.WatchPresenter;
import hr.foi.mjurinic.bach.mvp.views.WatchView;
import timber.log.Timber;

public class ConnectionFragment extends BaseFragment implements WatchView {

    @BindView(R.id.toolbar_primary_color)
    Toolbar toolbar;

    @BindView(R.id.tv_watch_progress_text)
    TextView tvProgressText;

    @Inject
    WatchPresenter watchPresenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_watch_connection, container, false);

        ButterKnife.bind(this, view);
        ((WatchContainerFragment) getParentFragment()).getWatchComponent().inject(this);

        toolbar.setTitle("Trying to Connect...");
        watchPresenter.updateView(this);

        return view;
    }

    public void init(WifiHostInformation wifiHostInformation) {
        watchPresenter.connectToWifiHost(wifiHostInformation);
    }

    @Override
    public void updateFrame() {

    }

    @Override
    public void updateProgressText(String message) {
        Timber.d(message);
        tvProgressText.setText(message);
    }
}
