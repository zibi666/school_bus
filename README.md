# 🚌 School Bus 系统开发文档

> **文档版本**: v1.1 (UML Enhanced) | **最后更新**: 2025-12-17
> **适用对象**: 开发团队、系统架构师、数据库管理员

**学生汽车包车预定系统** 是一个校园场景下的包车管理平台，支持学生在线申请包车，管理员审核和派车，实现包车业务的规范化、信息化管理。

---

## 📋 目录

1. [系统概述](#-系统概述)
2. [核心业务流程](#-核心业务流程)
3. [系统 UML 架构图 (新)](#-系统-uml-架构图)
4. [数据库设计](#-数据库设计)
5. [常见问题 & 排查](#-常见问题--排查)

---

## 🌟 系统概述

### 核心特性
| 模块 | 特性 | 说明 |
| :--- | :--- | :--- |
| **用户** | 🤝 **拼车机制** | 独创**邀请码**系统，支持多人通过 8 位码加入同一订单 |
| **后台** | ⚡ **快速审核** | 管理员实时处理，支持一键派车 |
| **逻辑** | 🔄 **级联退票** | 申请人退票时，自动级联同步至同组所有成员，防止死锁 |
| **安全** | 🛡️ **冲突检测** | 数据库级锁机制 + 应用层逻辑，智能防止车辆时间冲突 |

---

## 🔄 核心业务流程

### 1. 多人拼车与邀请码逻辑
```mermaid
sequenceDiagram
    autonumber
    participant A as 🧑‍🎓 申请人 (Leader)
    participant S as ⚙️ 系统核心
    participant B as 🧑‍🎓 加入者 (Member)
    participant Admin as 👮 管理员

    Note over A,S: 阶段一：创建订单
    A->>S: 提交包车申请 (时间/地点/车型)
    S-->>A: ✅ 订单创建成功 + 生成邀请码 [ABC12345]
    
    Note over A,B: 阶段二：分享与加入
    A->>B: 线下分享邀请码
    B->>S: 输入邀请码 [ABC12345] 加入
    S->>S: 校验邀请码 & 锁定原订单信息
    S-->>B: ✅ 加入成功 (状态: 审核中)

    Note over Admin,S: 阶段三：审核派车
    Admin->>S: 审核通过 -> 分配车辆 [Bus-01]
    S-->>A: 🔔 通知: 订单已通过 (Bus-01)
    S-->>B: 🔔 通知: 订单已通过 (Bus-01)
```

### 2. 级联退票逻辑 (Refund Cascade)
> ⚠️ **警告**：退票是破坏性操作，一旦执行，该邀请码关联的所有订单将全部失效。

```mermaid
graph TD
    Start[用户点击退票] --> Check{身份检查}
    
    Check -- "是加入者 (is_applicant=0)" --> Deny[❌ 拒绝操作: 无权限]
    
    Check -- "是申请人 (is_applicant=1)" --> Confirm{二次确认?}
    Confirm -- 取消 --> End[操作终止]
    
    Confirm -- 确认 --> Update[⚙️ 执行级联更新]
    
    subgraph DB_Transaction [数据库事务]
        Update --> Find[查找 invite_code='ABC' 的所有订单]
        Find --> U1[更新 申请人 -> 已退票]
        Find --> U2[更新 加入者A -> 已退票]
        Find --> U3[更新 加入者B -> 已退票]
    end

    DB_Transaction --> Result[✅ 退票成功]
    
    style Deny fill:#ffcccc,stroke:#ff0000
    style Result fill:#ccffcc,stroke:#00aa00
    style DB_Transaction fill:#f9f9f9,stroke:#333,stroke-dasharray: 5 5
```

---

## 📐 系统 UML 架构图

### 1. 实体关系图 (ER Diagram)
展示系统核心实体及其交互关系。

```mermaid
erDiagram
    STUDENT ||--o{ ORDER : "创建/加入 (1:N)"
    ADMIN ||--o{ ORDER : "审核/管理 (1:N)"
    BUS ||--o{ ORDER : "被分配 (1:N)"

    STUDENT {
        string student_id PK "学号"
        string name "姓名"
        string password "加密密码"
        string location "所在地"
    }

    ADMIN {
        int admin_id PK
        string account UK "账号"
        string name "管理员姓名"
    }

    BUS {
        int bus_id PK
        string plate_number UK "车牌号"
        string driver_name "司机"
        decimal price "单价"
    }

    ORDER {
        int order_id PK
        string invitation_code "邀请码 (索引)"
        int is_applicant "身份标识"
        string status "审核中/已通过/已拒绝"
        datetime start_time
        datetime end_time
    }
```

### 2. 系统用例图 (System Use Cases)
区分学生端与管理员端的职责边界。

```mermaid
graph LR
    subgraph Student_Zone [🧑‍🎓 学生端功能]
        direction TB
        S1(登录/注册)
        S2(创建订单 & 生成邀请码)
        S3(使用邀请码加入)
        S4(申请退票-级联)
        S5(查看历史订单)
    end

    subgraph Admin_Zone [👮 管理员端功能]
        direction TB
        A1(后台登录)
        A2(审核订单 & 派车)
        A3(车辆信息管理)
        A4(撤销已通过订单)
        A5(查看统计报表)
    end

    User((学生)) --> S1
    User --> S2
    User --> S3
    User --> S4
    
    Manager((管理员)) --> A1
    Manager --> A2
    Manager --> A3
    
    %% 关系连接
    S2 -.-> A2
    A2 -.-> S5
    
    style Student_Zone fill:#e1f5fe,stroke:#01579b
    style Admin_Zone fill:#fff3e0,stroke:#e65100
```

### 3. 状态流转图 (Order State Machine)
订单生命周期的完整流转逻辑。

```mermaid
stateDiagram-v2
    [*] --> 审核中: 学生提交申请
    
    state 审核中 {
        [*] --> 等待处理
        等待处理 --> 用户取消: 学生主动取消
    }

    审核中 --> 已通过: ✅ 管理员审核 & 派车
    审核中 --> 已拒绝: ❌ 车辆不足/冲突/信息错误

    state 已通过 {
        [*] --> 待出行
        待出行 --> 已退票: ⚠️ 申请人发起退票(级联)
        待出行 --> 已拒绝: 管理员撤销/车辆删除
    }

    已拒绝 --> [*]
    已退票 --> [*]
    
    note right of 已通过
        此时车辆已锁定
        Bus_ID 不为空
    end note
```

### 4. 类图 (Class Diagram)
核心业务类的属性与方法设计。

```mermaid
classDiagram
    class Student {
        +String student_id
        +String name
        +login()
        +createOrder()
        +joinOrder(code)
    }

    class Order {
        +int order_id
        +String invitation_code
        +boolean is_applicant
        +String status
        +checkConflict()
        +cascadeRefund()
    }

    class Bus {
        +int bus_id
        +String plate_number
        +boolean isAvailable(time)
    }

    Student "1" --> "0..*" Order : manages
    Order "0..*" --> "1" Bus : assigned to
```

---

## 💾 数据库设计

### 核心表：`t_order`
```sql
CREATE TABLE `t_order` (
  `order_id` int(11) NOT NULL AUTO_INCREMENT,
  `student_id` varchar(20) NOT NULL COMMENT '申请人学号',
  `bus_id` int(11) DEFAULT NULL COMMENT '分配车辆ID',
  `status` varchar(20) DEFAULT '审核中' COMMENT '状态',
  `invitation_code` varchar(20) DEFAULT NULL COMMENT '核心：邀请码',
  `is_applicant` tinyint(4) NOT NULL DEFAULT '1' COMMENT '1=发起人, 0=加入者',
  `start_time` datetime DEFAULT NULL,
  `end_time` datetime DEFAULT NULL,
  PRIMARY KEY (`order_id`),
  /* 复合索引优化冲突查询 */
  INDEX `idx_bus_time` (`bus_id`, `start_time`, `end_time`),
  /* 索引优化级联退票 */
  INDEX `idx_invite_code` (`invitation_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

---

## ❓ 常见问题 & 排查

<details>
<summary><strong>🔴 故障：管理员派车提示“时间冲突”</strong></summary>

**现象**：点击“通过”时，系统报错“该车辆在此时段已被占用”。

**逻辑图解**：
```mermaid
graph TD
    A[尝试分配 Bus-A] --> B{查询 Bus-A 时间段订单}
    B -- "Count > 0" --> C[❌ 阻止操作: 冲突]
    B -- "Count = 0" --> D[✅ 允许操作: 更新状态]
```
**解决方案**：请选择另一辆车，或先撤销原来的冲突订单。
</details>

<details>
<summary><strong>❓ 问题：为什么加入者不能退票？</strong></summary>

**设计初衷**：
为了防止拼车成员随意退出导致剩下的成员分摊费用变化（或车辆空置），系统规定**“同进同退”**。
* 只有发起人（队长）有权决定是否取消整个行程。
* 若加入者确实需要单独退出，需联系管理员在后台手动操作。
</details>

---