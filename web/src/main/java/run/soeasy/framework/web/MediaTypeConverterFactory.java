package run.soeasy.framework.web;

import org.springframework.http.MediaType;

import lombok.NonNull;
import run.soeasy.framework.core.convert.Converter;

/**
 * 媒体类型转换器工厂接口，用于根据媒体类型（MediaType）获取对应的转换器（Converter）。
 * 
 * <p>作为函数式接口，核心职责是建立媒体类型与数据转换器之间的映射关系，
 * 解耦媒体类型标识（如application/json）与具体的序列化/反序列化逻辑，
 * 支持根据HTTP请求/响应的Content-Type自动匹配合适的转换器。
 * 
 * <p>典型应用场景：在HTTP客户端中，根据响应的Content-Type选择对应的转换器解析响应体；
 * 或根据请求的Content-Type选择转换器序列化请求体。
 * 
 * @see MediaType
 * @see Converter
 * @see HttpRequestExecutor
 */
@FunctionalInterface
public interface MediaTypeConverterFactory {

    /**
     * 根据指定的媒体类型获取对应的转换器。
     * 
     * <p>实现类需根据媒体类型（如application/json、application/xml）返回匹配的转换器，
     * 用于该媒体类型的数据序列化（对象→字符串）或反序列化（字符串→对象）。
     * 
     * @param mediaType 媒体类型（如application/json），不可为null
     * @return 与媒体类型匹配的转换器；若未找到匹配的转换器，可能返回null（取决于实现类逻辑）
     */
    Converter getConverter(@NonNull MediaType mediaType);
}