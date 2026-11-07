package HRandDelivery.DomainLayer.HR.enums;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum ShiftType {
    MORNING("M"),
    EVENING("E");

    private final String code;

    ShiftType(String code) {
        this.code = code;
    }

    private static final Map<String, ShiftType> codeMap = new HashMap<>();

    static {
        for (ShiftType type : ShiftType.values()) {
            codeMap.put(type.code, type);
        }
    }

    public static ShiftType fromCode(String code) {
        return Arrays.stream(values())
                .filter(t -> t.code.equalsIgnoreCase(code) || t.name().equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid time slot: " + code));
    }

}