package hr.foi.mjurinic.bach.state;

import hr.foi.mjurinic.bach.models.ReceivedPacket;

public interface State<T> {

    void process(ReceivedPacket data, T presenter);
}
