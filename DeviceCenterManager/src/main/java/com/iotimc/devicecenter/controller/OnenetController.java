package com.iotimc.devicecenter.controller;

import com.alibaba.fastjson.JSONObject;
import com.iotimc.devicecenter.handler.HandlerFactory;
import com.iotimc.devicecenter.listener.ConfigListener;
import com.iotimc.devicecenter.service.onenet.util.OnenetUtil;
import com.iotimc.elsi.auth.annotation.NoneAuthorize;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 移动接入平台监听回调
 */
@RestController
@RequestMapping("/callback/onenet")
@Slf4j
@NoneAuthorize
@Api(value = "Onenet平台对接接口", description = "提供Onenet平台监听、对接功能")
public class OnenetController {
    @ApiOperation(value="监听onenet数据返回", notes="通过条件进行查询，返回设备分页记录", response = ResponseEntity.class, responseContainer = "List")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ELSIID", value = "与Token绑定的请求标识", required = true, paramType = "header", dataType = "String"),
            @ApiImplicitParam(name="nonce",value="nonce",required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name="msg",value="请求信息",required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name="signature",value="签名",required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name="token",value="token",required = true, paramType = "query", dataType = "String")
    })
    @RequestMapping(value="/listen", method = RequestMethod.GET)
    public ResponseEntity<String> listen(@RequestParam("nonce") String nonce,
                                         @RequestParam("msg") String msg,
                                         @RequestParam("signature")String signature ,
                                         @RequestParam(value = "token", required = false)String token) {
        log.debug("收到来自Onenet平台请求验证：msg: {}, nonce: {}, signature: {}, token: {}", msg, nonce, signature, token);
        signature = signature.replaceAll("\\s", "+");
        OnenetUtil.BodyObj body = new OnenetUtil.BodyObj(msg, nonce, signature);
        // 需要首先取出token
        if(OnenetUtil.checkSignature(ConfigListener.getCompanyBySystemname("onenet").getToken(), body)) {
            log.info("Onenet平台验证成功");
            return ResponseEntity.ok(msg);
        }
        log.error("Onenet平台验证失败");
        return ResponseEntity.ok("Error: not match");
    }

    @RequestMapping(value="/listen", method = RequestMethod.POST)
    public ResponseEntity<String> listen(@RequestBody JSONObject entity) throws Exception {
        //log.debug("收到来自Onenet平台数据：{}", entity.toJSONString());
        OnenetUtil.BodyObj body = OnenetUtil.resolveBody(entity.toJSONString(), false);
        if(!OnenetUtil.checkSignature(ConfigListener.getCompanyBySystemname("onenet").getToken(), body)) {
            log.debug("Onenet: 验证失败,收到的数据是{}", entity.toJSONString());
            return ResponseEntity.ok("");
        }
        // 接收到数据进行处理
        HandlerFactory.handleAll(entity.getJSONObject("msg"));
        return ResponseEntity.ok("");
    }

    @RequestMapping(value="/alarm", method= RequestMethod.POST)
    public ResponseEntity<String> alarm(@RequestBody JSONObject entity) throws Exception {

        return ResponseEntity.ok("");
    }
}
