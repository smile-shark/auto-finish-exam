package com.smileShark.service;

import com.smileShark.common.Result;
import com.smileShark.entity.Chapter;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author laolee
 * @since 2025年05月31日
 */
public interface ChapterService extends IService<Chapter> {

    Result selectChapterByCourseId(String courseId);
}
