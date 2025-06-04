package com.smileShark.service.imp;

import com.smileShark.common.Result;
import com.smileShark.entity.Subsection;
import com.smileShark.mapper.SubsectionMapper;
import com.smileShark.service.SubsectionService;
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
public class SubsectionServiceImp extends ServiceImpl<SubsectionMapper, Subsection> implements SubsectionService {

    @Override
    public Result selectSubsectionByChapterId(String chapterId) {
        return Result.success("查询成功",lambdaQuery().eq(Subsection::getChapterId, chapterId).list());
    }
}
