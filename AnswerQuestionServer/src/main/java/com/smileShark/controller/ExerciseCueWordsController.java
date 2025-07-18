package com.smileShark.controller;

import com.smileShark.common.Result;
import com.smileShark.entity.ExerciseCueWords;
import com.smileShark.service.ExerciseCueWordsService;
import com.smileShark.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author smileShark
 * @since 2025年07月18日
 */
@RestController
@RequestMapping("/exerciseCueWords")
@RequiredArgsConstructor
public class ExerciseCueWordsController {
    private final UserService userService;
    private final ExerciseCueWordsService exerciseCueWordsService;

    @GetMapping("/page-list")
    public Result pageList(@RequestParam int page,
                           @RequestParam int size,
                           @RequestParam(required = false) String word) {
        userService.verifyAdmin();
        return exerciseCueWordsService.pageList(page, size, word);
    }

    @PostMapping("/add")
    public Result add(@RequestBody ExerciseCueWords word) {
        userService.verifyAdmin();
        return exerciseCueWordsService.add(word);
    }

    @PutMapping("/update")
    public Result update(@RequestBody ExerciseCueWords word) {
        userService.verifyAdmin();
        return Result.success("更新成功", exerciseCueWordsService.updateById(word));
    }

    @DeleteMapping("/delete")
    public Result delete(@RequestParam String id) {
        userService.verifyAdmin();
        return Result.success("删除成功", exerciseCueWordsService.removeById(id));
    }

}
