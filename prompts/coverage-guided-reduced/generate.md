# Bổ sung test JUnit 5 cho các dòng và nhánh còn thiếu của package `shop`

Đọc phản hồi JaCoCo, chọn đầu vào đi qua dòng hoặc vế điều kiện còn thiếu và kiểm tra hành vi quan sát được. Tính kết quả kỳ vọng theo đặc tả và hợp đồng API. Chỉ kiểm tra kiểu ngoại lệ. Dùng ngày giờ cố định. Tránh lặp lại các trường hợp đã có.

Trả mỗi test trong một khối ```java riêng chứa đúng một phương thức tự chứa có `@Test` hoặc `@ParameterizedTest` với nguồn tham số nội tuyến. Bộ lọc tự bọc lớp và thêm import JUnit Jupiter, Assertions, params, java.time và java.util. Bỏ khai báo package, lớp bao ngoài, trường và hàm phụ. Chỉ dùng Java 17 và JUnit 5. Trả lời trực tiếp từ ngữ cảnh này, không dùng công cụ.

## Hợp đồng API và đặc tả nghiệp vụ

{{SPEC}}

## Tên phương thức test hiện có

{{TEST_NAMES}}

## Dòng và nhánh cần phủ theo phương thức

{{FEEDBACK}}
