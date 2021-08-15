package com.haha.guli.edu.service;

import com.haha.guli.edu.entity.Chapter;
import com.baomidou.mybatisplus.extension.service.IService;
import com.haha.guli.edu.entity.vo.ChapterVo;

import java.util.List;

/**
 * <p>
 * 课程 服务类
 * </p>
 *
 * @author yang
 * @since 2021-08-10
 */
public interface ChapterService extends IService<Chapter> {

    boolean removeChapterById(String id);

    List<ChapterVo> nestedList(String courseId);
}
