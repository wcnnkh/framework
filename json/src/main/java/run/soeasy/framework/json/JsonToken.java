package run.soeasy.framework.json;
/**
 * JSON Token 枚举（描述 JSON 语法的最小单元）
 */
public enum JsonToken {
    BEGIN_OBJECT,    // {
    END_OBJECT,      // }
    BEGIN_ARRAY,     // [
    END_ARRAY,       // ]
    NAME_SEPARATOR,  // :
    VALUE_SEPARATOR, // ,
    STRING,          // 字符串
    NUMBER,          // 数字
    BOOLEAN,         // true/false
    NULL,            // null
    EOF              // 流结束
}