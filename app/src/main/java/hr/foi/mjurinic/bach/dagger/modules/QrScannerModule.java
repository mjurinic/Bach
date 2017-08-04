package hr.foi.mjurinic.bach.dagger.modules;

import dagger.Module;
import dagger.Provides;
import hr.foi.mjurinic.bach.fragments.BaseFragment;
import hr.foi.mjurinic.bach.mvp.presenters.Impl.QrScannerPresenterImpl;
import hr.foi.mjurinic.bach.mvp.presenters.QrScannerPresenter;

@Module
public class QrScannerModule {

    private BaseFragment view;

    public QrScannerModule(BaseFragment view) {
        this.view = view;
    }

    @Provides
    public BaseFragment providesQrScannerView() {
        return view;
    }

    @Provides
    public QrScannerPresenter providesQrScannerPresenter(QrScannerPresenterImpl presenter) {
        return presenter;
    }
}
