@echo off
setlocal enabledelayedexpansion

:: 脚本用法说明
:usage
echo Usage: server.bat [start^|stop^|restart^|status] [jar文件名] [可选:环境变量名]
echo Example:
echo   启动默认环境:  server.bat start app.jar
echo   启动测试环境:  server.bat start app.jar test
echo   停止应用:      server.bat stop app.jar
echo   重启应用:      server.bat restart app.jar
echo   查看状态:      server.bat status app.jar
exit /b 1

:: 检查进程是否存在（返回 0=存在，1=不存在）
:is_exist
set "PID="
:: 查询包含指定jar名且是java -jar启动的进程（排除cmd和grep自身）
for /f "tokens=2 delims= " %%a in ('tasklist /fi "imagename eq java.exe" /v ^| findstr /i "%APP_NAME%" ^| findstr /i "java -jar" ^| findstr /v "findstr"') do (
    set "PID=%%a"
)
if defined PID (
    echo %APP_NAME% is running. Pid is !PID!
    exit /b 0  :: 进程存在，返回0
) else (
    exit /b 1  :: 进程不存在，返回1
)

:: 启动应用
:start
call :is_exist
if !errorlevel! equ 0 (
    echo %APP_NAME% is already running. pid=!PID!
) else (
    echo %APP_NAME% is starting...
    if not defined PROFILE (
        :: 无环境变量，直接启动
        start "" /b javaw -jar %APP_NAME% >nul 2>&1
    ) else (
        :: 指定Spring环境变量启动
        start "" /b javaw -jar %APP_NAME% --spring.profiles.active=%PROFILE% >nul 2>&1
    )
    :: 延迟1秒后检查启动状态
    timeout /t 1 /nobreak >nul
    call :is_exist
    if !errorlevel! equ 0 (
        echo %APP_NAME% started successfully.
    ) else (
        echo %APP_NAME% start failed!
    )
)
exit /b

:: 停止应用
:stop
call :is_exist
if !errorlevel! equ 0 (
    echo Stopping %APP_NAME% (pid=!PID!)...
    taskkill /f /pid !PID! >nul 2>&1
    if !errorlevel! equ 0 (
        echo %APP_NAME% stopped successfully.
    ) else (
        echo Failed to stop %APP_NAME%.
    )
) else (
    echo %APP_NAME% is not running.
)
exit /b

:: 查看应用状态
:status
call :is_exist
if !errorlevel! equ 0 (
    echo %APP_NAME% is running. Pid is !PID!
) else (
    echo %APP_NAME% is NOT running.
)
exit /b

:: 重启应用
:restart
echo Restarting %APP_NAME%...
call :stop
call :start
exit /b

:: 主逻辑入口
:: 检查参数数量（至少需要操作类型和jar文件名）
if %# lss 2 (
    echo Error: Missing required parameters!
    goto usage
)

:: 解析参数
set "ACTION=%1"
set "APP_NAME=%2"
set "PROFILE=%3"  :: 环境变量为可选参数
set "PID="

:: 根据操作类型执行对应逻辑
if "%ACTION%"=="start" (
    goto start
) else if "%ACTION%"=="stop" (
    goto stop
) else if "%ACTION%"=="status" (
    goto status
) else if "%ACTION%"=="restart" (
    goto restart
) else (
    echo Error: Invalid action "%ACTION%"!
    goto usage
)

endlocal