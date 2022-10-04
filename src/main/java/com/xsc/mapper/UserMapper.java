package com.xsc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xsc.domain.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}

