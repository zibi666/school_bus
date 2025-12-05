package com.lm.school_bus.mapper;

import com.lm.school_bus.entity.Admin;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AdminMapper {
    Admin selectByAccount(String account);
}
