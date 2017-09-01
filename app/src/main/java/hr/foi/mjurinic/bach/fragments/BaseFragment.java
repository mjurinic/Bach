package hr.foi.mjurinic.bach.fragments;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ProgressBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import hr.foi.mjurinic.bach.R;
import hr.foi.mjurinic.bach.activities.BaseActivity;

public abstract class BaseFragment extends Fragment {

    @BindView(R.id.view_stub_base)
    ViewStub viewStub;

    @BindView(R.id.vstub_progress)
    ProgressBar progressBar;

    private Bundle savedInstanceState;
    protected boolean hasInflated;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_viewstub, container, false);
        ButterKnife.bind(this, view);

        viewStub.setLayoutResource(getViewStubLayoutResource());

        this.savedInstanceState = savedInstanceState;

        if (getUserVisibleHint() && !hasInflated) {
            onCreateViewAfterViewStubInflated(viewStub.inflate(), this.savedInstanceState);
            afterViewStubInflated(view);
        }

        return view;
    }

    protected abstract int getViewStubLayoutResource();

    /**
     * View is attached. Run any initialization here.
     *
     * @param inflatedView
     * @param savedInstanceState
     */
    protected abstract void onCreateViewAfterViewStubInflated(View inflatedView, Bundle savedInstanceState);

    protected void afterViewStubInflated(View originalView) {
        hasInflated = true;
        progressBar.setVisibility(View.GONE);
    }

    @CallSuper
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser && viewStub != null && !hasInflated) {
            onCreateViewAfterViewStubInflated(viewStub.inflate(), savedInstanceState);
            afterViewStubInflated(getView());
        }
    }

    /**
     * Orientation changed.
     */
    @Override
    public void onDetach() {
        super.onDetach();
        hasInflated = false;
    }

    protected BaseActivity getBaseActivity() {
        return (BaseActivity) getActivity();
    }
}
