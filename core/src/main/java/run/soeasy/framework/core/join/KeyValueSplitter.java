package run.soeasy.framework.core.join;

import java.io.IOException;
import java.util.function.Function;
import java.util.stream.Stream;

import lombok.Getter;
import lombok.NonNull;
import run.soeasy.framework.codec.Codec;
import run.soeasy.framework.core.Assert;
import run.soeasy.framework.core.StringUtils;
import run.soeasy.framework.core.domain.KeyValue;
import run.soeasy.framework.io.IOUtils;

/**
 * 键值对分割器实现，支持将键值对字符串按指定格式分割为{@link KeyValue}对象流，
 * 并通过编解码器处理键值的解码操作。该类继承自{@link KeyValueJoiner}， 兼具键值对连接和分割的双向操作能力。
 *
 * <p>
 * 核心特性：
 * <ul>
 * <li>双向处理：支持键值对字符串的连接（继承自父类）和分割</li>
 * <li>格式定制：通过{@link #delimiter}和{@link #connector}定制分割格式</li>
 * <li>编解码支持：使用独立的键值编解码器实现格式转换</li>
 * <li>空值处理：自动处理null键值和不完整的键值对</li>
 * <li>流式处理：返回{@link Stream}支持函数式操作和惰性处理</li>
 * </ul>
 *
 * <p>
 * 使用场景：
 * <ul>
 * <li>解析URL查询参数（如"key1=value1&amp;key2=value2"）</li>
 * <li>处理配置文件键值对（如"property1=value1;property2=value2"）</li>
 * <li>解析HTTP请求头（如"Header1: Value1, Header2: Value2"）</li>
 * <li>需要编解码处理的键值对文本分割场景</li>
 * </ul>
 *
 * @see KeyValueJoiner
 * @see Splitter
 * @see KeyValue
 */
@Getter
public class KeyValueSplitter extends KeyValueJoiner<Object, Object> implements Splitter<KeyValue<String, String>> {

	/**
	 * 键的解码器，用于将分割后的键字符串转换为目标类型。
	 */
	private final Function<String, String> keyDecoder;

	/**
	 * 值的解码器，用于将分割后的值字符串转换为目标类型。
	 */
	private final Function<String, String> valueDecoder;

	/**
	 * 构造键值对分割器实例，指定分隔符、连接符和编解码器。
	 * <p>
	 * 初始化逻辑：
	 * <ol>
	 * <li>调用父类构造函数，设置分隔符、连接符和编码器</li>
	 * <li>验证分隔符和连接符不相同</li>
	 * <li>初始化键值解码器为指定编解码器的decode方法</li>
	 * </ol>
	 *
	 * @param delimiter  键值对之间的分隔符，不可为null
	 * @param connector  键与值之间的连接符，不可为null
	 * @param keyCodec   键的编解码器，不可为null
	 * @param valueCodec 值的编解码器，不可为null
	 * @throws IllegalArgumentException 当分隔符和连接符相同时抛出
	 */
	public KeyValueSplitter(@NonNull CharSequence delimiter, @NonNull CharSequence connector,
			@NonNull Codec<String, String> keyCodec, @NonNull Codec<String, String> valueCodec) {
		super(delimiter, connector, (key) -> key == null ? null : keyCodec.encode(String.valueOf(key)),
				(value) -> value == null ? null : valueCodec.encode(String.valueOf(value)));
		Assert.isTrue(!delimiter.equals(connector), "The delimiter and connector cannot be the same");
		this.keyDecoder = keyCodec::decode;
		this.valueDecoder = valueCodec::decode;
	}

	/**
	 * 按指定格式分割可读输入流为键值对对象流。
	 * <p>
	 * 处理流程：
	 * <ol>
	 * <li>使用{@link IOUtils#split}按分隔符分割输入</li>
	 * <li>对每个分割结果按连接符分割为键值数组</li>
	 * <li>处理不完整的键值对（只有键或空值）</li>
	 * <li>对键值应用解码器转换</li>
	 * <li>封装为{@link KeyValue}对象</li>
	 * <li>返回包含所有键值对的Stream</li>
	 * </ol>
	 *
	 * @param readable 可读输入流，不可为null
	 * @return 包含键值对对象的Stream，可能包含null（无效键值对）
	 * @throws IOException 当读取或分割操作失败时抛出
	 */
	@Override
	public Stream<KeyValue<String, String>> split(Readable readable) throws IOException {
		return IOUtils.split(readable, getDelimiter()).map((e) -> {
			String[] kv = StringUtils.splitToArray(e, getConnector());
			if (kv.length == 0) {
				return null;
			}
			// 处理键值对，忽略多余的分割部分
			String key = kv[0];
			String value = kv.length == 1 ? null : kv[1];

			// 应用解码器转换键值
			key = keyDecoder.apply(key);
			value = valueDecoder.apply(value);
			return KeyValue.of(key, value);
		});
	}
}