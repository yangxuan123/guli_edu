package com.haha.guli.mail.controller;

import com.haha.guli.common.base.R;
import com.haha.guli.common.base.ResultCodeEnum;
import com.haha.guli.mail.service.MailService;
import com.haha.guli.mail.util.CommonUtil;
import com.haha.guli.mail.util.RandomUtils;
import com.haha.guli.service.base.exception.GuliException;
import com.netflix.client.ClientException;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/sms")
@Api(tags = "邮件管理")
@CrossOrigin //跨域
@Slf4j
public class ApiSmsController {

    @Autowired
    private MailService mailService;

    @Autowired
    private RedisTemplate redisTemplate;

    @GetMapping("send/{mobile}")
    public R getCode(@PathVariable String mobile) throws ClientException {

        //校验手机号是否合法
        if(StringUtils.isEmpty(mobile) || !CommonUtil.isMobile(mobile)) {
            log.error("请输入正确的手机号码 ");
            throw new GuliException(ResultCodeEnum.LOGIN_PHONE_ERROR);
        }

        //生成验证码
        String checkCode = RandomUtils.getFourBitRandom();
        //发送验证码
        mailService.send(mobile, checkCode);
        //将验证码存入redis缓存
        redisTemplate.opsForValue().set(mobile, checkCode, 5, TimeUnit.MINUTES);

        return R.ok().message("邮件发送成功");
    }
}
