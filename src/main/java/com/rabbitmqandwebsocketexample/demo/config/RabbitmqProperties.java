package com.rabbitmqandwebsocketexample.demo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "rabbitmq",ignoreNestedProperties = false)
public class RabbitmqProperties {
    private String address;
    private String userName;
    private String password;
    private Boolean publisherConfirms;
    private String virtualHost;
    private Boolean druable;

    public Boolean getDruable() {
        return druable;
    }

    public void setDruable(Boolean druable) {
        this.druable = druable;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getPublisherConfirms() {
        return publisherConfirms;
    }

    public void setPublisherConfirms(Boolean publisherConfirms) {
        this.publisherConfirms = publisherConfirms;
    }

    public String getVirtualHost() {
        return virtualHost;
    }

    public void setVirtualHost(String virtualHost) {
        this.virtualHost = virtualHost;
    }

    @Override
    public String toString() {
        return "RabbitmqProperties{" +
                "address='" + address + '\'' +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", publisherConfirms=" + publisherConfirms +
                ", virtualHost='" + virtualHost + '\'' +
                ", druable=" + druable +
                '}';
    }
}
