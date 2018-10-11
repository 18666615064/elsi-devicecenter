package com.iotimc.devicecenter.listener;

import com.iotimc.devicecenter.dao.DevTimeTaskRepository;
import com.iotimc.devicecenter.domain.DevTimeTaskEntity;
import com.iotimc.devicecenter.util.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class QuartzListener implements InitializingBean {
    @Autowired
    private DevTimeTaskRepository timedTaskRepository;

    @Autowired
    private Scheduler scheduler;


    /**
     * 一个小时钟刷一次
     */
    private static final long DELAY = 3600000;

    @Override
    public void afterPropertiesSet() throws Exception {
        new Thread() {
            @Override
            public void run() {
                try {
                    while (true) {
                        Thread.sleep(20000);
                        refreshJob();
                        // 自动刷新间隔
                        Thread.sleep(DELAY);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * 重新加载定时任务
     */
    public void refreshJob() {
        refreshJob(null);
    }

    /**
     * 刷新某个定时任务
     *
     * @param id 任务id
     */
    public void refreshJob(Integer id) {

        List<DevTimeTaskEntity> tasklist = null;
        tasklist = timedTaskRepository.getRunList(id);
        try {
            if (id == null) scheduler.clear();
            else if (!tasklist.isEmpty()) {
                DevTimeTaskEntity task = tasklist.get(0);
                JobKey key = new JobKey(task.getService());
                scheduler.deleteJob(key);
                log.info("Quartz: 删除任务计划[{}]成功", task.getName());
            }
        } catch (Exception e) {
            log.error("Quartz: 清除/删除计划任务失败: ", e);
        }
        log.info("Quartz: 计划任务数量[{}]", tasklist.size());
        tasklist.forEach(task -> {
            try {
                // 为什么不放在查询语句，因为要在上面去掉无效的任务
                if (!task.getStatus().equals("P")) return;
                Object bean = SpringUtil.getBean(task.getService());
                Class jobClazz = Class.forName(bean.getClass().getCanonicalName());
                if (bean instanceof Job) {
                    JobDetail jobDetail = JobBuilder.newJob(jobClazz).withIdentity(task.getService()).build();
                    CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(task.getCron());
                    CronTrigger cronTrigger = TriggerBuilder.newTrigger().withIdentity(task.getService()).withSchedule(scheduleBuilder).build();
                    scheduler.scheduleJob(jobDetail, cronTrigger);
                }
            } catch (Exception e) {
                log.error("Quartz: 添加计划任务[{}]失败: ", task.getName(), e);
            }
        });
        log.info("Quartz: 刷新计划任务成功");
    }
}
