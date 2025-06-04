package com.smileShark.utils;

import com.smileShark.constant.Constant;
import com.smileShark.entity.news163.News163OutResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
@RequiredArgsConstructor
public class EarlyMorningReportUtil {
    private final Constant constant;
    private final RestTemplateUtil restTemplateUtil;

    /**
     * 获取早报数据
     */
    public News163OutResponse getEarlyMorningReport(Integer dayNum) {

        HashMap<String, Object> params = new HashMap<>();
        params.put("_vercel_no_cache", 1);
        params.put("cache", "any_value");
        params.put("index", dayNum);
        params.put("origin", 163);

        return restTemplateUtil.get(
                constant.EARLY_PAPER_URL,
                HttpMethod.GET,
                MediaType.APPLICATION_JSON,
                null,
                News163OutResponse.class,
                null,
                params
        );
    }

    public News163OutResponse getEarlyMorningReport() {

        HashMap<String, Object> params = new HashMap<>();
        params.put("_vercel_no_cache", 1);
        params.put("cache", "any_value");
        params.put("index", 0); // 当天的
        params.put("origin", 163);

        return restTemplateUtil.get(
                constant.EARLY_PAPER_URL,
                HttpMethod.GET,
                MediaType.APPLICATION_JSON,
                null,
                News163OutResponse.class,
                null,
                params
        );
    }

}
