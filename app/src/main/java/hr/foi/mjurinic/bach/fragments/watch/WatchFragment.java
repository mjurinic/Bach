package hr.foi.mjurinic.bach.fragments.watch;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import hr.foi.mjurinic.bach.R;
import hr.foi.mjurinic.bach.fragments.BaseFragment;
import hr.foi.mjurinic.bach.mvp.presenters.watch.WatchPresenter;
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


    }

    @Override
    public void updateFrame(final Bitmap frame) {
        getBaseActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                streamPreview.setImageBitmap(frame);
            }
        });
    }

    private void bindViews(View view) {
        streamPreview = (ImageView) view.findViewById(R.id.iv_stream_preview);
    }
}
