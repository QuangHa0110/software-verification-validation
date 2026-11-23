package shop;

public class Sample {
    private int count;
    private int[] buckets = {1, 2};

    public Sample(int count) {
        this.count = count;
        count = 1;
    }

    public boolean evaluate(boolean a, boolean b, int mode) {
        boolean flag = true;
        String noise = "a && b || true false";
        String block = """
                a && b true
                """;
        char ch = '&';
        // true && false; local = 9;
        /* false || true; local++; */
        int local = 0;
        local = 1;
        local += 2;
        local++;
        --local;
        count = 3;
        this.count = 4;
        count++;
        laterField += 5;
        buckets = new int[0];
        helper();
        if (a && b || flag) local = 5;
        else local--;
        for (int i = 0; i < 3; i++) {
            ++local;
            if (local > 10) break;
            if (local < 0) continue;
        }
        {
            int count = 0;
            count = 7;
        }
        count = 8;
        switch (mode) {
            case 1:
                local = 11;
                break;
            default:
                local = 12;
        }
        switch (mode) {
            case 1 -> local = 13;
            default -> local = 14;
        }
        local =
                1 + 2;
        if (local < 0) throw new IllegalStateException();
        return false;
    }

    private void helper() {
    }

    private int laterField;
}
