package hr.foi.mjurinic.bach.network;

import android.support.annotation.Nullable;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import hr.foi.mjurinic.bach.helpers.Serializator;
import timber.log.Timber;

public class MediaSocket {

    public static final int TIMEOUT = 1000;

    private DatagramSocket socket;
    private InetAddress ip;
    private int port;
    private InetAddress destinationIp;
    private int destinationPort;

    public MediaSocket() {
        try {
            socket = new DatagramSocket();
            ip = getLocalAddress();
            port = socket.getLocalPort();

        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public boolean send(Object obj) {
        byte[] data = Serializator.serialize(obj);

        if (data != null) {
            DatagramPacket packet = new DatagramPacket(data, data.length, destinationIp, destinationPort);

            try {
                socket.send(packet);
                return true;

            } catch (IOException e) {
                e.printStackTrace();
            }

            return false;
        }

        Timber.e("Serialized object is null.");

        return false;
    }

    @Nullable
    public Object receive() {
        byte[] data = new byte[64000];
        DatagramPacket packet = new DatagramPacket(data, data.length);

        try {
            socket.setSoTimeout(TIMEOUT);
            socket.receive(packet);

            return Serializator.deserialize(data);

        } catch (IOException e) {
            // e.printStackTrace();
        }

        return null;
    }

    public DatagramSocket getSocket() {
        return socket;
    }

    public InetAddress getIp() {
        return ip;
    }

    public String getIpToString() {
        return ip.getHostAddress().replace("/", "");
    }

    public int getPort() {
        return port;
    }

    public InetAddress getDestinationIp() {
        return destinationIp;
    }

    public void setDestinationIp(InetAddress destinationIp) {
        this.destinationIp = destinationIp;
    }

    public int getDestinationPort() {
        return destinationPort;
    }

    public void setDestinationPort(int destinationPort) {
        this.destinationPort = destinationPort;
    }

    @Nullable
    private static InetAddress getLocalAddress() {
        try {
            Enumeration e = NetworkInterface.getNetworkInterfaces();

            while (e.hasMoreElements()) {
                NetworkInterface n = (NetworkInterface) e.nextElement();
                Enumeration ee = n.getInetAddresses();

                while (ee.hasMoreElements()) {
                    InetAddress i = (InetAddress) ee.nextElement();
                    String substr = (i.getHostAddress()).substring(0, 3);

                    if (!substr.equals("127") && !substr.equals("0:0") && !substr.equals("fe8")) {
                        return i;
                    }
                }
            }
        } catch (SocketException e1) {
            e1.printStackTrace();
        }

        return null;
    }
}
