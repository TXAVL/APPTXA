# Quy Tắc Tăng Phiên Bản - TXA Hub App

## Format Version

Format: `MAJOR.MINOR.PATCH_SUFFIX`

Ví dụ:
- `1.0.0_txa` - Version đầu tiên
- `1.0.1_txa` - Bug fix
- `1.1.0_txa` - Cập nhật tính năng
- `2.0.0_txa` - Tính năng mới lớn

## Quy Tắc Tăng Version

### 1. Fix Bug / Lỗi (PATCH)
- **Version Name**: Tăng PATCH (1.0.0 → 1.0.1)
- **Version Code**: +1 (173 → 174)
- **Khi nào dùng**: Sửa lỗi nhỏ, bug fix, hotfix

### 2. Cập Nhật Tính Năng (MINOR)
- **Version Name**: Tăng MINOR, reset PATCH (1.0.0 → 1.1.0)
- **Version Code**: +10 (173 → 183)
- **Khi nào dùng**: Thêm tính năng mới nhỏ, cải thiện tính năng hiện có

### 3. Tính Năng Mới Lớn (MAJOR)
- **Version Name**: Tăng MAJOR, reset MINOR và PATCH (1.0.0 → 2.0.0)
- **Version Code**: +100 (173 → 273)
- **Khi nào dùng**: Tính năng lớn, thay đổi kiến trúc, breaking changes

## Ví Dụ

| Loại Thay Đổi | Version Cũ | Version Mới | Code Cũ | Code Mới |
|--------------|------------|-------------|---------|----------|
| Bug fix | 2.3.0_txa | 2.3.1_txa | 173 | 174 |
| Update feature | 2.3.0_txa | 2.4.0_txa | 173 | 183 |
| Major feature | 2.3.0_txa | 3.0.0_txa | 173 | 273 |

## Lưu ý

- Version Code luôn tăng, không bao giờ giảm
- Version Name phải tuân theo format MAJOR.MINOR.PATCH_SUFFIX
- Suffix `_txa` luôn được giữ nguyên

