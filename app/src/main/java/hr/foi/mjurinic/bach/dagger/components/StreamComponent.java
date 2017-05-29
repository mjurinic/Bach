package hr.foi.mjurinic.bach.dagger.components;

import javax.inject.Singleton;

import dagger.Component;
import hr.foi.mjurinic.bach.dagger.modules.AppContextModule;
import hr.foi.mjurinic.bach.dagger.modules.StreamModule;
import hr.foi.mjurinic.bach.fragments.stream.QrFragment;
import hr.foi.mjurinic.bach.utils.receivers.WifiDirectBroadcastReceiver;

@Component(modules = {
        AppContextModule.class,
        StreamModule.class
})
@Singleton
public interface StreamComponent {

    void inject(QrFragment fragment);

    void inject(WifiDirectBroadcastReceiver receiver);
}
