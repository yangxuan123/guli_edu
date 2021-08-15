package com.haha.guli.edu.feign.fallback;

import com.haha.guli.common.base.R;
import com.haha.guli.edu.feign.VodMediaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class VodMediaServiceFallBack implements VodMediaService {
    @Override
    public R removeVideo(String vodId) {
        log.info("移除视频熔断保护");
        return R.error();
    }

    @Override
    public R removeVideoByIdList(List<String> videoIdList) {
        log.info("批量删除视频触发熔断,ids:{}",videoIdList);
        return R.error().message("调用超时");
    }
}
