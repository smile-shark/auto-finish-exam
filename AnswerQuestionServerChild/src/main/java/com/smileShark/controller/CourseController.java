package com.smileShark.controller;

import com.smileShark.common.Result;
import com.smileShark.common.request.Request;
import com.smileShark.entity.Course;
import com.smileShark.service.CourseService;
import com.smileShark.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author laolee
 * @since 2025年05月31日
 */
@RestController
@RequestMapping("/course")
@RequiredArgsConstructor
public class CourseController {
    private final CourseService courseService;
    private final UserService userService;

    @GetMapping("/list")
    @SecurityRequirement(name = "bearerAuth")
    public Result list() {
        return courseService.selectAll();
    }

    @GetMapping("/count")
    @SecurityRequirement(name = "bearerAuth")
    public Result count() {
        userService.verifyAdmin();
        return Result.success("查询成功", courseService.lambdaQuery().count());
    }

    // 获取课程列表
    @GetMapping("/page-list")
    @SecurityRequirement(name = "bearerAuth")
    public Result pageList(@RequestParam int page,@RequestParam int size,@RequestParam(required = false) String courseName) {
        userService.verifyAdmin();
        return courseService.pageList(page, size, courseName);
    }

    // 根据课程获取详细课程信息
    @PostMapping("/admin-course-detail")
    @SecurityRequirement(name = "bearerAuth")
    public Result adminCourseDetail(@RequestBody Course course) {
        return courseService.saveCourseDetailByCourseId(course);
    }

}
