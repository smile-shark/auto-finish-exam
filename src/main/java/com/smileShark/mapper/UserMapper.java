package com.smileShark.mapper;

import com.smileShark.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @author smileShark
 * @since 2025年06月01日
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}
