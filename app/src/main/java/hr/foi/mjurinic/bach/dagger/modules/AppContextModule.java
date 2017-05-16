package hr.foi.mjurinic.bach.dagger.modules;

import android.content.Context;
import android.content.res.Resources;

import dagger.Module;
import dagger.Provides;
import hr.foi.mjurinic.bach.BachApp;

@Module
public class AppContextModule {

    @Provides
    public Context provideContext() {
        return BachApp.getInstance();
    }

    @Provides
    public Resources provideResources(Context context) {
        return context.getResources();
    }
}
