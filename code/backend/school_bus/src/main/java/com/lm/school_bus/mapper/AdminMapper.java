package com.lm.school_bus.mapper;

import com.lm.school_bus.entity.Admin;
import org.apache.ibatis.annotations.Mapper;

/**
 * 管理员 Mapper 接口
 * 对应数据库 admin_info 表的数据访问操作
 */
@Mapper
public interface AdminMapper {
    /**
     * 根据账户查询管理员信息（用于登录验证）
     * @param account 管理员账号
     * @return 管理员对象，若不存在返回 null
     */
    Admin selectByAccount(String account);
}
