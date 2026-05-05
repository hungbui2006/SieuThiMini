@echo off
chcp 65001 >nul
title Hệ Thống Quản Lý Siêu Thị Mini - PHENIKAA

echo ========================================
echo     SIÊU THỊ MINI - PHENIKAA UNIVERSITY
echo ========================================
echo.

set JAR_NAME=SieuThiMini.jar

if exist "%JAR_NAME%" (
    echo [OK] Đang khởi chạy ứng dụng...
    start javaw -jar "%JAR_NAME%"
    exit
) else (
    echo [LOI] Không tìm thấy file %JAR_NAME%.
    echo Vui lòng chạy file build-jar.bat trước để đóng gói ứng dụng.
    echo.
    pause
)
