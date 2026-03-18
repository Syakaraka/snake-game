# 🇳 Gitee（码云）部署指南

## 快速开始

### 1. 注册 Gitee 账号

访问：https://gitee.com/
- 使用手机号注册
- 完成实名认证（需要身份证）

### 2. 创建仓库

1. 登录后点击右上角 `+` → `新建仓库`
2. 仓库名：`snake-game`
3. 仓库介绍：贪吃蛇游戏 - 经典增强版
4. 设为 **公开仓库**
5. 点击 `创建`

### 3. 推送代码到 Gitee

```bash
cd /home/admin/openclaw/workspace/snake-game

# 添加 Gitee 远程仓库（替换 YOUR_USERNAME 为你的 Gitee 用户名）
git remote add gitee https://gitee.com/YOUR_USERNAME/snake-game.git

# 推送到 Gitee
git push gitee main
```

### 4. 配置 Gitee Go 自动构建

1. 打开你的仓库页面
2. 点击顶部 **`服务`** → **`Gitee Go`**
3. 点击 `启用 Gitee Go`
4. 选择 `build-android-apk` 工作流
5. 点击 `运行` 开始构建

### 5. 下载 APK

构建完成后：
1. 点击 **`服务`** → **`Gitee Go`**
2. 点击最新的构建记录
3. 在 **构建产物** 部分下载 `snake-game-apk.zip`
4. 解压得到 `app-debug.apk`

---

## 🔄 同步 GitHub 和 Gitee

如果想同时维护两个平台：

```bash
cd /home/admin/openclaw/workspace/snake-game

# 推送到 GitHub
git push github main

# 推送到 Gitee
git push gitee main
```

或者一次性推送：
```bash
git push github main && git push gitee main
```

---

## 📊 Gitee vs GitHub

| 功能 | GitHub | Gitee |
|------|--------|-------|
| 服务器位置 | 美国 | 中国 |
| 访问速度 | 慢（国内） | 快 |
| 广告 SDK 下载 | ❌ 失败 | ✅ 成功 |
| 构建时间 | 5-10 分钟 | 2-5 分钟 |
| 中文界面 | ❌ | ✅ |
| 自动构建 | GitHub Actions | Gitee Go |
| 免费额度 | 无限 | 无限 |

---

## 🎯 推荐工作流

### 开发阶段
- 在本地电脑开发
- 代码提交到 Git
- 同时推送到 GitHub 和 Gitee

### 发布阶段
- 使用 Gitee 构建 APK（成功率高）
- 下载 APK 上传到两个平台的 Releases
- TapTap 等国内商店使用 Gitee 下载链接
- 国际用户从 GitHub 下载

---

## 📞 Gitee 官方支持

- 帮助文档：https://help.gitee.com/
- Gitee Go 文档：https://help.gitee.com/gitee-go/
- 客服邮箱：support@gitee.com

---

## ⚠️ 注意事项

1. **实名认证**：Gitee 需要实名认证才能使用 Gitee Go
2. **构建额度**：免费用户每月 1000 分钟构建时间（足够用）
3. **产物保留**：构建产物保留 30 天，及时下载
4. **Release 发布**：手动创建 Release 并上传 APK

---

## 🎉 完成后的效果

- ✅ 代码自动同步到 Gitee
- ✅ 每次推送自动构建 APK
- ✅ 国内访问速度快
- ✅ 广告 SDK 下载稳定
- ✅ TapTap 发布更方便
