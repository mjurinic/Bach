package hr.foi.mjurinic.bach.listeners;

public interface Listener<T> {

    void onSuccess(T data);

    void onFailure();
}
