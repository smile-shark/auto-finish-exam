package com.smileShark.service.imp;

import com.smileShark.common.Result;
import com.smileShark.entity.Chapter;
import com.smileShark.mapper.ChapterMapper;
import com.smileShark.service.ChapterService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author laolee
 * @since 2025年05月31日
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChapterServiceImp extends ServiceImpl<ChapterMapper, Chapter> implements ChapterService {

    @Override
    public Result selectChapterByCourseId(String courseId) {
        return Result.success("查询成功",lambdaQuery().eq(Chapter::getCourseId,courseId).list());
    }
}
