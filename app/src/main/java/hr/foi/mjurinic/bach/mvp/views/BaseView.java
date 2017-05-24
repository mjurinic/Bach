package hr.foi.mjurinic.bach.mvp.views;

import com.afollestad.materialdialogs.MaterialDialog;

public interface BaseView {

    void showProgress(String message);

    void hideProgress();

    void showError(String message);

    void showDialog(String title, String message, MaterialDialog.SingleButtonCallback positiveCallback,
                    MaterialDialog.SingleButtonCallback negativeCallback, String positiveButtonText, String negativeButtonText);
}
