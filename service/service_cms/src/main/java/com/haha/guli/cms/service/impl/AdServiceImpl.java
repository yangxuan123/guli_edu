package com.haha.guli.cms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.haha.guli.cms.entity.Ad;
import com.haha.guli.cms.entity.vo.AdVo;
import com.haha.guli.cms.feign.OssFileService;
import com.haha.guli.cms.mapper.AdMapper;
import com.haha.guli.cms.service.AdService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haha.guli.common.base.R;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.ParameterResolutionDelegate;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 广告推荐 服务实现类
 * </p>
 *
 * @author yang
 * @since 2021-08-13
 */
@Service
public class AdServiceImpl extends ServiceImpl<AdMapper, Ad> implements AdService {
    private static final String AD_PREFIX = "GULI:CMS:AD:";

    @Autowired
    private OssFileService ossFileService;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public IPage<AdVo> selectPage(Long page, Long limit) {

        QueryWrapper<AdVo> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc("a.type_id", "a.sort");

        Page<AdVo> pageParam = new Page<>(page, limit);

        List<AdVo> records = baseMapper.selectPageByQueryWrapper(pageParam, queryWrapper);
        pageParam.setRecords(records);
        return pageParam;
    }

    @Override
    public boolean removeAdImageById(String id) {
        Ad ad = baseMapper.selectById(id);
        if(ad != null) {
            String imagesUrl = ad.getImageUrl();
            if(StringUtils.isNotEmpty(imagesUrl)){
                //删除图片
                R r = ossFileService.removeFile(imagesUrl);
                return r.getSuccess();
            }
        }
        return false;
    }

    @Override
    public List<Ad> selectByAdTypeId(String adTypeId) {
        Object o = redisTemplate.opsForValue().get(AD_PREFIX + adTypeId);
        if (ObjectUtils.isNotEmpty(o)){
            redisTemplate.expire(AD_PREFIX+adTypeId,5,TimeUnit.MINUTES);
            return (List<Ad>) o;
        }
        QueryWrapper<Ad> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc("sort", "id");
        queryWrapper.eq("type_id", adTypeId);
        List<Ad> adList = baseMapper.selectList(queryWrapper);
        redisTemplate.opsForValue().set(AD_PREFIX+adTypeId,adList, 5,TimeUnit.MINUTES);
        return adList;
    }
}
