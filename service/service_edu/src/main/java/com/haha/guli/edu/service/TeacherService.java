package com.haha.guli.edu.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.haha.guli.edu.entity.Teacher;
import com.baomidou.mybatisplus.extension.service.IService;
import com.haha.guli.edu.entity.vo.TeacherQueryVo;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 讲师 服务类
 * </p>
 *
 * @author yang
 * @since 2021-08-10
 */
public interface TeacherService extends IService<Teacher> {

    /**
     * 根据条件分页查询
     * @param page
     * @param limit
     * @param teacherQueryVo
     * @return
     */
    IPage<Teacher> selectPage(Long page, Long limit, TeacherQueryVo teacherQueryVo);

    List<Map<String, Object>> selectNameListByKey(String key);

    /**
     * feign 远程删除头像
     * @param id
     * @return
     */
    boolean removeAvatarById(String id);

    /**
     * 根据讲师id获取讲师详情页数据
     * @param id
     * @return
     */
    Map<String, Object> selectTeacherInfoById(String id);

    List<Teacher> selectHotTeacher();

}
