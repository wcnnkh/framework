package run.soeasy.framework.json;

import java.io.IOException;

import run.soeasy.framework.io.Exportable;

/**
 * JSON元素的抽象根接口，定义所有JSON元素（对象、数组、基本类型、Null）的通用行为契约。
 * 
 * <h3>核心设计目标</h3>
 * <ul>
 * <li>统一JSON元素的类型判定与类型安全转换能力，避免强制类型转换的类型安全问题</li>
 * <li>整合{@link Exportable}接口实现JSON数据的流式导出，支持高效序列化</li>
 * <li>提供标准化的JSON字符串序列化与字符串转义工具方法，保证JSON语法合规性</li>
 * </ul>
 * 
 * <h3>元素类型体系</h3>
 * 所有实现类需归属以下四类之一，通过接口默认方法完成类型判定与转换：
 * <ul>
 * <li>{@link JsonObject}：JSON对象（键值对集合）</li>
 * <li>{@link JsonArray}：JSON数组（有序元素集合）</li>
 * <li>{@link JsonPrimitive}：JSON基本类型（字符串、数字、布尔值）</li>
 * <li>{@link JsonNull}：JSON空值（显式的null表示）</li>
 * </ul>
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
     * 判断当前元素是否为JSON对象类型（{@link JsonObject}）
     * 
     * <p>默认实现基于{@code instanceof}判定，无额外性能损耗，可直接用于条件分支判断</p>
     * 
     * @return {@code true} - 当前元素是{@link JsonObject}实例；{@code false} - 非JSON对象类型
     */
    default boolean isJsonObject() {
        return this instanceof JsonObject;
    }

    /**
     * 将当前元素安全转换为{@link JsonObject}类型
     * 
     * <p>转换前会通过{@link #isJsonObject()}校验类型，校验失败时抛出明确的类型不匹配异常，
     * 异常信息包含当前元素的字符串表示，便于定位类型错误</p>
     * 
     * @return 当前元素对应的{@link JsonObject}实例（非null）
     * @throws IllegalStateException 当当前元素不是JSON对象类型时抛出，包含具体的类型不匹配信息
     */
    default JsonObject getAsJsonObject() {
        if (isJsonObject()) {
            return (JsonObject) this;
        }
        throw new IllegalStateException("Not a JSON Object: " + this);
    }

    /**
     * 判断当前元素是否为JSON数组类型（{@link JsonArray}）
     * 
     * <p>默认实现基于{@code instanceof}判定，无额外性能损耗，可直接用于条件分支判断</p>
     * 
     * @return {@code true} - 当前元素是{@link JsonArray}实例；{@code false} - 非JSON数组类型
     */
    default boolean isJsonArray() {
        return this instanceof JsonArray;
    }

    /**
     * 将当前元素安全转换为{@link JsonArray}类型
     * 
     * <p>转换前会通过{@link #isJsonArray()}校验类型，校验失败时抛出明确的类型不匹配异常，
     * 异常信息包含当前元素的字符串表示，便于定位类型错误</p>
     * 
     * @return 当前元素对应的{@link JsonArray}实例（非null）
     * @throws IllegalStateException 当当前元素不是JSON数组类型时抛出，包含具体的类型不匹配信息
     */
    default JsonArray getAsJsonArray() {
        if (isJsonArray()) {
            return (JsonArray) this;
        }
        throw new IllegalStateException("Not a JSON Array: " + this);
    }

    /**
     * 判断当前元素是否为JSON空值类型（{@link JsonNull}）
     * 
     * <p>默认实现基于{@code instanceof}判定，无额外性能损耗，可直接用于条件分支判断</p>
     * 
     * @return {@code true} - 当前元素是{@link JsonNull}实例；{@code false} - 非JSON空值类型
     */
    default boolean isJsonNull() {
        return this instanceof JsonNull;
    }

    /**
     * 将当前元素安全转换为{@link JsonNull}类型
     * 
     * <p>转换前会通过{@link #isJsonNull()}校验类型，校验失败时抛出明确的类型不匹配异常，
     * 异常信息包含当前元素的字符串表示，便于定位类型错误</p>
     * 
     * @return 当前元素对应的{@link JsonNull}实例（非null）
     * @throws IllegalStateException 当当前元素不是JSON空值类型时抛出，包含具体的类型不匹配信息
     */
    default JsonNull getAsJsonNull() {
        if (isJsonNull()) {
            return (JsonNull) this;
        }
        throw new IllegalStateException("Not a JSON Null: " + this);
    }

    /**
     * 判断当前元素是否为JSON基本类型（{@link JsonPrimitive}）
     * 
     * <p>JSON基本类型包含字符串、整数、浮点数、布尔值四种基础类型，
     * 默认实现基于{@code instanceof}判定，无额外性能损耗</p>
     * 
     * @return {@code true} - 当前元素是{@link JsonPrimitive}实例；{@code false} - 非JSON基本类型
     */
    default boolean isJsonPrimitive() {
        return this instanceof JsonPrimitive;
    }

    /**
     * 将当前元素安全转换为{@link JsonPrimitive}类型
     * 
     * <p>转换前会通过{@link #isJsonPrimitive()}校验类型，校验失败时抛出明确的类型不匹配异常，
     * 异常信息包含当前元素的字符串表示，便于定位类型错误</p>
     * 
     * @return 当前元素对应的{@link JsonPrimitive}实例（非null）
     * @throws IllegalStateException 当当前元素不是JSON基本类型时抛出，包含具体的类型不匹配信息
     */
    default JsonPrimitive getAsJsonPrimitive() {
        if (isJsonPrimitive()) {
            return (JsonPrimitive) this;
        }
        throw new IllegalStateException("Not a JSON Primitive: " + this);
    }

    /**
     * 将当前JSON元素序列化为标准JSON格式字符串
     * 
     * <h4>实现说明</h4>
     * <ul>
     * <li>基于{@link Exportable#export(Appendable)}实现流式导出，保证序列化效率</li>
     * <li>使用{@link StringBuilder}作为内存缓冲区，理论上不会抛出{@link IOException}，
     * 若意外抛出则包装为{@link IllegalStateException}，避免上层处理不必要的受检异常</li>
     * <li>序列化结果严格遵循JSON语法规范，可直接用于JSON数据传输或存储</li>
     * </ul>
     * 
     * @return 标准化的JSON格式字符串（非null，空元素会返回对应空表示，如空对象"{}"、空数组"[]"）
     * @throws IllegalStateException 当序列化过程中发生IO异常时抛出（理论上不会触发），包含原始异常信息
     */
    default String toJsonString() {
        StringBuilder sb = new StringBuilder();
        try {
            export(sb);
        } catch (IOException e) {
            throw new IllegalStateException("export error", e);
        }
        return sb.toString();
    }
    
}