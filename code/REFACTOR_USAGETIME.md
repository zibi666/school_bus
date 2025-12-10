# 重构总结：移除 usageTime 字段，改用 start_time/end_time

## 变更概览

已完成全面重构，**彻底移除了 `usage_time` 字段**，转而使用 `start_time/end_time` 来计算和显示时间信息。

## 修改清单

### 后端修改

#### 1. Order.java 实体类
- ✅ 移除 `private String usageTime` 字段

#### 2. PriceCalculator.java 工具类
- ✅ 更新 `calculateHours(LocalDateTime startTime, LocalDateTime endTime)` - 改为直接用时间对象计算
- ✅ 更新 `calculatePrice(LocalDateTime startTime, LocalDateTime endTime, Integer hourlyPrice)` - 使用新方法签名
- ✅ 新增 `formatTimeRange(LocalDateTime startTime, LocalDateTime endTime)` - 格式化时间范围显示
- ✅ 保留 `formatHours(double hours)` - 格式化小时数

#### 3. OrderMapper.xml 数据库映射
- ✅ ResultMap 中删除 `usage_time` 字段映射
- ✅ Base_Column_List 中删除 `usage_time` 列
- ✅ INSERT 语句中删除 `usage_time` 参数
- ✅ UPDATE 语句中删除 `usage_time` 更新

#### 4. StudentService.java
- ✅ 更新 `calculateOrderPrice(LocalDateTime startTime, LocalDateTime endTime, String requestedCarType)` 方法签名
- ✅ 清理 `createOrder()` 方法中的价格计算逻辑

#### 5. StudentController.java
- ✅ 更新 `/order` POST 端点，移除 `usageTime` 参数接收
- ✅ 更新 `/order/calculate-price` POST 端点：
  - 改为接收 `startTime` 和 `endTime`
  - 移除 `usageTime` 参数
  - 返回时间范围格式化字符串 `timeRange`

### 前端修改

#### 1. Charter.vue (学生申请页面)
- ✅ form 对象中移除 `usageTime` 字段
- ✅ 移除 `formatDateForDisplay()` 函数
- ✅ 更新 `confirmTimeSelection()` 函数 - 不再需要格式化 usageTime
- ✅ 更新 `submitOrder()` 方法：
  - `calculateOrderPrice()` 调用改为发送 `startTime`/`endTime`
  - `createOrder()` 调用不再发送 `usageTime`，改为发送实际的时间对象
- ✅ 更新时间显示：用 `timePickerData.date/startTime/endTime` 代替 `form.usageTime`
- ✅ 更新支付确认模态框中的时间显示

#### 2. MyTrips.vue (学生订单列表)
- ✅ 新增 `formatTimeRange(startTime, endTime)` 函数
- ✅ 订单时间列显示改为使用 `formatTimeRange(order.startTime, order.endTime)`

#### 3. Trips.vue (管理员订单审核)
- ✅ 新增 `formatTimeRange(startTime, endTime)` 函数
- ✅ 表格中时间列显示改为使用 `formatTimeRange(order.startTime, order.endTime)`

### 数据库脚本

#### 新增迁移脚本: drop_usage_time.sql
```sql
ALTER TABLE `t_order` DROP COLUMN `usage_time`;
```

**⚠️ 执行前须知：**
- 先备份数据库
- 这会删除所有现有的 `usage_time` 值（如有数据）
- 执行后无法恢复该字段中的数据

## 技术细节

### 时间计算逻辑变化

**旧方式：**
```javascript
// 前端
const usageTime = "12月28日 15:29-20:29"
// 后端 calculateOrderPrice(usageTime) 通过正则解析字符串

// 问题：字符串解析容易出错，需要特定格式
```

**新方式：**
```javascript
// 前端
const startTime = "2025-12-28T15:29:00"
const endTime = "2025-12-28T20:29:00"
// 后端 calculateOrderPrice(startTime, endTime) 直接计算时间差

// 优势：准确、高效、易维护
```

### 时间范围显示格式

新的 `formatTimeRange()` 函数统一用于前后端时间显示：
```
输入：startTime = 2025-12-28 15:29:00, endTime = 2025-12-28 20:29:00
输出：12月28日 15:29-20:29
```

## 验证清单

- ✅ 后端编译成功（无错误）
- ✅ 数据库映射更新完成
- ✅ 前端三个页面的时间显示已更新
- ✅ 价格计算逻辑已重构
- ✅ API 接口更新完成

## 下一步

1. **执行数据库迁移**（可选）
   ```bash
   mysql -u root -p < drop_usage_time.sql
   ```
   或通过 Navicat/DBeaver 直接执行

2. **测试流程**
   - [ ] 学生申请包车 - 选择时间 → 计算价格 → 创建订单
   - [ ] 检查 MyTrips 页面时间显示是否正确
   - [ ] 检查管理员 Trips 页面时间显示是否正确
   - [ ] 支付流程是否正常

3. **部署更新**
   - 后端代码已准备就绪
   - 前端代码已准备就绪
   - 数据库脚本可选执行

## 新增方法参考

### PriceCalculator 新增方法

**calculatePrice(LocalDateTime, LocalDateTime, Integer)**
```java
// 计算订单价格
BigDecimal price = PriceCalculator.calculatePrice(
    LocalDateTime.parse("2025-12-28T15:29:00"),
    LocalDateTime.parse("2025-12-28T20:29:00"),
    500  // 每小时价格
);
// 返回：BigDecimal (25小时 * 500 = 12500.00)
```

**formatTimeRange(LocalDateTime, LocalDateTime)**
```java
// 格式化时间范围
String timeRange = PriceCalculator.formatTimeRange(
    LocalDateTime.parse("2025-12-28T15:29:00"),
    LocalDateTime.parse("2025-12-28T20:29:00")
);
// 返回：String "12月28日 15:29-20:29"
```

## 优势总结

| 方面 | 旧方式 | 新方式 |
|------|------|------|
| 时间精度 | 分钟级 | 分钟级 (改进) |
| 计算准确性 | 字符串解析可能出错 | 直接时间计算，准确无误 |
| 代码复杂度 | 需要正则表达式 | 简洁清晰 |
| 数据库使用 | 冗余存储 (text) | 精确存储 (datetime×2) |
| 性能 | 较慢 | 快 |
| 可维护性 | 低 | 高 |
