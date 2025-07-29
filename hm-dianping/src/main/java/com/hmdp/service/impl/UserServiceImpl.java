package com.hmdp.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmdp.dto.LoginFormDTO;
import com.hmdp.dto.Result;
import com.hmdp.entity.User;
import com.hmdp.mapper.UserMapper;
import com.hmdp.service.IUserService;
import com.hmdp.utils.RegexUtils;
import org.apache.ibatis.jdbc.Null;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

  private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

  @Override
  public Result sendCode(String phone, HttpSession session) {
    //the main business logic for sending code is not implemented here
    //1.check the phone number format
    if (RegexUtils.isPhoneInvalid(phone)) {
      //2.if the phone number format is invalid, return an error message
      return Result.fail("the phone number format is invalid");
    }
    //if valid ,generate a random 6-digit code
    String code = RandomUtil.randomNumbers(6);

    //3.save the code in the session
    session.setAttribute("code", code);

    //4.send the code to the phone number
    log.debug("发送短信验证码成功，验证码：{}", code);

    return Result.ok();



  }

  @Override
  public Result login(LoginFormDTO loginForm, HttpSession session) {
    //1.check the phone number format
    String phone = loginForm.getPhone();
    if (RegexUtils.isPhoneInvalid(phone)) {
      //2.if the phone number format is invalid, return an error message
      return Result.fail("the phone number format is invalid");

    }
    //3.check the code from the session
    Object cancode = session.getAttribute("code");
    String code = loginForm.getCode();
    if (cancode == null || !cancode.toString().equals(code)) {
      //4.if the code is invalid, return an error message
      return Result.fail("the code is invalid");
    }
    User user = query().eq("phone", phone).one();
    //5.if the user does not exist, create a new user
    if (user == null) {
      // create a new user with the phone number
      user = createUserWithPhone(phone);
    }
    session.setAttribute("user", user);
    return Result.ok();
  }

  private User createUserWithPhone(String phone) {
    // create a new user with the phone number
    User user = new User();
    user.setPhone(phone);
    user.setNickName("USER_NICK_NAME_PREFIX" + RandomUtil.randomString(10));
    return null;
  }
}
