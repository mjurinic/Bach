package hr.foi.mjurinic.bach;

import android.app.Application;

import timber.log.Timber;

public class BachApp extends Application {

    private static BachApp instance;

    @Override
    public void onCreate() {
        super.onCreate();

        setInstance(this);

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }

    public static BachApp getInstance() {
        return instance;
    }

    public static void setInstance(BachApp instance) {
        BachApp.instance = instance;
    }
}
