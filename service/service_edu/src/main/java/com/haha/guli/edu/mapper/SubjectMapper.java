package com.haha.guli.edu.mapper;

import com.haha.guli.edu.entity.Subject;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.haha.guli.edu.entity.vo.SubjectVo;

import java.util.List;

/**
 * <p>
 * 课程科目 Mapper 接口
 * </p>
 *
 * @author yang
 * @since 2021-08-10
 */
public interface SubjectMapper extends BaseMapper<Subject> {
    /**
     * 批量查询处分类列表
     * @param parentId
     * @return
     */

    List<SubjectVo> selectNestedListByParentId(String parentId);

}
