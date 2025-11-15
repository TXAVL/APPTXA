@echo off
REM ============================================
REM TXA Hub Auto Build & Push Script (Windows)
REM Supports debug + release, auto auth GitHub
REM ============================================

setlocal enabledelayedexpansion

REM --- Config ---
set RELEASE_DIR=releases
REM Build standard APK (no flavors)
set DEBUG_APK_SRC=app\build\outputs\apk\debug\app-debug.apk
set RELEASE_APK_SRC=app\build\outputs\apk\release\app-release.apk
set KEYSTORE=%CD%\txa-release-key.keystore
set ALIAS=txa_release
set STOREPASS=
set KEYPASS=

REM --- Parse arguments ---
set MODE=debug
if "%1"=="--release" set MODE=release

REM --- Stop old Gradle daemons to free memory ---
echo ?? Stopping old Gradle daemons...
call gradlew.bat --stop >nul 2>&1

REM --- Build ---
if "%MODE%"=="release" (
    echo ?? Building TXA Hub Mobile RELEASE APK...
    
    REM Ki?m tra keystore, n?u ch?a c? th? t?o m?i
    if not exist "%KEYSTORE%" (
        echo ?? Keystore not found. Creating new keystore...
        if "!STOREPASS!"=="" (
            set /p STOREPASS="?? Enter keystore password (will be used for both store and key): "
            set KEYPASS=!STOREPASS!
        )
        echo ?? Creating keystore with alias: %ALIAS%
        keytool -genkey -v -keystore "%KEYSTORE%" -alias %ALIAS% -keyalg RSA -keysize 2048 -validity 10000 -storepass "!STOREPASS!" -keypass "!KEYPASS!" -dname "CN=TXA Hub, OU=Development, O=TXA Hub, L=Ho Chi Minh, ST=Ho Chi Minh, C=VN"
        if errorlevel 1 (
            echo ? Failed to create keystore!
            exit /b 1
        )
        echo ? Keystore created successfully!
    ) else (
        echo ? Keystore found: %KEYSTORE%
        if "!STOREPASS!"=="" (
            set /p STOREPASS="?? Enter keystore password: "
            set /p KEYPASS="?? Enter key password (if same, press Enter): "
            if "!KEYPASS!"=="" set KEYPASS=!STOREPASS!
        )
    )
    call gradlew.bat assembleRelease -Pandroid.injected.signing.store.file="%KEYSTORE%" ^
                                     -Pandroid.injected.signing.store.password="!STOREPASS!" ^
                                     -Pandroid.injected.signing.key.alias="%ALIAS%" ^
                                     -Pandroid.injected.signing.key.password="!KEYPASS!"
) else (
    echo ?? Building TXA Hub Mobile DEBUG APK...
    call gradlew.bat assembleDebug
)

if errorlevel 1 (
    echo ? Build failed!
    exit /b 1
)

REM --- Select built file ---
if "%MODE%"=="release" (
    set APK_SRC=!RELEASE_APK_SRC!
) else (
    set APK_SRC=!DEBUG_APK_SRC!
)

REM --- Check APK existence ---
if not exist "!APK_SRC!" (
    echo ? APK not found: !APK_SRC!
    exit /b 1
)

REM --- Copy to releases ---
if not exist "%RELEASE_DIR%" mkdir "%RELEASE_DIR%"
REM T?o t?n file v?i timestamp (YYYYMMDD_HHMMSS)
for /f "delims=" %%i in ('powershell -NoProfile -Command "Get-Date -Format \"yyyyMMdd_HHmmss\""') do set DATETIME=%%i
if "!DATETIME!"=="" (
    echo ?? Failed to get timestamp, using fallback...
    for /f "tokens=2 delims==" %%I in ('wmic os get localdatetime /value') do set datetime=%%I
    set datetime=!datetime:~0,8!_!datetime:~8,6!
    set DATETIME=!datetime!
)
set APK_DEST_NAME=TXAHUB_APP_!DATETIME!.apk
copy "!APK_SRC!" "%RELEASE_DIR%\!APK_DEST_NAME!" >nul

REM --- Git setup (check and configure remote if needed) ---
echo ?? Checking Git remote configuration...
git remote get-url origin >nul 2>&1
if errorlevel 1 (
    echo ?? Git remote not found. Setting up remote...
    git remote add origin https://github.com/TXAVL/APPTXA.git
    echo ? Remote added: https://github.com/TXAVL/APPTXA.git
) else (
    REM Ki?m tra xem remote c? ??ng repo kh?ng
    for /f "tokens=*" %%i in ('git remote get-url origin') do set CURRENT_REMOTE=%%i
    echo Current remote: !CURRENT_REMOTE!
    echo !CURRENT_REMOTE! | findstr /C:"APPTXA" >nul
    if errorlevel 1 (
        echo ?? Updating remote to new repository...
        git remote set-url origin https://github.com/TXAVL/APPTXA.git
        echo ? Remote updated: https://github.com/TXAVL/APPTXA.git
    ) else (
        echo ? Remote is already configured correctly.
    )
)

REM --- Ensure main branch ---
git branch -M main >nul 2>&1

REM --- Git commit ---
echo ?? Committing %MODE% APK to Git...
REM Add all files (gitignore will exclude unnecessary .md files)
REM Only README.md and BUILD_ERRORS_REPORT.md will be committed
git add -A
REM Commit
git commit -m "Auto-build: Add !APK_DEST_NAME!" 2>nul || echo No changes to commit

REM --- Git push ---
echo ?? Pushing to GitHub...
git push -u origin main
if errorlevel 1 (
    echo ?? Push failed. Attempting re-authorization...
    set /p GH_USER="?? Enter your GitHub username: "
    set /p GH_TOKEN="?? Enter your GitHub personal access token: "
    set NEW_URL=https://!GH_USER!:!GH_TOKEN!@github.com/TXAVL/APPTXA.git
    git remote set-url origin "!NEW_URL!"
    echo ?? Retrying push...
    git push -u origin main
    if errorlevel 1 (
        echo ? Push failed again. Check token or network.
        exit /b 1
    ) else (
        echo ? Push successful after re-auth.
    )
) else (
    echo ? Push successful.
)

REM --- Display success message with full GitHub link ---
echo.
echo ========================================
echo ?? Build and Push Successful!
echo ========================================
echo ?? APK File: releases\!APK_DEST_NAME!
echo.
echo ?? GitHub Repository: https://github.com/TXAVL/APPTXA
echo ?? Download Link: https://github.com/TXAVL/APPTXA/raw/main/releases/!APK_DEST_NAME!
echo ?? Releases Folder: https://github.com/TXAVL/APPTXA/tree/main/releases
echo.
echo ========================================

endlocal

