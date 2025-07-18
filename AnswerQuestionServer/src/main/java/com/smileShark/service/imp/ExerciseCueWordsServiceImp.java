package com.smileShark.service.imp;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smileShark.common.Result;
import com.smileShark.entity.ExerciseCueWords;
import com.smileShark.mapper.ExerciseCueWordsMapper;
import com.smileShark.service.ExerciseCueWordsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smileShark.utils.SearchStringUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author smileShark
 * @since 2025年07月18日
 */
@Service
@RequiredArgsConstructor
public class ExerciseCueWordsServiceImp extends ServiceImpl<ExerciseCueWordsMapper, ExerciseCueWords> implements ExerciseCueWordsService {

    @Override
    public Result pageList(int page, int size, String word) {
        return Result.success(
                lambdaQuery()
                        .like(ExerciseCueWords::getContent, SearchStringUtil.handler(word))
                        .page(
                                new Page<>(page, size)
                        )
        );
    }

    @Override
    public Result add(ExerciseCueWords word) {
        word.setExerciseCueWordsId(IdUtil.simpleUUID());
        return Result.success("添加成功", save(word));
    }
}
