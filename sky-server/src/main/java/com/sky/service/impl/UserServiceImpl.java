package com.sky.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;

@Service
public class UserServiceImpl implements UserService {

    //微信小程序登录接口
    private static final String WX_LOGIN_API = "https://api.weixin.qq.com/sns/jscode2session";
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private WeChatProperties weChatProperties;

    @Override
    public User wxLogin(UserLoginDTO userLoginDTO) {
        String openid = getOpenid(userLoginDTO);

        //判断openid是否为空
        if (openid == null) {
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }

        //判断用户是否存在
        User user = userMapper.selectByOpenid(openid);
        if (user == null) {
            //用户不存在，创建新用户
            user = User.builder()
                    .createTime(LocalDateTime.now())
                    .openid(openid).build();
            userMapper.insert(user); //插入新用户，主键返回
        }
        //返回用户信息 主键已经返回
        return user;
    }

    private String getOpenid(UserLoginDTO userLoginDTO) {
        //调用微信接口，获取用户信息openid
        HashMap<String, String> getClaims = new HashMap<>();
        getClaims.put("appid", weChatProperties.getAppid());
        getClaims.put("secret", weChatProperties.getSecret());
        getClaims.put("js_code", userLoginDTO.getCode());
        getClaims.put("grant_type", "authorization_code");
        //发送请求
        String jsonData = HttpClientUtil.doGet(WX_LOGIN_API, getClaims);

        //解析json数据
        JSONObject jsonObject = JSONObject.parseObject(jsonData);
        return jsonObject.getString("openid");
    }
}
