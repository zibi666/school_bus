package com.lm.school_bus.mapper;

import com.lm.school_bus.entity.Student;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface StudentMapper {
    Student selectById(String studentId);
    int insert(Student student);
    int countById(String studentId);
}
