# PIMS 前端项目

物业管理系统（PIMS）前端项目 - 基于 Vue 3 + Vite + Element Plus

## 🚀 快速开始

### 安装依赖
```bash
cd pims-frontend
npm install
```

### 启动开发服务器
```bash
npm run dev
```

访问: http://localhost:3000

### 构建生产版本
```bash
npm run build
```

## 📦 技术栈

- **Vue 3.3+** - 渐进式JavaScript框架
- **Vite 4.x** - 下一代前端构建工具
- **Element Plus** - Vue 3 UI组件库
- **Vue Router 4** - 官方路由管理器
- **Pinia** - 新一代状态管理
- **Axios** - HTTP客户端
- **SCSS** - CSS预处理器

## 📁 项目结构

```
pims-frontend/
├── public/                 # 静态资源
├── src/
│   ├── api/               # API接口封装
│   │   ├── request.js     # Axios封装
│   │   ├── user.js        # 用户API
│   │   ├── admin.js       # 管理员API
│   │   ├── payment.js     # 缴费API
│   │   ├── message.js     # 消息API
│   │   └── resident.js    # 住户API
│   ├── assets/            # 静态资源
│   │   └── styles/        # 样式文件
│   ├── components/        # 公共组件（待开发）
│   ├── router/            # 路由配置
│   ├── store/             # 状态管理
│   │   ├── user.js        # 用户状态
│   │   ├── admin.js       # 管理员状态
│   │   └── message.js     # 消息状态
│   ├── utils/             # 工具函数
│   │   ├── auth.js        # 认证工具
│   │   ├── validators.js  # 表单验证
│   │   └── constants.js   # 常量定义
│   ├── views/             # 页面组件
│   │   ├── user/          # 用户端页面
│   │   └── admin/         # 管理员端页面
│   ├── App.vue            # 根组件
│   └── main.js            # 入口文件
├── index.html             # HTML模板
├── vite.config.js         # Vite配置
└── package.json           # 项目依赖
```

## ✅ 已完成功能

### 核心基础设施
- ✅ 项目初始化和配置
- ✅ Vite构建配置
- ✅ Element Plus集成
- ✅ 路由配置和守卫
- ✅ Axios请求封装和拦截器
- ✅ 状态管理（Pinia）
- ✅ 工具函数和验证器
- ✅ 全局样式配置

### API接口封装
- ✅ 用户API（登录、注册、入住申请等）
- ✅ 管理员API（登录、审核管理等）
- ✅ 缴费API（提交缴费、查询记录）
- ✅ 消息API（消息列表、已读标记）
- ✅ 住户API（住户查询）

### 页面组件
- ✅ 用户登录页面（支持房号/手机号登录）
- ✅ 用户主布局（导航菜单、消息提醒）
- ⏳ 用户注册页面（待完善）
- ⏳ 入住申请页面（待开发）
- ⏳ 缴费管理页面（待开发）
- ⏳ 消息中心页面（待开发）
- ⏳ 个人中心页面（待开发）
- ⏳ 管理员页面（待开发）

## 🔧 开发指南

### 代理配置
开发环境下，API请求会自动代理到后端服务：
```
http://localhost:3000/api -> http://localhost:8080/api
```

### 环境变量
- `.env.development` - 开发环境配置
- `.env.production` - 生产环境配置

### 路由结构
```
/login              # 用户登录
/register           # 用户注册
/admin-login        # 管理员登录

/user               # 用户端（需要登录）
  /home             # 首页
  /apply            # 入住申请
  /payment          # 缴费管理
  /messages         # 消息中心
  /profile          # 个人中心

/admin              # 管理员端（需要登录）
  /audit            # 入住审核
  /residents        # 住户管理
  /payments         # 缴费管理
```

## 📋 待开发功能

### 高优先级
1. **用户端页面**
   - [ ] 注册页面完善（验证码功能）
   - [ ] 入住申请页面
   - [ ] 缴费管理页面（列表、提交）
   - [ ] 消息中心页面
   - [ ] 个人中心页面（住户信息展示）

2. **管理员端页面**
   - [ ] 管理员登录页面
   - [ ] 管理员布局
   - [ ] 入住审核管理
   - [ ] 住户信息管理
   - [ ] 缴费记录查询

### 中优先级
3. **公共组件**
   - [ ] 验证码输入组件
   - [ ] 房号输入组件
   - [ ] 消息铃铛组件
   - [ ] 数据表格组件
   - [ ] 分页组件

4. **功能优化**
   - [ ] 数据缓存优化
   - [ ] 加载状态优化
   - [ ] 错误处理完善
   - [ ] 响应式布局适配

### 低优先级
5. **其他功能**
   - [ ] 密码找回功能
   - [ ] 消息实时推送
   - [ ] 数据导出功能
   - [ ] 主题切换

## 🎨 设计规范

### 颜色
- 主色：`#409eff` (Element Plus Primary)
- 成功：`#67c23a`
- 警告：`#e6a23c`
- 危险：`#f56c6c`

### 间距
- 页面容器：`20px`
- 卡片内边距：`20px`
- 表单间距：`15px`

## 🔗 相关文档

- [Vue 3 文档](https://cn.vuejs.org/)
- [Vite 文档](https://cn.vitejs.dev/)
- [Element Plus 文档](https://element-plus.org/zh-CN/)
- [Vue Router 文档](https://router.vuejs.org/zh/)
- [Pinia 文档](https://pinia.vuejs.org/zh/)

## 📝 开发说明

### 当前状态
- ✅ 项目框架搭建完成
- ✅ 核心基础设施就绪
- ✅ API接口封装完成
- ⏳ 页面开发进行中（约30%）

### 下一步工作
1. 完成用户端所有页面开发
2. 完成管理员端所有页面开发
3. 开发公共组件
4. 进行联调测试
5. 优化和完善

## 📞 联系方式

如有问题，请参考：
- 后端API文档：`../前后端API接口文档.md`
- 前端设计文档：`../前端详细设计文档.md`
- 后端项目：`../pims-backend/`

---

**项目状态**: 🚧 开发中  
**完成度**: 30%  
**最后更新**: 2025-12-14
