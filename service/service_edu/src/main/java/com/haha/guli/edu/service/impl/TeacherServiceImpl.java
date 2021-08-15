package com.haha.guli.edu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.haha.guli.common.base.R;
import com.haha.guli.edu.entity.Course;
import com.haha.guli.edu.entity.Teacher;
import com.haha.guli.edu.entity.vo.TeacherQueryVo;
import com.haha.guli.edu.feign.OssFileService;
import com.haha.guli.edu.mapper.CourseMapper;
import com.haha.guli.edu.mapper.TeacherMapper;
import com.haha.guli.edu.service.TeacherService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.ParameterResolutionDelegate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 讲师 服务实现类
 * </p>
 *
 * @author yang
 * @since 2021-08-10
 */
@Service
public class TeacherServiceImpl extends ServiceImpl<TeacherMapper, Teacher> implements TeacherService {
    private static final String TEACHER_PREFIX = "GULI:EDU:HOT:TEACHER";
    @Autowired
    private OssFileService ossFileService;
    @Autowired
    private CourseMapper courseMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public IPage<Teacher> selectPage(Long page, Long limit, TeacherQueryVo teacherQueryVo) {
        Page<Teacher> pageParam = new Page<>(page, limit);

        LambdaQueryWrapper<Teacher> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Teacher::getSort);

        if (teacherQueryVo == null){
            return baseMapper.selectPage(pageParam, wrapper);
        }

        String name = teacherQueryVo.getName();
        Integer level = teacherQueryVo.getLevel();
        String begin = teacherQueryVo.getJoinDateBegin();
        String end = teacherQueryVo.getJoinDateEnd();
        wrapper.likeRight(StringUtils.isNotEmpty(name),Teacher::getName,name);
        wrapper.eq(level!=null,Teacher::getLevel,level);
        wrapper.ge(StringUtils.isNotEmpty(begin),Teacher::getJoinDate,begin);
        wrapper.le(StringUtils.isNotEmpty(end),Teacher::getJoinDate,end);
        return baseMapper.selectPage(pageParam,wrapper);
    }


    @Override
    public List<Map<String, Object>> selectNameListByKey(String key) {
        //todo 后续使用es的分词进行查询
        QueryWrapper<Teacher> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("name");
        queryWrapper.likeRight("name", key);
        //返回值是Map列表
        List<Map<String, Object>> list = baseMapper.selectMaps(queryWrapper);
        return list;
    }

    @Override
    public boolean removeAvatarById(String id) {
        Teacher teacher = baseMapper.selectById(id);
        if(teacher != null) {
            String avatar = teacher.getAvatar();
            if(!StringUtils.isEmpty(avatar)){
                //删除图片
                R r = ossFileService.removeFile(avatar);
                return r.getSuccess();
            }
        }
        return false;
    }

    @Override
    public Map<String, Object> selectTeacherInfoById(String id) {
        //获取讲师信息
        Teacher teacher = baseMapper.selectById(id);
        //根据讲师id获取讲师课程
        List<Course> courseList =  courseMapper.selectList(new QueryWrapper<Course>().eq("teacher_id", id));

        Map<String, Object> map = new HashMap<>();
        map.put("teacher", teacher);
        map.put("courseList", courseList);
        return map;
    }

    // 按照分数前四名，分数越小越靠前
    @Override
    public List<Teacher> selectHotTeacher() {
        Object o = redisTemplate.opsForValue().get(TEACHER_PREFIX);
        if (ObjectUtils.isNotEmpty(o)){
            redisTemplate.expire(TEACHER_PREFIX,5,TimeUnit.MINUTES);
            return (List<Teacher>) o;
        }
        QueryWrapper<Teacher> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc("sort");
        queryWrapper.last("limit 4");
        List<Teacher> teacherList = baseMapper.selectList(queryWrapper);
        redisTemplate.opsForValue().set(TEACHER_PREFIX,teacherList,5, TimeUnit.MINUTES);
        return teacherList;
    }
}
