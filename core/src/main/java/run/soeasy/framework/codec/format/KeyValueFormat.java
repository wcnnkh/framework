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
import run.soeasy.framework.core.collection.MultiValueMap;
import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.Converter;
import run.soeasy.framework.core.convert.ConverterAware;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.convert.strings.StringFormat;
import run.soeasy.framework.core.convert.support.SystemConversionService;
import run.soeasy.framework.core.domain.KeyValue;
import run.soeasy.framework.core.join.Joiner;
import run.soeasy.framework.core.join.KeyValueSplitter;
import run.soeasy.framework.core.mapping.DefaultMapper;
import run.soeasy.framework.core.mapping.property.MapMapping;
import run.soeasy.framework.core.mapping.property.Property;
import run.soeasy.framework.core.mapping.property.PropertyAccessor;
import run.soeasy.framework.core.mapping.property.PropertyMapper;
import run.soeasy.framework.core.streaming.Mapping;
import run.soeasy.framework.core.type.ResolvableType;

/**
 * 键值对格式处理器，提供键值对数据的编解码、类型转换和流式处理能力，适用于配置解析、参数处理、数据格式化等场景。
 * 
 * <p>
 * 核心功能：
 * <ul>
 * <li>将对象与键值对字符串相互转换（如"a=1&amp;b=2"与Map的互转）</li>
 * <li>支持自定义分隔符（键值对之间）和连接符（键与值之间）</li>
 * <li>处理多值键（同一键对应多个值），适配{@link Mapping}数据结构</li>
 * <li>集成类型转换服务，支持复杂对象与键值对的双向映射</li>
 * <li>提供流式读写能力，高效处理大文本或网络流数据</li>
 * </ul>
 * 
 * <p>
 * 实现特性：
 * <ul>
 * <li>继承{@link KeyValueSplitter}，复用键值对分割与拼接逻辑</li>
 * <li>实现{@link StringFormat}，支持对象与字符串的格式化转换</li>
 * <li>实现{@link Codec}，定义{@link MultiValueMap}与字符串的编解码规则</li>
 * <li>实现{@link ConverterAware}，集成类型转换能力，支持复杂对象处理</li>
 * <li>通过{@link DefaultMapper}实现对象与键值对的映射转换</li>
 * </ul>
 * 
 * @author soeasy.run
 * @see MultiValueMap 多值映射接口，支持一个键对应多个值
 * @see KeyValueSplitter 键值对分割工具，提供基础的分割与拼接能力
 * @see SystemConversionService 默认类型转换服务
 */
@Getter
@Setter
public class KeyValueFormat extends KeyValueSplitter
		implements StringFormat<Object>, ConverterAware, Codec<Mapping<String, String>, String> {

	/**
	 * 键值对映射器，负责对象与键值对结构的映射转换， 支持将任意对象转换为键值对集合，或将键值对集合转换为指定类型的对象。
	 */
	private final PropertyMapper<Property> keyValueMapper = new PropertyMapper<>();

	/**
	 * 获取当前使用的类型转换器
	 * 
	 * @return 类型转换器，默认使用{@link SystemConversionService}
	 */
	public Converter getConverter() {
		return keyValueMapper.getMapper().getConverter();
	}

	/**
	 * 设置类型转换器，用于对象与键值对之间的类型转换
	 * 
	 * @param converter 类型转换器，不可为null
	 */
	@Override
	public void setConverter(Converter converter) {
		keyValueMapper.getMapper().setConverter(converter);
	}

	/**
	 * 构造函数，初始化键值对格式处理器的核心参数
	 * 
	 * @param delimiter  键值对之间的分隔符（例如："," 或 "; "）
	 * @param connector  键与值之间的连接符（例如："=" 或 ":"）
	 * @param keyCodec   键的编解码器，用于键的编码（如URL编码）和解码
	 * @param valueCodec 值的编解码器，用于值的编码（如URL编码）和解码
	 */
	public KeyValueFormat(@NonNull CharSequence delimiter, @NonNull CharSequence connector,
			@NonNull Codec<String, String> keyCodec, @NonNull Codec<String, String> valueCodec) {
		super(delimiter, connector, keyCodec, valueCodec);
		setConverter(SystemConversionService.getInstance());
		keyValueMapper.getMappingProvider().registerFactory(Map.class,
				(map, type) -> new MapMapping(map, type, getConverter()));
	}

	/**
	 * 将键值对元素拼接并追加到目标Appendable，支持多值键的处理
	 * 
	 * <p>
	 * 特殊处理：
	 * <ul>
	 * <li>若值为{@link Iterable}（如List），则为每个元素生成一个键值对</li>
	 * <li>若值为数组，则为每个数组元素生成一个键值对</li>
	 * <li>普通值直接生成一个键值对</li>
	 * </ul>
	 * 
	 * @param appendable 目标追加器（如StringBuilder、FileWriter）
	 * @param count      已处理的键值对数量，用于分隔符的添加（首个元素前不加分隔符）
	 * @param element    待处理的键值对元素
	 * @return 处理后的键值对总数量
	 * @throws IOException 当向Appendable写入数据失败时抛出
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
	 * 将对象格式化为键值对字符串并追加到目标位置
	 * 
	 * <p>
	 * 处理流程：
	 * <ol>
	 * <li>通过{@link #keyValueMapper}将对象转换为键值对集合</li>
	 * <li>过滤出可读取的值，生成键值对迭代器</li>
	 * <li>使用{@link Joiner}将键值对拼接为字符串并写入Appendable</li>
	 * </ol>
	 * 
	 * @param source               待格式化的对象（通常为Map或可转换为Map的类型）
	 * @param sourceTypeDescriptor 源对象的类型描述符，用于精确转换
	 * @param appendable           目标追加位置（如StringBuilder、FileWriter）
	 * @throws IOException         当向Appendable写入数据失败时抛出
	 * @throws ConversionException 当对象转换为键值对失败时抛出
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
	 * 从可读数据源（如Reader）读取键值对字符串并转换为指定类型的对象
	 * 
	 * <p>
	 * 处理流程：
	 * <ol>
	 * <li>将可读数据源解析为键值对集合</li>
	 * <li>转换为Map结构（键为String类型，值为String列表）支持多值键</li>
	 * <li>通过{@link #keyValueMapper}将Map转换为目标类型的对象</li>
	 * </ol>
	 * 
	 * @param readable             可读数据源（如StringReader、FileReader）
	 * @param targetTypeDescriptor 目标对象的类型描述符
	 * @return 转换后的目标类型对象
	 * @throws ConversionException 当键值对转换为目标类型失败时抛出
	 * @throws IOException         当从Readable读取数据失败时抛出
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
	 * 将{@link MultiValueMap}编码为键值对字符串
	 * 
	 * <p>
	 * 默认实现通过类型转换完成，等价于将MultiValueMap转换为String类型。
	 * 
	 * @param source 待编码的MultiValueMap
	 * @return 编码后的键值对字符串
	 * @throws CodecException 当编码过程失败时抛出（如转换异常）
	 */
	@Override
	public final String encode(Mapping<String, String> source) throws CodecException {
		return convert(source, String.class);
	}

	/**
	 * 将键值对字符串解码为{@link MultiValueMap}
	 * 
	 * <p>
	 * 处理流程：
	 * <ol>
	 * <li>将字符串分割为键值对集合</li>
	 * <li>按键分组，收集每个键对应的多个值（保持插入顺序）</li>
	 * <li>转换为{@link Mapping}返回</li>
	 * </ol>
	 * 
	 * @param source 待解码的键值对字符串
	 * @return 解码后的MultiValueMap，键值顺序与字符串中保持一致
	 * @throws CodecException 当解码过程失败时抛出（如分割异常）
	 */
	@Override
	public final Mapping<String, String> decode(String source) throws CodecException {
		// 使用Stream流处理键值对，分组收集到LinkedHashMap保持顺序
		Map<String, List<String>> map = split(source).collect(Collectors.groupingBy(KeyValue::getKey, // 按键分组
				LinkedHashMap::new, // 使用LinkedHashMap保持键的顺序
				Collectors.mapping(KeyValue::getValue, Collectors.toList()) // 收集值列表
		));
		return Mapping.ofMultiMapped(map);
	}
}
