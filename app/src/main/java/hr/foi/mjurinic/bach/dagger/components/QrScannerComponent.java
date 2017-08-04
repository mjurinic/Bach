package hr.foi.mjurinic.bach.dagger.components;

import javax.inject.Singleton;

import dagger.Component;
import hr.foi.mjurinic.bach.dagger.modules.QrScannerModule;
import hr.foi.mjurinic.bach.dagger.modules.SocketInteractorInstanceModule;
import hr.foi.mjurinic.bach.fragments.BaseFragment;

@Singleton
@Component(modules = {
        QrScannerModule.class,
        SocketInteractorInstanceModule.class
})
public interface QrScannerComponent {

    void inject(BaseFragment fragment);
}
