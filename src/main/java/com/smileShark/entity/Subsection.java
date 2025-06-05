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
@TableName("subsection")
public class Subsection implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "subsection_id", type = IdType.ASSIGN_UUID)
    private String subsectionId;

    @TableField("subsection_name")
    private String subsectionName;

    @TableField("course_id")
    private String courseId;

    @TableField("chapter_id")
    private String chapterId;
}
