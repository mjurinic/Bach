package hr.foi.mjurinic.bach.fragments.watch;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import hr.foi.mjurinic.bach.R;
import hr.foi.mjurinic.bach.fragments.BaseFragment;
import hr.foi.mjurinic.bach.mvp.presenters.watch.WatchPresenter;
import hr.foi.mjurinic.bach.mvp.presenters.watch.impl.WatchPresenterImpl;
import hr.foi.mjurinic.bach.mvp.views.WatchView;

public class WatchFragment extends BaseFragment implements WatchView {

    private ImageView streamPreview;
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
    public void updateFrame(final Bitmap frame) {
        getBaseActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Matrix matrix = new Matrix();
                matrix.postRotate(90);

                streamPreview.setImageBitmap(Bitmap.createBitmap(frame, 0, 0, frame.getWidth(), frame.getHeight(), matrix, true));
            }
        });
    }

    private void bindViews(View view) {
        streamPreview = (ImageView) view.findViewById(R.id.iv_stream_preview);
    }
}
