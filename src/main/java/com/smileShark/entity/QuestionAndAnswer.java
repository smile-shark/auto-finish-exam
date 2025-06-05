package com.smileShark.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
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
@TableName("question_and_answer")
public class QuestionAndAnswer implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "question_id", type = IdType.ASSIGN_UUID)
    private String questionId;

    @TableField("question")
    private String question;

    @TableField("answers")
    private String answers;

    @TableField("start_time")
    private LocalDateTime startTime;
}
