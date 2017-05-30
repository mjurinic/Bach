package hr.foi.mjurinic.bach.dagger.components;

import javax.inject.Singleton;

import dagger.Component;
import hr.foi.mjurinic.bach.dagger.modules.AppContextModule;
import hr.foi.mjurinic.bach.dagger.modules.SocketInteractorInstanceModule;
import hr.foi.mjurinic.bach.dagger.modules.StreamModule;
import hr.foi.mjurinic.bach.fragments.stream.QrFragment;
import hr.foi.mjurinic.bach.fragments.stream.StreamFragment;

@Component(modules = {
        AppContextModule.class,
        SocketInteractorInstanceModule.class,
        StreamModule.class
})
@Singleton
public interface StreamComponent {

    void inject(StreamFragment fragment);

    void inject(QrFragment fragment);
}
