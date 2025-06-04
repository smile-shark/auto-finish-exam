package com.smileShark.service;

import com.smileShark.common.Result;
import com.smileShark.common.request.Request;
import com.smileShark.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.smileShark.entity.school.response.SchoolLoginResponse;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author laolee
 * @since 2025年05月31日
 */
public interface UserService extends IService<User> {

    Result login(Request request);

    SchoolLoginResponse teacherLogin(String account, String password);
    SchoolLoginResponse studentLogin(String account, String password);
    SchoolLoginResponse getStudentToken(String account, String password);
    SchoolLoginResponse getTeacherToken(String account, String password);
    void saveToken(String account,SchoolLoginResponse response);

    Result getUserInfo();

    void verifyAdmin();

    Result pageList(Integer page, Integer size, String account, String userName, Integer isTest, Integer identity);
}
