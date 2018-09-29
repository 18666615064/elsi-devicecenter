package com.iotimc.devicecenter.listener;

import com.alibaba.fastjson.JSONObject;
import com.iotimc.devicecenter.config.RabbitMQProperties;
import com.iotimc.devicecenter.domain.CompanyConfig;
import com.iotimc.devicecenter.domain.DevDeviceEntity;
import com.iotimc.devicecenter.domain.DeviceCache;
import com.iotimc.devicecenter.service.DeviceService;
import com.iotimc.devicecenter.util.MQBody;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


/**
 * 处理向onenet发送的请求任务
 */
@Component
@Slf4j
public class MQListener {
    @Autowired
    private RabbitMQProperties rabbitMQProperties;

    @Autowired
    @Qualifier("onenet")
    private DeviceService onenetService;

    @Autowired
    @Qualifier("easyiot")
    private DeviceService easyiotService;

    @Value("${config.retry}")
    private int retry;

    @RabbitListener(queues = "datacentercommand")
    @RabbitHandler
    public void consumeMessage(Message message) {
        try {
                String body = new String(message.getBody());
                log.debug("MQ: 消息监听器接收到消息[{}]",body);
                try {
                    doSend(MQBody.parse(body), message.getMessageProperties());
                } catch (Exception e) {
                    log.error("MQ: 处理消息: {}:{} 失败,原因: {}", message.getMessageProperties().getAppId(), body, e.getMessage());
                }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    @RabbitListener(queues = "topic.datacenterdata.test2")
//    @RabbitHandler
//    public void recv1(Message message) {
//        String body = new String(message.getBody());
//        log.info("recv2收到消息：" + body);
//    }

    public void doSend(MQBody body, MessageProperties message) {
        JSONObject data = body.getData();
        String method = data.getString("method");
        int times = 0;
        switch (method) {
            case "send":
                DeviceCache device = DeviceListener.getDeviceByImei(data.getString("imei"));
                if (device != null) {
                    do {
                        String result = null;
                        CompanyConfig company = ConfigListener.getCompanyById(device.getCompanyfk());
                        if(company.getSystemname().equalsIgnoreCase("onenet"))
                            result = onenetService.syncSend(data);
                        else if(company.getSystemname().equalsIgnoreCase("easyiot"))
                            result = easyiotService.syncSend(data);
                        JSONObject resultObj = (JSONObject) JSONObject.parse(result);
                        if ("0".equals(resultObj.getString("errno"))) break;
                        times++;
                        if (times < retry) {
                            log.debug("MQ: 发送控制命令失败:[{}:{}:{}]，进行第{}次重试", message.getAppId(), body, resultObj.getString("error"), times);
                        } else {
                            log.debug("MQ: 发送控制命令超过重试次数:['{}']", message.getAppId());
                        }

                    } while (times < retry);
                }
        }
    }
}

