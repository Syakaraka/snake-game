# 🐍 安卓贪吃蛇游戏

一个使用 HTML5 + WebView 开发的安卓贪吃蛇游戏。

## 功能特性

- ✅ 经典贪吃蛇玩法
- ✅ 触摸滑动控制
- ✅ 虚拟方向键
- ✅ 分数记录（本地存储）
- ✅ 游戏速度逐渐加快
- ✅ 精美 UI 设计
- ✅ 全屏沉浸体验

## 项目结构

```
snake-game/
├── app/
│   ├── src/
│   │   └── main/
│   │       ├── assets/
│   │       │   └── index.html      # 游戏主文件
│   │       ├── java/com/snake/game/
│   │       │   └── MainActivity.java
│   │       ├── res/
│   │       │   └── values/
│   │       │       ├── strings.xml
│   │       │       └── themes.xml
│   │       └── AndroidManifest.xml
│   └── build.gradle
├── build.gradle
├── settings.gradle
├── gradle.properties
└── README.md
```

## 构建说明

### 方法 1：使用 Android Studio（推荐）

1. 打开 Android Studio
2. 选择 `File` → `Open`
3. 选择 `snake-game` 文件夹
4. 等待 Gradle 同步完成
5. 点击 `Build` → `Build Bundle(s) / APK(s)` → `Build APK(s)`
6. APK 生成在：`app/build/outputs/apk/debug/app-debug.apk`

### 方法 2：使用命令行

```bash
cd snake-game

# Windows
gradlew.bat assembleDebug

# macOS/Linux
./gradlew assembleDebug
```

APK 位置：`app/build/outputs/apk/debug/app-debug.apk`

## 安装到手机

1. 将 APK 文件传输到安卓手机
2. 在手机上打开 APK 文件
3. 允许"未知来源"安装（如果需要）
4. 点击安装

## 游戏操作

- **方向键**：点击屏幕下方的方向按钮
- **滑动**：在游戏区域上下左右滑动
- **键盘**（模拟器）：方向键或 WASD
- **空格键**：开始/暂停游戏

## 技术说明

- **最低安卓版本**：Android 5.0 (API 21)
- **目标安卓版本**：Android 14 (API 34)
- **开发工具**：Android Studio 8.2+
- **核心技术**：WebView + HTML5 Canvas

## 自定义

### 修改游戏速度

编辑 `index.html`，找到：
```javascript
let gameSpeed = 150;  // 毫秒，数值越小速度越快
```

### 修改游戏区域大小

编辑 `index.html`，找到：
```html
<canvas id="gameCanvas" width="300" height="300"></canvas>
```

### 修改颜色主题

编辑 `index.html` 中的 CSS 部分，修改颜色值。

## 许可证

MIT License - 可自由使用和修改
