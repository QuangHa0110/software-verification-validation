# Luật bắt buộc cho worker agent

Đọc hết file này trước khi làm. Vi phạm bất kỳ mục nào thì kết quả bị loại.

## 1. Git và phạm vi

- **Không** chạy `git commit`, `git push`, `git checkout`, `git switch`, `git branch`, `git reset`, `git stash`, `git merge`, `git rebase`, `git clean`, `git worktree`. Chỉ orchestrator được commit. Được phép `git status`, `git diff`.
- Chỉ tạo hoặc sửa các file nằm trong **Phạm vi** của brief. Cần sửa file ngoài phạm vi thì không sửa, ghi vào báo cáo.
- Không xóa file ngoài thư mục làm việc. Không sửa gì trong `<USER_HOME>` ngoài thư mục làm việc, local Maven repository và môi trường Python được chỉ định.

## 2. Mã hóa

- Mọi file văn bản là **UTF-8 không BOM**. BOM làm `javac` báo lỗi.
- Ưu tiên công cụ sửa file của agent thay vì lệnh shell.
- Trên Windows PowerShell 5.1: đọc file bằng `Get-Content -Raw -Encoding UTF8`; **không** ghi file bằng `>` hay `Out-File` (sinh UTF-16).
- Code Python đọc/ghi file phải ghi rõ `encoding="utf-8"`.

## 3. Môi trường

| Thành phần | Dùng |
|---|---|
| Python | `python` (không dùng Python khác, không tạo venv) |
| Java | JDK 17 có sẵn (`java`, `javac` trên PATH), `maven.compiler.release=17` |
| Maven | `mvn -B` (3.9.15). Local repository đã đặt qua biến `MAVEN_OPTS`, không sửa |

## 4. Trung thực

- Không bịa kết quả, số liệu hay output. Không sửa tiêu chí kiểm tra để cho qua.
- Không sửa tay test do LLM sinh.
- Không đưa khóa API vào mã, log hay file.
- Khi đặc tả mơ hồ: chọn cách hiểu hợp lý nhất, ghi câu hỏi và cách đã chọn vào `docs/spec/open-questions.md` (nếu file đó nằm trong phạm vi), và nêu trong báo cáo.
- Tuân thủ mục 9 "Quy tắc cho AI" của `docs/thiet-ke-ky-thuat.md`.

## 5. Báo cáo cuối (tin nhắn cuối cùng của bạn)

Viết bằng tiếng Việt, đúng các mục sau:

```
## File đã tạo/sửa
- <đường dẫn>: <một dòng mô tả>

## Lệnh đã chạy và kết quả thực tế
- `<lệnh>` → exit <mã>; <trích output quan trọng, nguyên văn>

## Đã viết nhưng chưa chạy
- ...

## Giả định
- ...

## Câu hỏi mở / việc ngoài phạm vi
- ...
```

Mỗi khẳng định "chạy được" phải kèm lệnh và output thực tế ở mục 2.
