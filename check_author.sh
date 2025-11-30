#!/bin/bash

# 检查Java文件@author标记是否为 soeasy.run 的脚本
# 特性：
# 1. 仅检查有@author标记但值错误的文件，无@author的文件完全忽略
# 2. 兼容JavaDoc注释中@author的各种格式（如 * @author soeasy.run、@author   soeasy.run 等）
# 使用方式：
# 1. 赋予执行权限：chmod +x check_author.sh
# 2. 执行脚本：./check_author.sh [项目根目录]
#    示例：./check_author.sh /home/project/soeasy-framework

# 默认项目根目录为当前目录
PROJECT_DIR=${1:-.}

# 需要排除的目录（编译产物/第三方依赖）
EXCLUDE_DIRS=("target" "node_modules" "build" "out" "lib")

# 目标作者信息（核心匹配值）
TARGET_AUTHOR="soeasy.run"
# 正则匹配规则：兼容任意前缀（注释符号/空白）+ @author + 任意空白 + 目标作者
AUTHOR_REGEX="^\s*\*?\s*@author\s+${TARGET_AUTHOR}\s*$"

# 统计变量（仅统计有@author的文件）
TOTAL_CHECKED_FILES=0  # 有@author标记的文件总数
PASS_FILES=0           # @author正确的文件数
WRONG_AUTHOR_FILES=0   # @author错误的文件数

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
NC='\033[0m' # 重置颜色

echo -e "${GREEN}===== 开始检查Java文件@author标记（目标：@author ${TARGET_AUTHOR}）=====${NC}"
echo -e "检查目录：${PROJECT_DIR}"
echo -e "排除目录：${EXCLUDE_DIRS[*]}\n"

# 递归查找所有.java文件并排除指定目录
find "${PROJECT_DIR}" -type d \( $(printf -- '-name %s -o ' "${EXCLUDE_DIRS[@]}") -false \) -prune -o -type f -name "*.java" -print0 | while IFS= read -r -d '' FILE; do
    # 提取文件中所有包含@author的行（忽略大小写，仅保留有@author的行）
    AUTHOR_LINES=$(grep -i -E '^\s*\*?\s*@author' "$FILE" | sed -e 's/^\s*\*?\s*//' -e 's/\s*$//')
    
    # 仅处理有@author标记的文件
    if [ -n "$AUTHOR_LINES" ]; then
        ((TOTAL_CHECKED_FILES++))
        
        # 检查是否包含正确的@author值（正则匹配，忽略空白/注释符号）
        if grep -i -qE "${AUTHOR_REGEX}" "$FILE"; then
            ((PASS_FILES++))
        else
            # 提取错误的@author内容（去重+清理格式）
            WRONG_AUTHOR=$(echo "$AUTHOR_LINES" | head -n1 | sed -e 's/^@author\s*//' -e 's/\s*$//')
            ((WRONG_AUTHOR_FILES++))
            echo -e "${RED}[错误] ${FILE}${NC}"
            echo -e "       实际@author：${WRONG_AUTHOR} | 期望：${TARGET_AUTHOR}"
        fi
    fi
done

# 输出统计结果
echo -e "\n===== 检查完成 ======"
echo -e "有@author标记的文件总数：${TOTAL_CHECKED_FILES}"
echo -e "${GREEN}✅ @author正确：${PASS_FILES}${NC}"
echo -e "${RED}❌ @author错误：${WRONG_AUTHOR_FILES}${NC}"

# 非0退出码（便于CI/CD集成）
if [ ${WRONG_AUTHOR_FILES} -gt 0 ]; then
    echo -e "\n${RED}存在@author标记错误的文件，请检查并修正！${NC}"
    exit 1
else
    echo -e "\n${GREEN}所有有@author标记的文件均符合要求！${NC}"
    exit 0
fi