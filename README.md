# campusSHT (Fusion System)

一个基于 Spring Boot 3.5.0 + Vue 3 的现代化前后端分离管理系统。

## 📋 项目简介

campusSHT是一个功能完善的用户管理平台，提供用户注册、登录、权限管理等核心功能。系统采用前后端分离架构，后端使用 Spring Boot 构建 RESTful API，前端使用 Vue 3 + Vite 构建响应式单页应用。

### 主要特性

- 🔐 **用户认证与授权**：基于 JWT 的无状态认证机制
- 👥 **多角色管理**：支持管理员和普通用户两种角色
- 🔒 **密码加密**：使用 BCrypt 算法安全存储用户密码
- 📁 **文件管理**：支持文件上传和下载功能
- 🔄 **跨域支持**：完善的 CORS 配置，支持前后端分离部署
- 📄 **分页查询**：集成 MyBatis Plus 分页插件
- ⚡ **响应式设计**：前端采用现代化响应式设计

## 🛠 技术栈

### 后端技术

| 技术 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 3.5.0 | 基础框架 |
| JDK | 17 | Java 开发工具包 |
| MyBatis Plus | 3.5.9 | ORM 框架 |
| MySQL | 8.0+ | 关系型数据库 |
| Hutool | 5.8.21 | Java 工具类库 |
| JWT | 4.4.0 | JSON Web Token |
| Lombok | 1.18.30 | 简化实体类开发 |

### 前端技术

| 技术 | 说明 |
|------|------|
| Vue 3 | 渐进式 JavaScript 框架 |
| Vite | 下一代前端构建工具 |
| Vue Router | 官方路由管理器 |
| Axios | HTTP 客户端 |
| SCSS | CSS 预处理器 |

## 📁 项目结构

```
fusionsystem/
├── src/main/java/com/example/fusionsystem/
│   ├── BooksystemApplication.java          # 启动类
│   ├── common/                             # 公共类
│   │   ├── Constants.java                  # 常量定义
│   │   └── Result.java                     # 统一响应结果
│   ├── config/                             # 配置类
│   │   ├── CorsConfig.java                 # 跨域配置
│   │   ├── InterceptorConfig.java          # 拦截器配置
│   │   ├── MyBatisPlusConfig.java          # MyBatis Plus 配置
│   │   └── interceptor/                    # 拦截器
│   │       ├── AuthAccess.java             # 权限注解
│   │       └── JwtInterceptor.java         # JWT 拦截器
│   ├── controller/                         # 控制器层
│   │   ├── AdminController.java            # 管理员接口
│   │   ├── UserController.java             # 用户接口
│   │   └── WebController.java              # 公共接口（登录、注册等）
│   ├── enity/                              # 实体类
│   │   ├── Account.java                    # 账户基类
│   │   ├── Admin.java                      # 管理员实体
│   │   └── User.java                       # 用户实体
│   ├── exception/                          # 异常处理
│   │   ├── GlobalExceptionHander.java      # 全局异常处理器
│   │   └── ServiceException.java           # 自定义业务异常
│   ├── Mapper/                             # 数据访问层
│   │   ├── AdminMapper.java                # 管理员 Mapper
│   │   └── UserMapper.java                 # 用户 Mapper
│   ├── service/                            # 业务逻辑层
│   │   ├── IAdminService.java              # 管理员服务接口
│   │   ├── IUserService.java               # 用户服务接口
│   │   └── impl/                           # 服务实现
│   │       ├── AdminServiceImpl.java
│   │       └── UserServiceImpl.java
│   └── utils/                              # 工具类
│       ├── PasswordEncoder.java            # 密码加密工具
│       └── TokenUtils.java                 # JWT 工具类
├── src/main/resources/
│   └── application.yml                     # 应用配置文件
├── vue-project/                            # 前端项目
│   ├── src/
│   │   ├── views/                          # 页面组件
│   │   │   ├── front/                      # 前台页面
│   │   │   └── back/                       # 后台页面
│   │   ├── router/                         # 路由配置
│   │   └── utils/                          # 工具类
│   ├── index.html
│   ├── package.json
│   └── vite.config.js
└── pom.xml                                 # Maven 配置文件
```

## 🚀 快速开始

### 环境要求

- **JDK**: 17+
- **Maven**: 3.6+
- **MySQL**: 8.0+
- **Node.js**: 16+
- **npm**: 8+

### 后端部署

#### 1. 克隆项目

```bash
git clone <your-repository-url>
cd fusionsystem
```

#### 2. 创建数据库

```sql
CREATE DATABASE fusionsystem CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

#### 3. 创建数据表

```sql
USE fusionsystem;

-- 创建管理员表
CREATE TABLE sys_admin (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码（加密）',
    nickname VARCHAR(50) COMMENT '昵称',
    avatar_url VARCHAR(255) COMMENT '头像URL',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理员表';

-- 创建用户表
CREATE TABLE sys_user (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码（加密）',
    nickname VARCHAR(50) COMMENT '昵称',
    avatar_url VARCHAR(255) COMMENT '头像URL',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';
```

#### 4. 配置数据库连接

编辑 `src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/fusionsystem?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: root
    password: your_password  # 修改为你的数据库密码
```

#### 5. 启动后端服务

```bash
# 使用 Maven 启动
mvn spring-boot:run

# 或者使用 IDE 直接运行 BooksystemApplication.java
```

服务将在 `http://localhost:8080` 启动。

### 前端部署

#### 1. 进入前端目录

```bash
cd vue-project
```

#### 2. 安装依赖

```bash
npm install
```

#### 3. 启动开发服务器

```bash
npm run dev
```

前端将在 `http://localhost:5173` 启动（端口可能不同）。

#### 4. 生产环境打包

```bash
npm run build
```

构建产物将输出到 `dist` 目录。

## 🔌 API 接口

### 公共接口（无需认证）

| 接口 | 方法 | 说明 |
|------|------|------|
| `/web/login` | POST | 用户登录 |
| `/web/register` | POST | 用户注册 |

### 用户管理接口（需要认证）

| 接口 | 方法 | 说明 |
|------|------|------|
| `/user` | GET | 获取所有用户 |
| `/user/{id}` | GET | 获取单个用户 |
| `/user/page` | GET | 分页查询用户 |
| `/user` | POST | 新增/更新用户 |
| `/user/{id}` | DELETE | 删除用户 |
| `/user/del/batch` | POST | 批量删除用户 |

### 管理员接口（需要认证）

| 接口 | 方法 | 说明 |
|------|------|------|
| `/admin` | GET | 获取所有管理员 |
| `/admin/{id}` | GET | 获取单个管理员 |
| `/admin/page` | GET | 分页查询管理员 |
| `/admin` | POST | 新增/更新管理员 |
| `/admin/{id}` | DELETE | 删除管理员 |
| `/admin/del/batch` | POST | 批量删除管理员 |

### 文件管理接口

| 接口 | 方法 | 说明 |
|------|------|------|
| `/web/upload` | POST | 上传文件 |
| `/web/download/{fileUUID}` | GET | 下载文件 |

### 个人信息接口

| 接口 | 方法 | 说明 |
|------|------|------|
| `/web/userInfo` | GET | 获取当前登录用户信息 |
| `/web/password` | POST | 修改密码 |

## 🔐 认证机制

系统使用 JWT (JSON Web Token) 进行身份认证：

1. **登录**：用户登录成功后，后端生成 JWT token 并返回
2. **请求认证**：前端在请求头中携带 token（`Authorization: Bearer <token>`）
3. **token 验证**：后端拦截器验证 token 的有效性
4. **token 过期**：token 默认 2 小时有效期，过期后需要重新登录

### 跳过认证

使用 `@AuthAccess` 注解可以跳过认证检查：

```java
@AuthAccess
@PostMapping("/public")
public Result publicMethod() {
    // 无需认证即可访问
}
```

## 📝 配置说明

### 端口配置

默认端口为 8080，可在 `application.yml` 中修改：

```yaml
server:
  port: 8080
  ip: 127.0.0.1
```

### 文件存储

文件上传默认存储在项目根目录的 `files` 文件夹下。

### 跨域配置

系统已配置全局跨域支持，允许所有源访问（生产环境建议限制特定域名）。

## 🐛 常见问题

### 1. 编译错误

**问题**：找不到符号或包不存在

**解决方案**：
```bash
# 清理并重新编译
mvn clean install

# 在 IDE 中重新加载 Maven 项目
```

### 2. 数据库连接失败

**问题**：无法连接到数据库

**解决方案**：
- 确认数据库服务已启动
- 检查 `application.yml` 中的数据库配置
- 确认数据库名称为 `fusionsystem`（或修改配置中的数据库名）
- 检查用户名和密码是否正确

### 3. JWT token 验证失败

**问题**：请求返回 401 错误

**解决方案**：
- 确保请求头中包含有效的 token
- 检查 token 是否过期（默认 2 小时）
- 重新登录获取新的 token

### 4. 前端无法访问后端接口

**问题**：跨域错误或连接被拒绝

**解决方案**：
- 确认后端服务已启动
- 检查前端请求的 URL 是否正确
- 确认 CORS 配置正确

## 📚 开发指南

### 添加新的实体

1. 在 `enity` 包中创建实体类，使用 `@Data` 和 `@TableName` 注解
2. 在 `Mapper` 包中创建对应的 Mapper 接口
3. 在 `service` 包中创建服务接口和实现类
4. 在 `controller` 包中创建控制器

### 密码加密

系统使用 BCrypt 算法加密密码：

```java
// 加密密码
String hashedPassword = BCrypt.hashpw(rawPassword, BCrypt.gensalt());

// 验证密码
boolean isMatch = BCrypt.checkpw(rawPassword, hashedPassword);
```

### 生成 JWT Token

```java
String token = TokenUtils.createToken(userId + "-" + role, password);
```

### 获取当前登录用户

```java
Account currentUser = TokenUtils.getCurrentUser();
```

## 🤝 贡献指南

欢迎提交 Issue 和 Pull Request！

## 📄 许可证

本项目采用 MIT 许可证。

## 📮 联系方式

如有问题或建议，请通过以下方式联系：

- 提交 Issue
- 发送邮件至：your-email@example.com

---

**⚡ 快速开始提示：**
1. 创建数据库 `fusionsystem`
2. 修改 `application.yml` 中的数据库密码
3. 运行 `mvn spring-boot:run` 启动后端
4. 进入 `vue-project` 目录，运行 `npm install && npm run dev` 启动前端
5. 访问 `http://localhost:5173` 开始使用！

```
system
├─ .idea
│  ├─ compiler.xml
│  ├─ dataSources
│  ├─ dataSources.local.xml
│  ├─ dataSources.xml
│  ├─ encodings.xml
│  ├─ gradle.xml
│  ├─ inspectionProfiles
│  │  └─ Project_Default.xml
│  ├─ jarRepositories.xml
│  ├─ kotlinc.xml
│  ├─ misc.xml
│  ├─ uiDesigner.xml
│  └─ workspace.xml
├─ .playwright-mcp
│  ├─ admin-page-fixed.png
│  ├─ avatar-uploaded.png
│  ├─ back-home-error.png
│  ├─ back-home-fixed.png
│  ├─ login-error.png
│  ├─ person-page-before-upload.png
│  ├─ person-page-fixed.png
│  └─ user-page-fixed.png
├─ files
│  ├─ 07bbe4b58a684fdf9fa14cb313d89bb9.jpg
│  └─ 8a283f9aa30740ae9ff990fd464a00b8.jpg
├─ files2603d07a073d4865884e17e99e868fe0.jpg
├─ null
├─ pom.xml
├─ README.md
├─ src
│  └─ main
│     ├─ java
│     │  └─ com
│     │     └─ example
│     │        └─ fusionsystem
│     │           ├─ BooksystemApplication.java
│     │           ├─ common
│     │           │  ├─ Constants.java
│     │           │  └─ Result.java
│     │           ├─ config
│     │           │  ├─ CorsConfig.java
│     │           │  ├─ interceptor
│     │           │  │  ├─ AuthAccess.java
│     │           │  │  └─ JwtInterceptor.java
│     │           │  ├─ InterceptorConfig.java
│     │           │  └─ MyBatisPlusConfig.java
│     │           ├─ controller
│     │           │  ├─ AdminController.java
│     │           │  ├─ UserController.java
│     │           │  └─ WebController.java
│     │           ├─ enity
│     │           │  ├─ Account.java
│     │           │  ├─ Admin.java
│     │           │  └─ User.java
│     │           ├─ exception
│     │           │  ├─ GlobalExceptionHander.java
│     │           │  └─ ServiceException.java
│     │           ├─ Mapper
│     │           │  ├─ AdminMapper.java
│     │           │  └─ UserMapper.java
│     │           ├─ service
│     │           │  ├─ IAdminService.java
│     │           │  ├─ impl
│     │           │  │  ├─ AdminServiceImpl.java
│     │           │  │  └─ UserServiceImpl.java
│     │           │  └─ IUserService.java
│     │           └─ utils
│     │              ├─ PasswordEncoder.java
│     │              └─ TokenUtils.java
│     └─ resources
│        └─ application.yml
├─ target
│  ├─ classes
│  │  ├─ application.yml
│  │  └─ com
│  │     └─ example
│  │        └─ fusionsystem
│  │           ├─ BooksystemApplication.class
│  │           ├─ common
│  │           │  ├─ Constants.class
│  │           │  └─ Result.class
│  │           ├─ config
│  │           │  ├─ CorsConfig.class
│  │           │  ├─ interceptor
│  │           │  │  ├─ AuthAccess.class
│  │           │  │  └─ JwtInterceptor.class
│  │           │  ├─ InterceptorConfig.class
│  │           │  └─ MyBatisPlusConfig.class
│  │           ├─ controller
│  │           │  ├─ AdminController.class
│  │           │  ├─ UserController.class
│  │           │  └─ WebController.class
│  │           ├─ enity
│  │           │  ├─ Account.class
│  │           │  ├─ Admin.class
│  │           │  └─ User.class
│  │           ├─ exception
│  │           │  ├─ GlobalExceptionHander.class
│  │           │  └─ ServiceException.class
│  │           ├─ Mapper
│  │           │  ├─ AdminMapper.class
│  │           │  └─ UserMapper.class
│  │           ├─ service
│  │           │  ├─ IAdminService.class
│  │           │  ├─ impl
│  │           │  │  ├─ AdminServiceImpl.class
│  │           │  │  └─ UserServiceImpl.class
│  │           │  └─ IUserService.class
│  │           └─ utils
│  │              ├─ PasswordEncoder.class
│  │              └─ TokenUtils.class
│  ├─ generated-sources
│  │  └─ annotations
│  └─ generated-test-sources
│     └─ test-annotations
├─ vue-project
│  ├─ config
│  │  ├─ config.default.js
│  │  └─ Logo.svg
│  ├─ index.html
│  ├─ jsconfig.json
│  ├─ package-lock.json
│  ├─ package.json
│  ├─ public
│  │  └─ favicon.ico
│  ├─ src
│  │  ├─ App.vue
│  │  ├─ assets
│  │  │  └─ 404.svg
│  │  ├─ main.js
│  │  ├─ router
│  │  │  └─ index.js
│  │  ├─ style
│  │  │  └─ index.scss
│  │  ├─ utils
│  │  │  └─ request.js
│  │  └─ views
│  │     ├─ 404.vue
│  │     ├─ back
│  │     │  ├─ Admin.vue
│  │     │  ├─ Home.vue
│  │     │  ├─ Password.vue
│  │     │  ├─ Person.vue
│  │     │  └─ User.vue
│  │     ├─ Back.vue
│  │     ├─ front
│  │     │  ├─ Home.vue
│  │     │  ├─ Password.vue
│  │     │  ├─ Person.vue
│  │     │  └─ ToDolist.vue
│  │     ├─ Front.vue
│  │     ├─ Login.vue
│  │     └─ Register.vue
│  ├─ vite.config.js
│  └─ yarn.lock
└─ yarn.lock

```
```
system
├─ .idea
│  ├─ compiler.xml
│  ├─ dataSources
│  ├─ dataSources.local.xml
│  ├─ dataSources.xml
│  ├─ encodings.xml
│  ├─ gradle.xml
│  ├─ inspectionProfiles
│  │  └─ Project_Default.xml
│  ├─ jarRepositories.xml
│  ├─ kotlinc.xml
│  ├─ misc.xml
│  ├─ uiDesigner.xml
│  └─ workspace.xml
├─ .playwright-mcp
│  ├─ admin-page-fixed.png
│  ├─ avatar-uploaded.png
│  ├─ back-home-error.png
│  ├─ back-home-fixed.png
│  ├─ login-error.png
│  ├─ person-page-before-upload.png
│  ├─ person-page-fixed.png
│  └─ user-page-fixed.png
├─ files
│  ├─ 07bbe4b58a684fdf9fa14cb313d89bb9.jpg
│  └─ 8a283f9aa30740ae9ff990fd464a00b8.jpg
├─ files2603d07a073d4865884e17e99e868fe0.jpg
├─ null
├─ pom.xml
├─ README.md
├─ src
│  └─ main
│     ├─ java
│     │  └─ com
│     │     └─ example
│     │        └─ fusionsystem
│     │           ├─ BooksystemApplication.java
│     │           ├─ common
│     │           │  ├─ Constants.java
│     │           │  └─ Result.java
│     │           ├─ config
│     │           │  ├─ CorsConfig.java
│     │           │  ├─ interceptor
│     │           │  │  ├─ AuthAccess.java
│     │           │  │  └─ JwtInterceptor.java
│     │           │  ├─ InterceptorConfig.java
│     │           │  └─ MyBatisPlusConfig.java
│     │           ├─ controller
│     │           │  ├─ AdminController.java
│     │           │  ├─ UserController.java
│     │           │  └─ WebController.java
│     │           ├─ enity
│     │           │  ├─ Account.java
│     │           │  ├─ Admin.java
│     │           │  └─ User.java
│     │           ├─ exception
│     │           │  ├─ GlobalExceptionHander.java
│     │           │  └─ ServiceException.java
│     │           ├─ Mapper
│     │           │  ├─ AdminMapper.java
│     │           │  └─ UserMapper.java
│     │           ├─ service
│     │           │  ├─ IAdminService.java
│     │           │  ├─ impl
│     │           │  │  ├─ AdminServiceImpl.java
│     │           │  │  └─ UserServiceImpl.java
│     │           │  └─ IUserService.java
│     │           └─ utils
│     │              ├─ PasswordEncoder.java
│     │              └─ TokenUtils.java
│     └─ resources
│        └─ application.yml
├─ target
│  ├─ classes
│  │  ├─ application.yml
│  │  └─ com
│  │     └─ example
│  │        └─ fusionsystem
│  │           ├─ BooksystemApplication.class
│  │           ├─ common
│  │           │  ├─ Constants.class
│  │           │  └─ Result.class
│  │           ├─ config
│  │           │  ├─ CorsConfig.class
│  │           │  ├─ interceptor
│  │           │  │  ├─ AuthAccess.class
│  │           │  │  └─ JwtInterceptor.class
│  │           │  ├─ InterceptorConfig.class
│  │           │  └─ MyBatisPlusConfig.class
│  │           ├─ controller
│  │           │  ├─ AdminController.class
│  │           │  ├─ UserController.class
│  │           │  └─ WebController.class
│  │           ├─ enity
│  │           │  ├─ Account.class
│  │           │  ├─ Admin.class
│  │           │  └─ User.class
│  │           ├─ exception
│  │           │  ├─ GlobalExceptionHander.class
│  │           │  └─ ServiceException.class
│  │           ├─ Mapper
│  │           │  ├─ AdminMapper.class
│  │           │  └─ UserMapper.class
│  │           ├─ service
│  │           │  ├─ IAdminService.class
│  │           │  ├─ impl
│  │           │  │  ├─ AdminServiceImpl.class
│  │           │  │  └─ UserServiceImpl.class
│  │           │  └─ IUserService.class
│  │           └─ utils
│  │              ├─ PasswordEncoder.class
│  │              └─ TokenUtils.class
│  ├─ generated-sources
│  │  └─ annotations
│  └─ generated-test-sources
│     └─ test-annotations
├─ vue-project
│  ├─ config
│  │  ├─ config.default.js
│  │  └─ Logo.svg
│  ├─ index.html
│  ├─ jsconfig.json
│  ├─ package-lock.json
│  ├─ package.json
│  ├─ public
│  │  └─ favicon.ico
│  ├─ src
│  │  ├─ App.vue
│  │  ├─ assets
│  │  │  └─ 404.svg
│  │  ├─ main.js
│  │  ├─ router
│  │  │  └─ index.js
│  │  ├─ style
│  │  │  └─ index.scss
│  │  ├─ utils
│  │  │  └─ request.js
│  │  └─ views
│  │     ├─ 404.vue
│  │     ├─ back
│  │     │  ├─ Admin.vue
│  │     │  ├─ Home.vue
│  │     │  ├─ Password.vue
│  │     │  ├─ Person.vue
│  │     │  └─ User.vue
│  │     ├─ Back.vue
│  │     ├─ front
│  │     │  ├─ Home.vue
│  │     │  ├─ Password.vue
│  │     │  ├─ Person.vue
│  │     │  └─ ToDolist.vue
│  │     ├─ Front.vue
│  │     ├─ Login.vue
│  │     └─ Register.vue
│  ├─ vite.config.js
│  └─ yarn.lock
└─ yarn.lock

```