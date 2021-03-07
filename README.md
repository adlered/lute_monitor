# lute_monitor

### 用途

作为 Lute_HTTP 服务的反向代理，提供流量审查、缓存优化功能。

### 使用方法

在 [release](https://github.com/adlered/lute_monitor/releases) 中下载 jar 文件并运行：

```
java -jar lute_monitor.jar [monitorProxyPort] [luteServer] [ipLimitOptions]
```

**monitorProxyPort** 设置 Lute 监控机代理的监听端口（连接到该端口访问 Lute_HTTP 服务）  
**luteServer** Lute_HTTP 服务所在地址，例如：`127.0.0.1:8248`  
**ipLimitOptions** 每个 IP 访问频率限制，例如：`20/1` 表示每秒钟允许 20 次解析。如果你使用博客系统，建议不要低于 `20次每秒` 。