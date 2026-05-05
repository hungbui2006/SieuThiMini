@echo off
echo ========================================
echo   BUILD FAT JAR - Sieu Thi Mini
echo ========================================
echo.

set JAVA_HOME=C:\Program Files\Microsoft\jdk-17.0.18.8-hotspot
set JAVA_BIN=%JAVA_HOME%\bin
set JAVA_SRC=src\main\java
set OUT_DIR=out
set LIB_DIR=lib
set JAR_NAME=SieuThiMini.jar
set MAIN_CLASS=com.supermarket.view.swing.SwingApp
set BUILD_DIR=build_jar

echo [1/5] Clean...
if exist "%OUT_DIR%" rmdir /s /q "%OUT_DIR%"
if exist "%BUILD_DIR%" rmdir /s /q "%BUILD_DIR%"
if exist "%JAR_NAME%" del "%JAR_NAME%"
mkdir "%OUT_DIR%"
mkdir "%BUILD_DIR%"

echo [2/5] Compiling...
dir /s /b "%JAVA_SRC%\*.java" > sources.txt
"%JAVA_BIN%\javac.exe" -encoding UTF-8 -cp "%LIB_DIR%\gson-2.10.1.jar" -d "%OUT_DIR%" @sources.txt
del sources.txt

if %ERRORLEVEL% NEQ 0 (
    echo COMPILE FAILED!
    pause
    exit /b 1
)
echo    OK - Compile done!

echo [3/5] Copying classes...
xcopy /s /q /y "%OUT_DIR%\*" "%BUILD_DIR%\" >nul

echo [4/5] Extracting Gson into build...
cd "%BUILD_DIR%"
"%JAVA_BIN%\jar.exe" xf "..\%LIB_DIR%\gson-2.10.1.jar"
if exist META-INF rmdir /s /q META-INF
cd ..

echo [5/5] Creating JAR...
echo Manifest-Version: 1.0> manifest.txt
echo Main-Class: %MAIN_CLASS%>> manifest.txt
echo.>> manifest.txt

"%JAVA_BIN%\jar.exe" cfm "%JAR_NAME%" manifest.txt -C "%BUILD_DIR%" .

if %ERRORLEVEL% NEQ 0 (
    echo JAR CREATION FAILED!
    pause
    exit /b 1
)

del manifest.txt
rmdir /s /q "%BUILD_DIR%"
rmdir /s /q "%OUT_DIR%"

echo.
echo ========================================
echo   DONE! Created: %JAR_NAME%
echo ========================================
echo.
echo   Run with: java -jar %JAR_NAME%
echo   Or double-click the JAR file.
echo.
pause
