# Repository Guidelines for ACE-Boot

欢迎贡献代码与想法！为确保项目质量与协作效率，请遵循以下规范。

## 项目结构与模块组织
ACE-Boot 基于 Spring Boot 3 与轻量级 DDD 架构。核心代码位于 `src/main/java/com/aceboot`，对外接口集中在 `controller`，通用健康检查在 `health`。公共配置存放于 `src/main/resources`，以 `application.yml` 作为入口；贡献指南与设计文档统一放在 `docs/`。新增测试时请在 `src/test/java` 下按照生产包路径进行镜像。Maven 构建产物写入 `target/`，务必保持该目录不被提交。

## 构建、测试与开发命令
- `./mvnw clean verify` —— 完整编译并执行单元测试；推送前必须通过。
- `./mvnw spring-boot:run` —— 本地以开发配置启动服务，默认端口 8080。
- `./mvnw dependency:tree` —— 分析依赖冲突或确认版本来源。
- `curl http://localhost:8080/actuator/health` —— 服务启动后的快速健康检查。
默认使用 Java 17。如功能依赖 Redis 或 MySQL，请先准备对应实例。

## 代码风格与命名规范
遵循 Java 17 规范，统一使用四空格缩进。类名采用大驼峰，方法与变量用小驼峰，常量使用全大写加下划线。控制器类放在 `com.aceboot.controller`，新增领域层或适配层请沿用现有包名规则。常用样板代码优先使用 Lombok（如 `@RequiredArgsConstructor`），并为每个 REST 入口显式声明 `@RequestMapping` 路径。新增配置属性时同步更新 `spring-configuration-metadata.json` 相关元数据。

## 测试规范
项目基于 JUnit Jupiter 与 Spring Boot Test。测试类统一以 `*Test` 结尾，并保持与被测类相同的包层级。`@SpringBootTest` 仅在需加载完整上下文时使用，优先编写切片测试（如 `@WebMvcTest`、`@DataJpaTest`）以保障速度。每次提交前至少运行 `./mvnw test`，涉及持久化或配置变更时补充针对性的集成测试，覆盖正向流程与异常路径。

## 提交与 Pull Request 规范
延续约定式提交格式 `type(scope): 简述`，例如 `feat(health): 添加基础的健康检查端点`。若关联 Issue，可在 scope 中引用编号。提交 PR 时需要：
- 简要概述改动与动机；
- 列出验证步骤或关键命令输出，必要时附接口响应截图；
- 标明配置或数据迁移影响，并同步更新 `docs/`。
在创建 PR 前确认 `./mvnw clean verify` 已通过。

## 配置与安全提示
环境差异配置建议放入 `src/main/resources/application-<profile>.yml`，通过 `SPRING_PROFILES_ACTIVE` 激活。任何密钥或凭证均不得入库，优先使用 `ACEBOOT_*` 环境变量或部署平台的密钥托管。新增敏感配置时，将默认说明写入 `docs/configuration.md`，并确保相关健康检查已覆盖。

## 敏捷流程与服务管理
参考 `docs/AGILE_PLAN.md` 了解当前迭代目标，按照计划拆分任务，小步提交并同步更新任务状态。每个迭代须完成文档补充（含结构、配置、API 说明），并确保核心模块测试覆盖率 ≥ 80%。启动本地服务（如 `./mvnw spring-boot:run`）作业完成后务必停止，可用 `lsof -i:8080` 与 `ps aux | grep spring-boot` 排查遗留进程，如有残留立刻 `kill -9 <PID>` 释放端口。

## 共享讨论记录
跨智能体的工程讨论集中记录在根目录 `AI_DISCUSSION.md`，贡献前请查阅最新共识并在文末追加观点，注明身份与日期。
