package hr.foi.mjurinic.bach;

import android.app.Application;

import hr.foi.mjurinic.bach.dagger.components.DaggerAppComponent;

public class BachApp extends Application {

    protected static BachApp instance;

    @Override
    public void onCreate() {
        super.onCreate();

        setInstance(this);
        DaggerAppComponent.create().inject(this);
    }

    public static BachApp getInstance() {
        return instance;
    }

    public static void setInstance(BachApp instance) {
        BachApp.instance = instance;
    }
}
