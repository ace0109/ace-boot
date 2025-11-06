# CLAUDE.md

本文档为 Claude Code (claude.ai/code) 在此仓库工作时提供指导。

## 项目概述

ACE-Boot 是一个基于 Spring Boot 3.x 和 DDD（领域驱动设计）架构的生产级脚手架。项目采用敏捷开发方式，通过小步迭代逐步构建完整的企业级开发框架。

## 开发命令

```bash
# 构建和测试
mvn clean install                  # 完整构建（包含测试）
mvn clean install -DskipTests       # 构建但跳过测试
mvn clean compile                   # 仅编译

# 运行应用
mvn spring-boot:run                 # 使用Maven运行
java -jar target/ace-boot-1.0.0-SNAPSHOT.jar  # 运行JAR包

# 测试命令
mvn test                           # 运行所有测试
mvn test -Dtest=ClassName         # 运行特定测试类
mvn test -Dtest=ClassName#methodName  # 运行特定测试方法

# 打包
mvn clean package                  # 创建JAR包
mvn clean package -DskipTests      # 打包但跳过测试
```

## 架构说明

### 当前状态
项目当前是一个简单的 Spring Boot 单体应用，正在按照敏捷计划逐步转换为多模块架构。

### 目标DDD分层
1. **Interface Layer（接口层）** - HTTP端点、请求响应处理
2. **Application Layer（应用层）** - 用例编排、事务边界
3. **Domain Layer（领域层）** - 业务逻辑、领域模型、领域服务
4. **Infrastructure Layer（基础设施层）** - 数据访问、外部服务、技术实现

### 计划模块结构
```
ace-boot (根模块 - 多模块POM)
├── ace-boot-dependencies (版本管理)
├── ace-boot-parent (所有模块的父POM)
├── ace-boot-common (共享工具类)
├── ace-boot-starters (自动配置模块)
├── ace-boot-generator (代码生成器)
└── ace-boot-samples (示例应用)
```

## 敏捷开发流程

### Sprint计划
- 参考 `docs/AGILE_PLAN.md` 获取详细的Sprint计划
- 每个Sprint为2周
- 每个任务控制在2-4小时内完成

### 配置命名空间
所有自定义配置使用 `ace-boot` 前缀：
```yaml
ace-boot:
  security:
    jwt:
      secret: xxx
  mybatis:
    multi-datasource: true
  redis:
    enabled: true
```

## 开发规范

### 迭代完成标准

#### 1. 测试要求
每个迭代（Task）必须满足以下测试要求才算完成：
- ✅ 单元测试编写完成且全部通过
- ✅ 核心模块测试覆盖率 >= 80%
- ✅ 工具类测试覆盖率 = 100%
- ✅ 如涉及API变更，需要集成测试或端到端测试通过
- ✅ 测试执行时间 < 30秒

示例测试命令：
```bash
# 运行测试并生成覆盖率报告
mvn clean test jacoco:report

# 验证覆盖率是否满足要求
mvn verify
```

#### 2. 文档更新要求
每个迭代完成后必须更新相关文档：
- ✅ 如新增模块，更新项目结构说明
- ✅ 如新增配置项，更新配置说明
- ✅ 如新增API，更新API文档
- ✅ 如架构变更，更新 ARCHITECTURE.md
- ✅ 在 AGILE_PLAN.md 中标记任务状态为"已完成"
- ✅ 最后还需要提交代码

#### 3. 代码提交规范

##### Commit Message 格式
```
<type>(<scope>): <subject>

<body>

<footer>
```

##### Type类型说明
- **feat**: 新功能
- **fix**: 修复bug
- **docs**: 文档更新
- **style**: 代码格式调整（不影响代码运行）
- **refactor**: 重构（既不是新增功能，也不是修复bug）
- **test**: 添加或修改测试
- **chore**: 构建过程或辅助工具的变动

##### 示例
```bash
# 新功能
git commit -m "feat(security): 添加JWT认证功能

- 实现JwtTokenUtil工具类
- 添加认证过滤器
- 配置Spring Security

closes #123"

# 修复bug
git commit -m "fix(web): 修复全局异常处理器空指针问题

当异常message为null时会导致NPE

fixes #456"

# 文档更新
git commit -m "docs: 更新README中的快速开始部分"
```

#### 4. 代码审查要求
- ✅ 代码符合Java编码规范（阿里巴巴Java开发手册）
- ✅ 无明显的性能问题
- ✅ 无安全漏洞
- ✅ 注释清晰，公共API必须有JavaDoc

### 其他开发规范

<!-- ===== 用户自定义规范区域开始 ===== -->
<!-- 请在此处添加你的额外规范要求 -->
<!-- 格式建议：使用 ### 作为规范类别标题，使用列表说明具体要求 -->

### 服务管理规范
- 如果启动了任何服务（如 `mvn spring-boot:run`），必须在任务完成后关闭
- 使用后台进程时，记录进程ID或使用工具跟踪
- 任务结束前检查端口占用：`lsof -i:8080`
- 确保没有遗留进程：`ps aux | grep spring-boot`
- 如发现遗留进程，立即终止：`kill -9 PID`

### [待添加的自定义规范]

<!-- 示例格式：
### 数据库设计规范
- 表名使用小写，单词间用下划线分隔
- 主键统一命名为id
- 必须包含created_time和updated_time字段
- 逻辑删除使用deleted字段（0-未删除，1-已删除）
-->

<!-- ===== 用户自定义规范区域结束 ===== -->

## 技术决策

### 技术栈
- **Java 17+** - 最低JDK版本
- **Spring Boot 3.5.7** - 核心框架
- **Maven** - 构建工具（将转换为多模块）
- **MyBatis Plus** - 计划使用的ORM框架
- **JWT** - 身份认证机制
- **Redis** - 缓存解决方案

### 包结构约定
```
com.aceboot
├── {module}
│   ├── interface/controller
│   ├── application/service
│   ├── domain
│   │   ├── entity
│   │   ├── valueobject
│   │   └── service
│   └── infrastructure
│       ├── repository
│       └── mapper
```

## 重要文件

- **docs/AGILE_PLAN.md** - 敏捷开发计划，包含所有Sprint和Task
- **docs/ARCHITECTURE.md** - 完整的架构设计和模式说明
- **README.md** - 项目概述和快速参考

## 注意事项

1. **严格遵循敏捷计划**：不要一次性创建所有模块，按照Sprint计划逐步实现
2. **测试驱动开发**：先写测试，再写实现
3. **小步提交**：每完成一个小功能就提交，避免大量代码堆积
4. **持续重构**：发现代码异味立即重构，保持代码整洁
5. **文档同步**：代码变更后立即更新相关文档

## 常见问题处理

### 遇到测试失败
1. 先确认测试本身是否正确
2. 检查测试环境配置
3. 使用调试模式运行单个测试
4. 必要时可以临时跳过，但必须在PR中说明原因

### 遇到编译错误
1. 确认JDK版本是否正确
2. 执行 `mvn clean` 清理后重试
3. 检查依赖版本是否冲突

### 性能问题
1. 使用JProfiler或VisualVM进行性能分析
2. 检查是否有N+1查询问题
3. 考虑添加缓存或优化算法