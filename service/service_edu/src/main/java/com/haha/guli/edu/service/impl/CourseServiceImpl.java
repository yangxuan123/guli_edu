package com.haha.guli.edu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.haha.guli.common.base.R;
import com.haha.guli.edu.entity.*;
import com.haha.guli.edu.entity.form.CourseInfoForm;
import com.haha.guli.edu.entity.vo.*;
import com.haha.guli.edu.feign.OssFileService;
import com.haha.guli.edu.mapper.*;
import com.haha.guli.edu.service.CourseService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haha.guli.service.base.dto.CourseDto;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 课程 服务实现类
 * </p>
 *
 * @author yang
 * @since 2021-08-10
 */
@Service
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements CourseService {
    public static final String COURSE_PREFIX = "GULI:EDU:HOTCOURSE";

    @Autowired
    private VideoMapper videoMapper;
    @Autowired
    private ChapterMapper chapterMapper;
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private CourseCollectMapper courseCollectMapper;
    @Autowired
    private OssFileService ossFileService;
    @Autowired
    private CourseDescriptionMapper courseDescriptionMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public String saveCourseInfo(CourseInfoForm courseInfoForm) {

        //保存课程基本信息
        Course course = new Course();
        course.setStatus(Course.COURSE_DRAFT);
        BeanUtils.copyProperties(courseInfoForm, course);
        baseMapper.insert(course);

        //保存课程详情信息
        CourseDescription courseDescription = new CourseDescription();
        courseDescription.setDescription(courseInfoForm.getDescription());
        courseDescription.setId(course.getId());
        courseDescriptionMapper.insert(courseDescription);

        return course.getId();
    }

    @Override
    public CourseInfoForm getCourseInfoById(String id) {
        //todo 后续改成多表联查

        //从course表中取数据
        Course course = baseMapper.selectById(id);
        if (course == null) {
            return null;
        }

        //从course_description表中取数据
        CourseDescription courseDescription = courseDescriptionMapper.selectById(id);

        //创建courseInfoForm对象
        CourseInfoForm courseInfoForm = new CourseInfoForm();
        BeanUtils.copyProperties(course, courseInfoForm);
        courseInfoForm.setDescription(courseDescription.getDescription());

        return courseInfoForm;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateCourseInfoById(CourseInfoForm courseInfoForm) {
        //保存课程基本信息
        Course course = new Course();
        BeanUtils.copyProperties(courseInfoForm, course);
        baseMapper.updateById(course);

        //保存课程详情信息
        CourseDescription courseDescription = new CourseDescription();
        courseDescription.setDescription(courseInfoForm.getDescription());
        courseDescription.setId(course.getId());
        courseDescriptionMapper.updateById(courseDescription);
    }

    @Override
    public IPage<CourseVo> selectPage(Long page, Long limit, CourseQueryVo courseQueryVo) {
        QueryWrapper<CourseVo> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("c.gmt_create");
        String title = courseQueryVo.getTitle();
        String teacherId = courseQueryVo.getTeacherId();
        String subjectParentId = courseQueryVo.getSubjectParentId();
        String subjectId = courseQueryVo.getSubjectId();
        queryWrapper.like(!StringUtils.isEmpty(title), "c.title", title);
        queryWrapper.eq(!StringUtils.isEmpty(teacherId), "c.teacher_id", teacherId);
        queryWrapper.eq(!StringUtils.isEmpty(subjectParentId), "c.subject_parent_id", subjectParentId);
        queryWrapper.eq(!StringUtils.isEmpty(subjectId), "c.subject_id", subjectId);
        Page<CourseVo> pageParam = new Page<>(page, limit);
        //放入分页参数和查询条件参数，mp会自动组装
        List<CourseVo> records = baseMapper.selectPageByCourseQueryVo(pageParam, queryWrapper);
        pageParam.setRecords(records);
        return pageParam;
    }
    @Override
    public boolean removeCoverById(String id) {
        Course course = baseMapper.selectById(id);
        if(course != null) {
            String cover = course.getCover();
            if(!StringUtils.isEmpty(cover)){
                //删除图片
                R r = ossFileService.removeFile(cover);
                return r.getSuccess();
            }
        }
        return false;
    }
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean removeCourseById(String id) {

        //收藏信息：course_collect
        LambdaQueryWrapper<CourseCollect> courseCollectQueryWrapper = new LambdaQueryWrapper<>();
        courseCollectQueryWrapper.eq(CourseCollect::getCourseId, id);
        courseCollectMapper.delete(courseCollectQueryWrapper);
        //评论信息：comment
        LambdaQueryWrapper<Comment> commentQueryWrapper = new LambdaQueryWrapper<>();
        commentQueryWrapper.eq(Comment::getCourseId, id);
        commentMapper.delete(commentQueryWrapper);

        //课时信息：video
        LambdaQueryWrapper<Video> videoQueryWrapper = new LambdaQueryWrapper<>();
        videoQueryWrapper.eq(Video::getCourseId, id);
        videoMapper.delete(videoQueryWrapper);

        //章节信息：chapter
        LambdaQueryWrapper<Chapter> chapterQueryWrapper = new LambdaQueryWrapper<>();
        chapterQueryWrapper.eq(Chapter::getCourseId, id);
        chapterMapper.delete(chapterQueryWrapper);

        //课程详情：course_description
        courseDescriptionMapper.deleteById(id);

        //课程信息：course
        return this.removeById(id);
    }

    @Override
    public CoursePublishVo getCoursePublishVoById(String id) {
        return baseMapper.selectCoursePublishVoById(id);
    }

    @Override
    public boolean publishCourseById(String id) {
        Course course = new Course();
        course.setId(id);
        course.setStatus(Course.COURSE_NORMAL);
        return this.updateById(course);
    }

    @Override
    public List<Course> webSelectList(WebCourseQueryVo webCourseQueryVo) {

        LambdaQueryWrapper<Course> queryWrapper = new LambdaQueryWrapper<>();

        //查询已发布的课程
        queryWrapper.eq(Course::getStatus, Course.COURSE_NORMAL);
        // 拼接查询条件
        queryWrapper.eq(StringUtils.isNotEmpty(webCourseQueryVo.getSubjectParentId()),Course::getSubjectParentId, webCourseQueryVo.getSubjectParentId());
        queryWrapper.eq(StringUtils.isNotEmpty(webCourseQueryVo.getSubjectId()),Course::getSubjectId,webCourseQueryVo.getSubjectId());
        queryWrapper.orderByDesc(StringUtils.isNotEmpty(webCourseQueryVo.getGmtCreateSort()),Course::getGmtCreate);
        if (StringUtils.isNotEmpty(webCourseQueryVo.getPriceSort())) {
            if(webCourseQueryVo.getType() == null || webCourseQueryVo.getType() == 1){
                queryWrapper.orderByAsc(Course::getPrice);
            }else{
                queryWrapper.orderByDesc(Course::getPrice);
            }
        }
        return baseMapper.selectList(queryWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public WebCourseVo selectWebCourseVoById(String id) {
        //更新课程浏览数
        Course course = baseMapper.selectById(id);
        course.setViewCount(course.getViewCount() + 1);
        baseMapper.updateById(course);
        //获取课程信息
        return baseMapper.selectWebCourseVoById(id);
    }

    @Override
    public List<Course> selectHotCourse() {
        Object o = redisTemplate.opsForValue().get(COURSE_PREFIX);
        if (ObjectUtils.isNotEmpty(o)){
            // 延长至5分钟
            redisTemplate.expire(COURSE_PREFIX,5,TimeUnit.MINUTES);
            return (List<Course>) o;
        }
        QueryWrapper<Course> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("view_count");
        queryWrapper.last("limit 8");
        List<Course> courseList = baseMapper.selectList(queryWrapper);
        redisTemplate.opsForValue().set(COURSE_PREFIX,courseList,5, TimeUnit.MINUTES);
        return courseList;
    }

    @Override
    public CourseDto getCourseDtoById(String courseId) {
        return baseMapper.selectCourseDtoById(courseId);
    }

    @Override
    public void updateBuyCountById(String id) {
        Course course = baseMapper.selectById(id);
        course.setBuyCount(course.getBuyCount() + 1);
        this.updateById(course);
    }

}
