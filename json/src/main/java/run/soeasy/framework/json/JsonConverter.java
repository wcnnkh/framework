package run.soeasy.framework.json;

import run.soeasy.framework.core.convert.strings.StringConverter;

/**
 * JSON转换器接口，继承自{@link StringConverter<Object>}，负责对象与JSON格式的相互转换，
 * 扩展了字符串转换功能，提供将任意对象转换为{@link JsonElement}的核心方法，是JSON序列化与反序列化的基础接口。
 * 
 * <p>该接口默认实现了{@link #toJsonElement(Object)}方法，通过判断对象类型直接返回已有{@link JsonElement}，
 * 或包装为{@link ConvertibleJsonElement}实现动态转换，适用于各类对象到JSON元素的适配场景。
 * 
 * @author soeasy.run
 * @see StringConverter
 * @see JsonElement
 * @see ConvertibleJsonElement
 */
public interface JsonConverter extends StringConverter<Object> {

    /**
     * 将对象转换为{@link JsonElement}实例
     * 
     * <p>转换规则：
     * <ul>
     * <li>若输入对象已是{@link JsonElement}，则直接返回该对象；
     * <li>否则，通过当前转换器创建{@link ConvertibleJsonElement}包装输入对象，实现延迟转换。
     * </ul>
     * 
     * <p><strong>注意</strong>：默认实现通过{@link ConvertibleJsonElement}进行转换，性能相对较低，
     * 对于高频转换场景，建议子类重写此方法以提供更高效的类型判断与转换逻辑。
     * 
     * @param json 待转换的对象（可为任意类型，包括JSON字符串、集合、JavaBean等）
     * @return 转换后的{@link JsonElement}实例（非null）
     */
    default JsonElement toJsonElement(Object json) {
        if (json instanceof JsonElement) {
            return (JsonElement) json;
        }
        return new ConvertibleJsonElement(this, json);
    }
}