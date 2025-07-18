package com.smileShark.entity.news163;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@NoArgsConstructor
@AllArgsConstructor
public class News163OutResponse {
    private Boolean suc;
    @JsonProperty(value = "all_data")
    private List<String> allData;
    private Datas data;

    @Data
    @Component
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Datas {
        private String title;
        private String date;
        private List<String> news;
        private String weiyu;
    }
}
