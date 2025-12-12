package com.lm.school_bus.mapper;

import com.lm.school_bus.entity.Student;
import org.apache.ibatis.annotations.Mapper;

/**
 * 学生 Mapper 接口
 * 对应数据库 student_info 表的数据访问操作
 */
@Mapper
public interface StudentMapper {
    /**
     * 根据学号查询学生信息
     * @param studentId 学号
     * @return 学生对象，若不存在返回 null
     */
    Student selectById(String studentId);
    
    /**
     * 插入新学生记录
     * @param student 学生对象
     * @return 受影响的行数
     */
    int insert(Student student);
    
    /**
     * 检查学号是否存在
     * @param studentId 学号
     * @return 存在的学号数量
     */
    int countById(String studentId);
    
    /**
     * 更新学生信息（支持部分字段更新）
     * @param student 包含 studentId 和要更新的字段的学生对象
     * @return 受影响的行数
     */
    int updateById(Student student);
}
