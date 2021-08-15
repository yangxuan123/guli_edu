package com.haha.guli.edu.feign.fallback;

import com.haha.guli.common.base.R;
import com.haha.guli.edu.feign.OssFileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OssFileServiceFallBack implements OssFileService {


    @Override
    public R removeFile(String url) {
        log.info("熔断保护");
        return R.error();
    }
}
