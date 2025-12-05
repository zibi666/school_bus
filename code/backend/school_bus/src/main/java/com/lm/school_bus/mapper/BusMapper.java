package com.lm.school_bus.mapper;

import com.lm.school_bus.entity.Bus;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface BusMapper {
    Bus selectById(Integer busId);
    List<Bus> selectAll();
    int insert(Bus bus);
    int update(Bus bus);
    int deleteById(Integer busId);
}
