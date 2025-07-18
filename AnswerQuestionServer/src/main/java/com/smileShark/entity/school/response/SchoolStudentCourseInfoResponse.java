package com.smileShark.entity.school.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@NoArgsConstructor
@AllArgsConstructor
public class SchoolStudentCourseInfoResponse {
    private List<Chapter> chapters;
    private String courseName;
    private String id;
    @Data
    @Component
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Chapter {
        private String id;
        private String title; // 章节
        private String titleContent; // 标题
        private List<Knowledge> knowledgeList; // 小节
    }
    @Data
    @Component
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Knowledge{
        private Integer examNum; // 题目数量
        private String id;
        private String knowledge; // 小节的标题
        private TestMemberInfo testMemberInfo; // 做题信息，如果是null，标识没有做过
    }
    @Data
    @Component
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TestMemberInfo{
        private Integer times; // 做过的题目次数
        private Boolean isPass; // null 标识还未提交过
    }
}
