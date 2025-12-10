package run.soeasy.framework.messaging;

import lombok.NonNull;
import run.soeasy.framework.core.StringUtils;
import run.soeasy.framework.core.exchange.KeyValueRegistry;
import run.soeasy.framework.core.streaming.Streamable;

/**
 * 消息头集合类，继承自{@link AbstractMultiValueMap}，实现多值映射功能， 专门用于管理消息头（如HTTP请求头/响应头）
 * 
 * <p>
 * 核心特性： - 支持单键多值存储（符合HTTP头字段可重复的特性）； - 可配置键是否区分大小写（默认不区分，符合HTTP头字段规范）； -
 * 提供HTTP标准头字段（如Content-Type、Content-Length）的便捷操作方法； - 支持设置为只读模式，防止意外修改。
 * 
 * @author soeasy.run
 * @see AbstractMultiValueMap
 * @see MediaType
 */
public interface Headers extends KeyValueRegistry<String, String> {
	/**
	 * Content-Length头字段名，用于指定消息体的长度（字节数）
	 * 
	 * @see <a href="https://tools.ietf.org/html/rfc7230#section-3.3.2">RFC 7230
	 *      Section 3.3.2</a>
	 */
	public static final String CONTENT_LENGTH = "Content-Length";

	/**
	 * Content-Type头字段名，用于指定消息体的媒体类型
	 * 
	 * @see <a href="https://tools.ietf.org/html/rfc7231#section-3.1.1.5">RFC 7231
	 *      Section 3.1.1.5</a>
	 */
	public static final String CONTENT_TYPE = "Content-Type";

	/**
	 * 获取指定头字段的所有值，并按指定分隔符拆分后返回
	 * 
	 * <p>
	 * 适用于处理逗号分隔的头字段值（如Accept、Allow等），拆分后去重并保持顺序。
	 * 
	 * @param headerName 头字段名
	 * @param tokenize   分隔符（如","）
	 * @return 拆分后的所有值
	 */
	default Streamable<String> getValues(String key, String tokenize) {
		return getValues(key).flatMap((value) -> StringUtils.tokenize(value, tokenize).stream());
	}

	/**
	 * 获取Content-Length头字段的值，即消息体的长度（字节数）
	 * 
	 * @return 消息体长度，未知时返回-1
	 */
	default long getContentLength() {
		String value = getValues(CONTENT_LENGTH).first();
		return (value != null ? Long.parseLong(value) : -1);
	}

	default void setContentLength(long contentLength) {
		register(CONTENT_LENGTH, String.valueOf(contentLength));
	}

	/**
	 * 获取Content-Type头字段对应的MediaType对象
	 * 
	 * @return 媒体类型对象，未知时返回null
	 * @throws InvalidMediaTypeException 若头字段值格式非法
	 */
	default MediaType getContentType() {
		String value = getValues(CONTENT_TYPE).first();
		return (StringUtils.isEmpty(value) ? null : MediaType.parseMediaType(value));
	}

	default void setContentType(@NonNull MediaType contentType) {
		register(CONTENT_LENGTH, contentType.toString());
	}
}
