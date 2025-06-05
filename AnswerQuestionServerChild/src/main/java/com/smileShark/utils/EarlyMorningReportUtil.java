package com.smileShark.utils;

import cn.hutool.json.JSONUtil;
import com.mikuac.shiro.core.Bot;
import com.smileShark.constant.Constant;
import com.smileShark.entity.EarlyMorningReport;
import com.smileShark.entity.news163.News163OutResponse;
import com.smileShark.service.EarlyMorningReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

@Component
@RequiredArgsConstructor
public class EarlyMorningReportUtil {
    private final Constant constant;
    private final RestTemplateUtil restTemplateUtil;
    private final StringRedisTemplate stringRedisTemplate;
    private final EarlyMorningReportService earlyMorningReportService;
    private final RedisLockUtil redisLockUtil;

    private boolean getEarlyMorningLock(){
        // 设置锁防止频繁访问
        boolean lock = redisLockUtil.tryLock("earlyMorningReport", 60);
        if(!lock){
            GlobalBotUtil.bot.sendGroupMsg(
                    constant.REBOT_HANDLER_GROUPS,
                    "接口冷却中",
                    false
            );
        }
        return lock;
    }

    /**
     * 计算需要查询的日期
     * @param dayNum 向前的天数
     * @return 返回日期
     */
    private LocalDate figureDays(int dayNum) {
        // 获取当前日期
        LocalDate currentDate = LocalDate.now();
        // 向前推算 dayNum 天
        return currentDate.minusDays(dayNum);
    }

    /**
     * 获取早报数据
     */
    public News163OutResponse getEarlyMorningReport(Integer dayNum) {
        // 日期作为key，缓存数据
        LocalDate date = figureDays(dayNum);
        // 先在Redis中去查询
        String json = stringRedisTemplate.opsForValue().get(
                RedisKeyUtil.getSimpleKey(
                        constant.PROJECT_NAME,
                        "early_morning_report",
                        date.toString()
                )
        );
        if (json != null) {
            return JSONUtil.toBean(json, News163OutResponse.class);
        }
        // 没有就去mySql中查询
        EarlyMorningReport one = earlyMorningReportService.lambdaQuery().eq(EarlyMorningReport::getOnDay, date).one();
        if(one != null) {
            return JSONUtil.toBean(one.getData(), News163OutResponse.class);
        }

        if(!getEarlyMorningLock()){
            return null;
        }

        // 如果为空就从接口获取
        HashMap<String, Object> params = new HashMap<>();
        params.put("_vercel_no_cache", 1);
        params.put("cache", "any_value");
        params.put("index", dayNum);
        params.put("origin", 163);

        News163OutResponse news163OutResponse = restTemplateUtil.get(
                constant.EARLY_PAPER_URL,
                HttpMethod.GET,
                MediaType.APPLICATION_JSON,
                null,
                News163OutResponse.class,
                null,
                params
        );
        if(news163OutResponse == null){
            return null;
        }
        // 持久化到MySQL
        earlyMorningReportService.save(new EarlyMorningReport(date, JSONUtil.toJsonStr(news163OutResponse)));
        // 缓存到Redis
        stringRedisTemplate.opsForValue().set(
                RedisKeyUtil.getSimpleKey(
                        constant.PROJECT_NAME,
                        "early_morning_report",
                        date.toString()
                ),
                JSONUtil.toJsonStr(news163OutResponse)
        );
        return news163OutResponse;
    }

}
