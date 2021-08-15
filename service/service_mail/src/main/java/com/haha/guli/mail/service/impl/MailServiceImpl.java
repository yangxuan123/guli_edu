package com.haha.guli.mail.service.impl;

import cn.hutool.extra.mail.MailUtil;
import com.haha.guli.mail.service.MailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MailServiceImpl implements MailService {
    @Override
    public void send(String mobile, String checkCode) {
        String target = mobile+"@163.com";
        log.info("--------发送验证码到{}------",target);
        MailUtil.send(target,"验证码",String.format("验证码为%s,5分钟内有效",checkCode),false);
    }
}
