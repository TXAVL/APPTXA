# Hướng Dẫn Setup AI Generation

## Cách Lấy Gemini API Key (Miễn Phí)

1. Truy cập: https://makersuite.google.com/app/apikey
2. Đăng nhập bằng Google account
3. Click "Create API Key"
4. Copy API key

## Cách Thêm API Key Vào App

### Cách 1: Code trực tiếp (Nhanh)
Mở file: `app/src/main/java/com/txa/flashcards/utils/ApiKeyManager.kt`

Thay dòng:
```kotlin
private const val DEFAULT_API_KEY = "AIzaSyDummyKeyReplaceWithYourOwn"
```

Thành:
```kotlin
private const val DEFAULT_API_KEY = "YOUR_API_KEY_HERE"
```

### Cách 2: Runtime (An toàn hơn)
App sẽ tự động lưu API key vào SharedPreferences khi bạn nhập lần đầu.

## Tính Năng

- ✅ **Auto-generate**: Tự động tạo 30 từ vựng khi app mở lần đầu
- ✅ **Generate More**: Click FAB để tạo thêm 20 từ mới
- ✅ **AI-Powered**: Sử dụng Gemini AI để tạo từ vựng đa dạng
- ✅ **Smart Parsing**: Tự động parse JSON response từ AI

## Lưu ý

- API key miễn phí có giới hạn requests/ngày
- Nếu hết quota, app sẽ hiển thị lỗi
- Có thể tạo nhiều API keys để dùng luân phiên

