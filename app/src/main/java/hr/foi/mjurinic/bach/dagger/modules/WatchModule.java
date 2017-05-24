package hr.foi.mjurinic.bach.dagger.modules;

import dagger.Module;
import dagger.Provides;
import hr.foi.mjurinic.bach.mvp.presenters.WatchPresenter;
import hr.foi.mjurinic.bach.mvp.presenters.Impl.WatchPresenterImpl;
import hr.foi.mjurinic.bach.mvp.views.WatchView;

@Module
public class WatchModule {

    private WatchView watchView;

    public WatchModule(WatchView watchView) {
        this.watchView = watchView;
    }

    @Provides
    public WatchView providesWatchView() {
        return watchView;
    }

    @Provides
    public WatchPresenter providesWatchPresenter(WatchPresenterImpl watchPresenter) {
        return watchPresenter;
    }
}
