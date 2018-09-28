package com.iotimc.devicecenter.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(RabbitMQProperties.PREFIX)
public class RabbitMQProperties {
    public static final String PREFIX = "spring.rabbitmq";
    private Boolean enable;
    private String host;
    private Integer port;
    private String username;
    private String password;
    private String publisherConfirm;
    private String virtualHost;
    private Integer defaultTopic;
    private String commandTopic;
    private String dataTopic;
    private String loginTopic;

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPublisherConfirm() {
        return publisherConfirm;
    }

    public void setPublisherConfirm(String publisherConfirm) {
        this.publisherConfirm = publisherConfirm;
    }

    public String getVirtualHost() {
        return virtualHost;
    }

    public void setVirtualHost(String virtualHost) {
        this.virtualHost = virtualHost;
    }

    public Integer getDefaultTopic() {
        return defaultTopic;
    }

    public void setDefaultTopic(Integer defaultTopic) {
        this.defaultTopic = defaultTopic;
    }

    public String getCommandTopic() {
        return commandTopic;
    }

    public void setCommandTopic(String commandTopic) {
        this.commandTopic = commandTopic;
    }

    public String getDataTopic() {
        return dataTopic;
    }

    public void setDataTopic(String dataTopic) {
        this.dataTopic = dataTopic;
    }

    public String getLoginTopic() {
        return loginTopic;
    }

    public void setLoginTopic(String loginTopic) {
        this.loginTopic = loginTopic;
    }
}
