package com.smileShark.controller;

import com.smileShark.common.Result;
import com.smileShark.service.ChapterService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author laolee
 * @since 2025年05月31日
 */
@RestController
@RequestMapping("/chapter")
@RequiredArgsConstructor
public class ChapterController {
    private final ChapterService chapterService;
    @GetMapping("/list-by-course-id")
    @SecurityRequirement(name = "bearerAuth")
    public Result selectChapterByCourseId(@RequestParam String courseId) {
        return chapterService.selectChapterByCourseId(courseId);
    }
}
