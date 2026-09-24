@if "%DEBUG%" == "" @echo off
@rem Copyright (C) 2012-2024 The Gradle Foundation
@rem ----------
@rem Sub-Gradle Windows launch script ----------

if "%DIRNAME%" == "" set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.

set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%

@rem Resolve any relative paths
if "%APP_HOME%" == "" (
    set APP_HOME=.
)

set DEFAULT_JVM_OPTS="-Xmx64m" "-Xms64m"

set JAVA_EXE=
if not "%JAVA_HOME%" == "" (
    set "JAVA_EXE=%JAVA_HOME%\bin\java.exe"
    if exist "%JAVA_EXE%" goto execute
)

set JAVA_EXE=java.exe
%JAVA_EXE% -version >N2>N1
if %ERRORLEVEL% equ 0 goto execute

echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
echo Please reload Android Studio or set JAVA_HOME.
exit /b 1

:execute
@emit the execution
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% -Dorg.gradle.appname="%APP_BASE_NAME%" -classpath "%APP_HOME%\gradle\wrapper\gradle-wrapper.jar" org.gradle.wrapper.GradleWrapperMain %*
