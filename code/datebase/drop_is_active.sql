-- 删除 is_active 字段迁移脚本
-- 执行前请备份数据库！

ALTER TABLE `t_bus` DROP COLUMN `is_active`;

-- 验证字段已删除
-- SELECT * FROM t_bus LIMIT 1;
