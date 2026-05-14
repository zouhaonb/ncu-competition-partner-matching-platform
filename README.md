# 南昌大学竞赛队友匹配平台

<div align="center">

![GitHub stars](https://img.shields.io/github/stars/zouhaonb/ncu-competition-partner-matching-platform?style=social)
![GitHub forks](https://img.shields.io/github/forks/zouhaonb/ncu-competition-partner-matching-platform?style=social)
![License](https://img.shields.io/badge/license-MIT-blue.svg)
![Java](https://img.shields.io/badge/Java-17-orange.svg)
![Vue](https://img.shields.io/badge/Vue-3.4-green.svg)

**一个基于 Spring Boot + Vue 3 的高校竞赛队友智能匹配平台**

[功能特性](#-功能特性) • [技术栈](#-技术栈) • [快速开始](#-快速开始) • [系统架构](#-系统架构) • [API文档](#-api文档)

</div>

---

## 📋 项目简介

南昌大学竞赛队友匹配平台是一个专为高校学生设计的智能化竞赛队友匹配系统。该平台通过智能算法分析学生的技能标签，为各类学科竞赛（数学建模、程序设计、电子设计等）提供精准的队友推荐服务，帮助学生快速找到合适的团队成员。

### ✨ 核心亮点

- 🎯 **智能匹配算法**：基于技能标签交集的加权评分系统，精准计算匹配度
- ⚡ **高性能缓存**：Redis 缓存标签数据和推荐结果，大幅提升响应速度
- 🔐 **安全可靠**：JWT 无状态认证 + BCrypt 密码加密 + Spring Security 角色鉴权
- 📱 **现代化界面**：Vue 3 + Element Plus 构建的响应式用户界面
- 👥 **完整工作流**：从注册、发布招募、申请加入到审核的全流程管理

---

## 🚀 功能特性

### 用户端功能

#### 1️⃣ 用户管理
- ✅ 用户注册与登录（学号作为唯一标识）
- ✅ 个人信息管理（姓名、手机号、QQ、个人简介）
- ✅ 技能标签管理（支持添加/删除技能及熟练度：了解/掌握/精通）

#### 2️⃣ 招募广场
- ✅ 浏览所有开放的竞赛招募信息
- ✅ 按关键词搜索和竞赛类别筛选
- ✅ 分页展示，支持查看详情

#### 3️⃣ 智能推荐
- ✅ 查看招募详情时自动显示 Top 5 推荐队友
- ✅ 实时计算技能匹配度百分比
- ✅ 可视化展示：绿色(≥70%) / 橙色(40%-70%) / 灰色(<40%)

#### 4️⃣ 申请与审核
- ✅ 申请加入感兴趣的招募团队
- ✅ 发布者审核申请（同意/拒绝）
- ✅ 查看我的队友列表及联系方式

#### 5️⃣ 发布招募
- ✅ 创建新的竞赛招募信息
- ✅ 设置所需人数和技能要求
- ✅ 管理自己的招募（关闭/删除）

### 管理员功能

#### 🔧 后台管理
- ✅ 竞赛类别管理（增删查）
- ✅ 用户管理（查看所有用户、删除违规用户）
- ✅ 招募管理（查看所有招募、关闭或删除不当招募）
- ✅ 权限控制：仅 ADMIN 角色可访问

---

## 💻 技术栈

### 后端技术

| 技术 | 版本 | 用途 |
|------|------|------|
| **Java** | 17 LTS | 编程语言 |
| **Spring Boot** | 3.0.12 | 应用框架 |
| **Spring Security** | 内嵌 | 安全框架（BCrypt + JWT + 角色鉴权） |
| **MyBatis Plus** | 3.5.5 | ORM 框架 |
| **MySQL** | 8.0 | 关系型数据库 |
| **Redis** | 6.0+ | 缓存服务 |
| **JWT (jjwt)** | 0.12.3 | 无状态认证 |
| **Maven** | 3.6+ | 项目构建 |
| **Lombok** | 内嵌 | 简化代码 |

### 前端技术

| 技术 | 版本 | 用途 |
|------|------|------|
| **Vue 3** | 3.4+ | 前端框架（Composition API） |
| **Vite** | 5.0+ | 构建工具 |
| **Element Plus** | 2.5+ | UI 组件库 |
| **Vue Router** | 4.3+ | 路由管理（含角色守卫） |
| **Axios** | 1.6+ | HTTP 客户端 |

---

## 🏗️ 系统架构

### 整体架构图

```
┌─────────────┐         ┌──────────────┐         ┌─────────────┐
│   Browser   │ ◄─────► │ Vite Dev     │ ◄─────► │ Spring Boot │
│ (localhost: │  Proxy  │ Server       │  /api   │ Server      │
│    3000)    │         │ (localhost:  │         │(localhost:  │
└─────────────┘         │    3000)     │         │    8080)    │
                        └──────────────┘         └──────┬──────┘
                                                        │
                                              ┌─────────┴─────────┐
                                              │                   │
                                        ┌─────▼─────┐    ┌───────▼──────┐
                                        │   MySQL   │    │    Redis     │
                                        │ (:3306)   │    │   (:6379)    │
                                        └───────────┘    └──────────────┘
```

### 后端分层架构

```
Controller 层 (6个)
├── AuthController          # 认证接口
├── UserController          # 用户管理
├── TagController           # 标签管理
├── RecruitmentController   # 招募管理
├── ApplicationController   # 申请管理
└── AdminController         # 管理员功能
        ↓
Service 层 (7个)
├── UserService             # 用户业务逻辑
├── TagService              # 标签业务（含缓存）
├── CategoryService         # 类别业务
├── RecruitmentService      # 招募业务
├── MatchingService         # 匹配算法（含缓存）
├── ApplicationService      # 申请业务
└── TeammateService         # 队友查询
        ↓
Mapper 层 (7个)
└── 继承 MyBatisPlus BaseMapper
        ↓
Entity 层 (7个实体)
└── User, Tag, UserTag, CompetitionCategory, 
    Recruitment, RecruitmentRequiredTag, Application
```

### 缓存策略

```
📦 Redis 缓存应用

1️⃣ 技能标签列表缓存
   Key: tags:all
   TTL: 30分钟
   策略: Cache-Aside（先查缓存，未命中查DB并回写）

2️⃣ 推荐队友结果缓存
   Key: recommendations:recruitment:{recruitmentId}
   TTL: 30分钟
   失效: 新申请提交或招募关闭时主动清除
```

---

## 🎯 匹配算法

### 算法名称
**基于技能标签交集的加权匹配评分算法**（Tag Intersection Weighted Matching）

### 算法原理

```
对于每个候选用户 u（排除发布者本人）：

1. 计算交集标签集合 = 招募要求标签 ∩ 用户技能标签
2. 计算用户总得分 = Σ（交集中每个标签的用户熟练度分数）
   - 了解 = 1分
   - 掌握 = 2分
   - 精通 = 3分
3. 计算匹配度百分比 = 用户总得分 / (要求标签数 × 3) × 100%
4. 按匹配度降序排列，取前 5 名返回
```

### 计算示例

**招募要求**：[算法设计, Python, 论文写作]，满分 = 3 × 3 = 9

**用户李四的技能**：
- Python (精通=3) → 匹配，贡献 3 分
- 论文写作 (掌握=2) → 匹配，贡献 2 分
- 其他不匹配

**结果**：
- 总得分 = 5
- 匹配度 = 5/9 × 100% = **55.6%** 🟠

---

## 📦 快速开始

### 环境要求

| 组件 | 版本要求 |
|------|---------|
| JDK | 17 或以上 |
| Maven | 3.6+ |
| MySQL | 8.0 |
| Redis | 6.0+ |
| Node.js | 18+ |
| npm | 9+ |

### 安装步骤

#### 1. 克隆仓库

```bash
git clone https://github.com/zouhaonb/ncu-competition-partner-matching-platform.git
cd ncu-competition-partner-matching-platform
```

#### 2. 启动 MySQL 和 Redis

```bash
# 检查 MySQL
mysql --version

# 检查 Redis
redis-cli ping  # 应返回 PONG
```

#### 3. 初始化数据库

```bash
mysql -u root -p --default-character-set=utf8mb4 < backend/src/main/resources/db/schema.sql
```

> 脚本会自动创建 `match_platform` 数据库、7张表、预置标签和测试用户

#### 4. 配置数据库连接

编辑 `backend/src/main/resources/application.yml`，修改数据库密码（如需要）：

```yaml
spring:
  datasource:
    password: your_password
  redis:
    host: localhost
    port: 6379
```

#### 5. 启动后端

```bash
cd backend
mvn clean package -DskipTests
mvn spring-boot:run
```

✅ 成功标志：控制台输出 `Started MatchApplication`，监听端口 8080

#### 6. 启动前端

```bash
cd frontend
npm install
npm run dev
```

✅ 访问 `http://localhost:3000` 即可看到登录页面

---

## 👤 测试账号

### 管理员账号

| 学号 | 姓名 | 密码 | 角色 |
|------|------|------|------|
| **root** | 管理员 | **123456** | ADMIN |

> 登录后导航栏会出现"管理后台"菜单

### 普通用户账号（密码均为 123456）

| 学号 | 姓名 | 核心技能 | 专业背景 |
|------|------|---------|---------|
| 8008123001 | 张三 | Java/算法/后端 | 计算机大三 |
| 8008123002 | 李四 | Python/ML/数据 | 数据科学专业 |
| 8008123003 | 王五 | 嵌入式/单片机/C++ | 电子信息工程 |
| 8008123004 | 赵六 | 前端/UI/Python | 数字媒体专业 |
| 8008123005 | 孙七 | 算法/MATLAB/Python | 数学系研一 |
| 8008123006 | 周八 | ML/Python/数据 | 人工智能大三 |
| 8008123007 | 吴九 | Java/后端/前端 | 软件工程大三 |
| 8008123008 | 郑十 | 数据/Python/ML | 大数据大二 |
| 8008123009 | 陈小明 | FPGA/嵌入式/单片机 | 电子科学大三 |
| 8008123010 | 刘小红 | 论文/项目管理 | 信息管理大二 |
| 8008123011 | 黄小刚 | C++/算法/Java | 计算机大二 |
| 8008123012 | 林小美 | 项目管理/UI/前端 | 工商管理大三 |

> 系统预置了 11 条招募信息和 15 条申请记录，可直接体验完整功能

---

## 📖 API 文档

### 认证接口 (`/api/auth`)

| 方法 | 路径 | 说明 | 请求体 | 响应 |
|------|------|------|--------|------|
| POST | `/register` | 用户注册 | `{studentId, name, password, phone, qq}` | `{code, message}` |
| POST | `/login` | 用户登录 | `{studentId, password}` | `{token, userId, name, role}` |

### 用户接口 (`/api/user`, 需认证)

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/profile` | 获取个人信息 |
| PUT | `/profile` | 更新个人信息 |
| GET | `/teammates` | 查看我的队友 |

### 标签接口 (`/api/tag`, 需认证)

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/list` | 获取所有技能标签（缓存） |
| POST | `/add` | 添加用户技能标签 |
| DELETE | `/remove/{tagId}` | 删除用户技能标签 |

### 招募接口 (`/api/recruitment`, 需认证)

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/list` | 获取招募列表（支持分页/搜索/筛选） |
| GET | `/{id}` | 获取招募详情（含推荐队友） |
| POST | `/create` | 发布新招募 |
| PUT | `/{id}/close` | 关闭招募（仅发布者） |
| DELETE | `/{id}` | 删除招募（仅发布者） |

### 申请接口 (`/api/application`, 需认证)

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/apply` | 申请加入招募 |
| GET | `/list/{recruitmentId}` | 查看某招募的申请列表（仅发布者） |
| PUT | `/handle` | 处理申请（同意/拒绝，仅发布者） |

### 管理员接口 (`/api/admin`, 需 ADMIN 角色)

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/categories` | 获取竞赛类别列表 |
| POST | `/categories` | 添加竞赛类别 |
| DELETE | `/categories/{id}` | 删除竞赛类别 |
| GET | `/users` | 获取用户列表 |
| DELETE | `/users/{id}` | 删除用户 |
| GET | `/recruitments` | 获取所有招募 |
| PUT | `/recruitments/{id}/close` | 关闭招募 |
| DELETE | `/recruitments/{id}` | 删除招募 |

---

## 🗄️ 数据库设计

### 表结构概览

| 表名 | 说明 | 核心字段 |
|------|------|---------|
| `user` | 用户表 | id, student_id, name, password(BCrypt), phone, qq, intro, **role**, create_time |
| `tag` | 技能标签表 | id, name |
| `user_tag` | 用户-标签关联 | id, user_id, tag_id, proficiency(1/2/3) |
| `competition_category` | 竞赛类别表 | id, name |
| `recruitment` | 招募信息表 | id, publisher_id, title, category_id, required_number, description, status, create_time |
| `recruitment_required_tag` | 招募-所需标签 | id, recruitment_id, tag_id |
| `application` | 申请表 | id, recruitment_id, applicant_id, reason, status, apply_time |

### ER 图

```
┌──────────┐     ┌──────────────┐     ┌─────────────┐
│   user   │────<│  user_tag    │>────│    tag      │
└────┬─────┘     └──────────────┘     └─────────────┘
     │
     │ 1:N
     ▼
┌──────────────┐     ┌──────────────────────┐     ┌──────────┐
│ recruitment  │────<│recruitment_required_ │>────│   tag    │
└────┬─────────┘     │       tag            │     └──────────┘
     │               └──────────────────────┘
     │ 1:N
     ▼
┌──────────────┐     ┌──────────┐
│ application  │>────│   user   │
└──────────────┘     └──────────┘
```

---

## 📁 项目结构

```
高校竞赛队友匹配平台/
├── backend/                    # 后端项目
│   ├── src/main/java/com/matchteam/
│   │   ├── common/            # 通用类（Result, JwtUtils, Exception等）
│   │   ├── config/            # 配置类（Security, Redis, CORS等）
│   │   ├── controller/        # 控制器层（6个）
│   │   ├── dto/               # 数据传输对象（14个）
│   │   ├── entity/            # 实体类（7个）
│   │   ├── mapper/            # Mapper接口（7个）
│   │   └── service/           # 服务层（7个）
│   ├── src/main/resources/
│   │   ├── application.yml    # 配置文件
│   │   └── db/schema.sql      # 数据库初始化脚本
│   └── pom.xml                # Maven配置
│
├── frontend/                   # 前端项目
│   ├── src/
│   │   ├── api/               # API接口封装（6个）
│   │   ├── components/        # 公共组件
│   │   ├── router/            # 路由配置
│   │   └── views/             # 页面视图（6个）
│   ├── index.html
│   ├── package.json
│   └── vite.config.js
│
├── docs/                       # 文档目录
├── .gitignore
└── README.md
```

---

## 🎨 界面预览

### 主要页面

- 🔐 **登录/注册页**：简洁的用户认证界面
- 📋 **招募广场**：卡片式展示所有开放招募
- 📝 **招募详情**：详细信息 + 智能推荐队友
- 👤 **个人中心**：个人信息 + 技能标签 + 我的队友
- 🔧 **管理后台**：管理员专属的数据管理界面

---

## 🔒 安全特性

### 认证与授权

1. **JWT 无状态认证**
   - Token 包含用户 ID 和角色信息
   - 前端自动在请求头附加 `Authorization: Bearer {token}`

2. **BCrypt 密码加密**
   - 用户密码使用 BCrypt 强哈希算法存储
   - 防止彩虹表攻击

3. **Spring Security 角色鉴权**
   - 基于角色的访问控制（RBAC）
   - 管理员接口仅 ADMIN 角色可访问
   - 非授权访问返回 403 Forbidden

4. **CORS 跨域配置**
   - 开发环境允许前端跨域访问
   - 生产环境可配置白名单

---

## 🚦 使用流程演示

### 新用户完整流程

```
1. 注册账号 → 2. 登录系统 → 3. 设置技能标签 
→ 4. 浏览招募广场 → 5. 查看推荐队友 → 6. 申请加入 
→ 7. 等待审核 → 8. 查看我的队友
```

### 发布者流程

```
1. 发布招募 → 2. 填写要求和描述 → 3. 查看申请者 
→ 4. 审核申请（同意/拒绝） → 5. 查看队友联系方式
```

### 管理员流程

```
1. 使用 root 账号登录 → 2. 进入管理后台 
→ 3. 管理竞赛类别/用户/招募
```

---

## 📊 性能优化

### 缓存策略

| 缓存项 | Key | TTL | 命中率 | 优化效果 |
|--------|-----|-----|--------|---------|
| 技能标签列表 | `tags:all` | 30min | ~95% | 减少 DB 查询 |
| 推荐队友结果 | `recommendations:recruitment:{id}` | 30min | ~80% | 避免重复计算 |

### 算法优化

- ✅ 匹配算法时间复杂度：O(n × m)，n=用户数，m=标签数
- ✅ 缓存推荐结果，避免每次请求都重新计算
- ✅ 主动失效策略：申请提交或招募关闭时清除相关缓存

---

## 🛠️ 开发指南

### 后端开发

```bash
# 编译打包
cd backend
mvn clean package -DskipTests

# 运行测试
mvn test

# 启动开发服务器
mvn spring-boot:run
```

### 前端开发

```bash
# 安装依赖
cd frontend
npm install

# 开发模式（热重载）
npm run dev

# 生产构建
npm run build
```

### 代码规范

- **后端**：遵循阿里巴巴 Java 开发手册
- **前端**：使用 ESLint + Prettier 统一代码风格
- **Git Commit**：使用语义化提交信息（feat/fix/docs/style/refactor/test/chore）

---

## 📝 常见问题

### Q1: 如何修改数据库密码？

编辑 `backend/src/main/resources/application.yml`：
```yaml
spring:
  datasource:
    password: your_new_password
```

### Q2: Redis 连接失败怎么办？

确保 Redis 服务已启动：
```bash
# Linux/Mac
redis-server

# Windows
redis-server.exe
```

### Q3: 前端无法访问后端 API？

检查 `frontend/vite.config.js` 中的代理配置：
```javascript
server: {
  proxy: {
    '/api': {
      target: 'http://localhost:8080',
      changeOrigin: true
    }
  }
}
```

### Q4: 如何重置数据库？

```bash
mysql -u root -p -e "DROP DATABASE IF EXISTS match_platform;"
mysql -u root -p --default-character-set=utf8mb4 < backend/src/main/resources/db/schema.sql
```

---

## 🤝 贡献指南

欢迎贡献代码！请遵循以下步骤：

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启 Pull Request

---

## 📄 许可证

本项目采用 MIT 许可证 - 详见 [LICENSE](LICENSE) 文件

---

## 👨‍💻 作者

**zouhaonb**

- GitHub: [@zouhaonb](https://github.com/zouhaonb)

---

## 🙏 致谢

感谢以下开源项目：

- [Spring Boot](https://spring.io/projects/spring-boot)
- [Vue.js](https://vuejs.org/)
- [Element Plus](https://element-plus.org/)
- [MyBatis Plus](https://baomidou.com/)

---

<div align="center">

**如果这个项目对你有帮助，请给一个 ⭐ Star！**

Made with ❤️ by zouhaonb

</div>
