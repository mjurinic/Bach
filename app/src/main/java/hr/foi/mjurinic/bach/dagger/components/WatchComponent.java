package hr.foi.mjurinic.bach.dagger.components;

import javax.inject.Singleton;

import dagger.Component;
import hr.foi.mjurinic.bach.dagger.modules.AppContextModule;
import hr.foi.mjurinic.bach.dagger.modules.SocketInteractorInstanceModule;
import hr.foi.mjurinic.bach.dagger.modules.WatchModule;
import hr.foi.mjurinic.bach.fragments.watch.ConnectionFragment;
import hr.foi.mjurinic.bach.fragments.watch.QrScannerFragment;
import hr.foi.mjurinic.bach.fragments.watch.WatchContainerFragment;

@Component(modules = {
        AppContextModule.class,
        SocketInteractorInstanceModule.class,
        WatchModule.class
})
@Singleton
public interface WatchComponent {

    void inject(WatchContainerFragment fragment);

    void inject(QrScannerFragment fragment);

    void inject(ConnectionFragment fragment);
}
