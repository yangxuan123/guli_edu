package com.haha.guli.cms.feign.fallback;

import com.haha.guli.cms.feign.OssFileService;
import com.haha.guli.common.base.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OssFileServiceFallBack implements OssFileService {

    @Override
    public R removeFile(String url) {
        log.info("cms删除图片熔断保护");
        return R.error().message("调用超时");
    }
}
