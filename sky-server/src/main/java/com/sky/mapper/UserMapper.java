package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.entity.User;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

@Mapper
public interface UserMapper {
    /**
     * 根据openid查询用户
     */
    @Select("select * from user where openid = #{openid}")
    User selectByOpenid(String openid);

    /**
     * 插入用户 主键返回
     */
    @AutoFill(OperationType.INSERT)
    void insert(User user);

    /**
     * 根据用户id查询用户
     */
    @Select("select * from user where id = #{userId}")
    User getById(Long userId);
}
