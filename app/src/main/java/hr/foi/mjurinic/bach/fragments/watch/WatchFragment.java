package hr.foi.mjurinic.bach.fragments.watch;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import hr.foi.mjurinic.bach.R;
import hr.foi.mjurinic.bach.activities.MainActivity;
import hr.foi.mjurinic.bach.fragments.BaseFragment;
import hr.foi.mjurinic.bach.mvp.presenters.watch.WatchPresenter;
import hr.foi.mjurinic.bach.mvp.presenters.watch.impl.WatchPresenterImpl;
import hr.foi.mjurinic.bach.mvp.views.WatchView;
import hr.foi.mjurinic.bach.network.protocol.ProtoMultimedia;

public class WatchFragment extends BaseFragment implements WatchView {

    // Views
    private RelativeLayout watchStreamLayout;
    private RelativeLayout endOfStreamLayout;
    private ImageView streamPreview;
    private ImageView ivStop;
    private Button btnOk;
    private Toolbar toolbar;

    private WatchPresenter watchPresenter;

    @Override
    protected int getViewStubLayoutResource() {
        return R.layout.fragment_watch;
    }

    @Override
    protected void onCreateViewAfterViewStubInflated(View inflatedView, Bundle savedInstanceState) {
        bindViews(inflatedView);

        watchPresenter = new WatchPresenterImpl(this, ((WatchContainerFragment) getParentFragment()).getSocketInteractor());
        watchPresenter.sendClientReady();
    }

    @Override
    public void updateFrame(final ProtoMultimedia multimedia) {
        final Matrix matrix = new Matrix();
        matrix.postRotate(multimedia.isFrontCameraFrame() ? 270 : 90);

        final Bitmap frame = multimedia.getFrameAsBitmap();

        getBaseActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                streamPreview.setImageBitmap(Bitmap.createBitmap(frame, 0, 0, frame.getWidth(), frame.getHeight(), matrix, true));
            }
        });
    }

    @Override
    public void displayEndOfStreamView() {
        getBaseActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                toolbar.setTitle("Stream Ended");
                ((MainActivity) getBaseActivity()).showTabLayout();
                streamPreview.setImageBitmap(null);
                watchStreamLayout.setVisibility(View.GONE);
                endOfStreamLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void clearComponents() {
        streamPreview.setImageBitmap(null);
        ((QrScannerFragment) ((WatchContainerFragment) getParentFragment()).getNthFragment(0)).disconnect();
        ((QrScannerFragment) ((WatchContainerFragment) getParentFragment()).getNthFragment(0)).resetFragment();
        ((WatchContainerFragment) getParentFragment()).changeActiveFragment(0);
        ((MainActivity) getBaseActivity()).showTabLayout();
        ((MainActivity) getBaseActivity()).jumpToHomeFragment();
    }

    /**
     * Method used for sending "ClientReady" message after
     * the fragment is revisited. Since ViewStub is used
     * the fragment wont be re-inflated and the "onCreateView"
     * wont be called so we have to manually call this.
     */
    public void sendClientReady() {
        if (watchPresenter != null) {
            watchPresenter.updateSocketCallback();
            watchPresenter.sendClientReady();
        }
    }

    public void resetFragment() {
        endOfStreamLayout.setVisibility(View.GONE);
        watchStreamLayout.setVisibility(View.VISIBLE);
    }

    private void bindViews(View view) {
        watchStreamLayout = (RelativeLayout) view.findViewById(R.id.watch_stream_layout);
        endOfStreamLayout = (RelativeLayout) view.findViewById(R.id.end_stream_layout);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar_primary_color);
        streamPreview = (ImageView) view.findViewById(R.id.iv_stream_preview);

        ivStop = (ImageView) view.findViewById(R.id.iv_stop);
        ivStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                watchPresenter.closeStream();
            }
        });

        btnOk = (Button) view.findViewById(R.id.btn_ok);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetFragment();
                clearComponents();
                ((QrScannerFragment) ((WatchContainerFragment) getParentFragment()).getNthFragment(0)).resetFragment();
                ((WatchContainerFragment) getParentFragment()).changeActiveFragment(0);
                ((MainActivity) getBaseActivity()).jumpToHomeFragment();
            }
        });
    }
}
