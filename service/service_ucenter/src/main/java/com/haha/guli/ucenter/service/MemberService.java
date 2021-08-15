package com.haha.guli.ucenter.service;

import com.haha.guli.service.base.dto.MemberDto;
import com.haha.guli.ucenter.entity.Member;
import com.baomidou.mybatisplus.extension.service.IService;
import com.haha.guli.ucenter.entity.vo.LoginVo;
import com.haha.guli.ucenter.entity.vo.RegisterVo;

/**
 * <p>
 * 会员表 服务类
 * </p>
 *
 * @author yang
 * @since 2021-08-13
 */
public interface MemberService extends IService<Member> {

    void register(RegisterVo registerVo);

    String login(LoginVo loginVo);

    /**
     * 根据openid返回用户信息
     * @param openid
     * @return
     */
    Member getByOpenid(String openid);

    MemberDto getMemberDtoByMemberId(String memberId);
}
