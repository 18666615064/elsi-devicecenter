package com.iotimc.devicecenter.service.onenet.util;

import ch.qos.logback.core.rolling.helper.IntegerTokenConverter;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.iotimc.devicecenter.dao.DevControllogRepository;
import com.iotimc.devicecenter.domain.DevControllogEntity;
import com.iotimc.devicecenter.domain.DevProductdtlEntity;
import com.iotimc.devicecenter.domain.DeviceCache;
import com.iotimc.devicecenter.listener.ConfigListener;
import com.iotimc.devicecenter.listener.DeviceListener;
import com.iotimc.devicecenter.util.RedisUtil;
import com.iotimc.devicecenter.util.Tool;
import com.iotimc.elsi.auth.bean.UserToken;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.*;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
@Slf4j
public class OnenetUtil {
    private static DevControllogRepository controllogRepository;
    @Autowired
    private DevControllogRepository controllogRepository_priv;

    @Autowired
    private RedisUtil redisUtil_priv;

    private static RedisUtil redisUtil;

    private static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    @PostConstruct
    public void init() {
        controllogRepository = controllogRepository_priv;
        redisUtil = redisUtil_priv;
    }

    private static DevControllogEntity saveControllog(String imei, String type, String name, Object val, int usetime, String result) {
        return saveControllog(imei, type, name, val, usetime, result, null);
    }

    /**
     * 保存控制日志
     * @param imei
     * @param type
     * @param name
     * @param val
     * @param usetime
     * @param result
     * @param asyncuuid
     */
    private static DevControllogEntity saveControllog(String imei, String type, String name, Object val, int usetime, String result, String asyncuuid) {
        try {
            DeviceCache device = DeviceListener.getDeviceByImei(imei);
            DevProductdtlEntity prop = ConfigListener.getPropByDevImeiName(imei, name);
            if (device != null) {
                // value 处理
                String value = null;
                if (val instanceof Map) {
                    value = ((JSONObject) JSONObject.toJSON(val)).toJSONString();
                } else if (val instanceof List) {
                    value = ((JSONArray) JSONArray.toJSON(val)).toJSONString();
                } else {
                    value = String.valueOf(val);
                }
                DevControllogEntity entity = new DevControllogEntity();
                entity.setResult(result);
                entity.setDevicefk(device.getId());
                entity.setType(type);
                entity.setCretime(new Timestamp(System.currentTimeMillis() - usetime));
                entity.setUsetime(usetime);
                entity.setValue(value);
                entity.setName(name);
                entity.setImei(imei);
                entity.setProductdtlfk(prop != null? prop.getId() : null);
                entity.setAsyncuuid(asyncuuid);
                controllogRepository.save(entity);
                return entity;
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 在onenet平台上添加设备
     *
     * @param imei
     * @param imsi
     * @return
     */
    public static String addDevice(String url, String apikey, String imei, String imsi) {
        long before = System.currentTimeMillis();
        Map<String, Object> params = new HashMap<String, Object>();
        Map<String, String> auth_info = new HashMap<String, String>();
        auth_info.put(imei, imsi);
        params.put("title", imei);
        params.put("protocol", "LWM2M");
        params.put("auth_info", auth_info);
        params.put("obsvr", true);
        String result = doPost(url + "/devices", params, apikey);
        saveControllog(imei, "addDevice", null, null, (int) (System.currentTimeMillis() - before), result);
        return result;
    }

    /**
     * 获取设备状态
     *
     * @param ids 设备id列表
     * @return
     * @example 1, 2, 3
     */
    public static String getDeviceStatus(String url, String apikey, String ids) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("devIds", ids);
        return doGet(url + "/devices/status", params, apikey);
    }

    /**
     * 获取缓存指令信息
     *
     * @param uuid
     * @return
     */
    public static String getCacheInstruction(String url, String apikey, String uuid, String imei) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("imei", imei);
        return doGet(url + "/nbiot/offline/history/" + uuid, params, apikey);
    }

    /**
     * 删除缓存指令
     *
     * @param uuid
     * @return
     */
    public static String deleteOffline(String url, String apikey, String uuid, String imei) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("imei", imei);
        return doPut(url + "/nbiot/offline/cancel/" + uuid, params, apikey);
    }

    /**
     * 删除设备
     * @param url
     * @param apikey
     * @param platformid 平台设备id
     * @return
     */
    public static String delete(String url, String apikey, String platformid) {
        return doDelete(url + "/devices/" + platformid, null, apikey);
    }


    /**
     * 设置缓存模式onenet平台的值
     *
     * @param imei
     * @param objid
     * @param resid
     * @param insid
     * @param value
     * @return
     */
    public static String send(String url, String apikey, String imei, Integer objid, Integer resid, Byte insid, Object value, Integer timeout) {
        long before = System.currentTimeMillis();
        DevProductdtlEntity dtl = ConfigListener.getPropByDevImeiDsid(imei, objid, resid, insid);
        value = Tool.convert(value, dtl.getType(), dtl.getIshex());
        Map<String, String> httpParams = new HashMap();
        httpParams.put("imei", imei);
        httpParams.put("obj_id", String.valueOf(objid));
        httpParams.put("obj_inst_id", String.valueOf(insid));
        httpParams.put("mode", "2");
        httpParams.put("expired_time", formatter.format(new Date(System.currentTimeMillis() + (timeout==null?3600000:timeout))));

        Map<String, Object> params = new HashMap();
        List data = new ArrayList();
        Map<String, Object> data0 = new HashMap();
        data0.put("res_id", resid);
        data0.put("val", value);
        data.add(data0);
        params.put("data", data);
        String result = doPost(url + "/nbiot/offline", params, httpParams, apikey);
        JSONObject resultJson = (JSONObject) JSONObject.parse(result);
        if(resultJson.getString("errno").equalsIgnoreCase("0")) {
            String uuid = resultJson.getJSONObject("data").getString("uuid");
            DevControllogEntity loz = saveControllog(imei, "send", dtl.getName(), value, (int) (System.currentTimeMillis() - before), result, uuid);
            DeviceListener.putCommandCache(imei, dtl.getName(), "send", uuid, loz.getId());
        } else {
            DevControllogEntity loz = saveControllog(imei, "send", dtl.getName(), value, (int) (System.currentTimeMillis() - before), result, null);
        }
        return result;
    }

    /**
     * 设置onenet平台的值
     *
     * @param imei
     * @param objid
     * @param resid
     * @param value
     * @return
     */
    public static String sendSync(String url, String apikey, String imei, Integer objid, Integer resid, Byte insid, Object value) {
        DevProductdtlEntity dtl = ConfigListener.getPropByDevImeiDsid(imei, objid, resid, insid);
        value = Tool.convert(value, dtl.getType(), dtl.getIshex());
        Map<String, String> httpParams = new HashMap();
        httpParams.put("imei", imei);
        httpParams.put("obj_id", String.valueOf(objid));
        httpParams.put("obj_inst_id", String.valueOf(insid));
        httpParams.put("mode", "2");

        Map<String, Object> params = new HashMap();
        List data = new ArrayList();
        Map<String, Object> data0 = new HashMap();
        data0.put("res_id", resid);
        data0.put("val", value);
        data.add(data0);
        params.put("data", data);
        return doPost(url + "/nbiot", params, httpParams, apikey);
    }

    /**
     * 根据属性名称设置onenet平台的值
     *
     * @param imei
     * @param dsid
     * @param value
     * @return
     */
    public static String send(String url, String apikey, String imei, String dsid, Object value) {
        if (dsid == null) return "{\"error\": \"属性名不存在\", \"errno\": -1}";
        Integer objid = Integer.parseInt(dsid.split("_")[0]);
        Byte insid = Byte.parseByte(dsid.split("_")[1]);
        Integer resid = Integer.parseInt(dsid.split("_")[2]);
        String result = send(url, apikey, imei, objid, resid, insid, value, null);
        return result;
    }

    /**
     * 异步读取属性
     * @param url
     * @param apikey
     * @param imei
     * @param objid
     * @param resid
     * @param insid
     * @return
     */
    public static String readProps(String url, String apikey, String imei, Integer objid, Integer resid, Byte insid, Integer timeout) {
        Map<String, String> httpParams = new HashMap();
        httpParams.put("imei", imei);
        httpParams.put("obj_id", String.valueOf(objid));
        httpParams.put("res_id", String.valueOf(resid));
        httpParams.put("obj_inst_id", String.valueOf(insid));
        // 默认超时60分钟
        if(timeout == null) timeout = 60 * 60 * 1000;
        else timeout = timeout * 60 * 1000;
        httpParams.put("expired_time", formatter.format(Tool.long2date(new Date().getTime() + timeout)));
        String result = doGet(url + "/nbiot/offline", httpParams, apikey);
        return result;
    }

    /**
     * 读取属性
     * @param url
     * @param apikey
     * @param imei
     * @param objid
     * @param resid
     * @param insid
     * @return
     */
    public static String readPropsSync(String url, String apikey, String imei, Integer objid, Integer resid, Byte insid) {
        long before = System.currentTimeMillis();
        DevProductdtlEntity prop = ConfigListener.getPropByDevImeiDsid(imei, objid, resid, insid);
        Map<String, String> httpParams = new HashMap();
        httpParams.put("imei", imei);
        httpParams.put("obj_id", String.valueOf(objid));
        httpParams.put("res_id", String.valueOf(resid));
        httpParams.put("obj_inst_id", String.valueOf(insid));
        String result = doGet(url + "/nbiot", httpParams, apikey);
        String uuid = "";
        DevControllogEntity loz = saveControllog(imei, "read", prop.getName(), null, (int) (System.currentTimeMillis() - before), result, uuid);
        DeviceListener.putCommandCache(imei, "read", prop.getName(), uuid, loz.getId());
        return result;
    }

    /**
     * 读取属性
     * @param url
     * @param apikey
     * @param imei
     * @param dsid
     * @return
     */
    public static String readPropsSync(String url, String apikey, String imei, String dsid) {
        if (dsid == null) return "{\"error\": \"属性名不存在\", \"errno\": -1}";
        Integer objid = Integer.parseInt(dsid.split("_")[0]);
        Byte insid = Byte.parseByte(dsid.split("_")[1]);
        Integer resid = Integer.parseInt(dsid.split("_")[2]);
        String result = readPropsSync(url, apikey, imei, objid, resid, insid);

        return result;
    }
/*
    public static String signature(String nonce, String msg) {
        String signature = "";
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            BASE64Encoder base64en = new BASE64Encoder();
            signature = base64en.encode(md5.digest((token + nonce + msg).getBytes("utf-8")));
        } catch(Exception e) {
            e.printStackTrace();
        }
        return signature;
    }*/

    /**
     * 解密
     * @param data
     * @return
     */
   /* public static String decode(String data) {
       *//* String result = "";
        byte[] decoding = Base64.getDecoder().decode(encodingAESKey + "=");
        byte[] iv = new byte[16];
        System.arraycopy(decoding, 0, iv, 0, 16);

        return result;*//*
       return data;
    }*/

    /**
     * 通过http参数格式化url地址
     * @param httpParams
     * @param url
     * @return
     */
    private static String setUrl(Map httpParams, String url) {
        if (httpParams != null) {
            if (!url.contains("?")) {
                url += "?";
            }
            if (!httpParams.isEmpty()) {
                url += Tool.joinHttpParam(httpParams, "&");
            }
        }
        return url;
    }

    /**
     * 发送GET请求
     *
     * @param url    地址
     * @param params url参数
     * @return
     */
    public static String doGet(String url, Map<String, String> params, String apikey) {
        try {
            if (!url.contains("?")) {
                url += "?";
            }
            if (!params.isEmpty()) {
                url += Tool.joinHttpParam(params, "&");
            }
            log.debug("=GET=========================\n正在请求：{}", url);
            java.net.URL urlObj = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.addRequestProperty("api-key", apikey);

            conn.connect();
            int code = conn.getResponseCode();
            if (code == 200) {
                return receive(conn);
            }
            return "{\"errno\": \"" + code + "\", \"message\":\"" + receive(conn, true) + "\"}";
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"errno\": -1,\"error\": \"" + e.getMessage() + "\"}";
        }
    }

    /**
     * 发送PUT请求
     *
     * @param url        地址
     * @param httpParams url参数
     * @return
     */
    public static String doPut(String url, Map<String, String> httpParams, String apikey) {
        try {
            url = setUrl(httpParams, url);
            log.debug("=PUT=========================\n正在请求：{}", url);
            URL urlObj = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();
            conn.setRequestMethod("PUT");
            conn.setConnectTimeout(5000);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.addRequestProperty("api-key", apikey);

            conn.connect();
            int code = conn.getResponseCode();
            if (code == 200) {
                return receive(conn);
            }
            return "{\"errno\": \"" + code + "\", \"message\":\"" + receive(conn, true) + "\"}";
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"errno\":-1,\"error\": \"" + e.getMessage() + "\"}";
        }
    }

    /**
     * 发送POST请求
     *
     * @param url    地址
     * @param params 消息体
     * @return
     */
    public static String doPost(String url, Map<String, Object> params, String apikey) {
        return doPost(url, params, null, apikey);
    }

    /**
     * 发送POST请求
     *
     * @param url        地址
     * @param params     消息体
     * @param httpParams url参数
     * @return
     */
    public static String doPost(String url, Map<String, Object> params, Map<String, String> httpParams, String apikey) {
        return doPost(url, params, httpParams, null, apikey);
    }

    /**
     * 发送POST请求
     *
     * @param url        地址
     * @param params     消息体
     * @param httpParams url参数
     * @param headParams 请求头
     * @return
     */
    public static String doPost(String url, Map<String, Object> params, Map<String, String> httpParams, Map<String, String> headParams, String apikey) {
        try {
            url = setUrl(httpParams, url);
            log.debug("=POST=========================\n正在请求：{}", url);
            URL urlObj = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(5000);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.addRequestProperty("api-key", apikey);
            conn.addRequestProperty("Content-Type", "application/json");
            if (params != null) {
                OutputStream os = conn.getOutputStream();
                os.write(JSON.toJSONString(params).getBytes());
                os.flush();
            }
            if (headParams != null) {
                for (Map.Entry<String, String> item : headParams.entrySet()) {
                    conn.addRequestProperty(item.getKey(), item.getValue());
                }
            }
            conn.connect();
            int code = conn.getResponseCode();
            if (code == 200) {
                return receive(conn);
            }
            return "{\"errno\": \"" + code + "\", \"message\":\"" + receive(conn, true) + "\"}";
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"errno\":-1,\"error\": \"" + e.getMessage() + "\"}";
        }
    }

    /**
     * 删除的请求
     * @param url
     * @param httpParams
     * @param apikey
     * @return
     */
    public static String doDelete(String url, Map<String, String> httpParams, String apikey) {
        try {
            url = setUrl(httpParams, url);
            log.debug("=DELETE=========================\n正在请求：{}", url);
            URL urlObj = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();
            conn.setRequestMethod("DELETE");
            conn.setConnectTimeout(5000);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.addRequestProperty("api-key", apikey);
            conn.addRequestProperty("Content-Type", "application/json");
            conn.connect();
            int code = conn.getResponseCode();
            if (code == 200) {
                return receive(conn);
            }
            return "{\"errno\": \"" + code + "\", \"message\":\"" + receive(conn, true) + "\"}";
        } catch(Exception e) {
            e.printStackTrace();
            return "{\"errno\":-1,\"error\": \"" + e.getMessage() + "\"}";
        }
    }

    /**
     * 接收处理返回数据
     * @param conn
     * @return
     * @throws Exception
     */
    private static String receive(HttpURLConnection conn) throws Exception {
        return receive(conn,false);
    }

    /**
     * 接收处理返回数据
     *
     * @param conn 连接信息
     * @param error 是否错误信息
     * @return
     * @throws Exception
     */
    private static String receive(HttpURLConnection conn, boolean error) throws Exception {
        if(error) return "";
        InputStream is = conn.getInputStream();
        String result = "";
        byte[] buffer = new byte[2048];
        byte[] total = new byte[0];
        int size = -1;
        while ((size = is.read(buffer)) != -1) {
            byte[] temp = new byte[size + total.length];
            System.arraycopy(total, 0, temp, 0, total.length);
            System.arraycopy(buffer, 0, temp, total.length, size);
            total = temp;
        }
        result = new String(total, "UTF-8");

        JSONObject parse = (JSONObject) JSONObject.parse(result);
        if (parse.getInteger("errno") == 5106) {
            parse.put("error", "连接超时");
        } else if (parse.getInteger("errno") == 2001) {
            parse.put("error", "设备离线");
        }

        result = parse.toString();
        log.debug("返回结果：{}\n===========================", result);

        return result;
    }

    // 下方开始是提供签名验证、解密的方法
    private static MessageDigest mdInst;

    static {
        try {
            mdInst = MessageDigest.getInstance("MD5");
            Security.addProvider(new BouncyCastleProvider());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }


    /**
     * 功能描述:在OneNet平台配置数据接收地址时，平台会发送URL&token验证请求<p>
     * 使用此功能函数验证token
     *
     * @param msg       请求参数 <msg>的值
     * @param nonce     请求参数 <nonce>的值
     * @param signature 请求参数 <signature>的值
     * @return token检验成功返回true；token校验失败返回false
     */
    public static boolean checkToken(String token, String msg, String nonce, String signature) throws UnsupportedEncodingException {

        byte[] paramB = new byte[token.length() + 8 + msg.length()];
        System.arraycopy(token.getBytes(), 0, paramB, 0, token.length());
        System.arraycopy(nonce.getBytes(), 0, paramB, token.length(), 8);
        System.arraycopy(msg.getBytes(), 0, paramB, token.length() + 8, msg.length());
        String sig = com.sun.org.apache.xerces.internal.impl.dv.util.Base64.encode(mdInst.digest(paramB));
        //log.debug("url&token validation: result {},  detail receive:{} calculate:{}", sig.equals(signature.replace(' ','+')),signature,sig);
        return sig.equals(signature.replace(' ', '+'));
    }

    /**
     * 功能描述: 检查接收数据的信息摘要是否正确。<p>
     * 方法非线程安全。
     *
     * @param obj 消息体对象
     * @return
     */
    public static boolean checkSignature(String token, BodyObj obj) {
        //计算接受到的消息的摘要
        //token长度 + 8B随机字符串长度 + 消息长度
        byte[] signature = new byte[token.length() + 8 + obj.getMsg().toString().length()];
        System.arraycopy(token.getBytes(), 0, signature, 0, token.length());
        System.arraycopy(obj.getNonce().getBytes(), 0, signature, token.length(), 8);
        System.arraycopy(obj.getMsg().toString().getBytes(), 0, signature, token.length() + 8, obj.getMsg().toString().length());
        mdInst.update(signature);
        String calSig = Base64.encodeBase64String(mdInst.digest());
        //log.debug("check signature: result:{}  receive sig:{},calculate sig: {}",calSig.equals(obj.getMsgSignature()),obj.getMsgSignature(),calSig);
        return calSig.equals(obj.getMsgSignature());
    }

    /**
     * 功能描述 解密消息
     *
     * @param obj 消息体对象
     * @return
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidAlgorithmParameterException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    public static String decryptMsg(String encodingAESKey, BodyObj obj) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        byte[] encMsg = Base64.decodeBase64(obj.getMsg().toString());
        byte[] aeskey = Base64.decodeBase64(encodingAESKey + "=");
        SecretKey secretKey = new SecretKeySpec(aeskey, 0, 32, "AES");
        Cipher cipher = null;
        cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(aeskey, 0, 16));
        byte[] allmsg = cipher.doFinal(encMsg);
        byte[] msgLenBytes = new byte[4];
        System.arraycopy(allmsg, 16, msgLenBytes, 0, 4);
        int msgLen = getMsgLen(msgLenBytes);
        byte[] msg = new byte[msgLen];
        System.arraycopy(allmsg, 20, msg, 0, msgLen);
        return new String(msg);
    }

    /**
     * 功能描述 解析数据推送请求，生成code>BodyObj</code>消息对象
     *
     * @param body      数据推送请求body部分
     * @param encrypted 表征是否为加密消息
     * @return 生成的<code>BodyObj</code>消息对象
     */
    public static BodyObj resolveBody(String body, boolean encrypted) {
        JSONObject jsonMsg = (JSONObject) JSONObject.parseObject(body);
        BodyObj obj = new BodyObj();
        obj.setNonce(jsonMsg.getString("nonce"));
        obj.setMsgSignature(jsonMsg.getString("msg_signature").replaceAll("\\s", "+"));
        if (encrypted) {
            if (!jsonMsg.containsKey("enc_msg")) {
                return null;
            }
            obj.setMsg(jsonMsg.getString("enc_msg"));
        } else {
            if (!jsonMsg.containsKey("msg")) {
                return null;
            }
            obj.setMsg(jsonMsg.getJSONObject("msg"));
        }
        return obj;
    }

    private static int getMsgLen(byte[] arrays) {
        int len = 0;
        len += (arrays[0] & 0xFF) << 24;
        len += (arrays[1] & 0xFF) << 16;
        len += (arrays[2] & 0xFF) << 8;
        len += (arrays[3] & 0xFF);
        return len;
    }


    public static class BodyObj {
        private Object msg;
        private String nonce;
        private String msgSignature;

        public BodyObj() {

        }

        public BodyObj(Object msg, String nonce, String msgSignature) {
            this.msg = msg;
            this.nonce = nonce;
            this.msgSignature = msgSignature;
        }

        public Object getMsg() {
            return msg;
        }

        public void setMsg(Object msg) {
            this.msg = msg;
        }

        public String getNonce() {
            return nonce;
        }

        public void setNonce(String nonce) {
            this.nonce = nonce;
        }

        public String getMsgSignature() {
            return msgSignature;
        }

        public void setMsgSignature(String msgSignature) {
            this.msgSignature = msgSignature;
        }

        public String toString() {
            return "{ \"msg\":" + this.msg + "，\"nonce\":" + this.nonce + "，\"signature\":" + this.msgSignature + "}";
        }

    }
}
