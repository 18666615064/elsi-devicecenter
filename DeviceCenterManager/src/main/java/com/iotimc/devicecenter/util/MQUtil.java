package com.iotimc.devicecenter.util;

import com.iotimc.devicecenter.config.RabbitMQProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * MQ工具
 */
@Component
@Slf4j
public class MQUtil {
    /**
     * 数据主题
     */
    public static final Integer DATATOPIC = 1;
    /**
     * 登录主题
     */
    public static final Integer LOGINTOPIC = 2;
    /**
     * 命令主题
     */
    public static final Integer COMMANDTOPIC = 3;

    @Autowired
    private RabbitMQProperties properties;

    @Autowired
    private AmqpTemplate template;

    public void send(MQBody body) {
        if(!properties.getEnable()) {
            log.warn("MQ: 尝试请求发送MQ消息失败，系统未启用MQ服务。");
            return;
        }
        log.debug("发送MQ消息：[{}]", body.toString());
        String topic = body.getQueue() == DATATOPIC?properties.getDataTopic():body.getQueue() == LOGINTOPIC?properties.getLoginTopic():properties.getCommandTopic();
        MessageProperties props = new MessageProperties();
        props.setAppId(Tool.uuid());
        if(body.getPersistent()) {
            props.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
            props.setExpiration("300000");
        }
        Message message = new Message(body.toString().getBytes(), props);
        if(body.getIstopic())
            template.send(topic, "topic." + topic+".default", message);
        else
            template.send(topic, message);
    }
}
