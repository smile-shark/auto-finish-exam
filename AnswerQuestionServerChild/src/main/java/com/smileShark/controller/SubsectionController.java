package com.smileShark.controller;

import com.smileShark.common.Result;
import com.smileShark.service.SubsectionService;
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
@RequestMapping("/subsection")
@RequiredArgsConstructor
public class SubsectionController {
    private final SubsectionService subsectionService;
    @GetMapping("/list-by-chapter-id")
    @SecurityRequirement(name = "bearerAuth")
    public Result selectSubsectionByChapterId(@RequestParam  String chapterId) {
        return subsectionService.selectSubsectionByChapterId(chapterId);
    }
}
