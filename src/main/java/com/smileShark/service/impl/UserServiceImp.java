package com.smileShark.service.impl;

import com.smileShark.entity.User;
import com.smileShark.mapper.UserMapper;
import com.smileShark.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author smileShark
 * @since 2025年06月05日
 */
@Service
public class UserServiceImp extends ServiceImpl<UserMapper, User> implements UserService {

}
