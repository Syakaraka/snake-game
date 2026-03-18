# 🚀 GitHub Actions 自动构建 APK 指南

## 快速开始

### 1. 创建 GitHub 仓库

1. 登录 https://github.com
2. 点击右上角 `+` → `New repository`
3. 仓库名：`snake-game`（或你喜欢的名字）
4. 设为 **Public** 或 **Private** 都可以
5. 点击 `Create repository`

### 2. 上传代码

```bash
cd /home/admin/openclaw/workspace/snake-game

# 初始化 git
git init

# 添加所有文件
git add .

# 提交
git commit -m "Initial commit: Snake Game"

# 关联远程仓库（替换 YOUR_USERNAME 为你的 GitHub 用户名）
git remote add origin https://github.com/YOUR_USERNAME/snake-game.git

# 推送
git push -u origin main
```

如果提示分支名不对，可能是 `master`：
```bash
git branch -M main
git push -u origin main
```

### 3. 自动构建

推送成功后：

1. 打开你的 GitHub 仓库页面
2. 点击顶部的 **Actions** 标签
3. 你会看到 "Build Android APK" 工作流正在运行
4. 等待构建完成（约 5-10 分钟）

### 4. 下载 APK

构建完成后，有两种方式下载：

#### 方式 A：从 Actions 下载（每次构建）

1. 点击 **Actions** 标签
2. 点击最新的构建记录
3. 在页面底部找到 **Artifacts** 部分
4. 点击 `snake-game-apk` 下载 APK 文件

> ⚠️ Artifact 保留 30 天

#### 方式 B：从 Releases 下载（推荐，永久保存）

创建一个 tag 来发布正式版本：

```bash
# 创建版本标签
git tag v1.0.0

# 推送标签
git push origin v1.0.0
```

推送后会自动触发构建，并在 **Releases** 页面生成永久下载链接：
1. 点击仓库的 **Releases** 标签（或右侧的 Releases）
2. 找到 v1.0.0 版本
3. 在 Assets 中下载 `app-debug.apk`

---

## 手动触发构建

你也可以随时手动触发构建：

1. 点击 **Actions** 标签
2. 点击左侧的 "Build Android APK"
3. 点击右侧的 **Run workflow** 按钮
4. 选择分支（默认 main）
5. 点击绿色按钮

---

## 构建配置说明

### 环境变量

- **JDK 版本**: 17
- **Gradle 版本**: 8.2
- **Android SDK**: 自动安装（API 34）
- **Artifact 保留期**: 30 天

### 触发条件

- ✅ 推送到 main/master 分支
- ✅ 创建 Pull Request
- ✅ 手动触发（workflow_dispatch）
- ✅ 创建版本标签（自动发布 Release）

---

## 常见问题

### Q: 构建失败怎么办？

A: 点击失败的构建记录，查看日志，常见原因：
- 网络连接问题（重试即可）
- Gradle 配置错误（检查 build.gradle）

### Q: APK 在哪里？

A: 
- 临时下载：Actions → 构建记录 → Artifacts
- 永久下载：Releases → 对应版本 → Assets

### Q: 如何修改应用名称？

A: 编辑 `app/src/main/res/values/strings.xml`：
```xml
<string name="app_name">你的游戏名</string>
```

### Q: 如何修改包名？

A: 需要修改多处：
1. `AndroidManifest.xml` 中的 `package`
2. `app/build.gradle` 中的 `applicationId`
3. `src/main/java/` 目录结构

---

## 后续更新

修改代码后，只需：

```bash
git add .
git commit -m "更新说明"
git push
```

GitHub Actions 会自动构建新的 APK！

---

## 许可证

MIT License - 可自由使用和修改
