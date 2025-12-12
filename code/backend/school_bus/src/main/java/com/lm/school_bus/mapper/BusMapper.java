package com.lm.school_bus.mapper;

import com.lm.school_bus.entity.Bus;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * 车辆 Mapper 接口
 * 对应数据库 t_bus 表的数据访问操作
 */
@Mapper
public interface BusMapper {
    /**
     * 根据车辆 ID 查询车辆信息
     * @param busId 车辆 ID
     * @return 车辆对象，若不存在返回 null
     */
    Bus selectById(Integer busId);
    
    /**
     * 查询所有车辆
     * @return 车辆列表
     */
    List<Bus> selectAll();
    
    /**
     * 插入新车辆
     * @param bus 车辆对象
     * @return 受影响的行数
     */
    int insert(Bus bus);
    
    /**
     * 更新车辆信息
     * @param bus 包含 busId 和要更新的字段的车辆对象
     * @return 受影响的行数
     */
    int update(Bus bus);
    
    /**
     * 删除车辆
     * @param busId 车辆 ID
     * @return 受影响的行数
     */
    int deleteById(Integer busId);
    
    /**
     * 根据车牌号查询车辆（用于检查是否重复）
     * @param plateNumber 车牌号
     * @return 车辆对象，若不存在返回 null
     */
    Bus selectByPlateNumber(String plateNumber);
    
    /**
     * 根据车型查询车辆列表（用于获取某车型的所有可用车辆）
     * @param carType 车型
     * @return 车辆列表
     */
    List<Bus> selectByCarType(String carType);
}
