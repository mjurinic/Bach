package hr.foi.mjurinic.bach.models;

public class WifiHostInformation {

    private String networkName;
    private String passphrase;
    private String deviceAddress;

    public WifiHostInformation() {
    }

    public WifiHostInformation(String networkName, String passphrase, String deviceAddress) {
        this.networkName = networkName;
        this.passphrase = passphrase;
        this.deviceAddress = deviceAddress;
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

    public String getDeviceAddress() {
        return deviceAddress;
    }

    public void setDeviceAddress(String deviceAddress) {
        this.deviceAddress = deviceAddress;
    }
}
