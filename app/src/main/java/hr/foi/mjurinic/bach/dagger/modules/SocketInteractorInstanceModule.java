package hr.foi.mjurinic.bach.dagger.modules;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import hr.foi.mjurinic.bach.mvp.interactors.SocketInteractor;
import hr.foi.mjurinic.bach.mvp.interactors.impl.SocketInteractorImpl;

@Module
public class SocketInteractorInstanceModule {

    @Provides
    @Singleton
    public SocketInteractor providesSocketInteractor() {
        return new SocketInteractorImpl();
    }
}
