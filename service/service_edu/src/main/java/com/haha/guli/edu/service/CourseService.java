package com.haha.guli.edu.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.haha.guli.edu.entity.Course;
import com.baomidou.mybatisplus.extension.service.IService;
import com.haha.guli.edu.entity.form.CourseInfoForm;
import com.haha.guli.edu.entity.vo.*;
import com.haha.guli.service.base.dto.CourseDto;

import java.util.List;

/**
 * <p>
 * 课程 服务类
 * </p>
 *
 * @author yang
 * @since 2021-08-10
 */
public interface CourseService extends IService<Course> {

    /**
     * 保存课程和课程详情信息
     * @param courseInfoForm
     * @return 新生成的课程id
     */
    String saveCourseInfo(CourseInfoForm courseInfoForm);

    CourseInfoForm getCourseInfoById(String id);

    void updateCourseInfoById(CourseInfoForm courseInfoForm);

    IPage<CourseVo> selectPage(Long page, Long limit, CourseQueryVo courseQueryVo);

    boolean removeCoverById(String id);

    boolean removeCourseById(String id);

    CoursePublishVo getCoursePublishVoById(String id);

    boolean publishCourseById(String id);

    List<Course> webSelectList(WebCourseQueryVo webCourseQueryVo);

    /**
     * 获取课程信息并更新浏览量
     * @param id
     * @return
     */
    WebCourseVo selectWebCourseVoById(String id);

    List<Course> selectHotCourse();

    CourseDto getCourseDtoById(String courseId);

    void updateBuyCountById(String id);
}
