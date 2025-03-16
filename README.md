# 在线OJ判题系统

## 项目简介
**开发者**：李欣、刘朔飞、刘珂  
**学年**：2023-2024a  
**课程教师**：王鹏伟  
**核心定位**：面向编程学习/技术面试场景的高并发代码评测平台  

---

## 核心功能
### 用户侧功能
- 🚀 **代码实时评测**  
  - 支持Java代码提交（语法检查→沙箱隔离运行→多测试用例验证）
  - 返回编译错误/运行结果/内存&CPU消耗指标
- 🔍 **智能题目检索**  
  - 支持标签筛选、难度分级、关键词模糊搜索
- 📊 **数据可视化**  
  - 用户通过率分析、题目热度统计、提交记录时间轴

### 管理侧功能
- ⚙️ **题目全生命周期管理**  
  - Markdown富文本编辑题目描述
  - 测试用例批量导入/版本控制
  - 题目访问权限分级控制
- 🛡️ **系统安全监控**  
  - Docker沙箱资源隔离
  - 恶意代码特征检测（AST静态分析+运行时行为监控）

---

## 分层架构说明
### 系统分层设计


### 1. 用户界面层（Presentation Layer）
- **技术栈**: 
  - Vue3 SPA：构建响应式单页应用
  - Monaco Editor：提供类IDE的代码编辑器体验
  - Ant Design Vue：企业级UI组件库
- **核心能力**:
  - 实时代码编辑与语法高亮
  - 交互式题目展示与提交
  - 评测结果可视化（通过图表展示时间/内存消耗）

### 2. 业务逻辑层（Business Layer）
- **技术组件**:
  - Spring Boot：RESTful API服务
  - 消息队列（RabbitMQ/Kafka）：异步任务调度
  - 判题引擎：核心评测逻辑处理器
- **关键特性**:
  - 基于优先级队列的任务调度
  - 自动水平扩展的判题集群
  - 支持多语言（Java/Python/C++）的适配器模式

### 3. 数据服务层（Data Service Layer）
- **存储系统**:
  - MySQL集群（主从复制+分库分表）：题目/用户数据存储
  - Redis缓存（Cluster模式）：热点数据缓存（如排行榜）
  - 分布式文件存储：测试用例/代码模板存储
- **数据流**:
  ```mermaid
  graph LR
    A[元数据] --> MySQL
    B[测试用例] --> MinIO
    C[评测结果] --> MySQL
    D[实时排行] --> Redis
---

## 技术选型
### 后端技术栈
| 模块           | 技术方案                              | 版本       |
|----------------|---------------------------------------|------------|
| 基础框架       | Spring Boot 3.x                       | 3.1.5      |
| 安全隔离       | Docker + Java Security Manager        | 20.10.17   |
| 数据存储       | MySQL 8 + ShardingSphere              | 5.3.2      |
| 缓存加速       | Redis 7 + Caffeine                    | 3.2.0      |
| 消息通信       | RabbitMQ                              | 3.12.0     |
| 代码分析       | ANTLR 4 + JavaParser                  | 4.13.0     |

### 前端技术栈
| 模块           | 技术方案                              | 版本       |
|----------------|---------------------------------------|------------|
| 核心框架       | Vue3 + Composition API                | 3.3.4      |
| 代码编辑       | Monaco Editor                         | 0.45.0     |
| 状态管理       | Vuex                                  | 4.1.0      |
| 样式组件       | Ant Design Vue                        | 3.2.20     |
| 工程化         | Vite 4 + TypeScript 5                 | 4.4.5      |

---

## 部署要求
### 硬件配置
| 节点类型       | 最低配置                | 推荐配置                |
|----------------|-------------------------|-------------------------|
| 应用服务器     | 2核4G                   | 4核8G（Docker集群）     |
| 数据库服务器   | 4核8G + 200GB SSD       | 8核16G + RAID10 SSD     |
| 网络带宽       | 50Mbps                  | ≥100Mbps（BGP线路）     |

### 软件依赖
- Docker Engine 20.10+
- OpenJDK 17+
- Node.js 18.x LTS
- MySQL 8.x with InnoDB Cluster

---

## 关键设计
### 安全沙箱实现
```java
// Docker沙箱管理器示例代码
public class DockerJudgeEnv {
    private DockerClient dockerClient;
    
    public ExecutionResult executeCode(Submission submission) {
        // 1. 创建临时容器
        CreateContainerCmd cmd = dockerClient.createContainerCmd("openjdk:17")
            .withMemorySwap(0L)
            .withMemory(512 * 1024 * 1024L) // 内存限制512MB
            .withCpuShares(512); // CPU权重限制
        
        // 2. 注入安全策略
        HostConfig hostConfig = new HostConfig()
            .withCapDrop("ALL")
            .withSecurityOpts(List.of("no-new-privileges"));
        cmd.withHostConfig(hostConfig);
        
        // 3. 执行代码并获取结果
        // ...（省略具体实现）
    }
}
```

### 高并发处理
- **二级缓存策略**：
  ```plaintext
  用户请求 → Redis分布式缓存 → Caffeine本地缓存 → DB查询
  ```
- **动态队列分流**：
  ```mermaid
  graph LR
      A[提交请求] --> B{队列长度<100?}
      B -->|是| C[即时处理队列]
      B -->|否| D[批量处理队列]
      C --> E[优先调度]
      D --> F[空闲时段处理]
  ```

---

## 快速开始
### 本地开发模式
```bash
# 后端服务
mvn clean install
java -jar -Dspring.profiles.active=dev target/oj-server.jar

# 前端开发
cd frontend
npm install
npm run dev -- --port 5173
```

### 生产环境部署
```bash
# 使用Docker Compose启动核心服务
docker-compose -f docker-compose.prod.yml up -d

# 初始化数据库
mysql -h 127.0.0.1 -u root -p < init_schema.sql
```

---

## 故障处理指南
| 故障现象               | 排查步骤                              | 应急方案                      |
|------------------------|---------------------------------------|-------------------------------|
| 评测服务无响应         | 1. 检查Docker服务状态<br>2. 查看RabbitMQ队列堆积情况 | 重启判题引擎容器              |
| 数据库连接超时         | 1. 验证主从同步状态<br>2. 检查连接池配置           | 切换只读副本+限流            |
| 内存泄漏               | 1. 生成Heap Dump分析<br>2. 检查未关闭的资源句柄    | 隔离问题容器+自动横向扩展     |
| 恶意代码攻击            | 1. 检查沙箱逃逸日志<br>2. 分析系统调用记录         | 封禁IP+增强安全策略          |

---

## 参考资源
- [Hcode Online Judge架构设计](https://github.com/HimitZH/HOJ)
- 青岛大学OJ系统实现方案
- Java安全沙箱最佳实践

[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
```
