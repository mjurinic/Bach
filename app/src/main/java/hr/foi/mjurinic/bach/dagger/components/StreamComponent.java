package hr.foi.mjurinic.bach.dagger.components;

import dagger.Component;
import hr.foi.mjurinic.bach.dagger.modules.AppContextModule;
import hr.foi.mjurinic.bach.dagger.modules.StreamModule;
import hr.foi.mjurinic.bach.fragments.stream.QrFragment;

@Component(modules = {
        AppContextModule.class,
        StreamModule.class
})
public interface StreamComponent {

    void inject(QrFragment fragment);
}
