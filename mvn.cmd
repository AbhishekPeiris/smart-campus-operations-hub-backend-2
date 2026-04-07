@echo off
setlocal

set "JAVA_HOME=C:\Users\Abhishek\.jdk\jdk-21.0.8"
if not exist "%JAVA_HOME%\bin\java.exe" set "JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-21"
if not exist "%JAVA_HOME%\bin\java.exe" (
  echo [ERROR] Java 21 not found.
  echo Checked: C:\Users\Abhishek\.jdk\jdk-21.0.8 and C:\Program Files\Eclipse Adoptium\jdk-21
  echo Install Temurin 21 or update this path in mvn.cmd.
  exit /b 1
)

set "PATH=%JAVA_HOME%\bin;%PATH%"
call "%~dp0operationshub\mvnw.cmd" -f "%~dp0operationshub\pom.xml" %*
exit /b %ERRORLEVEL%
