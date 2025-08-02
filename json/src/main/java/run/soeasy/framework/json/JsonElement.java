package run.soeasy.framework.json;

import java.io.IOException;

import run.soeasy.framework.io.Exportable;

/**
 * JSON元素的基础接口，所有JSON相关元素（对象、数组、基本类型、null）都应实现此接口，
 * 定义了判断元素类型、转换为特定类型以及JSON序列化的核心方法，继承自{@link Exportable}支持数据导出。
 * 
 * <p>该接口提供了类型判断的默认方法（如{@link #isJsonObject()}）和类型转换方法（如{@link #getAsJsonObject()}），
 * 以及JSON字符串序列化（{@link #toJsonString()}）和字符串转义（{@link #escaping(String)}）功能，
 * 为所有JSON元素提供统一的操作规范。
 * 
 * @author soeasy.run
 * @see JsonObject
 * @see JsonArray
 * @see JsonPrimitive
 * @see JsonNull
 * @see Exportable
 */
public interface JsonElement extends Exportable {

    /**
     * 判断当前元素是否为JSON对象（{@link JsonObject}）
     * 
     * @return 是JSON对象则返回true，否则返回false
     */
    default boolean isJsonObject() {
        return this instanceof JsonObject;
    }

    /**
     * 将当前元素转换为{@link JsonObject}
     * 
     * @return 当前元素对应的{@link JsonObject}实例
     * @throws IllegalStateException 当当前元素不是JSON对象时抛出
     */
    default JsonObject getAsJsonObject() {
        if (isJsonObject()) {
            return (JsonObject) this;
        }
        throw new IllegalStateException("Not a JSON Object: " + this);
    }

    /**
     * 判断当前元素是否为JSON数组（{@link JsonArray}）
     * 
     * @return 是JSON数组则返回true，否则返回false
     */
    default boolean isJsonArray() {
        return this instanceof JsonArray;
    }

    /**
     * 将当前元素转换为{@link JsonArray}
     * 
     * @return 当前元素对应的{@link JsonArray}实例
     * @throws IllegalStateException 当当前元素不是JSON数组时抛出
     */
    default JsonArray getAsJsonArray() {
        if (isJsonArray()) {
            return (JsonArray) this;
        }
        throw new IllegalStateException("Not a JSON Array: " + this);
    }

    /**
     * 判断当前元素是否为JSON null（{@link JsonNull}）
     * 
     * @return 是JSON null则返回true，否则返回false
     */
    default boolean isJsonNull() {
        return this instanceof JsonNull;
    }

    /**
     * 将当前元素转换为{@link JsonNull}
     * 
     * @return 当前元素对应的{@link JsonNull}实例
     * @throws IllegalStateException 当当前元素不是JSON null时抛出
     */
    default JsonNull getAsJsonNull() {
        if (isJsonNull()) {
            return (JsonNull) this;
        }
        throw new IllegalStateException("Not a JSON Null: " + this);
    }

    /**
     * 判断当前元素是否为JSON基本类型（{@link JsonPrimitive}，如字符串、数字、布尔值）
     * 
     * @return 是JSON基本类型则返回true，否则返回false
     */
    default boolean isJsonPrimitive() {
        return this instanceof JsonPrimitive;
    }

    /**
     * 将当前元素转换为{@link JsonPrimitive}
     * 
     * @return 当前元素对应的{@link JsonPrimitive}实例
     * @throws IllegalStateException 当当前元素不是JSON基本类型时抛出
     */
    default JsonPrimitive getAsJsonPrimitive() {
        if (isJsonPrimitive()) {
            return (JsonPrimitive) this;
        }
        throw new IllegalStateException("Not a JSON Primitive: " + this);
    }

    /**
     * 将当前JSON元素序列化为JSON格式字符串
     * 
     * <p>通过{@link Exportable#export(Appendable)}方法导出数据到字符串构建器，
     * 忽略序列化过程中可能出现的{@link IOException}（因操作内存缓冲区，通常不会发生）。
     * 
     * @return 序列化后的JSON字符串
     */
    default String toJsonString() {
        StringBuilder sb = new StringBuilder();
        try {
            export(sb);
        } catch (IOException e) {
            // 操作内存缓冲区时异常概率极低，此处忽略
        }
        return sb.toString();
    }

    /**
     * 对JSON字符串进行转义处理，确保符合JSON语法规范
     * 
     * <p>当前实现转义双引号（"）和反斜杠（\），可根据需要扩展以支持更多控制字符（如换行符、制表符等）。
     * 
     * @param value 待转义的字符串
     * @return 转义后的字符串
     */
    public static String escaping(String value) {
        int size = value.length();
        // 预分配1.25倍长度的缓冲区，减少扩容次数
        StringBuilder sb = new StringBuilder(Math.toIntExact(Math.round(size * 1.25)));
        for (int i = 0; i < size; i++) {
            char c = value.charAt(i);
            if (c == '"') {
                sb.append("\\\""); // 转义双引号
            } else if (c == '\\') {
                sb.append("\\\\"); // 转义反斜杠
            } else {
                sb.append(c); // 其他字符直接追加
            }
        }
        return sb.toString();
    }
}