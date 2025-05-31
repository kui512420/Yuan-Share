# 猿分享 - 全栈Web应用

一个基于Vue3 + Spring Boot的现代化全栈Web应用，集成了文章管理、用户系统、商品管理等多种功能。

## 🚀 项目特色

- **前后端分离架构**：Vue3 + Spring Boot，现代化开发模式
- **完整的用户系统**：注册、登录、权限管理、个人资料管理
- **文章管理系统**：支持Markdown编辑、文章发布、点赞评论功能
- **商品管理**：完整的商品CRUD操作
- **智能AI集成**：集成阿里通义千问和字节豆包AI
- **数据可视化**：使用ECharts进行数据展示
- **响应式设计**：基于Element Plus的现代UI界面

## 🛠 技术栈

### 前端技术
- **框架**: Vue 3.5.13 + TypeScript
- **构建工具**: Vite 6.0.11
- **UI组件库**: Element Plus 2.9.3
- **状态管理**: Pinia 2.3.1
- **路由**: Vue Router 4.5.0
- **HTTP客户端**: Axios 1.7.9
- **Markdown编辑器**: @kangc/v-md-editor 2.3.18
- **图表库**: ECharts 5.6.0
- **拖拽组件**: VueDraggable 2.24.3

### 后端技术
- **框架**: Spring Boot 2.5.5
- **数据库**: MySQL 8.0 + MyBatis Plus 3.5.0
- **缓存**: Redis
- **认证**: JWT (Json Web Token)
- **API文档**: Swagger 2.9.2
- **工具库**: Hutool 5.8.25
- **邮件服务**: Spring Boot Mail
- **Excel处理**: EasyExcel 3.3.2
- **图片处理**: Thumbnailator 0.4.9
- **验证码**: Kaptcha 2.3.2
- **AI集成**: 
  - 阿里通义千问 (DashScope SDK)
  - 字节豆包 (Volcengine SDK)

### 数据库设计
- **用户表**: 用户基本信息、权限管理
- **文章表**: 文章内容、状态、标签、点赞数
- **评论表**: 文章评论系统
- **商品表**: 商品信息管理
- **登录日志**: 用户登录记录
- **签到系统**: 用户签到积分
- **通知系统**: 系统公告管理

## 📁 项目结构

```
yuan-share/
├── ui/                     # 前端Vue3项目
│   ├── src/
│   │   ├── components/     # 公共组件
│   │   ├── views/         # 页面组件
│   │   ├── router/        # 路由配置
│   │   ├── stores/        # Pinia状态管理
│   │   ├── api/           # API接口
│   │   ├── utils/         # 工具函数
│   │   └── assets/        # 静态资源
│   ├── package.json       # 依赖配置
│   └── vite.config.ts     # Vite配置
├── service/               # 后端Spring Boot项目
│   ├── src/main/java/space/kuikui/
│   │   ├── controller/    # 控制器层
│   │   ├── service/       # 业务逻辑层
│   │   ├── mapper/        # 数据访问层
│   │   ├── pojo/          # 实体类
│   │   ├── utils/         # 工具类
│   │   ├── configuration/ # 配置类
│   │   ├── interceptor/   # 拦截器
│   │   └── exception/     # 异常处理
│   ├── pom.xml           # Maven依赖配置
│   └── CONFIG.md         # 配置文件说明
└── sql/                  # 数据库文件
    └── test.sql          # 数据库结构和测试数据
```

## 🚦 快速开始

### 环境要求

- **Node.js**: >= 18.0.0
- **Java**: JDK 1.8+
- **Maven**: 3.6+
- **MySQL**: 8.0+
- **Redis**: 6.0+

### 数据库配置

1. 创建MySQL数据库：
```sql
CREATE DATABASE test CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
```

2. 导入数据库结构：
```bash
mysql -u root -p test < sql/test.sql
```

### 后端启动

1. 进入后端目录：
```bash
cd service
```

2. **配置敏感信息**（⚠️ 重要）：
```bash
# 复制示例配置文件
cp src/main/resources/application.yml.example src/main/resources/application.yml

# 编辑配置文件，填入真实的配置信息
# 包括：数据库密码、QQ邮箱授权码、AI API密钥等
```

> 📋 详细配置说明请参考：[service/CONFIG.md](service/CONFIG.md)

3. 启动Spring Boot应用：
```bash
./mvnw spring-boot:run
```

后端服务将在 `http://localhost:8080` 启动

### 前端启动

1. 进入前端目录：
```bash
cd ui
```

2. 安装依赖：
```bash
npm install
```

3. 启动开发服务器：
```bash
npm run dev
```

前端应用将在 `http://localhost:5173` 启动

## 🔒 安全注意事项

- ✅ 配置文件中的敏感信息已通过 `.gitignore` 保护
- ✅ 提供了 `application.yml.example` 示例文件
- ⚠️ 请勿将包含真实密码/密钥的配置文件提交到版本控制
- 🔑 生产环境建议使用环境变量或配置中心管理敏感配置

## 📖 主要功能

### 🔐 用户系统
- 用户注册/登录
- JWT身份认证
- 权限角色管理（管理员/普通用户）
- 个人资料管理
- 登录日志记录
- 邮箱验证

### 📝 文章系统
- Markdown文章编写和预览
- 文章分类和标签管理
- 文章点赞和评论功能
- 文章状态管理（草稿/发布）
- 文章置顶功能
- 富文本编辑器支持

### 🛒 商品管理
- 商品信息CRUD操作
- 商品图片上传
- 商品状态管理
- 商品销量统计

### 🤖 AI功能
- 集成阿里通义千问AI
- 集成字节豆包AI
- 智能对话功能

### 📊 数据统计
- 用户登录统计
- 文章发布统计
- 商品销售数据
- ECharts图表展示

### 🎯 其他功能
- 签到积分系统
- 系统公告管理
- 图片验证码
- 文件上传管理
- Excel数据导入导出

## 🔧 开发指南

### API文档
启动后端服务后，访问 `http://localhost:8080/swagger-ui.html` 查看API文档

### 开发模式
- 前端: `npm run dev` - 热重载开发模式
- 后端: `./mvnw spring-boot:run` - Spring Boot开发模式

### 构建部署
- 前端构建: `npm run build`
- 后端构建: `./mvnw clean package`

## 🤝 贡献指南

1. Fork 本仓库
2. 创建你的特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交你的更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 打开一个 Pull Request

## 📄 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情

## 👨‍💻 作者

**魁魁** - 项目创建者和主要维护者

## 🙏 致谢

感谢所有为这个项目做出贡献的开发者和使用的开源项目。

---

如果这个项目对你有帮助，请给它一个 ⭐️ Star！ 