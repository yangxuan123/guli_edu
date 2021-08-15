package com.haha.guli.edu.service;

import com.haha.guli.edu.entity.CourseCollect;
import com.baomidou.mybatisplus.extension.service.IService;
import com.haha.guli.edu.entity.vo.CourseCollectVo;

import java.util.List;

/**
 * <p>
 * 课程收藏 服务类
 * </p>
 *
 * @author yang
 * @since 2021-08-10
 */
public interface CourseCollectService extends IService<CourseCollect> {

    boolean isCollect(String courseId, String id);

    void saveCourseCollect(String courseId, String id);

    List<CourseCollectVo> selectListByMemberId(String memberId);

    boolean removeCourseCollect(String courseId, String memberId);
}
