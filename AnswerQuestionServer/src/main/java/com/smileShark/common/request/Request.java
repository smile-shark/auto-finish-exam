package com.smileShark.common.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
@Schema(description = "请求对象")
public class Request {
    @Schema(description = "请求类型")
    private Integer identity; // 0 学生，1 教师，2 管理员
    @Schema(description = "用户ID",example = "null")
    private String userId;
    @Schema(description = "用户密码",example = "null")
    private String userPassword;
    @Schema(description = "用户姓名",example = "null")
    private String userName;
    @Schema(description = "问题",example = "mysql")
    private String question;
    @Schema(description = "页数",example = "null")
    private Integer index;
    @Schema(description = "课程名称",example = "null")
    private String selectCourseName;
    @Schema(description = "章节名称",example = "null")
    private String selectChapterName;
    @Schema(description = "小节名称",example = "null")
    private String selectSubsectionName;
    @Schema(description = "课程id",example = "null")
    private String courseId;
    @Schema(description = "章节id",example = "null")
    private String chapterId;
    @Schema(description = "小节id",example = "null")
    private String subsectionId;
}
