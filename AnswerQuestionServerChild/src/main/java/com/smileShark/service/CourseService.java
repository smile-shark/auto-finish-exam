package com.smileShark.service;

import com.smileShark.common.Result;
import com.smileShark.common.request.Request;
import com.smileShark.entity.Course;
import com.baomidou.mybatisplus.extension.service.IService;
import com.smileShark.entity.school.response.*;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author laolee
 * @since 2025年05月31日
 */
public interface CourseService extends IService<Course> {

    Result selectAll();

    SchoolStudentHaveCourseResponse getSchoolStudentHaveCourse(SchoolLoginResponse schoolLoginResponse);
    SchoolStudentCourseInfoResponse getSchoolStudentCourseInfoByCourseId(SchoolLoginResponse schoolLoginResponse,String courseId);
    SchoolTeacherCourseInfoResponse getSchoolTeacherCourseInfoByCourseId(SchoolLoginResponse schoolLoginResponse, String courseId);
    List<SchoolTeacherCourseSimpleInfoResponse> getSchoolTeacherCourseSimpleInfoList(SchoolLoginResponse schoolLoginResponse);

    Result pageList(int page, int size,String courseName);

    Result saveCourseDetailByCourseId(Course course);

    void saveCourseDetail(SchoolStudentCourseInfoResponse courseInfo);
}
