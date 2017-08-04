package hr.foi.mjurinic.bach.listeners;

import hr.foi.mjurinic.bach.models.ReceivedPacket;

public interface SocketListener {

    void handleDatagram(ReceivedPacket data);
}