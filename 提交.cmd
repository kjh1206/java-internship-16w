@echo off
cd /d "%~dp0"

echo ==============================
echo  提交今天的代码到 GitHub
echo ==============================
echo.

git add -A
git diff --cached --quiet
if not errorlevel 1 (
    echo [跳过] 没有检测到改动，无法提交
    echo.
    pause
    exit /b 0
)

echo 将要提交的文件：
git diff --cached --name-status
echo.

set "MSG=%~1"
if "%MSG%"=="" set "MSG=update %date%"

echo 提交说明：%MSG%
git commit -m "%MSG%"
if errorlevel 1 (
    echo.
    echo [失败] 提交没有成功，请看上面的报错
    pause
    exit /b 1
)

echo.
echo 正在推送...
git push
if errorlevel 1 (
    echo.
    echo [失败] 推送失败。常见原因：
    echo   1. 网络问题，重试一次
    echo   2. 远程有新提交，先执行: git pull --rebase
    echo.
    pause
    exit /b 1
)

echo.
echo [完成] 已推送到 github.com/kjh1206/java-internship-16w
pause
