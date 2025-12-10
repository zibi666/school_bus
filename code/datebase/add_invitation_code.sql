-- 添加邀请码字段迁移脚本
-- 为 t_order 表添加 invitation_code 列

ALTER TABLE t_order ADD COLUMN `invitation_code` varchar(20) DEFAULT NULL COMMENT '邀请码' AFTER `is_paid`;

-- 为现有订单生成邀请码
UPDATE t_order SET invitation_code = CONCAT(
  CHAR(FLOOR(65 + RAND() * 26)),
  CHAR(FLOOR(65 + RAND() * 26)),
  FLOOR(RAND() * 1000000000) % 100000000
) WHERE invitation_code IS NULL;

-- 设置邀请码不能为空（如果需要的话，可选）
-- ALTER TABLE t_order MODIFY COLUMN `invitation_code` varchar(20) NOT NULL;
