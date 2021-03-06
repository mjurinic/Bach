package hr.foi.mjurinic.bach.models;

public class WifiHostInformation {

    private String networkName;
    private String passphrase;
    private String deviceMacAddress;
    private String deviceIpAddress;
    private String devicePort;
    private String b64AESkey;

    public WifiHostInformation() {
    }

    public WifiHostInformation(String networkName, String passphrase, String deviceMacAddress) {
        this.networkName = networkName;
        this.passphrase = passphrase;
        this.deviceMacAddress = deviceMacAddress;
    }

    public String getNetworkName() {
        return networkName;
    }

    public void setNetworkName(String networkName) {
        this.networkName = networkName;
    }

    public String getPassphrase() {
        return passphrase;
    }

    public void setPassphrase(String passphrase) {
        this.passphrase = passphrase;
    }

    public String getDeviceMacAddress() {
        return deviceMacAddress;
    }

    public void setDeviceMacAddress(String deviceMacAddress) {
        this.deviceMacAddress = deviceMacAddress;
    }

    public String getDeviceIpAddress() {
        return deviceIpAddress;
    }

    public void setDeviceIpAddress(String deviceIpAddress) {
        this.deviceIpAddress = deviceIpAddress;
    }

    public String getDevicePort() {
        return devicePort;
    }

    public int getDevicePortAsInt() {
        return Integer.parseInt(devicePort);
    }

    public void setDevicePort(String devicePort) {
        this.devicePort = devicePort;
    }

    public String getB64AESkey() {
        return b64AESkey;
    }

    public void setB64AESkey(String b64AESkey) {
        this.b64AESkey = b64AESkey;
    }
}
