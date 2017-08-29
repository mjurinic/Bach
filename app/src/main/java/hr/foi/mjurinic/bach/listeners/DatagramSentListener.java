package hr.foi.mjurinic.bach.listeners;

import hr.foi.mjurinic.bach.mvp.presenters.BasePresenter;
import hr.foi.mjurinic.bach.network.protocol.ProtoMessage;
import timber.log.Timber;

public abstract class DatagramSentListener {

    public static final int RETRY_TIME = 2000;

    private BasePresenter presenter;
    private int retryCnt;

    public DatagramSentListener(BasePresenter presenter) {
        this.presenter = presenter;
    }

    public DatagramSentListener(BasePresenter presenter, int retryCnt) {
        this.presenter = presenter;
        this.retryCnt = retryCnt;
    }

    public abstract void onSuccess();

    public void onError(ProtoMessage message) {
        if (retryCnt == 5) {
            Timber.d("Host unreachable.");
            return;
        }

        try {
            Timber.d("Resending message in " + RETRY_TIME / 1000 + " seconds...");

            Thread.sleep(RETRY_TIME);
            retryCnt += 1;

            presenter.sendMessage(message, this);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
