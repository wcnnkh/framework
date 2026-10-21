package run.soeasy.framework.json;

/**
 * JSON Token 枚举（描述 JSON 语法的最小单元）
 */
public enum JsonToken {
    BEGIN_OBJECT(false),
    END_OBJECT(false),
    BEGIN_ARRAY(false),
    END_ARRAY(false),
    NAME_SEPARATOR(false),
    VALUE_SEPARATOR(false),
    STRING(true),
    NUMBER(true),
    BOOLEAN(true),
    NULL(true),
    EOF(false);

    private final boolean valueToken;

    JsonToken(boolean valueToken) {
        this.valueToken = valueToken;
    }

    /**
     * 是否为结构型 token（无字面值）。
     */
    public boolean isStructural() {
        return !valueToken;
    }

    /**
     * 是否为值型 token（有字面值）。
     */
    public boolean isValue() {
        return valueToken;
    }
}