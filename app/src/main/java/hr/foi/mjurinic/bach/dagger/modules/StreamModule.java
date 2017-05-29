package hr.foi.mjurinic.bach.dagger.modules;

import dagger.Module;
import dagger.Provides;
import hr.foi.mjurinic.bach.mvp.presenters.Impl.StreamPresenterImpl;
import hr.foi.mjurinic.bach.mvp.presenters.StreamPresenter;
import hr.foi.mjurinic.bach.mvp.views.StreamView;

@Module
public class StreamModule {

    private StreamView streamView;

    public StreamModule(StreamView streamView) {
        this.streamView = streamView;
    }

    @Provides
    public StreamView providesStreamView() {
        return streamView;
    }

    @Provides
    public StreamPresenter providesStreamPresenter(StreamPresenterImpl presenter) {
        return presenter;
    }
}
