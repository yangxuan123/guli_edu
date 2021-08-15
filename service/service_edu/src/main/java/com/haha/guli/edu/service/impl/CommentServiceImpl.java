package com.haha.guli.edu.service.impl;

import com.haha.guli.edu.entity.Comment;
import com.haha.guli.edu.mapper.CommentMapper;
import com.haha.guli.edu.service.CommentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 评论 服务实现类
 * </p>
 *
 * @author yang
 * @since 2021-08-10
 */
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

}
