package hr.foi.mjurinic.bach.fragments.stream;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hr.foi.mjurinic.bach.R;
import hr.foi.mjurinic.bach.fragments.BaseFragment;

public class ConnectionTypeFragment extends BaseFragment {

    @BindView(R.id.toolbar_primary_color)
    Toolbar toolbar;

    @BindView(R.id.radio_wifi_p2p)
    RadioButton radioWifiP2P;

    @BindView(R.id.radio_internet)
    RadioButton radioInternet;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stream_connection, container, false);

        ButterKnife.bind(this, view);

        toolbar.setTitle("Choose Connection Type");

        return view;
    }

    @OnClick(R.id.btn_stream_connection_next)
    void onBtnNextClick() {
        int nextItemPosition = -1;

        if (radioWifiP2P.isChecked()) {
            nextItemPosition = 1;
        }

        if (radioInternet.isChecked()) {
            nextItemPosition = -1; // TODO update
        }

        if (nextItemPosition != -1) {
            ((StreamContainerFragment) getParentFragment()).changeActiveFragment(nextItemPosition);
        }
    }
}
