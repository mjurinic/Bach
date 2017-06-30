package hr.foi.mjurinic.bach.models;

import java.net.InetAddress;

public class ReceivedPacket {

    private InetAddress senderIp;
    private int senderPort;
    private Object payload;

    public ReceivedPacket() {}

    public ReceivedPacket(InetAddress senderIp, int senderPort, Object payload) {
        this.senderIp = senderIp;
        this.senderPort = senderPort;
        this.payload = payload;
    }

    public InetAddress getSenderIp() {
        return senderIp;
    }

    public void setSenderIp(InetAddress senderIp) {
        this.senderIp = senderIp;
    }

    public int getSenderPort() {
        return senderPort;
    }

    public void setSenderPort(int senderPort) {
        this.senderPort = senderPort;
    }

    public Object getPayload() {
        return payload;
    }

    public void setPayload(Object payload) {
        this.payload = payload;
    }
}
