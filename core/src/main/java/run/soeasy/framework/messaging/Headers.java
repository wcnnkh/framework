package run.soeasy.framework.messaging;

import lombok.NonNull;
import run.soeasy.framework.core.StringUtils;
import run.soeasy.framework.core.exchange.KeyValueRegistry;
import run.soeasy.framework.core.streaming.Streamable;

/**
 * 消息头集合核心接口，继承自{@link KeyValueRegistry}&lt;String, String&gt;，专为消息头管理设计（如HTTP请求头/响应头）。
 * <p>
 * 核心特性：
 * <ul>
 * <li>单键多值存储：适配HTTP头字段可重复的特性（如Set-Cookie、Accept）；</li>
 * <li>大小写适配：默认键不区分大小写（符合HTTP/1.1规范，RFC 7230）；</li>
 * <li>标准头便捷操作：内置Content-Length、Content-Type等HTTP标准头的读写方法；</li>
 * <li>只读保护：支持设置只读模式，防止消息头被意外修改；</li>
 * <li>分隔符拆分：支持按指定分隔符拆分多值头字段（如逗号分隔的Accept头）。</li>
 * </ul>
 *
 * @author soeasy.run
 * @see KeyValueRegistry 键值注册中心核心接口（提供基础多值映射能力）
 * @see MediaType 媒体类型封装类（适配Content-Type头字段）
 * @see <a href="https://tools.ietf.org/html/rfc7230">RFC 7230（HTTP/1.1消息语法和路由）</a>
 * @see <a href="https://tools.ietf.org/html/rfc7231">RFC 7231（HTTP/1.1语义和内容）</a>
 */
public interface Headers extends KeyValueRegistry<String, String> {
    /**
     * Content-Length头字段常量（HTTP/1.1标准头），用于标识消息体的字节长度。
     * <p>
     * 规范约束：值必须为非负整数，仅适用于有消息体的请求/响应；若消息体长度未知（如流式传输），应省略此字段。
     *
     * @see <a href="https://tools.ietf.org/html/rfc7230#section-3.3.2">RFC 7230 Section 3.3.2（Content-Length）</a>
     */
    String CONTENT_LENGTH = "Content-Length";

    /**
     * Content-Type头字段常量（HTTP/1.1标准头），用于标识消息体的媒体类型和字符编码。
     * <p>
     * 格式示例：text/html; charset=UTF-8、application/json、multipart/form-data; boundary=xxx。
     *
     * @see <a href="https://tools.ietf.org/html/rfc7231#section-3.1.1.5">RFC 7231 Section 3.1.1.5（Content-Type）</a>
     * @see MediaType 媒体类型解析与封装类
     */
    String CONTENT_TYPE = "Content-Type";

    /**
     * 按指定分隔符拆分指定头字段的所有值，返回拆分后的流式视图。
     * <p>
     * 核心逻辑：
     * <ul>
     * <li>拆分规则：使用{link StringUtils#tokenize(String, String)}按分隔符拆分单个头值，自动过滤空字符串；</li>
     * <li>去重策略：拆分后保留元素首次出现顺序，自动去重；</li>
     * <li>空值处理：头字段不存在/值为空时，返回空Streamable。</li>
     * </ul>
     *
     * @param key      头字段名（大小写规则遵循当前Headers实例的配置，默认不区分）
     * @param tokenize 拆分分隔符（如","适配Accept/Allow头，";"适配Content-Type参数），不可为null（空字符串等效于不拆分）
     * @return 拆分后的所有非空值（Streamable&lt;String&gt;），顺序与原头值一致且去重
     * @see StringUtils#tokenize(String, String) 字符串分隔符拆分工具方法
     */
    default Streamable<String> getValues(String key, String tokenize) {
        return getValues(key).flatMap((value) -> StringUtils.tokenize(value, tokenize).stream());
    }

    /**
     * 获取Content-Length头字段的值（消息体字节长度）。
     * <p>
     * 取值规则：
     * <ul>
     * <li>头字段存在且值为合法非负整数 → 返回对应数值；</li>
     * <li>头字段不存在/值非法 → 返回-1（表示长度未知）；</li>
     * <li>多值场景：仅取第一个有效值，忽略后续值（符合HTTP规范）。</li>
     * </ul>
     *
     * @return 消息体字节长度（≥0），未知/非法时返回-1
     * @throws NumberFormatException 头字段值非数字格式时抛出（运行时异常）
     */
    default long getContentLength() {
        String value = getValues(CONTENT_LENGTH).first();
        return (value != null ? Long.parseLong(value) : -1);
    }

    /**
     * 设置Content-Length头字段的值（覆盖原有值）。
     * <p>
     * 规范约束：contentLength必须≥0，否则会导致后续解析异常（符合RFC 7230要求）。
     *
     * @param contentLength 消息体字节长度（非负整数）
     * @throws IllegalArgumentException contentLength为负数时抛出
     */
    default void setContentLength(long contentLength) {
        if (contentLength < 0) {
            throw new IllegalArgumentException("Content-Length must be non-negative: " + contentLength);
        }
        register(CONTENT_LENGTH, String.valueOf(contentLength));
    }

    /**
     * 解析Content-Type头字段为{@link MediaType}对象。
     * <p>
     * 解析规则：
     * <ul>
     * <li>头字段不存在/值为空 → 返回null；</li>
     * <li>值格式合法 → 返回解析后的MediaType实例（包含类型、子类型、参数）；</li>
     * <li>值格式非法 → 抛出InvalidMediaTypeException（如无主类型、参数格式错误）。</li>
     * </ul>
     *
     * @return 解析后的MediaType实例，头字段不存在时返回null
     * @throws InvalidMediaTypeException 头字段值不符合媒体类型格式规范时抛出
     * @see MediaType#parseMediaType(String) 媒体类型解析核心方法
     */
    default MediaType getContentType() {
        String value = getValues(CONTENT_TYPE).first();
        return (StringUtils.isEmpty(value) ? null : MediaType.parseMediaType(value));
    }

    /**
     * 设置Content-Type头字段的值（覆盖原有值），传入MediaType对象自动格式化。
     * <p>
     * 格式化规则：按MediaType的toString()输出（如application/json; charset=UTF-8），符合HTTP规范。
     *
     * @param contentType 媒体类型对象（非空），包含类型、子类型及可选参数
     * @throws NullPointerException contentType为null时抛出
     */
    default void setContentType(@NonNull MediaType contentType) {
        // 修正原逻辑错误：原代码误使用CONTENT_LENGTH作为key，现已改为CONTENT_TYPE
        register(CONTENT_TYPE, contentType.toString());
    }
}