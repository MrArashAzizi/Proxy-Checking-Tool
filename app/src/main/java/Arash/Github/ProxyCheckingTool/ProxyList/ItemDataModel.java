package Arash.Github.ProxyCheckingTool.ProxyList;

public class ItemDataModel {

    public String getIpPort() {
        return ipPort;
    }

    public void setIpPort(String ipPort) {
        this.ipPort = ipPort;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLast_checked() {
        return last_checked;
    }

    public void setLast_checked(String last_checked) {
        this.last_checked = last_checked;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getProxy_level() {
        return proxy_level;
    }

    public void setProxy_level(String proxy_level) {
        this.proxy_level = proxy_level;
    }

    private String ipPort, country, last_checked, type, proxy_level;
}
