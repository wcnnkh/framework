package run.soeasy.framework.json;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.convert.TypeDescriptor;

/**
 * 可转换的JSON元素包装类，实现{@link JsonElement}接口，用于适配通过{@link JsonConverter}转换的任意JSON数据源（如原始字符串、集合、映射等），
 * 动态将外部JSON对象转换为标准{@link JsonElement}类型（数组、对象、基本类型等），支持延迟初始化以优化性能。
 * 
 * <p>该类通过{@link JsonConverter}实现对任意对象的JSON类型判断与转换，缓存转换后的{@link JsonArray}或{@link JsonObject}，
 * 避免重复转换操作，适用于需要兼容多种JSON表示形式的场景（如第三方JSON库对象、JSON字符串等）。
 * 
 * @author soeasy.run
 * @see JsonElement
 * @see JsonConverter
 */
@RequiredArgsConstructor
@EqualsAndHashCode(of = "json")
final class ConvertibleJsonElement implements JsonElement {

    /**
     * JSON转换器，负责将原始对象转换为标准JSON元素或字符串
     */
    @NonNull
    private final JsonConverter jsonConverter;

    /**
     * 原始JSON对象（可为JSON字符串、集合、映射、基本类型等）
     */
    private final Object json;

    /**
     * 缓存的JSON数组（延迟初始化，仅当判定为数组时有效）
     */
    private JsonElement jsonArray;

    /**
     * 缓存的JSON对象（延迟初始化，仅当判定为对象时有效）
     */
    private JsonElement jsonObject;

    /**
     * 通过JSON转换器将原始对象导出为JSON格式字符串
     * 
     * @param target 接收导出数据的Appendable（如StringBuilder、Writer等）
     * @throws IOException 当转换器写入Appendable失败时抛出
     */
    @Override
    public void export(Appendable target) throws IOException {
        jsonConverter.to(json, TypeDescriptor.forObject(json), target);
    }

    /**
     * 获取对应的JSON数组（若当前元素为数组）
     * 
     * @return 转换后的{@link JsonArray}实例
     * @throws IllegalStateException 当当前元素不是数组时抛出
     */
    @Override
    public JsonArray getAsJsonArray() {
        if (isJsonArray()) {
            return this.jsonArray.getAsJsonArray();
        }
        return JsonElement.super.getAsJsonArray();
    }

    /**
     * 获取对应的JSON null（若原始对象为null）
     * 
     * @return {@link JsonNull#INSTANCE}（当原始对象为null时）
     * @throws IllegalStateException 当原始对象不为null时抛出
     */
    @Override
    public JsonNull getAsJsonNull() {
        if (json == null) {
            return JsonNull.INSTANCE;
        }
        return JsonElement.super.getAsJsonNull();
    }

    /**
     * 获取对应的JSON对象（若当前元素为对象）
     * 
     * @return 转换后的{@link JsonObject}实例
     * @throws IllegalStateException 当当前元素不是对象时抛出
     */
    @Override
    public JsonObject getAsJsonObject() {
        if (isJsonObject()) {
            return jsonObject.getAsJsonObject();
        }
        return JsonElement.super.getAsJsonObject();
    }

    /**
     * 获取对应的JSON基本类型（默认将原始对象包装为{@link JsonPrimitive}）
     * 
     * @return 包装原始对象的{@link JsonPrimitive}实例
     */
    @Override
    public JsonPrimitive getAsJsonPrimitive() {
        return new JsonPrimitive(json);
    }

    /**
     * 判断当前元素是否为JSON数组
     * 
     * <p>判断逻辑：
     * 1. 若原始对象为null或已判定为对象，则返回false；
     * 2. 延迟初始化jsonArray：通过转换器将原始对象转为字符串，检查是否以"["开头且以"]"结尾；
     * 3. 若符合数组格式，将原始对象转为List，再转换为{@link JsonArray}并缓存；
     * 4. 最终通过缓存的jsonArray判断是否为数组。
     * 
     * @return 是JSON数组则返回true，否则返回false
     */
    @SuppressWarnings("unchecked")
    @Override
    public boolean isJsonArray() {
        // 若原始对象为null或已确定为对象，则不是数组
        if (json == null || (jsonObject != null && jsonObject.isJsonObject())) {
            return false;
        }

        // 延迟初始化jsonArray
        if (jsonArray == null) {
            // 转换原始对象为字符串并校验格式
            String content = jsonConverter.convert(json, String.class);
            content = content.trim();
            if ((content.startsWith("[") && content.endsWith("]")) 
                    || (content.startsWith("\"[") && content.endsWith("]\""))) {
                // 转换为List并构建JsonArray
                List<Object> collection = (List<Object>) jsonConverter.convert(
                        json, TypeDescriptor.collection(ArrayList.class, Object.class));
                JsonArray jsonArray = new JsonArray();
                collection.forEach(value -> jsonArray.add(jsonConverter.toJsonElement(value)));
                this.jsonArray = jsonArray;
            }
        }

        // 若仍为null，标记为JsonNull（非数组）
        if (jsonArray == null) {
            jsonArray = JsonNull.INSTANCE;
        }
        return jsonArray.isJsonArray();
    }

    /**
     * 判断当前元素是否为JSON null（原始对象为null）
     * 
     * @return 原始对象为null则返回true，否则返回false
     */
    @Override
    public boolean isJsonNull() {
        return json == null;
    }

    /**
     * 判断当前元素是否为JSON对象
     * 
     * <p>判断逻辑：
     * 1. 若原始对象为null或已判定为数组，则返回false；
     * 2. 延迟初始化jsonObject：通过转换器将原始对象转为字符串，检查是否以"{"开头且以"}"结尾；
     * 3. 若符合对象格式，将原始对象转为Map，再转换为{@link JsonObject}并缓存；
     * 4. 最终通过缓存的jsonObject判断是否为对象。
     * 
     * @return 是JSON对象则返回true，否则返回false
     */
    @SuppressWarnings("unchecked")
    @Override
    public boolean isJsonObject() {
        // 若原始对象为null或已确定为数组，则不是对象
        if (json == null || (jsonArray != null && jsonArray.isJsonArray())) {
            return false;
        }

        // 延迟初始化jsonObject
        if (jsonObject == null) {
            // 转换原始对象为字符串并校验格式
            String content = jsonConverter.convert(json, String.class);
            content = content.trim();
            if ((content.startsWith("{") && content.endsWith("}"))
                    || (content.startsWith("\"{") && content.endsWith("}\""))) {
                // 转换为Map并构建JsonObject
                Map<String, Object> map = (Map<String, Object>) jsonConverter.convert(
                        json, TypeDescriptor.map(LinkedHashMap.class, String.class, Object.class));
                JsonObject jsonObject = new JsonObject();
                map.forEach((key, value) -> jsonObject.put(key, jsonConverter.toJsonElement(value)));
                this.jsonObject = jsonObject;
            }
        }

        // 若仍为null，标记为JsonNull（非对象）
        if (jsonObject == null) {
            jsonObject = JsonNull.INSTANCE;
        }
        return jsonObject.isJsonObject();
    }

    /**
     * 判断当前元素是否为JSON基本类型（非数组、非对象、非null）
     * 
     * @return 不是数组、对象或null则返回true，否则返回false
     */
    @Override
    public boolean isJsonPrimitive() {
        return !(isJsonArray() || isJsonObject() || isJsonNull());
    }

    /**
     * 返回当前元素的JSON字符串表示
     * 
     * @return 序列化后的JSON字符串
     */
    @Override
    public String toString() {
        return toJsonString();
    }
}