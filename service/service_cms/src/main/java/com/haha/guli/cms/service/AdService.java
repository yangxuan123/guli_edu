package com.haha.guli.cms.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.haha.guli.cms.entity.Ad;
import com.baomidou.mybatisplus.extension.service.IService;
import com.haha.guli.cms.entity.vo.AdVo;

import java.util.List;

/**
 * <p>
 * 广告推荐 服务类
 * </p>
 *
 * @author yang
 * @since 2021-08-13
 */
public interface AdService extends IService<Ad> {

    IPage<AdVo> selectPage(Long page, Long limit);

    boolean removeAdImageById(String id);

    List<Ad> selectByAdTypeId(String adTypeId);
}
