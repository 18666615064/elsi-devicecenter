package com.iotimc.devicecenter.util;

import com.iotimc.devicecenter.controller.ESController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class WebsocketUtil {

    public static void send(String imei, WebSocketMessage message) {
        ESController.send(imei, message);
    }

    public static void send(String[] imeis, WebSocketMessage message) {
        ESController.send(imeis, message);
    }
}
