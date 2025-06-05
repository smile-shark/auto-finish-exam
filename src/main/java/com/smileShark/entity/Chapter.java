package com.smileShark.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author smileShark
 * @since 2025年06月05日
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("chapter")
public class Chapter implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "chapter_id", type = IdType.ASSIGN_UUID)
    private String chapterId;

    @TableField("chapter_title")
    private String chapterTitle;

    @TableField("chapter_name")
    private String chapterName;

    @TableField("course_id")
    private String courseId;
}
