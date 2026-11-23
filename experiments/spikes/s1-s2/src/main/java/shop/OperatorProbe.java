package shop;

class OperatorProbe {
    private int recorded;

    boolean evaluate(int left, int right, boolean enabled) {
        boolean greater = left > right;       // S2: so sánh
        int sum = left + right;               // S2: phép cộng
        boolean accepted = true;              // S2: hằng boolean gán cho biến
        if (greater && enabled) {             // S2: điều kiện ngắn mạch
            record(sum);                      // S2: lời gọi hàm void
        } else {
            accepted = false;                 // S2: gán lại biến cục bộ
        }
        return accepted;                      // S2: trả về giá trị boolean
    }

    private void record(int value) {
        recorded = value;                     // S2: gán giá trị cho trường
    }

    int recorded() {
        return recorded;
    }
}
