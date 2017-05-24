package hr.foi.mjurinic.bach.dagger.components;

import dagger.Component;
import hr.foi.mjurinic.bach.activities.MainActivity;
import hr.foi.mjurinic.bach.dagger.modules.WatchModule;

@Component(modules = {
        WatchModule.class
})
public interface WatchComponent {

    void inject(MainActivity activity);
}
