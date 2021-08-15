package com.haha.guli.edu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.haha.guli.edu.entity.Chapter;
import com.haha.guli.edu.entity.Video;
import com.haha.guli.edu.entity.vo.ChapterVo;
import com.haha.guli.edu.entity.vo.VideoVo;
import com.haha.guli.edu.mapper.ChapterMapper;
import com.haha.guli.edu.mapper.VideoMapper;
import com.haha.guli.edu.service.ChapterService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 课程 服务实现类
 * </p>
 *
 * @author yang
 * @since 2021-08-10
 */
@Service
public class ChapterServiceImpl extends ServiceImpl<ChapterMapper, Chapter> implements ChapterService {

    @Autowired
    private VideoMapper videoMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean removeChapterById(String id) {

        //课时信息：video
       LambdaQueryWrapper<Video> videoQueryWrapper = new LambdaQueryWrapper<>();
        videoQueryWrapper.eq(Video::getChapterId, id);
        videoMapper.delete(videoQueryWrapper);
        //章节信息：chapter
        return this.removeById(id);
    }

    @Override
    public List<ChapterVo> nestedList(String courseId) {

        //获取章信息
        LambdaQueryWrapper<Chapter> queryWrapperChapter = new LambdaQueryWrapper<>();
        queryWrapperChapter.eq(Chapter::getCourseId, courseId);
        queryWrapperChapter.orderByAsc(Chapter::getSort,Chapter::getId);
        List<Chapter> chapterList = baseMapper.selectList(queryWrapperChapter);

        //获取课时信息
        LambdaQueryWrapper<Video> queryWrapperVideo = new LambdaQueryWrapper<>();
        queryWrapperVideo.eq(Video::getCourseId, courseId);
        queryWrapperVideo.orderByAsc(Video::getSort,Video::getId);
        List<Video> videoList = videoMapper.selectList(queryWrapperVideo);
        //lamda表达式进行过滤
        Map<String, List<VideoVo>> videoMap = videoList.stream()
                .map(video -> {
                    VideoVo videoVo = new VideoVo();
                    BeanUtils.copyProperties(video, videoVo);
                    return videoVo;
                })
                .collect(Collectors.groupingBy(VideoVo::getChapterId));
        List<ChapterVo> chapterVoList = chapterList.stream()
                .map(chapter -> {
                    ChapterVo chapterVo = new ChapterVo();
                    BeanUtils.copyProperties(chapter, chapterVo);
                    chapterVo.setChildren(videoMap.get(chapter.getId()));
                    return chapterVo;
                }).collect(Collectors.toList());

        return chapterVoList;
    }
}
