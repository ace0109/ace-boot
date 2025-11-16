#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR=$(cd "$(dirname "$0")" && pwd)
cd "$SCRIPT_DIR"

modules_file=$(mktemp)
cleanup() {
  rm -f "$modules_file"
}
trap cleanup EXIT

find "$SCRIPT_DIR" -mindepth 2 -maxdepth 2 -name pom.xml | sort | while IFS= read -r pom; do
  if grep -q "spring-boot-maven-plugin" "$pom"; then
    dir=${pom#$SCRIPT_DIR/}
    dir=${dir%/pom.xml}
    printf '%s\n' "$dir"
  fi
done > "$modules_file"

if [[ ! -s "$modules_file" ]]; then
  echo "未发现可运行的模块（需在模块 pom.xml 中声明 spring-boot-maven-plugin）。" >&2
  exit 1
fi

echo "可启动的模块："
idx=1
while IFS= read -r module; do
  printf '%2d) %s\n' "$idx" "$module"
  idx=$((idx+1))
done < "$modules_file"

read -r -p "请选择模块序号: " selection
if ! [[ "$selection" =~ ^[0-9]+$ ]]; then
  echo "输入序号无效" >&2
  exit 1
fi

module_count=$(wc -l < "$modules_file")
if (( selection < 1 || selection > module_count )); then
  echo "输入序号超出范围" >&2
  exit 1
fi

read -r -p "请输入 Spring Profile (默认 dev): " profile
profile=${profile:-dev}

module_path=$(sed -n "${selection}p" "$modules_file")

(
  cd "$module_path"
  ../mvnw spring-boot:run -Dspring-boot.run.profiles="$profile"
)
