package hr.foi.mjurinic.bach.fragments.watch;

import android.os.Bundle;
import android.view.View;

import hr.foi.mjurinic.bach.fragments.BaseFragment;
import hr.foi.mjurinic.bach.mvp.views.WatchView;

public class WatchFragment extends BaseFragment implements WatchView {

    @Override
    protected int getViewStubLayoutResource() {
        // TODO correct view-id
        return 0;
    }

    @Override
    protected void onCreateViewAfterViewStubInflated(View inflatedView, Bundle savedInstanceState) {
        // TODO init logic here
    }

    @Override
    public void updateFrame() {

    }
}
