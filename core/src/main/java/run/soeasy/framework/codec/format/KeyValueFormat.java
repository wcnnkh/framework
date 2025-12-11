package run.soeasy.framework.codec.format;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import run.soeasy.framework.codec.Codec;
import run.soeasy.framework.codec.CodecException;
import run.soeasy.framework.core.collection.ArrayUtils;
import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.Converter;
import run.soeasy.framework.core.convert.ConverterAware;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.convert.strings.StringFormat;
import run.soeasy.framework.core.convert.support.SystemConversionService;
import run.soeasy.framework.core.domain.KeyValue;
import run.soeasy.framework.core.join.Joiner;
import run.soeasy.framework.core.join.KeyValueSplitter;
import run.soeasy.framework.core.mapping.property.MapMapping;
import run.soeasy.framework.core.mapping.property.Property;
import run.soeasy.framework.core.mapping.property.PropertyAccessor;
import run.soeasy.framework.core.mapping.property.PropertyMapper;
import run.soeasy.framework.core.streaming.Mapping;
import run.soeasy.framework.core.type.ResolvableType;

/**
 * 键值对格式处理器，实现键值对字符串与对象/ {@link Mapping} 的双向转换，支持多值键、自定义分隔符和类型转换。
 * 
 * <p>核心能力：
 * <ul>
 * <li>键值对字符串 ↔ 对象（Map/POJO）双向转换；</li>
 * <li>处理多值键（同一键对应多个值），适配 Iterable/数组类型；</li>
 * <li>自定义键值对分隔符、键值连接符，支持键/值独立编解码；</li>
 * <li>流式读写（Appendable/Readable），适配大文本场景；</li>
 * <li>集成类型转换服务，支持复杂对象与键值对的映射。</li>
 * </ul>
 * 
 * <p>实现规范：
 * <ul>
 * <li>继承 {@link KeyValueSplitter}：复用键值对分割、拼接基础逻辑；</li>
 * <li>实现 {@link StringFormat}：标准化对象与字符串的格式化转换；</li>
 * <li>实现 {@link ConverterAware}：集成类型转换能力；</li>
 * <li>实现 {@link Codec}：定义 {@link Mapping} 与字符串的编解码规则。</li>
 * </ul>
 * 
 * @author soeasy.run
 * @see KeyValueSplitter 键值对分割/拼接基础类
 * @see Mapping 多值映射数据结构
 * @see SystemConversionService 默认类型转换服务
 */
@Getter
@Setter
public class KeyValueFormat extends KeyValueSplitter
		implements StringFormat<Object>, ConverterAware, Codec<Mapping<String, String>, String> {

	/**
	 * 键值对属性映射器，用于对象与键值对结构的双向映射：
	 * <li>对象 → 键值对：解析对象属性为键值对集合；</li>
	 * <li>键值对 → 对象：将键值对集合映射为指定类型对象。</li>
	 */
	private final PropertyMapper<Property> keyValueMapper = new PropertyMapper<>();

	/**
	 * 获取当前使用的类型转换器
	 * @return 类型转换器，默认返回 {@link SystemConversionService} 实例
	 */
	public Converter getConverter() {
		return keyValueMapper.getMapper().getConverter();
	}

	/**
	 * 设置类型转换器，用于键值对与对象属性的类型转换
	 * @param converter 类型转换器，不可为 null
	 */
	@Override
	public void setConverter(Converter converter) {
		keyValueMapper.getMapper().setConverter(converter);
	}

	/**
	 * 构造键值对格式处理器
	 * @param delimiter  键值对之间的分隔符（如 &quot;&amp;&quot;、&quot;,&quot;），不可为 null
	 * @param connector  键与值之间的连接符（如 &quot;=&quot;、&quot;:&quot;），不可为 null
	 * @param keyCodec   键的编解码器，不可为 null
	 * @param valueCodec 值的编解码器，不可为 null
	 */
	public KeyValueFormat(@NonNull CharSequence delimiter, @NonNull CharSequence connector,
			@NonNull Codec<String, String> keyCodec, @NonNull Codec<String, String> valueCodec) {
		super(delimiter, connector, keyCodec, valueCodec);
		setConverter(SystemConversionService.getInstance());
		keyValueMapper.getMappingProvider().registerFactory(Map.class,
				(map, type) -> new MapMapping(map, type, getConverter()));
	}

	/**
	 * 将键值对元素拼接并追加到目标 Appendable，支持多值处理
	 * 
	 * <p>多值处理规则：
	 * <ul>
	 * <li>值为 Iterable：为每个元素生成独立键值对；</li>
	 * <li>值为数组：遍历数组元素生成独立键值对；</li>
	 * <li>普通值：直接生成单个键值对。</li>
	 * </ul>
	 * 
	 * @param appendable 目标追加器（如 StringBuilder），不可为 null
	 * @param count      已处理的键值对数量（用于分隔符添加）
	 * @param element    待处理的键值对元素，不可为 null
	 * @return 处理后的键值对总数量
	 * @throws IOException 写入 Appendable 失败时抛出
	 */
	@Override
	public long join(Appendable appendable, long count, KeyValue<? extends Object, ? extends Object> element)
			throws IOException {
		Object value = element.getValue();
		if (value instanceof Iterable) {
			for (Object obj : (Iterable<?>) value) {
				count += super.join(appendable, count, KeyValue.of(element.getKey(), obj));
			}
			return count;
		}

		if (value != null && value.getClass().isArray()) {
			Iterator<Object> iterator = ArrayUtils.elements(value).stream().iterator();
			while (iterator.hasNext()) {
				count += super.join(appendable, count, KeyValue.of(element.getKey(), iterator.next()));
			}
			return count;
		}
		return super.join(appendable, count, element);
	}

	/**
	 * 将对象格式化为键值对字符串并写入 Appendable
	 * @param source               待格式化的对象（如 Map/POJO），不可为 null
	 * @param sourceTypeDescriptor 源对象的类型描述符
	 * @param appendable           目标追加器，不可为 null
	 * @throws ConversionException 对象转换为键值对失败时抛出
	 * @throws IOException         写入 Appendable 失败时抛出
	 */
	@Override
	public void to(Object source, TypeDescriptor sourceTypeDescriptor, Appendable appendable)
			throws ConversionException, IOException {
		Mapping<String, PropertyAccessor> mapping = keyValueMapper.getMapping(source, sourceTypeDescriptor);
		Iterator<KeyValue<String, Object>> iterator = mapping.stream()
				.filter((e) -> e.getValue().isReadable()).map((e) -> KeyValue.of(e.getKey(), e.getValue().get()))
				.iterator();
		Joiner.joinAll(appendable, iterator, this);
	}

	/**
	 * 从 Readable 读取键值对字符串并转换为指定类型的对象
	 * @param readable             可读数据源（如 StringReader），不可为 null
	 * @param targetTypeDescriptor 目标对象的类型描述符，不可为 null
	 * @return 转换后的目标类型对象
	 * @throws ConversionException 键值对转换为目标对象失败时抛出
	 * @throws IOException         读取 Readable 失败时抛出
	 */
	@Override
	public Object from(Readable readable, TypeDescriptor targetTypeDescriptor) throws ConversionException, IOException {
		TypeDescriptor mapTypeDescriptor = TypeDescriptor.map(LinkedHashMap.class, String.class,
				ResolvableType.forClassWithGenerics(List.class, String.class));
		Map<String, List<String>> map = split(readable).collect(Collectors.groupingBy(KeyValue::getKey,
				LinkedHashMap::new, Collectors.mapping(KeyValue::getValue, Collectors.toList())));
		return keyValueMapper.convert(map, mapTypeDescriptor, targetTypeDescriptor);
	}

	/**
	 * 将 {@link Mapping} 编码为键值对字符串
	 * @param source 待编码的 Mapping，不可为 null
	 * @return 编码后的键值对字符串
	 * @throws CodecException 编码失败时抛出
	 */
	@Override
	public final String encode(Mapping<String, String> source) throws CodecException {
		return convert(source, String.class);
	}

	/**
	 * 将键值对字符串解码为 {@link Mapping}
	 * @param source 待解码的键值对字符串，不可为 null
	 * @return 解码后的 Mapping（保持键的插入顺序）
	 * @throws CodecException 解码失败时抛出
	 */
	@Override
	public final Mapping<String, String> decode(String source) throws CodecException {
		Map<String, List<String>> map = split(source).collect(Collectors.groupingBy(KeyValue::getKey,
				LinkedHashMap::new, Collectors.mapping(KeyValue::getValue, Collectors.toList())));
		return Mapping.ofMultiMapped(map);
	}
}