# ACE-Boot 架构设计文档

## 1. 概述

ACE-Boot 是一个基于领域驱动设计（DDD）思想构建的企业级Spring Boot脚手架，旨在提供一个标准化、可扩展、易维护的应用开发框架。

## 2. 设计理念

### 2.1 核心原则

- **领域驱动**：以业务为核心，技术服务于业务
- **分层架构**：清晰的层次划分，各层职责明确
- **模块化**：高内聚低耦合，模块独立可插拔
- **约定优于配置**：提供合理的默认配置，减少配置工作
- **开闭原则**：对扩展开放，对修改关闭

### 2.2 设计目标

1. **降低开发成本**：通过脚手架和代码生成器提高开发效率
2. **提高代码质量**：统一的规范和最佳实践
3. **便于维护扩展**：清晰的架构和模块化设计
4. **生产级可靠性**：完善的监控、日志、容错机制

## 3. 架构设计

### 3.1 整体架构

```
┌─────────────────────────────────────────────────────────┐
│                     前端应用                             │
│              (Web/Mobile/Desktop/小程序)                 │
└─────────────────────┬───────────────────────────────────┘
                      │ HTTP/WebSocket
┌─────────────────────▼───────────────────────────────────┐
│                    API网关层                             │
│          (路由/认证/限流/监控/日志)                        │
└─────────────────────┬───────────────────────────────────┘
                      │
┌─────────────────────▼───────────────────────────────────┐
│                  ACE-Boot应用层                          │
├──────────────────────────────────────────────────────────┤
│  ┌──────────────────────────────────────────────────┐  │
│  │            Interface Layer (接口层)               │  │
│  │         REST Controller / GraphQL / gRPC         │  │
│  └────────────────────┬─────────────────────────────┘  │
│  ┌────────────────────▼─────────────────────────────┐  │
│  │          Application Layer (应用层)               │  │
│  │      Application Service / Command / Query       │  │
│  └────────────────────┬─────────────────────────────┘  │
│  ┌────────────────────▼─────────────────────────────┐  │
│  │            Domain Layer (领域层)                  │  │
│  │    Entity / Aggregate / Domain Service / Event   │  │
│  └────────────────────┬─────────────────────────────┘  │
│  ┌────────────────────▼─────────────────────────────┐  │
│  │       Infrastructure Layer (基础设施层)            │  │
│  │     Repository Impl / Cache / MQ / External API  │  │
│  └──────────────────────────────────────────────────┘  │
└──────────────────────────────────────────────────────────┘
                      │
┌─────────────────────▼───────────────────────────────────┐
│                   基础设施服务                            │
│     (MySQL/PostgreSQL/Redis/MQ/OSS/ElasticSearch)       │
└─────────────────────────────────────────────────────────┘
```

### 3.2 DDD分层详解

#### 3.2.1 Interface Layer（接口层）

**职责：**
- 接收和响应外部请求
- 请求参数校验和转换
- 调用应用层服务
- 统一响应格式封装

**核心组件：**
```java
// Controller示例
@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserApplicationService userApplicationService;

    @PostMapping
    public Result<UserDTO> createUser(@Valid @RequestBody CreateUserCommand command) {
        return Result.success(userApplicationService.createUser(command));
    }
}

// 统一响应格式
public class Result<T> {
    private int code;
    private String message;
    private T data;
    private long timestamp;
}
```

#### 3.2.2 Application Layer（应用层）

**职责：**
- 编排业务用例流程
- 事务边界管理
- DTO与领域模型转换
- 调用领域服务

**核心组件：**
```java
// Application Service示例
@Service
@Transactional
public class UserApplicationService {
    @Autowired
    private UserDomainService userDomainService;
    @Autowired
    private UserRepository userRepository;

    public UserDTO createUser(CreateUserCommand command) {
        // 1. 命令转换为领域模型
        User user = UserFactory.create(command);

        // 2. 调用领域服务进行业务处理
        userDomainService.validateUser(user);

        // 3. 持久化
        userRepository.save(user);

        // 4. 发布领域事件
        DomainEventPublisher.publish(new UserCreatedEvent(user));

        // 5. 转换为DTO返回
        return UserAssembler.toDTO(user);
    }
}
```

#### 3.2.3 Domain Layer（领域层）

**职责：**
- 业务规则实现
- 领域模型定义
- 领域服务提供
- 领域事件发布

**核心组件：**
```java
// 聚合根
@Entity
public class User extends BaseAggregateRoot {
    private UserId userId;
    private Username username;
    private Email email;
    private Password password;
    private UserStatus status;

    // 业务行为
    public void changePassword(Password newPassword) {
        // 业务规则验证
        if (this.password.equals(newPassword)) {
            throw new DomainException("新密码不能与旧密码相同");
        }
        this.password = newPassword;
        // 添加领域事件
        this.addDomainEvent(new PasswordChangedEvent(this.userId));
    }
}

// 值对象
@Value
public class Email {
    private final String value;

    public Email(String value) {
        if (!isValid(value)) {
            throw new IllegalArgumentException("Invalid email");
        }
        this.value = value;
    }
}

// 领域服务
@DomainService
public class UserDomainService {
    public void validateUser(User user) {
        // 复杂的业务验证逻辑
    }
}
```

#### 3.2.4 Infrastructure Layer（基础设施层）

**职责：**
- 仓储接口实现
- 外部服务集成
- 消息队列操作
- 缓存管理

**核心组件：**
```java
// 仓储实现
@Repository
public class UserRepositoryImpl implements UserRepository {
    @Autowired
    private UserMapper userMapper;

    @Override
    public User findById(UserId userId) {
        UserDO userDO = userMapper.selectById(userId.getValue());
        return UserConverter.toDomain(userDO);
    }

    @Override
    public void save(User user) {
        UserDO userDO = UserConverter.toDO(user);
        userMapper.insert(userDO);
    }
}
```

### 3.3 模块设计

#### 模块实现状态

| 模块 | 状态 | 说明 |
|------|------|------|
| ace-boot-dependencies | 🔴 未开始 | 依赖版本管理 |
| ace-boot-parent | 🔴 未开始 | 父POM配置 |
| ace-boot-common-core | 🔴 未开始 | 核心工具类 |
| ace-boot-common-web | 🔴 未开始 | Web工具类 |
| ace-boot-common-redis | 🔴 未开始 | Redis工具类 |
| ace-boot-starter-web | 🔴 未开始 | Web自动配置 |
| ace-boot-starter-security | 🔴 未开始 | 安全认证 |
| ace-boot-starter-mybatis | 🔴 未开始 | 数据访问 |
| ace-boot-starter-redis | 🔴 未开始 | 缓存配置 |
| ace-boot-starter-doc | 🔴 未开始 | API文档 |
| ace-boot-starter-monitor | 🔴 未开始 | 监控配置 |
| ace-boot-starter-log | 🔴 未开始 | 日志配置 |
| ace-boot-starter-limiter | 🔴 未开始 | 限流配置 |
| ace-boot-starter-job | 🔴 未开始 | 任务调度 |
| ace-boot-generator | 🔴 未开始 | 代码生成器 |
| ace-boot-samples | 🟢 已完成 | 示例项目（当前为单体） |

**状态说明**：
- 🔴 未开始 - 尚未开始开发
- 🟡 开发中 - 正在开发
- 🟢 已完成 - 已完成并测试
- ⏳ 计划中 - 已规划但未排期

#### 3.3.1 ace-boot-common

公共模块，提供基础工具类和通用功能。

**子模块：**
- **ace-boot-common-core**: 核心工具类（字符串、日期、加密等）
- **ace-boot-common-web**: Web相关工具（请求处理、响应封装等）
- **ace-boot-common-redis**: Redis工具类（缓存操作、分布式锁等）

#### 3.3.2 ace-boot-starters

自动配置模块，提供开箱即用的功能。

**主要Starters：**

| Starter | 功能 | 核心依赖 |
|---------|------|----------|
| ace-boot-starter-web | Web基础配置 | Spring Web, Validation |
| ace-boot-starter-security | 安全认证 | Spring Security, JWT |
| ace-boot-starter-mybatis | 数据访问 | MyBatis Plus, Druid |
| ace-boot-starter-redis | 缓存配置 | Redisson, Spring Cache |
| ace-boot-starter-doc | API文档 | SpringDoc OpenAPI |
| ace-boot-starter-monitor | 监控配置 | Micrometer, Actuator |
| ace-boot-starter-log | 日志配置 | Logback, SkyWalking |
| ace-boot-starter-limiter | 限流配置 | Sentinel |
| ace-boot-starter-job | 任务调度 | XXL-Job |

## 4. 核心功能设计

### 4.1 认证授权

#### 4.1.1 JWT认证流程

```
Client          Gateway         AuthService      UserService
  │                │                 │                │
  ├──[1.Login]────▶│                 │                │
  │                ├──[2.Forward]───▶│                │
  │                │                 ├──[3.Validate]─▶│
  │                │                 │◀────[4.User]────│
  │                │                 ├──[5.Generate Token]
  │                │◀──[6.Token]─────│                │
  │◀──[7.Response]─│                 │                │
  │                │                 │                │
  ├──[8.Request]──▶│                 │                │
  │   with Token   ├──[9.Verify]────▶│                │
  │                │◀──[10.Claims]───│                │
  │                ├──[11.Forward to Service]         │
  │◀──[12.Response]│                 │                │
```

#### 4.1.2 权限模型（RBAC）

```
User (用户) ─────many-to-many─────▶ Role (角色)
                                        │
                                        │ many-to-many
                                        ▼
                                  Permission (权限)
                                        │
                                        │ one-to-many
                                        ▼
                                   Resource (资源)
```

### 4.2 数据访问

#### 4.2.1 多数据源配置

```yaml
ace-boot:
  datasource:
    master:
      url: jdbc:mysql://localhost:3306/master
      username: root
      password: password
    slave:
      - url: jdbc:mysql://localhost:3306/slave1
        username: root
        password: password
      - url: jdbc:mysql://localhost:3306/slave2
        username: root
        password: password
```

#### 4.2.2 读写分离

```java
@Service
public class UserService {
    @Master  // 写操作走主库
    public void createUser(User user) {
        userMapper.insert(user);
    }

    @Slave   // 读操作走从库
    public User getUser(Long id) {
        return userMapper.selectById(id);
    }
}
```

### 4.3 缓存策略

#### 4.3.1 多级缓存

```
Request ──▶ Local Cache (Caffeine) ──▶ Redis Cache ──▶ Database
              │                           │                │
              │◀──────── Hit ─────────────│                │
              │                           │◀──── Hit ──────│
              │◀────────────────────────── Hit ───────────│
```

#### 4.3.2 缓存注解

```java
@Service
public class UserService {
    @Cacheable(value = "user", key = "#id")
    public User getById(Long id) {
        return userRepository.findById(id);
    }

    @CacheEvict(value = "user", key = "#user.id")
    public void update(User user) {
        userRepository.update(user);
    }

    @CachePut(value = "user", key = "#user.id")
    public User save(User user) {
        return userRepository.save(user);
    }
}
```

### 4.4 限流熔断

#### 4.4.1 限流策略

- **QPS限流**：限制每秒请求数
- **并发数限流**：限制同时处理的请求数
- **流量整形**：平滑流量，防止突发

#### 4.4.2 熔断机制

```
正常 ──[错误率超过阈值]──▶ 熔断开启 ──[冷却时间]──▶ 半开状态
 ▲                                                    │
 └──────────[成功率恢复]────────────────────────────┘
```

### 4.5 分布式事务

#### 4.5.1 最终一致性方案

```
Service A                    MQ                    Service B
    │                        │                         │
    ├──[1.Local Transaction]─│                         │
    ├──[2.Send Message]─────▶│                         │
    │                        ├──[3.Store Message]      │
    │                        ├──[4.Deliver Message]───▶│
    │                        │                    [5.Process]
    │                        │◀──[6.ACK]───────────────│
    │                        │                         │
```

## 5. 部署架构

### 5.1 容器化部署

```yaml
# docker-compose.yml
version: '3.8'
services:
  app:
    image: ace-boot:latest
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=prod
    depends_on:
      - mysql
      - redis

  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: password
      MYSQL_DATABASE: aceboot

  redis:
    image: redis:7.0
    ports:
      - "6379:6379"
```

### 5.2 Kubernetes部署

```yaml
# deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: ace-boot
spec:
  replicas: 3
  selector:
    matchLabels:
      app: ace-boot
  template:
    metadata:
      labels:
        app: ace-boot
    spec:
      containers:
      - name: ace-boot
        image: ace-boot:latest
        ports:
        - containerPort: 8080
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "prod"
```

## 6. 性能优化

### 6.1 数据库优化

- **索引优化**：合理创建索引，避免全表扫描
- **SQL优化**：避免N+1查询，使用批量操作
- **连接池**：合理配置连接池大小
- **分库分表**：大数据量场景下的水平拆分

### 6.2 缓存优化

- **缓存预热**：系统启动时加载热点数据
- **缓存更新**：使用Cache Aside模式
- **缓存穿透**：布隆过滤器防护
- **缓存雪崩**：随机过期时间，限流降级

### 6.3 JVM优化

```bash
-Xms4g -Xmx4g                     # 堆内存
-XX:MetaspaceSize=256m            # 元空间
-XX:MaxMetaspaceSize=512m
-XX:+UseG1GC                      # G1垃圾收集器
-XX:MaxGCPauseMillis=100          # GC停顿时间
-XX:+HeapDumpOnOutOfMemoryError   # OOM时生成堆转储
```

## 7. 监控体系

### 7.1 监控指标

- **应用指标**：QPS、RT、错误率、JVM指标
- **业务指标**：用户量、订单量、转化率
- **系统指标**：CPU、内存、磁盘、网络

### 7.2 监控方案

```
Application ──[Metrics]──▶ Micrometer ──▶ Prometheus ──▶ Grafana
     │                                                        ▲
     ├──[Traces]──▶ SkyWalking Agent ──▶ SkyWalking OAP ────┘
     │                                           │
     └──[Logs]────▶ Logback ──▶ FileBeat ──▶ ElasticSearch ──▶ Kibana
```

## 8. 安全设计

### 8.1 安全措施

- **身份认证**：JWT Token、OAuth2
- **权限控制**：RBAC、数据权限
- **数据加密**：敏感数据加密存储
- **传输安全**：HTTPS、数据签名
- **SQL注入**：参数化查询、输入验证
- **XSS防护**：输出转义、CSP头
- **CSRF防护**：Token验证

### 8.2 审计日志

```java
@Aspect
@Component
public class AuditAspect {
    @Around("@annotation(audit)")
    public Object audit(ProceedingJoinPoint joinPoint, Audit audit) {
        // 记录操作前状态
        // 执行操作
        Object result = joinPoint.proceed();
        // 记录操作后状态
        // 保存审计日志
        return result;
    }
}
```

## 9. 开发流程

### 9.1 开发步骤

1. **需求分析**：理解业务需求
2. **领域建模**：识别领域对象和边界
3. **接口设计**：定义API接口
4. **编码实现**：按DDD分层实现
5. **单元测试**：编写测试用例
6. **集成测试**：验证功能完整性
7. **代码审查**：确保代码质量
8. **部署上线**：发布到生产环境

### 9.2 代码生成器使用

```bash
# 生成Entity
mvn ace-boot:generate -Dtype=entity -Dtable=user

# 生成完整CRUD
mvn ace-boot:generate -Dtype=crud -Dtable=user -Dmodule=user

# 生成结果
generated/
├── entity/User.java
├── dto/UserDTO.java
├── mapper/UserMapper.java
├── service/UserService.java
└── controller/UserController.java
```

## 10. 最佳实践

### 10.1 编码规范

- 遵循阿里巴巴Java开发手册
- 使用SonarQube进行代码质量检查
- 保持代码覆盖率在80%以上

### 10.2 性能规范

- 接口响应时间 < 200ms (P99)
- 数据库查询时间 < 100ms
- Redis操作时间 < 10ms

### 10.3 安全规范

- 所有接口必须认证
- 敏感数据必须加密
- 日志不能包含敏感信息

## 11. 故障处理

### 11.1 常见问题

| 问题 | 原因 | 解决方案 |
|------|------|----------|
| OOM | 内存泄漏/配置不当 | 分析堆转储，调整JVM参数 |
| 连接池耗尽 | 连接未释放/池太小 | 检查代码，增大连接池 |
| 缓存穿透 | 恶意请求 | 布隆过滤器，空值缓存 |
| 接口超时 | 慢查询/网络问题 | 优化SQL，检查网络 |

### 11.2 应急预案

1. **降级**：关闭非核心功能
2. **限流**：限制请求频率
3. **扩容**：增加服务实例
4. **回滚**：恢复到上一版本

## 12. 路线图

### Phase 1 (当前)
- ✅ 基础架构搭建
- ✅ 核心功能实现
- ⬜ 示例项目完善

### Phase 2
- ⬜ 微服务支持
- ⬜ 服务网格集成
- ⬜ 云原生适配

### Phase 3
- ⬜ 低代码平台
- ⬜ AI辅助开发
- ⬜ 自动化运维

## 13. 参考资料

- [领域驱动设计](https://www.domainlanguage.com/ddd/)
- [Spring Boot官方文档](https://spring.io/projects/spring-boot)
- [阿里巴巴Java开发手册](https://github.com/alibaba/p3c)
- [微服务架构设计模式](https://microservices.io/patterns/)

---

文档版本：v1.0.0
更新日期：2024-11-06
维护团队：ACE-Boot Team