package enums;

public enum ExcelCellNumbers {
    C0(0), C1(1), C2(2), C3(3), C4(4), C5(5), C6(6), C7(7), C8(8);
    
    private int value;

    private ExcelCellNumbers(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }
}
