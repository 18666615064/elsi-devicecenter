package com.iotimc.devicecenter.listener;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.iotimc.devicecenter.dao.DevDeviceEntityRepository;
import com.iotimc.devicecenter.domain.CompanyConfig;
import com.iotimc.devicecenter.domain.DevDeviceEntity;
import com.iotimc.devicecenter.domain.DeviceCache;
import com.iotimc.devicecenter.service.DeviceService;
import com.iotimc.devicecenter.util.RedisUtil;
import com.iotimc.devicecenter.util.Tool;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * 初始化设备信息，将设备信息放入到redis中
 */
@Service
@Slf4j
public class DeviceListener implements InitializingBean{
    @Autowired
    private RedisUtil redisUtil_prv;

    private static RedisUtil redisUtil;
    public static final String DEVICES= "devices";
    public static final String DEVID = "devid";
    public static final String PLATFORMID = "platformid";
    public static final String COMMCACHE = "commandcache";
    public static final int ONLINE = 1;
    public static final int OFFLINE = 0;


    @Autowired
    private DevDeviceEntityRepository devDeviceEntityRepository_prv;
    private static DevDeviceEntityRepository devDeviceEntityRepository;

    @Qualifier("onenet")
    @Autowired
    private DeviceService onenetDevice_prv;
    private static DeviceService onenetDevice;

    @Qualifier("easyiot")
    @Autowired
    private DeviceService easyiotDevice_prv;
    private static DeviceService easyiotDevice;

    /**
     * 刷新设备列表，将设备放到缓存里面
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        redisUtil = redisUtil_prv;
        devDeviceEntityRepository = devDeviceEntityRepository_prv;
        onenetDevice = onenetDevice_prv;
        easyiotDevice = easyiotDevice_prv;
        List<DevDeviceEntity> list = devDeviceEntityRepository.getAllList();
        list.forEach(item -> {
            putCacheItem(item);
        });
        new Thread() {
            @Override
            public void run() {
                try {
                    // 8秒后开始刷状态
                    Thread.sleep(8000);
                } catch(Exception e) {}
                refreshOnlineStatus();
            }
        }.start();
        log.info("初始化设备列表成功");
    }

    private static DeviceCache putCacheItem(DevDeviceEntity item) {
        return putCacheItem(item, OFFLINE);
    }

    private static DeviceCache putCacheItem(DevDeviceEntity item, int onlinestatus) {
        DeviceCache cache = null;
        if(redisUtil.hHasKey(DEVICES, item.getImei()))
            cache = (DeviceCache) redisUtil.hget(DEVICES, item.getImei());
        else
            cache = new DeviceCache();
        cache.setCode(item.getCode());
        cache.setCompanyfk(item.getCompanyfk());
        cache.setCretime(item.getCretime());
        cache.setId(item.getId());
        cache.setImei(item.getImei());
        cache.setImsi(item.getImsi());
        cache.setName(item.getName());
        cache.setOnlinestatus(onlinestatus);
        cache.setPlatformid(item.getPlatformid());
        cache.setProductfk(item.getProductfk());
        cache.setStatus(item.getStatus());
        cache.setUpdatetime(Tool.getNowTiestamp());
        setDeviceCache(cache);
        return cache;
    }

    /**
     * 设置在线状态
     * @param imei
     * @param status
     */
    public static void setDeviceStatus(String imei, int status) {
        if(status == ONLINE || status == OFFLINE) {
            DeviceCache cache = getDeviceByImei(imei);
            if(cache != null) {
                cache.setOnlinestatus(status);
                setDeviceCache(cache);
            }
        }
    }

    /**
     * 设置设备缓存信息
     * @param cache
     */
    public static void setDeviceCache(DeviceCache cache) {
        String imei = cache.getImei();
        if(!StringUtils.isBlank(imei) && cache.getId() != 0 ) {
            cache.setUpdatetime(Tool.getNowTiestamp());
            redisUtil.hset(DEVICES, imei, cache);
            redisUtil.hset(DEVID, String.valueOf(cache.getId()), imei);
            if(!StringUtils.isBlank(cache.getPlatformid()))redisUtil.hset(PLATFORMID, String.valueOf(cache.getPlatformid()), imei);
        }
    }

    public static void refreshOnlineStatus() {
        List<DevDeviceEntity> list = devDeviceEntityRepository.getAllList();
        list.forEach(item -> {
            try {
                refreshOnlineStatus(item);
            } catch(Exception e) {
                log.error("设备状态获取失败IMEI:{}[{}]", item.getImei(), e.getMessage());
            }
        });
        log.info("初始化: 初始化设备状态完成");
    }

    /**
     * 获取某个设备的在线状态并保存到缓存
     * @param item
     * @return
     */
    private static DeviceCache refreshOnlineStatus(DevDeviceEntity item) {
        CompanyConfig company = ConfigListener.getCompanyById(item.getCompanyfk());
        if(company.getSystemname().equalsIgnoreCase("onenet")) {
            String resultStr = onenetDevice.getStatus(item.getImei(), item.getPlatformid());
            JSONObject result = (JSONObject) JSONObject.parse(resultStr);
            if(result.getInteger("errno") == 0 && result.containsKey("data")) {
                JSONArray devices = result.getJSONObject("data").getJSONArray("devices");
                for(int index = devices.size()-1; index>=0; index--) {
                    JSONObject device = devices.getJSONObject(index);
                    DeviceCache cache = getDeviceByImei(item.getImei());
                    int online = device.getBoolean("online")?ONLINE:OFFLINE;
                    if(cache != null) {
                        cache.setOnlinestatus(online);
                        setDeviceCache(cache);
                        return cache;
                    } else {
                        return putCacheItem(item, online);
                    }
                }
            } else {
                log.error("设备状态获取失败IMEI:{}[{}]", item.getImei(), result.getString("error"));
            }
        } else if(company.getSystemname().equalsIgnoreCase("easyiot")) {
            easyiotDevice.getStatus(item.getImei(), item.getPlatformid());
            //TODO: 未实现easyiot的状态获取
        }
        return null;
    }

    /**
     * 根据imei获取缓存中的设备
     * @param imei
     * @return
     */
    public static DeviceCache getDeviceByImei(String imei) {
        if(redisUtil.hHasKey(DEVICES, imei)) {
            return (DeviceCache) redisUtil.hget(DEVICES, imei);
        }
        return null;
    }

    /**
     * 根据设备id获取缓存中的设备
     * @param id
     * @return
     */
    public static DeviceCache getDeviceById(Integer id) {
        Object imei = redisUtil.hget(DEVID, String.valueOf(id));
        if(imei != null) {
            return getDeviceByImei((String) imei);
        }
        return null;
    }

    /**
     * 根据设备的平台id获取设备
     * @param id
     * @return
     */
    public static DeviceCache getDeviceByPlatformid(Integer id) {
        Object imei = redisUtil.hget(PLATFORMID, String.valueOf(id));
        if(imei != null) {
            return getDeviceByImei((String) imei);
        }
        return null;
    }

    /**
     * 刷新redis中的缓存数据
     * @param id
     */
    public static void refreshCache(Integer id) {
        Optional<DevDeviceEntity> obj = devDeviceEntityRepository.findById(id);
        if(obj == null) return;
        DevDeviceEntity entity = obj.get();
        DeviceCache cache = refreshOnlineStatus(entity);
        if(cache == null) {
            //如果是删除的情况下
            redisUtil.hdel(DEVICES, entity.getImei());
            redisUtil.hdel(DEVID, String.valueOf(id));
            redisUtil.hdel(PLATFORMID, entity.getPlatformid());
        }
    }

    /**
     * 删除redis中的缓存数据
     * @param id
     */
    public static void delCacheDeviceById(Integer id) {
        Optional<DevDeviceEntity> obj = devDeviceEntityRepository.findById(id);
        if(obj == null) return;
        DevDeviceEntity entity = obj.get();
        redisUtil.hdel(DEVICES, entity.getImei());
        redisUtil.hdel(DEVID, String.valueOf(entity.getId()));
        redisUtil.hdel(PLATFORMID, entity.getPlatformid());
    }

    /**
     * 放置设备操作指令缓存
     * @param imei
     * @param propname
     * @param type
     * @param uuid
     */
    public static void putCommandCache(String imei, String propname, String type, String uuid, Integer controllogid) {
        if(!redisUtil.hHasKey(COMMCACHE, imei)) {
            //创建缓存
            DeviceCache device = getDeviceByImei(imei);
            CompanyConfig company = ConfigListener.getCompanyById(device.getCompanyfk());
            JSONObject cache = new JSONObject();
            cache.put("imei", imei);
            cache.put("devicefk", device.getId());
            cache.put("platformid", device.getPlatformid());
            cache.put("platform", company.getSystemname());
            cache.put("productfk", device.getProductfk());
            cache.put("controllist", new JSONArray());
            cache.put("controllogid", controllogid);
            redisUtil.hset(COMMCACHE, imei, cache);
        }
        JSONObject cache = (JSONObject) redisUtil.hget(COMMCACHE, imei);
        JSONArray controllist = cache.getJSONArray("controllist");
        //TODO 检查是否存在同样的命令，如果有则覆盖,并记录起操作日志

    }
}
