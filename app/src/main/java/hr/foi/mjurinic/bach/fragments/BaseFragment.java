package hr.foi.mjurinic.bach.fragments;

import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;

import com.afollestad.materialdialogs.MaterialDialog;

import hr.foi.mjurinic.bach.R;
import hr.foi.mjurinic.bach.activities.BaseActivity;
import hr.foi.mjurinic.bach.mvp.views.BaseView;

public class BaseFragment extends Fragment implements BaseView {

    private MaterialDialog progressDialog;

    @Override
    public void showProgress(String message) {
        progressDialog = new MaterialDialog.Builder(getBaseActivity())
                .title("Bach")
                .content(message)
                .progress(true, 0)
                .build();
    }

    @Override
    public void hideProgress() {
        if (progressDialog != null && progressDialog.isShowing() && !isRemoving()) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void showError(String message) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getBaseActivity())
                .setMessage(message != null ? message : getString(R.string.error_general))
                .setPositiveButton(android.R.string.ok, null);

        if (!isRemoving()) {
            dialog.show();
        }
    }

    @Override
    public void showDialog(String title, String message, MaterialDialog.SingleButtonCallback positiveCallback,
                           MaterialDialog.SingleButtonCallback negativeCallback, String positiveButtonText, String negativeButtonText) {
        getBaseActivity().showDialog(title, message, positiveCallback, negativeCallback, positiveButtonText, negativeButtonText);
    }

    protected BaseActivity getBaseActivity() {
        return (BaseActivity) getActivity();
    }
}
