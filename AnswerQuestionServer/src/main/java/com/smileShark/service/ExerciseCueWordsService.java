package com.smileShark.service;

import com.smileShark.common.Result;
import com.smileShark.entity.ExerciseCueWords;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author smileShark
 * @since 2025年07月18日
 */
public interface ExerciseCueWordsService extends IService<ExerciseCueWords> {

    Result pageList(int page, int size, String word);

    Result add(ExerciseCueWords word);
}
