package com.smileShark.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * 早报表
 * </p>
 *
 * @author smileShark
 * @since 2025年06月05日
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("early_morning_report")
@AllArgsConstructor
public class EarlyMorningReport implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 日期
     */
    @TableId(value = "on_day", type = IdType.ASSIGN_UUID)
    private LocalDate onDay;

    @TableField("data")
    private String data;
}
