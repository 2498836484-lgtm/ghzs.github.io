# PIMS - 物业管理系统

> Property Information Management System  
> 基于 Spring Boot + Vue3 的前后端分离物业管理系统

---

## 📖 项目简介

PIMS是一个完整的物业管理系统，实现了用户注册、入住申请、管理员审核、缴费管理等核心功能。采用前后端分离架构，后端使用Spring Boot，前端使用Vue3。

**当前状态**: ✅ 后端核心功能已完成，前端框架已搭建，可运行测试！

---

## 🎯 核心功能

### 已实现（✅）

**后端功能：**
- ✅ 用户注册登录（手机验证码）
- ✅ 入住申请提交
- ✅ 管理员审核（同意/拒绝）
- ✅ 住户信息查询
- ✅ **缴费管理模块（新增）**
- ✅ **消息提醒模块（新增）**
- ✅ JWT Token认证
- ✅ 数据验证校验

**前端功能：**
- ✅ 项目框架搭建
- ✅ 用户登录页面（房号/手机号）
- ✅ 用户注册页面（验证码）
- ✅ 用户主布局（导航菜单、消息提醒）
- ✅ 用户首页（快捷功能）
- ✅ API接口封装（21个API）
- ✅ 状态管理（Pinia）
- ✅ 路由配置和权限控制

### 待实现（⏳）
- ⏳ 前端功能页面开发（入住申请、缴费、消息、个人中心）
- ⏳ 管理员端页面开发
- ⏳ 管理员权限管理
- ⏳ 数据报表统计
- ⏳ 前后端联调测试

---

## 🛠️ 技术栈

### 后端
- Java 8
- Spring Boot 2.7.18
- MyBatis Plus 3.5.3.1
- MySQL 8.0
- JWT
- Swagger/Knife4j

### 前端
- Vue 3.3+
- Element Plus
- Vite 4.x
- Pinia
- Axios
- Vue Router 4

---

## 📁 项目结构

```
PIMS/
├── 文档/
│   ├── 需求分析.txt
│   ├── 后端详细设计文档.md
│   ├── 前端详细设计文档.md
│   ├── 前后端API接口文档.md
│   ├── 快速启动和测试指南.md
│   └── 项目交付完成报告.md
│
├── pims.sql                    # 数据库脚本
│
└── pims-backend/              # 后端项目
    ├── pom.xml
    └── src/
        ├── main/
        │   ├── java/com/pims/
        │   │   ├── config/         # 配置类
        │   │   ├── controller/     # 控制器
        │   │   ├── service/        # 服务层
        │   │   ├── mapper/         # 数据访问层
        │   │   ├── entity/         # 实体类
        │   │   └── utils/          # 工具类
        │   └── resources/
        │       └── application.yml
        └── test/
```

---

## 🚀 快速开始

### 1. 环境准备
- JDK 1.8+
- Maven 3.6+
- MySQL 8.0+
- IDE（推荐IntelliJ IDEA）

### 2. 数据库初始化

```bash
# 使用CMD执行（不要用PowerShell）
cd d:\mypro\PIMS
mysql -uroot -p123456 < pims.sql
```

或使用MySQL客户端工具（Workbench/Navicat）执行 `pims.sql`

### 3. 启动后端

#### 方式1：IDE运行（推荐）
1. 用IDEA打开 `pims-backend` 项目
2. 等待Maven依赖下载
3. 运行 `PimsApplication.java`

#### 方式2：Maven命令
```bash
cd pims-backend
mvn spring-boot:run
```

### 4. 访问测试

启动成功后：
- **接口文档**：http://localhost:8080/api/doc.html
- **基础路径**：http://localhost:8080/api

---

## 📝 快速测试

### 测试场景1：用户注册流程

1. **发送验证码**
   ```
   POST /api/user/send-code?phone=13800138000
   ```
   查看控制台获取验证码

2. **用户注册**
   ```json
   POST /api/user/register
   {
     "phone": "13800138000",
     "password": "abc123",
     "roomNumber": "A310",
     "verifyCode": "控制台的验证码"
   }
   ```

3. **用户登录**
   ```json
   POST /api/user/login/room
   {
     "account": "A310",
     "password": "abc123"
   }
   ```

### 测试场景2：入住审核流程

1. **提交入住申请**（需要先登录获取userId）
   ```json
   POST /api/user/apply-resident?userId=1000
   {
     "name": "张三",
     "idCard": "110101199001011234",
     "area": 80.5
   }
   ```

2. **管理员登录**
   ```json
   POST /api/admin/login
   {
     "account": "admin",
     "password": "admin123"
   }
   ```

3. **查看待审核列表**
   ```
   GET /api/admin/audit/pending-list?pageNum=1&pageSize=10
   ```

4. **审核通过**
   ```json
   POST /api/admin/audit/process?adminId=1
   {
     "auditId": 1,
     "status": 1,
     "pricePerSqm": 2.0
   }
   ```

---

## 📚 文档导航

| 文档 | 说明 | 适合人群 |
|------|------|---------|
| [快速启动和测试指南](快速启动和测试指南.md) | 详细的启动步骤和测试用例 | 所有人 |
| [项目交付完成报告](项目交付完成报告.md) | 完整的项目交付说明 | 项目管理者 |
| [后端详细设计文档](后端详细设计文档.md) | 后端架构和业务逻辑设计 | 后端开发者 |
| [前端详细设计文档](前端详细设计文档.md) | 前端页面和组件设计 | 前端开发者 |
| [前后端API接口文档](前后端API接口文档.md) | 完整的API接口说明 | 前后端开发者 |
| [后端开发实现指南](后端开发实现指南.md) | 代码实现细节和示例 | 后端开发者 |

---

## 🗄️ 数据库

### 表结构（6张表）

| 表名 | 说明 |
|------|------|
| t_user | 用户表 |
| t_admin | 管理员表 |
| t_resident | 住户信息表 |
| t_audit_record | 入住审核表 |
| t_payment_record | 缴费记录表 |
| t_message | 系统消息表 |

### 默认账号

- **管理员**：admin / admin123

---

## 🔧 配置说明

### application.yml 核心配置

```yaml
# 数据库配置
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/pims
    username: root
    password: 123456

# 服务端口
server:
  port: 8080
  servlet:
    context-path: /api

# JWT配置
jwt:
  secret: pims-secret-key-for-jwt-token-generation-2024
  expiration: 86400000  # 24小时
```

---

## ❓ 常见问题

### 1. 数据库连接失败
- 检查MySQL是否启动
- 确认数据库pims已创建
- 验证用户名密码（root/123456）

### 2. 编译失败
- 确保JDK版本为1.8
- 清理后重新编译：`mvn clean compile`
- 检查Maven配置

### 3. 端口被占用
修改 `application.yml` 中的端口号

### 4. 验证码验证失败
验证码使用内存存储，重启后失效，需要重新发送

---

## 📊 项目统计

- **Java文件**：38个
- **代码行数**：约2000行
- **API接口**：10个
- **数据库表**：6张
- **文档数量**：7份

---

## 🎯 下一步计划

### 短期目标
1. 完成缴费管理模块
2. 实现消息提醒功能
3. 添加住户信息管理接口

### 中期目标
1. 搭建Vue3前端项目
2. 开发用户端页面
3. 开发管理员端页面

### 长期目标
1. 完善权限管理
2. 添加数据统计分析
3. 优化性能和安全性

---

## 🤝 贡献指南

欢迎提交Issue和Pull Request！

### 开发建议
1. 遵循现有代码规范
2. 添加必要的注释
3. 编写单元测试
4. 更新相关文档

---

## 📄 许可证

本项目仅供学习交流使用。

---

## 👥 联系方式

如有问题，请参考项目文档或提交Issue。

---

**⭐ 如果这个项目对你有帮助，请给个Star！**

*最后更新：2024年12月14日*
