package hr.foi.mjurinic.bach.dagger.modules;

import dagger.Module;
import dagger.Provides;
import hr.foi.mjurinic.bach.mvp.presenters.Impl.StreamPresenterImpl;
import hr.foi.mjurinic.bach.mvp.presenters.StreamPresenter;
import hr.foi.mjurinic.bach.mvp.views.BaseStreamView;

@Module
public class StreamModule {

    private BaseStreamView streamView;

    public StreamModule(BaseStreamView streamView) {
        this.streamView = streamView;
    }

    @Provides
    public BaseStreamView providesBaseStreamView() {
        return streamView;
    }

    @Provides
    public StreamPresenter providesStreamPresenter(StreamPresenterImpl presenter) {
        return presenter;
    }
}
