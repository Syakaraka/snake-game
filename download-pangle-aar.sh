#!/bin/bash
# 穿山甲 SDK AAR 下载脚本
# 在本地电脑运行此脚本下载 AAR 文件

mkdir -p app/libs

echo "正在下载穿山甲 SDK..."

# 尝试从多个源下载
curl -L "https://maven.bytedance.com/repository/pangle/com/pangle/cn/mediation-sdk/6.5.0.0/mediation-sdk-6.5.0.0.aar" -o app/libs/mediation-sdk-6.5.0.0.aar

# 检查下载是否成功
if [ -f "app/libs/mediation-sdk-6.5.0.0.aar" ] && [ -s "app/libs/mediation-sdk-6.5.0.0.aar" ]; then
    echo "✅ 下载成功！"
    ls -lh app/libs/mediation-sdk-6.5.0.0.aar
else
    echo "❌ 下载失败"
    echo ""
    echo "请手动下载："
    echo "1. 访问：https://maven.bytedance.com/repository/pangle/com/pangle/cn/mediation-sdk/6.5.0.0/"
    echo "2. 下载 mediation-sdk-6.5.0.0.aar"
    echo "3. 放入：app/libs/mediation-sdk-6.5.0.0.aar"
fi
