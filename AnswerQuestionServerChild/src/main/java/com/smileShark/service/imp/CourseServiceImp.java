package com.smileShark.service.imp;

import cn.hutool.core.map.MapUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smileShark.common.Result;
import com.smileShark.common.request.Request;
import com.smileShark.constant.Constant;
import com.smileShark.entity.Chapter;
import com.smileShark.entity.Course;
import com.smileShark.entity.Subsection;
import com.smileShark.entity.User;
import com.smileShark.entity.school.SchoolResult;
import com.smileShark.entity.school.response.*;
import com.smileShark.mapper.CourseMapper;
import com.smileShark.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smileShark.utils.RestTemplateUtil;
import com.smileShark.utils.SearchStringUtil;
import com.smileShark.utils.ThreadUtils;
import com.smileShark.utils.TokenUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Future;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author laolee
 * @since 2025年05月31日
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CourseServiceImp extends ServiceImpl<CourseMapper, Course> implements CourseService {
    private final RestTemplateUtil restTemplateUtil;
    private final Constant constant;
    private final UserService userService;
    private final ChapterService chapterService;
    private final SubsectionService subsectionService;

    @Override
    public Result selectAll() {
        return Result.success("课程获取成功", query().list());
    }

    @Override
    public SchoolStudentHaveCourseResponse getSchoolStudentHaveCourse(SchoolLoginResponse schoolLoginResponse) {
        return restTemplateUtil.get(
                constant.STUDENT_HAVE_COURSE_URL,
                HttpMethod.GET,
                MediaType.APPLICATION_JSON,
                null,
                new ParameterizedTypeReference<SchoolResult<SchoolStudentHaveCourseResponse>>() {
                },
                TokenUtil.montage(schoolLoginResponse),
                null
        ).getData();
    }

    @Override
    public SchoolStudentCourseInfoResponse getSchoolStudentCourseInfoByCourseId(SchoolLoginResponse schoolLoginResponse, String courseId) {
        SchoolResult<SchoolStudentCourseInfoResponse> courseInfo = restTemplateUtil.get(
                constant.STUDENT_GET_COURSE_INFO_URL,
                HttpMethod.GET,
                MediaType.APPLICATION_JSON,
                null,
                new ParameterizedTypeReference<>() {
                },
                TokenUtil.montage(schoolLoginResponse),
                MapUtil.of("CourseID", courseId));
        if (!courseInfo.getSuccess()) {
            System.out.println(courseInfo.getMsg());
        }
        return courseInfo.getData();
    }

    @Override
    public SchoolTeacherCourseInfoResponse getSchoolTeacherCourseInfoByCourseId(SchoolLoginResponse schoolLoginResponse, String courseId) {
        SchoolResult<SchoolTeacherCourseInfoResponse> courseInfo = restTemplateUtil.get(
                constant.TEACHER_PASS_SUBSECTION_EXAM_URL,
                HttpMethod.GET,
                MediaType.APPLICATION_JSON,
                null,
                new ParameterizedTypeReference<>() {
                },
                TokenUtil.montage(schoolLoginResponse),
                MapUtil.of("ECourseId", courseId));
        if (!courseInfo.getSuccess()) {
            System.out.println(courseInfo.getMsg());
        }
        return courseInfo.getData();
    }

    @Override
    public List<SchoolTeacherCourseSimpleInfoResponse> getSchoolTeacherCourseSimpleInfoList(SchoolLoginResponse schoolLoginResponse) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("LVID", "fe2bb098419f4474a621413c14eddafe");
        map.put("Key", null);
        map.put("TopCount", "99999");

        return restTemplateUtil.get(
                constant.TEACHER_COURSE_LIST_URL,
                HttpMethod.GET,
                MediaType.APPLICATION_JSON,
                null,
                new ParameterizedTypeReference<SchoolResult<List<SchoolTeacherCourseSimpleInfoResponse>>>() {
                },
                TokenUtil.montage(schoolLoginResponse),
                map
        ).getData();
    }

    @Override
    public Result pageList(int page, int size, String courseName) {
        if(courseName==null){
            courseName="";
        }
        courseName= SearchStringUtil.handler(courseName);
        return Result
                .success()
                .setMessage("课程分页获取成功")
                .setData(lambdaQuery()
                        .like(Course::getCourseName, courseName)
                        .page(new Page<>(page,size)));
    }

    @Override
    public Result saveCourseDetailByCourseId(Course course) {
        // 随机在数据库中获取一个测试账号
        User user = userService.lambdaQuery().eq(User::getIsTest, 1).last("order by rand() limit 1").one();
        SchoolLoginResponse studentToken = userService.getStudentToken(user.getUserId(), user.getUserPassword());
        if (studentToken == null) {
            return Result.error("获取学生token失败");
        }
        // 获取课程详情
        SchoolStudentCourseInfoResponse courseInfo = getSchoolStudentCourseInfoByCourseId(studentToken, course.getCourseId());
        saveCourseDetail(courseInfo);
        return Result.success("课程详情保存成功");
    }

    @Override
    public void saveCourseDetail(SchoolStudentCourseInfoResponse courseInfo) {
        for (SchoolStudentCourseInfoResponse.Chapter chapter : courseInfo.getChapters()) {
            boolean addChapter = chapterService.saveOrUpdate(new Chapter() {{
                setCourseId(courseInfo.getId());
                setChapterId(chapter.getId());
                setChapterName(chapter.getTitleContent());
                setChapterTitle(chapter.getTitle());
            }});
            // 如果添加成功了就添加小节
            if (addChapter) {
                for (SchoolStudentCourseInfoResponse.Knowledge subsection : chapter.getKnowledgeList()) {
                    subsectionService.saveOrUpdate(new Subsection() {{
                        setCourseId(courseInfo.getId());
                        setChapterId(chapter.getId());
                        setSubsectionId(subsection.getId());
                        setSubsectionName(subsection.getKnowledge());
                    }});
                }
            }
        }
    }
}
