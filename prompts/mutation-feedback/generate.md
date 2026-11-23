# Bổ sung test JUnit 5 để phân biệt các mutant còn sống của package `shop`

Với từng mutant có thể phân biệt bằng API, chọn đầu vào và assertion làm lộ thay đổi ở giá trị trả về, trạng thái hoặc kiểu ngoại lệ. Lấy kỳ vọng từ đặc tả nghiệp vụ và hợp đồng API. Dùng ngày giờ cố định. Tránh lặp lại các trường hợp đã có.

Trả mỗi test trong một khối ```java riêng chứa đúng một phương thức tự chứa có `@Test` hoặc `@ParameterizedTest` với nguồn tham số nội tuyến. Bộ lọc tự bọc lớp và thêm import JUnit Jupiter, Assertions, params, java.time và java.util. Bỏ khai báo package, lớp bao ngoài, trường và hàm phụ. Chỉ dùng Java 17 và JUnit 5. Trả lời trực tiếp từ ngữ cảnh này, không dùng công cụ.

## Hợp đồng API và đặc tả nghiệp vụ

{{SPEC}}

## Mã nguồn đối tượng

{{SOURCE}}

## Tên phương thức test hiện có

{{TEST_NAMES}}

## Mutant cần diệt theo phương thức

{{FEEDBACK}}
