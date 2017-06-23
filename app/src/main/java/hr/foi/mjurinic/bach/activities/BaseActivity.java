package hr.foi.mjurinic.bach.activities;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;

import com.afollestad.materialdialogs.MaterialDialog;

import hr.foi.mjurinic.bach.R;
import hr.foi.mjurinic.bach.mvp.views.BaseView;

public class BaseActivity extends AppCompatActivity implements BaseView {

    private MaterialDialog progressDialog;

    @Override
    public void showProgress(String message) {
        progressDialog = new MaterialDialog.Builder(this)
                .title("Bach")
                .content(message)
                .progress(true, 0)
                .build();
    }

    @Override
    public void hideProgress() {
        if (progressDialog != null && isFinishing()) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void updateProgressText(String text) {
        // Handle manually
    }

    @Override
    public void showError(String message) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this)
                .setMessage(message != null ? message : getString(R.string.error_general))
                .setPositiveButton(android.R.string.ok, null);

        if (!isFinishing()) {
            dialog.show();
        }
    }

    @Override
    public void showDialog(String title, String message, MaterialDialog.SingleButtonCallback positiveCallback,
                           MaterialDialog.SingleButtonCallback negativeCallback, String positiveButtonText, String negativeButtonText) {

        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title(title)
                .content(message)
                .positiveText(positiveButtonText)
                .onPositive(positiveCallback)
                .negativeText(negativeButtonText)
                .onNegative(negativeCallback)
                .build();

        dialog.setCanceledOnTouchOutside(false);

        if (!isFinishing()) {
            dialog.show();
        }
    }

    public boolean hasNavBar() {
        return !((KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK) && KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_HOME)));
    }

    public int convertDpToPx(int dp) {
        return (int) ((dp * getResources().getDisplayMetrics().density) + 0.5);
    }
}
