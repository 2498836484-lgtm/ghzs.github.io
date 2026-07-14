# 物业管理系统（PIMS）前后端API接口文档

## 1. 接口规范

### 1.1 基础信息
- **Base URL（开发环境）**: http://localhost:8080/api
- **Base URL（生产环境）**: https://your-domain.com/api
- **协议**: HTTP/HTTPS
- **数据格式**: JSON
- **字符编码**: UTF-8

### 1.2 请求头规范
```http
Content-Type: application/json
Authorization: Bearer {token}  // 需要认证的接口
```

### 1.3 统一响应格式
```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

### 1.4 统一错误码
| 错误码 | 说明 |
|--------|------|
| 200 | 请求成功 |
| 400 | 参数错误 |
| 401 | 未认证/Token失效 |
| 403 | 无权限 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |
| 1001 | 验证码错误 |
| 1002 | 用户名或密码错误 |
| 1003 | 房号已存在 |
| 1004 | 手机号已注册 |
| 1005 | 审核中不能重复申请 |
| 1006 | 物业费未缴清，不能退房 |
| 1007 | 用户不存在 |
| 1008 | 用户已注销 |
| 1009 | 管理员用户名已存在 |
| 1010 | 无权限修改该管理员信息 |

### 1.5 分页参数
```json
{
  "pageNum": 1,
  "pageSize": 10
}
```

### 1.6 分页响应格式
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 100,
    "pageNum": 1,
    "pageSize": 10,
    "pages": 10,
    "list": []
  }
}
```

## 2. 用户端接口

### 2.1 用户认证相关

#### 2.1.1 获取验证码
**接口说明**: 发送手机验证码（模拟，实际打印到后台日志）

**请求地址**: `/user/send-code`

**请求方式**: POST

**请求参数**:
```json
{
  "phone": "13800138000"
}
```

**参数说明**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| phone | String | 是 | 手机号，11位中国大陆手机号 |

**响应示例**:
```json
{
  "code": 200,
  "message": "验证码已发送，请查看控制台日志",
  "data": null
}
```

#### 2.1.2 用户注册
**接口说明**: 普通用户注册

**请求地址**: `/user/register`

**请求方式**: POST

**请求参数**:
```json
{
  "phone": "13800138000",
  "password": "123456",
  "roomNumber": "A310",
  "verifyCode": "123456"
}
```

**参数说明**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| phone | String | 是 | 手机号 |
| password | String | 是 | 密码，6-20位 |
| roomNumber | String | 是 | 房号，格式：Xxxx |
| verifyCode | String | 是 | 验证码，6位数字 |

**响应示例**:
```json
{
  "code": 200,
  "message": "注册成功",
  "data": null
}
```

#### 2.1.3 用户登录（房号登录）
**接口说明**: 使用房号和密码登录

**请求地址**: `/user/login/room`

**请求方式**: POST

**请求参数**:
```json
{
  "roomNumber": "A310",
  "password": "123456"
}
```

**参数说明**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| roomNumber | String | 是 | 房号 |
| password | String | 是 | 密码 |

**响应示例**:
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "userInfo": {
      "id": 1,
      "phone": "13800138000",
      "roomNumber": "A310",
      "status": 0
    }
  }
}
```

#### 2.1.4 用户登录（手机号登录）
**接口说明**: 使用手机号和密码登录

**请求地址**: `/user/login/phone`

**请求方式**: POST

**请求参数**:
```json
{
  "phone": "13800138000",
  "password": "123456"
}
```

**参数说明**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| phone | String | 是 | 手机号 |
| password | String | 是 | 密码 |

**响应示例**:
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "userInfo": {
      "id": 1,
      "phone": "13800138000",
      "roomNumber": "A310",
      "status": 0
    }
  }
}
```

#### 2.1.5 找回密码
**接口说明**: 通过手机号和验证码重置密码

**请求地址**: `/user/reset-password`

**请求方式**: POST

**请求参数**:
```json
{
  "phone": "13800138000",
  "newPassword": "654321",
  "verifyCode": "123456"
}
```

**参数说明**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| phone | String | 是 | 手机号 |
| newPassword | String | 是 | 新密码 |
| verifyCode | String | 是 | 验证码 |

**响应示例**:
```json
{
  "code": 200,
  "message": "密码重置成功",
  "data": null
}
```

#### 2.1.6 修改密码
**接口说明**: 用户修改自己的密码

**请求地址**: `/user/change-password`

**请求方式**: POST

**请求头**: 需要Token

**请求参数**:
```json
{
  "phone": "13800138000",
  "oldPassword": "123456",
  "newPassword": "654321",
  "verifyCode": "123456"
}
```

**参数说明**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| phone | String | 是 | 手机号 |
| oldPassword | String | 是 | 原密码 |
| newPassword | String | 是 | 新密码 |
| verifyCode | String | 是 | 验证码 |

**响应示例**:
```json
{
  "code": 200,
  "message": "密码修改成功",
  "data": null
}
```

### 2.2 入住申请相关

#### 2.2.1 提交入住申请
**接口说明**: 用户提交入住申请

**请求地址**: `/user/apply-resident`

**请求方式**: POST

**请求头**: 需要Token

**请求参数**:
```json
{
  "name": "张三",
  "idCard": "110101199001011234",
  "area": 80.5
}
```

**参数说明**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| name | String | 是 | 姓名，2-20个字符 |
| idCard | String | 是 | 身份证号，18位 |
| area | BigDecimal | 是 | 住房面积，大于0 |

**响应示例**:
```json
{
  "code": 200,
  "message": "申请提交成功，请等待审核",
  "data": null
}
```

#### 2.2.2 查询入住申请状态
**接口说明**: 查询当前用户的入住申请审核状态

**请求地址**: `/user/audit-status`

**请求方式**: GET

**请求头**: 需要Token

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "status": 0,  // 0-待审核，1-通过，2-拒绝
    "name": "张三",
    "idCard": "110101199001011234",
    "area": 80.5,
    "applyTime": "2024-01-01 10:00:00"
  }
}
```

**说明**: 如果用户没有申请记录或已通过审核，返回null

### 2.3 住户信息相关

#### 2.3.1 获取住户信息
**接口说明**: 获取当前用户的住户信息

**请求地址**: `/user/resident-info`

**请求方式**: GET

**请求头**: 需要Token

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "userId": 1,
    "name": "张三",
    "idCard": "110101199001011234",
    "phone": "13800138000",
    "roomNumber": "A310",
    "area": 80.5,
    "pricePerSqm": 2.0,
    "status": 1,
    "registerTime": "2024-01-01 10:00:00"
  }
}
```

### 2.4 缴费相关

#### 2.4.1 立即缴费
**接口说明**: 用户缴纳物业费

**请求地址**: `/user/payment/pay`

**请求方式**: POST

**请求头**: 需要Token

**请求参数**: 无（系统自动计算金额）

**响应示例**:
```json
{
  "code": 200,
  "message": "缴费成功",
  "data": {
    "id": 1,
    "amount": 161.0,  // 面积 × 单价
    "paymentTime": "2024-01-15 14:30:00",
    "paymentMonth": "2024-01"
  }
}
```

#### 2.4.2 查询缴费记录
**接口说明**: 查询用户的缴费记录

**请求地址**: `/user/payment/records`

**请求方式**: GET

**请求头**: 需要Token

**请求参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| startDate | String | 否 | 开始日期，格式：YYYY-MM-DD |
| endDate | String | 否 | 结束日期，格式：YYYY-MM-DD |
| pageNum | Integer | 否 | 页码，默认1 |
| pageSize | Integer | 否 | 每页条数，默认10 |

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 50,
    "pageNum": 1,
    "pageSize": 10,
    "pages": 5,
    "list": [
      {
        "id": 1,
        "roomNumber": "A310",
        "area": 80.5,
        "pricePerSqm": 2.0,
        "amount": 161.0,
        "paymentTime": "2024-01-15 14:30:00",
        "paymentMonth": "2024-01"
      }
    ],
    "totalAmount": 1610.0  // 当前查询条件下的总金额
  }
}
```

### 2.5 消息相关

#### 2.5.1 获取消息列表
**接口说明**: 获取当前用户的系统消息列表

**请求地址**: `/user/messages`

**请求方式**: GET

**请求头**: 需要Token

**请求参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| pageNum | Integer | 否 | 页码，默认1 |
| pageSize | Integer | 否 | 每页条数，默认10 |

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 20,
    "pageNum": 1,
    "pageSize": 10,
    "pages": 2,
    "list": [
      {
        "id": 1,
        "title": "物业费缴费提醒",
        "content": "尊敬的住户，您本月即将缴纳物业费，金额为161元，请及时缴费，如已完成缴费请忽略",
        "isRead": 0,
        "messageType": 1,
        "createdTime": "2024-01-10 00:00:00"
      }
    ]
  }
}
```

#### 2.5.2 获取未读消息数量
**接口说明**: 获取当前用户的未读消息数量

**请求地址**: `/user/messages/unread-count`

**请求方式**: GET

**请求头**: 需要Token

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": 5
}
```

#### 2.5.3 标记所有消息为已读
**接口说明**: 将当前用户的所有未读消息标记为已读

**请求地址**: `/user/messages/mark-read`

**请求方式**: POST

**请求头**: 需要Token

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": null
}
```

## 3. 管理员端接口

### 3.1 管理员认证相关

#### 3.1.1 管理员登录
**接口说明**: 管理员登录

**请求地址**: `/admin/login`

**请求方式**: POST

**请求参数**:
```json
{
  "username": "admin",
  "password": "admin123"
}
```

**参数说明**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| username | String | 是 | 用户名 |
| password | String | 是 | 密码 |

**响应示例**:
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "adminInfo": {
      "id": 1,
      "username": "admin",
      "isSuperAdmin": 1
    }
  }
}
```

### 3.2 入住审核相关

#### 3.2.1 获取待审核列表
**接口说明**: 获取待审核的入住申请列表

**请求地址**: `/admin/audit/pending-list`

**请求方式**: GET

**请求头**: 需要Token

**请求参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| pageNum | Integer | 否 | 页码，默认1 |
| pageSize | Integer | 否 | 每页条数，默认10 |

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 10,
    "pageNum": 1,
    "pageSize": 10,
    "pages": 1,
    "list": [
      {
        "id": 1,
        "userId": 1,
        "name": "张三",
        "idCard": "110101199001011234",
        "area": 80.5,
        "roomNumber": "A310",
        "status": 0,
        "createdTime": "2024-01-01 10:00:00"
      }
    ]
  }
}
```

#### 3.2.2 审核入住申请
**接口说明**: 管理员审核入住申请（同意或拒绝）

**请求地址**: `/admin/audit/process`

**请求方式**: POST

**请求头**: 需要Token

**请求参数**:
```json
{
  "auditId": 1,
  "status": 1,  // 1-同意，2-拒绝
  "pricePerSqm": 2.0  // 物业费单价（同意时必填）
}
```

**参数说明**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| auditId | Long | 是 | 审核记录ID |
| status | Integer | 是 | 审核结果，1-同意，2-拒绝 |
| pricePerSqm | BigDecimal | 否 | 物业费单价，同意时必填，默认2.0 |

**响应示例**:
```json
{
  "code": 200,
  "message": "审核成功",
  "data": null
}
```

### 3.3 住户信息管理相关

#### 3.3.1 查询住户信息列表
**接口说明**: 查询住户信息列表，支持多条件查询和排序

**请求地址**: `/admin/resident/list`

**请求方式**: GET

**请求头**: 需要Token

**请求参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| roomNumber | String | 否 | 房号（模糊查询） |
| name | String | 否 | 姓名（模糊查询） |
| phone | String | 否 | 手机号（模糊查询） |
| status | Integer | 否 | 入住状态，0-未入住，1-已入住 |
| orderBy | String | 否 | 排序字段，如：area、pricePerSqm |
| orderType | String | 否 | 排序方式，asc-升序，desc-降序 |
| pageNum | Integer | 否 | 页码，默认1 |
| pageSize | Integer | 否 | 每页条数，默认10 |

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 100,
    "pageNum": 1,
    "pageSize": 10,
    "pages": 10,
    "list": [
      {
        "id": 1,
        "userId": 1,
        "name": "张三",
        "idCard": "110101199001011234",
        "phone": "13800138000",
        "roomNumber": "A310",
        "area": 80.5,
        "pricePerSqm": 2.0,
        "status": 1,
        "registerTime": "2024-01-01 10:00:00"
      }
    ]
  }
}
```

#### 3.3.2 修改住户信息
**接口说明**: 管理员修改住户信息（仅能修改物业费单价和入住状态）

**请求地址**: `/admin/resident/update`

**请求方式**: PUT

**请求头**: 需要Token

**请求参数**:
```json
{
  "residentId": 1,
  "pricePerSqm": 2.5,
  "status": 1
}
```

**参数说明**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| residentId | Long | 是 | 住户ID |
| pricePerSqm | BigDecimal | 否 | 物业费单价 |
| status | Integer | 否 | 入住状态 |

**响应示例**:
```json
{
  "code": 200,
  "message": "修改成功",
  "data": null
}
```

#### 3.3.3 住户退房
**接口说明**: 管理员为住户办理退房（检查物业费是否结清）

**请求地址**: `/admin/resident/checkout`

**请求方式**: POST

**请求头**: 需要Token

**请求参数**:
```json
{
  "residentId": 1
}
```

**参数说明**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| residentId | Long | 是 | 住户ID |

**响应示例**:
```json
{
  "code": 200,
  "message": "退房成功",
  "data": null
}
```

**错误响应**:
```json
{
  "code": 1006,
  "message": "该住户还有未缴清的物业费，不能退房",
  "data": null
}
```

#### 3.3.4 检查物业费缴纳情况
**接口说明**: 检查住户物业费是否缴清

**请求地址**: `/admin/resident/check-payment/{residentId}`

**请求方式**: GET

**请求头**: 需要Token

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "isPaid": true,  // 是否已缴清
    "unpaidMonths": 0,  // 未缴月份数
    "unpaidAmount": 0  // 未缴金额
  }
}
```

### 3.4 管理员管理相关

#### 3.4.1 获取管理员列表
**接口说明**: 查询所有管理员信息

**请求地址**: `/admin/admin-manage/list`

**请求方式**: GET

**请求头**: 需要Token

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "username": "admin",
      "isSuperAdmin": 1,
      "createdTime": "2024-01-01 00:00:00"
    },
    {
      "id": 2,
      "username": "admin2",
      "isSuperAdmin": 0,
      "createdTime": "2024-01-02 10:00:00"
    }
  ]
}
```

#### 3.4.2 创建管理员
**接口说明**: 创建新管理员（仅超级管理员可用）

**请求地址**: `/admin/admin-manage/create`

**请求方式**: POST

**请求头**: 需要Token

**请求参数**:
```json
{
  "username": "admin2",
  "password": "123456"
}
```

**参数说明**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| username | String | 是 | 用户名，唯一 |
| password | String | 是 | 密码 |

**响应示例**:
```json
{
  "code": 200,
  "message": "管理员创建成功",
  "data": null
}
```

#### 3.4.3 修改管理员密码
**接口说明**: 修改管理员密码

**请求地址**: `/admin/admin-manage/change-password`

**请求方式**: POST

**请求头**: 需要Token

**请求参数**:
```json
{
  "adminId": 2,
  "newPassword": "654321"
}
```

**参数说明**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| adminId | Long | 是 | 管理员ID |
| newPassword | String | 是 | 新密码 |

**响应示例**:
```json
{
  "code": 200,
  "message": "密码修改成功",
  "data": null
}
```

**权限说明**:
- 超级管理员（admin）可以修改所有管理员的密码
- 普通管理员只能修改自己的密码

## 4. 接口调用示例

### 4.1 完整的用户注册登录流程

#### 步骤1: 获取验证码
```javascript
// 请求
POST /api/user/send-code
{
  "phone": "13800138000"
}

// 响应
{
  "code": 200,
  "message": "验证码已发送，请查看控制台日志",
  "data": null
}
// 控制台日志会打印：验证码：123456
```

#### 步骤2: 注册
```javascript
// 请求
POST /api/user/register
{
  "phone": "13800138000",
  "password": "abc123",
  "roomNumber": "A310",
  "verifyCode": "123456"
}

// 响应
{
  "code": 200,
  "message": "注册成功",
  "data": null
}
```

#### 步骤3: 登录
```javascript
// 请求
POST /api/user/login/room
{
  "roomNumber": "A310",
  "password": "abc123"
}

// 响应
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "userInfo": {
      "id": 1,
      "phone": "13800138000",
      "roomNumber": "A310",
      "status": 0
    }
  }
}
```

#### 步骤4: 提交入住申请（携带Token）
```javascript
// 请求
POST /api/user/apply-resident
Headers: {
  "Authorization": "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
Body: {
  "name": "张三",
  "idCard": "110101199001011234",
  "area": 80.5
}

// 响应
{
  "code": 200,
  "message": "申请提交成功，请等待审核",
  "data": null
}
```

### 4.2 管理员审核流程

#### 步骤1: 管理员登录
```javascript
// 请求
POST /api/admin/login
{
  "username": "admin",
  "password": "admin123"
}

// 响应
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "adminInfo": {
      "id": 1,
      "username": "admin",
      "isSuperAdmin": 1
    }
  }
}
```

#### 步骤2: 查看待审核列表
```javascript
// 请求
GET /api/admin/audit/pending-list?pageNum=1&pageSize=10
Headers: {
  "Authorization": "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}

// 响应
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 1,
    "pageNum": 1,
    "pageSize": 10,
    "pages": 1,
    "list": [
      {
        "id": 1,
        "userId": 1,
        "name": "张三",
        "idCard": "110101199001011234",
        "area": 80.5,
        "roomNumber": "A310",
        "status": 0,
        "createdTime": "2024-01-01 10:00:00"
      }
    ]
  }
}
```

#### 步骤3: 审核通过
```javascript
// 请求
POST /api/admin/audit/process
Headers: {
  "Authorization": "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
Body: {
  "auditId": 1,
  "status": 1,
  "pricePerSqm": 2.0
}

// 响应
{
  "code": 200,
  "message": "审核成功",
  "data": null
}
```

### 4.3 缴费流程

#### 步骤1: 获取住户信息（查看应缴金额）
```javascript
// 请求
GET /api/user/resident-info
Headers: {
  "Authorization": "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}

// 响应
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "userId": 1,
    "name": "张三",
    "idCard": "110101199001011234",
    "phone": "13800138000",
    "roomNumber": "A310",
    "area": 80.5,
    "pricePerSqm": 2.0,
    "status": 1,
    "registerTime": "2024-01-01 10:00:00"
  }
}
// 应缴金额 = 80.5 * 2.0 = 161元
```

#### 步骤2: 立即缴费
```javascript
// 请求
POST /api/user/payment/pay
Headers: {
  "Authorization": "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}

// 响应
{
  "code": 200,
  "message": "缴费成功",
  "data": {
    "id": 1,
    "amount": 161.0,
    "paymentTime": "2024-01-15 14:30:00",
    "paymentMonth": "2024-01"
  }
}
```

#### 步骤3: 查询缴费记录
```javascript
// 请求
GET /api/user/payment/records?startDate=2024-01-01&endDate=2024-12-31&pageNum=1&pageSize=10
Headers: {
  "Authorization": "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}

// 响应
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 12,
    "pageNum": 1,
    "pageSize": 10,
    "pages": 2,
    "list": [
      {
        "id": 1,
        "roomNumber": "A310",
        "area": 80.5,
        "pricePerSqm": 2.0,
        "amount": 161.0,
        "paymentTime": "2024-01-15 14:30:00",
        "paymentMonth": "2024-01"
      }
    ],
    "totalAmount": 1932.0
  }
}
```

## 5. 错误处理示例

### 5.1 参数错误
```json
{
  "code": 400,
  "message": "手机号格式不正确",
  "data": null
}
```

### 5.2 认证失败
```json
{
  "code": 401,
  "message": "未登录或Token已失效",
  "data": null
}
```

### 5.3 权限不足
```json
{
  "code": 403,
  "message": "无权访问该资源",
  "data": null
}
```

### 5.4 业务错误
```json
{
  "code": 1003,
  "message": "房号已存在",
  "data": null
}
```

## 6. 注意事项

1. **Token携带方式**: 所有需要认证的接口都需要在请求头中携带Token，格式为：`Authorization: Bearer {token}`

2. **Token有效期**: Token有效期为24小时，过期后需要重新登录

3. **验证码有效期**: 验证码有效期为5分钟，使用后立即失效

4. **验证码限制**: 同一手机号60秒内只能发送一次验证码

5. **日期格式**: 所有日期参数格式为 `YYYY-MM-DD`，日期时间格式为 `YYYY-MM-DD HH:mm:ss`

6. **分页默认值**: pageNum默认为1，pageSize默认为10

7. **排序参数**: orderBy为字段名，orderType为asc或desc

8. **金额精度**: 所有金额保留2位小数

9. **管理员权限**: 
   - 超级管理员（username=admin）拥有所有权限
   - 普通管理员只能修改自己的密码

10. **退房检查**: 退房前会检查物业费是否缴清，未缴清不允许退房
