package run.soeasy.framework.messaging.convert.support;

import run.soeasy.framework.core.streaming.Streamable;
import run.soeasy.framework.messaging.MediaType;

/**
 * JSON消息转换器，继承自{@link TextMessageConverter}，专注于JSON格式消息与Java对象的转换，
 * 支持标准JSON媒体类型及扩展JSON类型（如application/vnd.example+json），是处理JSON数据的核心转换器。
 * 
 * <p>核心特性：
 * - 基于文本消息转换器实现，继承字符集处理逻辑（默认UTF-8）；
 * - 支持标准JSON类型（application/json）和所有扩展JSON类型（application/*+json）；
 * - 适用于RESTful API中JSON请求/响应的序列化与反序列化（如将Java对象转为JSON字符串，或反之）。
 * 
 * @author soeasy.run
 * @see TextMessageConverter
 * @see MediaType#APPLICATION_JSON
 */
public class JsonMessageConverter extends TextMessageConverter {

    /**
     * 匹配所有扩展JSON媒体类型的通用类型（application/*+json），
     * 用于支持自定义JSON子类型（如application/user+json、application/order+json等）。
     */
    public static final MediaType JSON_ALL = new MediaType("application", "*+json");

    /**
     * 初始化JSON消息转换器，注册支持的JSON媒体类型
     * 
     * <p>操作说明：
     * 1. 清除默认媒体类型注册表，确保仅包含JSON相关类型；
     * 2. 添加标准JSON类型（{@link MediaType#APPLICATION_JSON}）和扩展JSON类型（{@link #JSON_ALL}），
     *    覆盖常见JSON使用场景。
     */
    public JsonMessageConverter() {
        getMediaTypeRegistry().reset();
        getMediaTypeRegistry().registerAll(Streamable.array(
            MediaType.APPLICATION_JSON,  // 标准JSON类型（application/json）
            JSON_ALL                     // 扩展JSON类型（application/*+json）
        ));
    }
}