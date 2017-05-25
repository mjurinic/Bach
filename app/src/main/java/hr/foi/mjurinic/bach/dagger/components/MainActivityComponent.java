package hr.foi.mjurinic.bach.dagger.components;

import dagger.Component;
import hr.foi.mjurinic.bach.activities.MainActivity;
import hr.foi.mjurinic.bach.dagger.modules.AppContextModule;
import hr.foi.mjurinic.bach.dagger.modules.StreamModule;
import hr.foi.mjurinic.bach.dagger.modules.WatchModule;

@Component(modules = {
        AppContextModule.class,
        WatchModule.class,
        StreamModule.class
})
public interface MainActivityComponent {

    void inject(MainActivity activity);
}
