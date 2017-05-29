package hr.foi.mjurinic.bach.dagger.components;

import javax.inject.Singleton;

import dagger.Component;
import hr.foi.mjurinic.bach.BachApp;
import hr.foi.mjurinic.bach.dagger.modules.AppContextModule;

@Component(modules = {
        AppContextModule.class
})
@Singleton
public interface AppComponent {

    void inject(BachApp app);
}
