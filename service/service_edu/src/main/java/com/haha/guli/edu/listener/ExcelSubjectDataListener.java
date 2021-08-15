package com.haha.guli.edu.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.haha.guli.edu.entity.Subject;
import com.haha.guli.edu.entity.excel.ExcelSubjectData;
import com.haha.guli.edu.mapper.SubjectMapper;
import com.haha.guli.edu.service.SubjectService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@AllArgsConstructor //全参
@NoArgsConstructor //无参
public class ExcelSubjectDataListener extends AnalysisEventListener<ExcelSubjectData> {

    /**
     * 假设这个是一个DAO，当然有业务逻辑这个也可以是一个service。当然如果不用存储这个对象没用。
     * todo 后续考虑批量
     */
    private SubjectMapper subjectMapper;



    /**
     * 这个每一条数据解析都会来调用
     */
    @Override
    public void invoke(ExcelSubjectData data, AnalysisContext context) {
        log.info("解析到一条数据:{}", data);
        //处理读取进来的数据
        String titleLevelOne = data.getLevelOneTitle();
        String titleLevelTwo = data.getLevelTwoTitle();

        //判断一级分类是否重复
        Subject subjectLevelOne = this.getByTitle(titleLevelOne);
        String parentId = null;
        if(subjectLevelOne == null) {
            //将一级分类存入数据库
            Subject subject = new Subject();
            subject.setParentId("0");
            subject.setTitle(titleLevelOne);//一级分类名称
            subjectMapper.insert(subject);
            parentId = subject.getId();
        }else{
            parentId = subjectLevelOne.getId();
        }

        //判断二级分类是否重复
        Subject subjectLevelTwo = this.getSubByTitle(titleLevelTwo, parentId);
        if(subjectLevelTwo == null){
            //将二级分类存入数据库
            Subject subject = new Subject();
            subject.setTitle(titleLevelTwo);
            subject.setParentId(parentId);
            subjectMapper.insert(subject);//添加
        }
    }

    /**
     * 所有数据解析完成了 都会来调用
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        log.info("------所有数据解析完成-------");
    }

    /**
     * 根据分类名称查询这个一级分类是否存在
     * @param title
     * @return
     */
    private Subject getByTitle(String title) {
        LambdaQueryWrapper<Subject> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Subject::getTitle,title);
        // 一级分类
        wrapper.eq(Subject::getParentId,0);
        return subjectMapper.selectOne(wrapper);
    }

    /**
     * 根据分类名称和父id查询这个二级分类是否存在
     * @param title
     * @return
     */
    private Subject getSubByTitle(String title, String parentId) {

        LambdaQueryWrapper<Subject> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Subject::getTitle, title);
        queryWrapper.eq(Subject::getParentId, parentId);
        return subjectMapper.selectOne(queryWrapper);
    }
}
