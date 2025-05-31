# 配置文件说明

## 重要提示 ⚠️

本项目的 `application.yml` 文件包含敏感信息，已被 `.gitignore` 忽略，不会提交到版本控制系统。

## 配置步骤

1. **复制示例配置文件**
   ```bash
   cp src/main/resources/application.yml.example src/main/resources/application.yml
   ```

2. **修改配置信息**

### 数据库配置
```yaml
spring:
  datasource:
    url: jdbc:mysql://127.0.0.1:3306/test?useUnicode=true&characterEncoding=utf8&autoReconnect=true&useSSL=false
    username: root
    password: your_database_password  # 替换为你的数据库密码
```

### QQ邮箱配置
```yaml
spring:
  mail:
    username: your_qq_email@qq.com     # 替换为你的QQ邮箱
    password: your_qq_email_auth_code   # 替换为QQ邮箱授权码
```

#### 如何获取QQ邮箱授权码：
1. 登录QQ邮箱网页版
2. 点击左上角"设置" → "账户"
3. 找到"POP3/IMAP/SMTP/Exchange/CardDAV/CalDAV服务"
4. 开启"POP3/SMTP服务"或"IMAP/SMTP服务"
5. 验证密保后获得授权码（16位字符）

### AI配置
```yaml
ai:
  key: your_ai_api_key_here  # 替换为你的AI API密钥
```

支持的AI服务：
- **阿里通义千问**: 在[阿里云百炼平台](https://bailian.console.aliyun.com/)获取API Key
- **字节豆包**: 在[火山引擎控制台](https://console.volcengine.com/)获取API Key

### Redis配置
```yaml
spring:
  redis:
    host: 127.0.0.1  # Redis服务器地址
    port: 6379       # Redis端口号
    database: 0      # 数据库索引
```

## 安全建议

1. **绝不提交敏感信息到Git**
   - 配置文件中的密码、密钥等敏感信息
   - 确保 `.gitignore` 包含配置文件

2. **生产环境配置**
   - 使用环境变量或配置中心管理敏感配置
   - 定期更换密钥和密码

3. **开发团队协作**
   - 团队成员各自维护本地配置文件
   - 通过安全渠道分享必要的配置信息

## 常见问题

**Q: 为什么我的应用启动失败？**
A: 请检查是否已正确配置 `application.yml` 文件，特别是数据库连接信息。

**Q: 邮件发送功能不工作？**
A: 请确认QQ邮箱授权码配置正确，并且已开启SMTP服务。

**Q: AI功能无法使用？**
A: 请检查AI API密钥是否有效，账户余额是否充足。 