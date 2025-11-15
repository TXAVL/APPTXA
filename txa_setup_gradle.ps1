# ============================================
# TXA Gradle Wrapper Setup Script (PowerShell)
# Tự động cài đặt Gradle Wrapper nếu thiếu
# ============================================

Write-Host "🔍 Checking Gradle Wrapper..." -ForegroundColor Cyan

$gradleWrapperJar = "gradle\wrapper\gradle-wrapper.jar"
$gradleWrapperProperties = "gradle\wrapper\gradle-wrapper.properties"

# Kiểm tra xem Gradle Wrapper đã tồn tại chưa
if (Test-Path $gradleWrapperJar) {
    Write-Host "✅ Gradle Wrapper already exists." -ForegroundColor Green
    exit 0
}

Write-Host "📦 Gradle Wrapper not found. Installing..." -ForegroundColor Yellow

# Tạo thư mục gradle/wrapper nếu chưa có
$wrapperDir = "gradle\wrapper"
if (-not (Test-Path $wrapperDir)) {
    New-Item -ItemType Directory -Path $wrapperDir -Force | Out-Null
    Write-Host "📁 Created directory: $wrapperDir" -ForegroundColor Green
}

# Kiểm tra xem có file gradle-wrapper.properties chưa
if (-not (Test-Path $gradleWrapperProperties)) {
    Write-Host "📝 Creating gradle-wrapper.properties..." -ForegroundColor Yellow
    $propertiesContent = @"
distributionBase=GRADLE_USER_HOME
distributionPath=wrapper/dists
distributionUrl=https\://services.gradle.org/distributions/gradle-8.0-bin.zip
networkTimeout=10000
validateDistributionUrl=true
zipStoreBase=GRADLE_USER_HOME
zipStorePath=wrapper/dists
"@
    Set-Content -Path $gradleWrapperProperties -Value $propertiesContent -Encoding UTF8
    Write-Host "✅ Created gradle-wrapper.properties" -ForegroundColor Green
}

# Download Gradle Wrapper JAR
Write-Host "⬇️ Downloading Gradle Wrapper JAR..." -ForegroundColor Yellow
$wrapperJarUrl = "https://raw.githubusercontent.com/gradle/gradle/v8.0.0/gradle/wrapper/gradle-wrapper.jar"
$wrapperJarPath = $gradleWrapperJar

try {
    # Tạo thư mục nếu chưa có
    $jarDir = Split-Path -Parent $wrapperJarPath
    if (-not (Test-Path $jarDir)) {
        New-Item -ItemType Directory -Path $jarDir -Force | Out-Null
    }
    
    # Download file
    Invoke-WebRequest -Uri $wrapperJarUrl -OutFile $wrapperJarPath -UseBasicParsing
    Write-Host "✅ Gradle Wrapper JAR downloaded successfully!" -ForegroundColor Green
} catch {
    Write-Host "❌ Failed to download Gradle Wrapper JAR: $_" -ForegroundColor Red
    Write-Host "💡 Alternative: Run 'gradle wrapper' command if you have Gradle installed." -ForegroundColor Yellow
    exit 1
}

# Kiểm tra lại
if (Test-Path $gradleWrapperJar) {
    Write-Host "✅ Gradle Wrapper setup completed!" -ForegroundColor Green
    Write-Host "📦 You can now run: .\txa_build_push.bat" -ForegroundColor Cyan
    exit 0
} else {
    Write-Host "❌ Gradle Wrapper setup failed!" -ForegroundColor Red
    exit 1
}

