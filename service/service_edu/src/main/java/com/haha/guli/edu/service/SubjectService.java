package com.haha.guli.edu.service;

import com.haha.guli.edu.entity.Subject;
import com.baomidou.mybatisplus.extension.service.IService;
import com.haha.guli.edu.entity.vo.SubjectVo;

import java.io.InputStream;
import java.util.List;

/**
 * <p>
 * 课程科目 服务类
 * </p>
 *
 * @author yang
 * @since 2021-08-10
 */
public interface SubjectService extends IService<Subject> {
    /**
     * excel解析
     * @param inputStream
     */
    void batchImport(InputStream inputStream);

    List<SubjectVo> nestedList();

}
