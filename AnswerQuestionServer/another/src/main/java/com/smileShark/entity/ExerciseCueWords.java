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
 * @since 2025年07月18日
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("exercise_cue_words")
public class ExerciseCueWords implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "exercise_cue_words_id", type = IdType.ASSIGN_UUID)
    private String exerciseCueWordsId;

    @TableField("content")
    private String content;
}
