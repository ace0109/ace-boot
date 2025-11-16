# ACE-Boot 生产级脚手架

## 项目简介

ACE-Boot 是一个基于 Spring Boot 3.x 和 DDD（领域驱动设计）架构的生产级脚手架，旨在提供一个开箱即用的企业级应用开发框架。

### 核心特性

- **DDD分层架构**：清晰的分层设计，业务逻辑与技术实现分离
- **模块化设计**：基于Maven多模块，功能模块可插拔
- **开箱即用**：预置常用功能，开发者可立即开始业务开发
- **生产就绪**：包含监控、日志、限流等生产环境必需功能
- **代码生成**：提供代码生成器，快速生成CRUD代码
- **最佳实践**：遵循业界最佳实践和设计模式

## 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 3.5.7 | 基础框架 |
| JDK | 17+ | Java开发环境 |
| MyBatis Plus | 3.5.5 | ORM框架 |
| Redis | 7.0+ | 缓存中间件 |
| JWT | 0.12.5 | 身份认证 |
| SpringDoc | 2.3.0 | API文档 |
| Sentinel | 1.8.6 | 限流熔断 |
| XXL-Job | 2.4.0 | 任务调度 |
| Docker | 20.10+ | 容器化 |

## 快速开始

### 环境要求

- JDK 17+
- Maven 3.8+
- MySQL 8.0+ / PostgreSQL 12+（计划）
- Redis 7.0+（计划）

### 本地开发

```bash
# 克隆项目
git clone https://github.com/your-org/ace-boot.git
cd ace-boot

# 编译项目
mvn clean install

# 运行项目
mvn spring-boot:run

# 使用开发环境配置启动（dev profile）
SPRING_PROFILES_ACTIVE=dev mvn spring-boot:run
# 或
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### 质量保障

- `./mvnw clean verify` —— 运行全部测试、生成 JaCoCo 覆盖率报告并校验覆盖率（核心工具包需 100%，辅助模块需 ≥ 90%）。
- `./mvnw checkstyle:check` —— 以阿里巴巴规范执行 Checkstyle，规则文件位于 `config/checkstyle/alibaba-checkstyle.xml`。
- 覆盖率报告输出在 `target/site/jacoco/index.html`，用于 CI 截图或人工复核。

### 健康检查

项目集成了 Spring Boot Actuator，提供健康检查端点：

```bash
# 检查服务健康状态
curl http://localhost:8080/actuator/health
```

响应示例：
```json
{
  "status": "UP",
  "components": {
    "custom": {
      "status": "UP",
      "details": {
        "application": "ace-boot",
        "version": "1.0.0-SNAPSHOT"
      }
    }
  }
}
```

### 参数校验与示例

- Web Starter 默认集成 `jakarta.validation`，并扩展了 `@AllowedValues` 注解，可用于限制字符串字段的枚举值。
- 统一的 `GlobalExceptionHandler` 会把校验异常转换为 `VALIDATION_ERROR`，并返回首个字段的友好提示文案。
- 示例接口 `POST /greetings` 接收 `name`、`type` 字段，其中 `type` 仅支持 `hi/hello`。

请求示例：

```bash
curl -X POST http://localhost:8080/greetings \
  -H "Content-Type: application/json" \
  -d '{"name":"Ace","type":"hi"}'
```

成功响应：

```json
{"code":"SUCCESS","message":"OK","data":"Hi, Ace!"}
```

当 `name` 留空时，响应：

```json
{"code":"VALIDATION_ERROR","message":"昵称不能为空"}
```


### 跨域配置

- Web Starter 提供 `aceboot.cors.*` 配置项，用于集中管理 CORS 策略。
- 将 `aceboot.cors.enabled=true` 后会自动注册 `CorsConfigurationSource`，并按配置输出允许的 Origin、Method、Headers 与 Cookie 策略。
- 示例（`application.yml`）：

```yaml
aceboot:
  cors:
    enabled: true
    allowed-origins:
      - "https://example.com"
      - "http://localhost:3000"
    allowed-methods:
      - GET
      - POST
    allow-credentials: true
```

### Docker部署

```bash
# 构建镜像
docker build -t ace-boot:latest .

# 运行容器
docker-compose up -d
```

## 项目结构

```
ace-boot/
├── ace-boot-dependencies/          # 依赖版本管理
├── ace-boot-parent/                # 父POM
├── ace-boot-common/                # 公共模块
│   ├── ace-boot-common-core/      # 核心工具类
│   ├── ace-boot-common-redis/     # Redis工具
│   └── ace-boot-common-web/       # Web工具
├── ace-boot-starters/              # 自动配置模块
│   ├── ace-boot-starter-web/      # Web基础配置
│   ├── ace-boot-starter-security/ # 安全认证
│   ├── ace-boot-starter-mybatis/  # MyBatis配置
│   ├── ace-boot-starter-redis/    # Redis配置
│   ├── ace-boot-starter-validation/ # 参数校验
│   ├── ace-boot-starter-doc/      # API文档
│   ├── ace-boot-starter-monitor/  # 监控配置
│   ├── ace-boot-starter-log/      # 日志配置
│   ├── ace-boot-starter-limiter/  # 限流配置
│   └── ace-boot-starter-job/      # 任务调度
├── ace-boot-generator/             # 代码生成器
├── ace-boot-samples/               # 示例项目
│   ├── ace-boot-sample-web/       # Web应用示例
│   └── ace-boot-sample-ddd/       # DDD架构示例
└── docs/                          # 文档

```

## DDD分层架构

### 分层说明

```
┌──────────────────────────────────────────┐
│         Interface Layer (接口层)          │
│   Controller / REST API / WebSocket      │
├──────────────────────────────────────────┤
│       Application Layer (应用层)          │
│   Application Service / DTO / Command    │
├──────────────────────────────────────────┤
│         Domain Layer (领域层)             │
│   Entity / Value Object / Domain Service │
├──────────────────────────────────────────┤
│    Infrastructure Layer (基础设施层)       │
│   Repository / External Service / MQ     │
└──────────────────────────────────────────┘
```

### 层次职责

#### Interface Layer（接口层）
- 处理HTTP请求和响应
- 参数验证和转换
- 调用应用服务
- 统一异常处理

#### Application Layer（应用层）
- 编排业务流程
- 事务管理
- DTO转换
- 权限控制

#### Domain Layer（领域层）
- 业务规则和逻辑
- 领域模型
- 领域事件
- 业务异常

#### Infrastructure Layer（基础设施层）
- 数据持久化
- 外部服务调用
- 消息发送
- 缓存操作

## 核心功能

### 认证授权

- JWT Token认证
- OAuth2授权
- RBAC权限模型
- 接口级权限控制

### 数据访问

- MyBatis Plus集成
- 多数据源支持
- 读写分离
- 分页查询
- 审计日志

### 缓存管理

- Redis缓存
- 本地缓存（Caffeine）
- 缓存注解
- 分布式锁

### API文档

- SpringDoc OpenAPI 3.0
- Swagger UI
- 在线调试
- 文档导出

### 监控告警

- 应用健康检查
- 性能监控
- 链路追踪
- 日志收集
- 告警通知

### 限流熔断

- 接口限流
- 熔断降级
- 流量控制
- 系统保护

### 任务调度

- 定时任务
- 异步任务
- 分布式任务
- 任务监控

## 使用指南

### 创建新模块

1. 在对应目录下创建新模块
2. 继承ace-boot-parent
3. 添加必要依赖
4. 编写业务代码

### 使用代码生成器

```bash
cd ace-boot-generator
mvn clean compile exec:java -Dexec.args="--table=user --module=user-service"
```

### 配置文件说明

```yaml
# application.yml
spring:
  profiles:
    active: dev
  application:
    name: ace-boot-demo

ace-boot:
  security:
    jwt:
      secret: your-secret-key
      expiration: 86400
  mybatis:
    multi-datasource: true
  redis:
    enabled: true
```

## 开发规范

### 命名规范

- 包名：com.aceboot.{module}.{layer}
- 类名：驼峰命名，见名知意
- 方法名：动词开头，驼峰命名
- 常量：全大写，下划线分隔

### 代码规范

- 使用Lombok减少样板代码
- 使用MapStruct进行对象转换
- 使用验证注解进行参数校验
- 遵循SOLID原则

### Git规范

- 分支：feature/xxx, bugfix/xxx, hotfix/xxx
- 提交信息：type(scope): description
- 类型：feat, fix, docs, style, refactor, test, chore

## 常见问题

### Q: 如何切换数据库？
A: 修改application.yml中的数据源配置即可。

### Q: 如何添加新的Starter？
A: 在ace-boot-starters目录下创建新模块，实现自动配置类。

### Q: 如何自定义异常处理？
A: 继承BaseException类，在GlobalExceptionHandler中添加处理逻辑。

## 贡献指南

欢迎提交Issue和Pull Request！

### 提交Issue
- 使用Issue模板
- 提供详细的问题描述
- 附带错误日志

### 提交PR
- Fork项目
- 创建功能分支
- 提交代码
- 发起Pull Request

## 版本历史

### v1.0.0 (开发中)
- 初始版本
- 基础架构搭建
- 核心功能实现

## 许可证

[Apache License 2.0](LICENSE)

## 联系我们

- 项目主页：https://github.com/your-org/ace-boot
- 问题反馈：https://github.com/your-org/ace-boot/issues
- 技术支持：support@aceboot.com
