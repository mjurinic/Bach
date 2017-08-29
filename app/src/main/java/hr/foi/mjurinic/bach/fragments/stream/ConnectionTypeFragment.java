package hr.foi.mjurinic.bach.fragments.stream;

import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.Map;

import hr.foi.mjurinic.bach.BachApp;
import hr.foi.mjurinic.bach.R;
import hr.foi.mjurinic.bach.fragments.BaseFragment;
import hr.foi.mjurinic.bach.models.WifiHostInformation;
import hr.foi.mjurinic.bach.mvp.presenters.stream.ConnectionTypePresenter;
import hr.foi.mjurinic.bach.mvp.presenters.stream.impl.ConnectionTypePresenterImpl;
import hr.foi.mjurinic.bach.mvp.views.ConnectionTypeView;
import hr.foi.mjurinic.bach.utils.receivers.WifiDirectBroadcastReceiver;
import timber.log.Timber;

import static android.os.Looper.getMainLooper;

public class ConnectionTypeFragment extends BaseFragment implements ConnectionTypeView {

    private static final String SERVICE_TYPE = "_bach._udp.";

    // Views
    private Button btnNext;
    private RadioButton radioWifiP2P;
    private RadioButton radioInternet;
    private RadioGroup radioGroup;
    private RelativeLayout qrLayout;
    private TextView tvProgress;
    private ImageView ivQrCode;
    private ProgressBar progressBar;

    // Wifi
    private WifiP2pManager wifiManager;
    private WifiP2pManager.Channel wifiChannel;
    private WifiP2pDnsSdServiceInfo serviceInfo;
    private WifiDirectBroadcastReceiver broadcastReceiver;

    private ConnectionTypePresenter connectionTypePresenter;

    @Override
    protected int getViewStubLayoutResource() {
        return R.layout.fragment_stream_connection;
    }

    @Override
    protected void onCreateViewAfterViewStubInflated(View inflatedView, Bundle savedInstanceState) {
        Timber.d("ConnectionTypeFragment inflated!");
        bindViews(inflatedView);

        connectionTypePresenter = new ConnectionTypePresenterImpl(
                ((StreamContainerFragment) getParentFragment()).getSocketInteractor(), this);

        ((ConnectionTypePresenterImpl) connectionTypePresenter).setAccessPointCreated(false);
    }

    @Override
    public void updateProgressText(String progress) {
        tvProgress.setText(progress);
    }

    @Override
    public void advertiseAccessPoint(final WifiHostInformation hostInformation) {
        Gson gson = new Gson();
        Map<String, String> extraInfo = gson.fromJson(gson.toJson(hostInformation), Map.class);

        serviceInfo = WifiP2pDnsSdServiceInfo.newInstance(hostInformation.getNetworkName(), SERVICE_TYPE, extraInfo);
        updateProgressText("Advertising newly created access point...");

        wifiManager.addLocalService(wifiChannel, serviceInfo, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Timber.d("New local service created.");
                connectionTypePresenter.generateQrCode(hostInformation);
            }

            @Override
            public void onFailure(int reason) {
                Timber.d("Failed to register new local service. (" + reason + ")");
            }
        });
    }

    @Override
    public void showQrCode(Bitmap qrCode) {
        // Hide progress
        progressBar.setVisibility(View.GONE);
        tvProgress.setVisibility(View.GONE);

        // Show QR code
        ivQrCode.setVisibility(View.VISIBLE);
        ivQrCode.setImageBitmap(qrCode);
    }

    @Override
    public void nextFragment() {
        ((StreamContainerFragment) getParentFragment()).changeActiveFragment(1);
    }

    @Override
    public void onPause() {
        super.onPause();

        // Timber.d("Closing Wi-Fi P2P access point...");
        // disconnect();
        // ((ConnectionTypePresenterImpl) connectionTypePresenter).setAccessPointCreated(false);
    }

    public void disconnect() {
        Timber.d("Wi-Fi P2P disconnect() called!");

        if (wifiManager != null && wifiChannel != null) {
            wifiManager.requestGroupInfo(wifiChannel, new WifiP2pManager.GroupInfoListener() {
                @Override
                public void onGroupInfoAvailable(WifiP2pGroup group) {
                    if (group != null && wifiManager != null && wifiChannel != null && group.isGroupOwner()) {
                        wifiManager.removeGroup(wifiChannel, new WifiP2pManager.ActionListener() {

                            @Override
                            public void onSuccess() {
                                Timber.d("Remove p2p group success.");
                            }

                            @Override
                            public void onFailure(int reason) {
                                Timber.d("Remove p2p group error: " + reason);
                            }
                        });
                    }
                }
            });
        }
    }

    private void removeWifiP2PGroup() {
        wifiManager.removeGroup(wifiChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Timber.d("Removed Wi-Fi P2P group.");
            }

            @Override
            public void onFailure(int reason) {
                Timber.d("Failed to remove Wi-Fi P2P group: " + reason);
            }
        });
    }

    private void createWifiP2PGroup() {
        // Prevents "Busy" bug
        // deletePersistentGroups();
        removeWifiP2PGroup();

        updateProgressText("Creating Wi-Fi P2P group...");

        wifiManager.createGroup(wifiChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Timber.d("Wi-Fi P2P group created.");
            }

            @Override
            public void onFailure(int reason) {
                Timber.d("P2P group creation failed. Make sure Wi-Fi is on. (" + reason + ")");
            }
        });
    }

    private void initWifiP2P() {
        wifiManager = (WifiP2pManager) BachApp.getInstance().getSystemService(Context.WIFI_P2P_SERVICE);
        wifiChannel = wifiManager.initialize(getBaseActivity().getApplicationContext(), getMainLooper(), null);
        broadcastReceiver = new WifiDirectBroadcastReceiver(wifiManager, wifiChannel, connectionTypePresenter);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);

        BachApp.getInstance().registerReceiver(broadcastReceiver, intentFilter);
    }

    private void changeView() {
        // Hide step-1 view
        radioGroup.setVisibility(View.GONE);
        btnNext.setVisibility(View.GONE);

        // Show step-2 view
        qrLayout.setVisibility(View.VISIBLE);
    }

    private void bindViews(View inflatedView) {
        radioGroup = (RadioGroup) inflatedView.findViewById(R.id.rg_connection_type);
        radioWifiP2P = (RadioButton) inflatedView.findViewById(R.id.radio_wifi_p2p);
        radioInternet = (RadioButton) inflatedView.findViewById(R.id.radio_internet);
        qrLayout = (RelativeLayout) inflatedView.findViewById(R.id.qr_layout);
        tvProgress = (TextView) inflatedView.findViewById(R.id.tv_progress);
        ivQrCode = (ImageView) inflatedView.findViewById(R.id.iv_qr_code);
        progressBar = (ProgressBar) inflatedView.findViewById(R.id.progress_bar);

        Toolbar toolbar = (Toolbar) inflatedView.findViewById(R.id.toolbar_primary_color);
        toolbar.setTitle("Choose Connection Type");

        btnNext = (Button) inflatedView.findViewById(R.id.btn_next);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int nextItemPosition = -1;

                if (radioWifiP2P.isChecked()) {
                    nextItemPosition = 1;
                }

                if (radioInternet.isChecked()) {
                    nextItemPosition = -1; // TODO update
                }

                if (nextItemPosition != -1) {
                    changeView();

                    if (nextItemPosition == 1) {
                        initWifiP2P();
                        createWifiP2PGroup();
                    }
                }
            }
        });
    }
}
