package com.smileShark.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * 用户表
 * </p>
 *
 * @author smileShark
 * @since 2025年06月01日
 */
@Data
@Getter
@Setter
@Accessors(chain = true)
@TableName("user")
public class User implements Serializable {

    @TableId(value = "user_id", type = IdType.ASSIGN_UUID)
    private String userId;

    @TableField("username")
    private String username;

    @TableField("user_password")
    private String userPassword;

    @TableField("is_test")
    private Integer isTest;

    @TableField("class_name")
    private String className;

    @TableField("class_id")
    private String classId;

    @TableField("teacher_name")
    private String teacherName;

    @TableField("teacher_id")
    private String teacherId;

    @TableField("level_id")
    private String levelId;

    @TableField("level_name")
    private String levelName;

    @TableField("school_id")
    private String schoolId;

    @TableField("school_name")
    private String schoolName;

    /**
     * 身份：0学生，1教师，2管理
     */
    @TableField("identity")
    private Integer identity;

    @TableField("token")
    private String token;

    @TableField("token_create_time")
    private LocalDateTime tokenCreateTime;
}
