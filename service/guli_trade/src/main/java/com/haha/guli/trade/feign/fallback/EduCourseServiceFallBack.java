package com.haha.guli.trade.feign.fallback;

import com.haha.guli.common.base.R;
import com.haha.guli.service.base.dto.CourseDto;
import com.haha.guli.trade.feign.EduCourseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EduCourseServiceFallBack implements EduCourseService {
    @Override
    public CourseDto getCourseDtoById(String courseId) {
        log.info("熔断保护");
        return null;
    }

    @Override
    public R updateBuyCountById(String id) {
        log.error("熔断器被执行");
        return R.error().message("熔断");
    }
}
