package run.soeasy.framework.codec.format;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import run.soeasy.framework.codec.Codec;
import run.soeasy.framework.codec.DecodeException;
import run.soeasy.framework.codec.EncodeException;
import run.soeasy.framework.core.collection.ArrayUtils;
import run.soeasy.framework.core.collection.DefaultMultiValueMap;
import run.soeasy.framework.core.collection.MultiValueMap;
import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.Converter;
import run.soeasy.framework.core.convert.ConverterAware;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.convert.strings.StringFormat;
import run.soeasy.framework.core.convert.support.SystemConversionService;
import run.soeasy.framework.core.convert.value.TypedValueAccessor;
import run.soeasy.framework.core.domain.KeyValue;
import run.soeasy.framework.core.join.Joiner;
import run.soeasy.framework.core.join.KeyValueSplitter;
import run.soeasy.framework.core.transform.collection.MapEntryMapping;
import run.soeasy.framework.core.transform.templates.DefaultMapper;
import run.soeasy.framework.core.transform.templates.Mapping;
import run.soeasy.framework.core.type.ResolvableType;

/**
 * 键值对格式处理器 支持键值对数据的编解码、字符流处理和类型转换，适用于配置解析、参数处理等场景
 * 
 * 实现特点： 1. 继承自KeyValueSplitter，复用键值对分割逻辑 2. 实现CharacterStreamFormat接口，支持流式读写 3.
 * 实现Codec接口，定义MultiValueMap与字符串的编解码规则 4. 实现ConverterAware接口，集成类型转换能力
 * 
 * @author soeasy.run
 */
@Getter
@Setter
public class KeyValueFormat extends KeyValueSplitter
		implements StringFormat<Object>, ConverterAware, Codec<MultiValueMap<String, String>, String> {
	private final DefaultMapper<Object, TypedValueAccessor, Mapping<Object, TypedValueAccessor>> keyValueMapper = new DefaultMapper<>();

	public Converter getConverter() {
		return keyValueMapper.getMapper().getConverter();
	}

	@Override
	public void setConverter(Converter converter) {
		keyValueMapper.getMapper().setConverter(converter);
	}

	/**
	 * 构造函数
	 * 
	 * @param delimiter  键值对之间的分隔符（例如："," 或 "; "）
	 * @param connector  键与值之间的连接符（例如："=" 或 ":"）
	 * @param keyCodec
	 * @param valueCodec
	 */
	public KeyValueFormat(@NonNull CharSequence delimiter, @NonNull CharSequence connector,
			@NonNull Codec<String, String> keyCodec, @NonNull Codec<String, String> valueCodec) {
		super(delimiter, connector, keyCodec, valueCodec);
		setConverter(SystemConversionService.getInstance());
		keyValueMapper.getMappingProvider().registerFactory(Map.class,
				(map, type) -> new MapEntryMapping(map, type, getConverter()));
	}

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
			for (Object obj : ArrayUtils.elements(value)) {
				count += super.join(appendable, count, KeyValue.of(element.getKey(), obj));
			}
			return count;
		}
		return super.join(appendable, count, element);
	}

	/**
	 * 将对象格式化为键值对并追加到目标位置 支持将任意对象转换为Map后再格式化为键值对字符串
	 * 
	 * @param source               待格式化的对象（通常为Map或可转换为Map的类型）
	 * @param sourceTypeDescriptor 源对象的类型描述符
	 * @param appendable           目标追加位置（如StringBuilder、FileWriter）
	 * @throws IOException 当向Appendable写入数据失败时抛出
	 */
	@Override
	public void to(Object source, TypeDescriptor sourceTypeDescriptor, Appendable appendable)
			throws ConversionException, IOException {
		Mapping<Object, TypedValueAccessor> mapping = keyValueMapper.getMapping(source, sourceTypeDescriptor);
		Joiner.joinAll(appendable, mapping.getElements().stream().filter((e) -> e.getValue().isReadable())
				.map((e) -> KeyValue.of(e.getKey(), e.getValue().get())).iterator(), this);
	}

	@Override
	public Object from(Readable readable, TypeDescriptor targetTypeDescriptor) throws ConversionException, IOException {
		TypeDescriptor mapTypeDescriptor = TypeDescriptor.map(LinkedHashMap.class, String.class,
				ResolvableType.forClassWithGenerics(List.class, String.class));
		Map<String, List<String>> map = split(readable).collect(Collectors.groupingBy(KeyValue::getKey,
				LinkedHashMap::new, Collectors.mapping(KeyValue::getValue, Collectors.toList())));
		return keyValueMapper.convert(map, mapTypeDescriptor, targetTypeDescriptor);
	}

	@Override
	public final String encode(MultiValueMap<String, String> source) throws EncodeException {
		return convert(source, String.class);
	}

	@Override
	public final MultiValueMap<String, String> decode(String source) throws DecodeException {
		// 使用Stream流处理键值对，分组收集到LinkedHashMap保持顺序
		Map<String, List<String>> map = split(source).collect(Collectors.groupingBy(KeyValue::getKey, // 按键分组
				LinkedHashMap::new, // 使用LinkedHashMap保持键的顺序
				Collectors.mapping(KeyValue::getValue, Collectors.toList()) // 收集值列表
		));
		return new DefaultMultiValueMap<>(map);
	}
}
