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
import run.soeasy.framework.core.collection.DefaultMultiValueMap;
import run.soeasy.framework.core.collection.MultiValueMap;
import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.Converter;
import run.soeasy.framework.core.convert.ConverterAware;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.convert.support.SystemConversionService;
import run.soeasy.framework.core.convert.value.TypedValueAccessor;
import run.soeasy.framework.core.domain.KeyValue;
import run.soeasy.framework.core.join.Joiner;
import run.soeasy.framework.core.join.KeyValueSplitter;
import run.soeasy.framework.core.transform.collection.MapMapping;
import run.soeasy.framework.core.transform.templates.DefaultMapper;
import run.soeasy.framework.core.transform.templates.Mapping;
import run.soeasy.framework.core.transform.templates.MappingContext;
import run.soeasy.framework.io.CharacterStreamFormat;

/**
 * 键值对格式处理器 支持键值对数据的编解码、字符流处理和类型转换，适用于配置解析、参数处理等场景
 * 
 * 实现特点： 1. 继承自KeyValueSplitter，复用键值对分割逻辑 2. 实现CharacterStreamFormat接口，支持流式读写 3.
 * 实现Codec接口，定义MultiValueMap与字符串的编解码规则 4. 实现ConverterAware接口，集成类型转换能力
 */
@Getter
@Setter
public class KeyValueFormat extends KeyValueSplitter
		implements CharacterStreamFormat, Codec<MultiValueMap<String, String>, String>, ConverterAware {
	@NonNull
	private Converter converter = SystemConversionService.getInstance();
	private final DefaultMapper<Object, TypedValueAccessor, Mapping<Object, TypedValueAccessor>> keyValueMapper = new DefaultMapper<>();

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
	}

	/**
	 * 将键值对字符串解码为MultiValueMap 支持处理重复键，例如："key=val1,key=val2" 会被解析为 {key=[val1,
	 * val2]}
	 * 
	 * @param source 键值对字符串（格式：key1=val1,key2=val2）
	 * @return 解析后的MultiValueMap，保留插入顺序
	 * @throws DecodeException 当字符串格式无效时抛出
	 */
	@Override
	public final MultiValueMap<String, String> decode(String source) throws DecodeException {
		// 使用Stream流处理键值对，分组收集到LinkedHashMap保持顺序
		Map<String, List<String>> map = split(source).collect(Collectors.groupingBy(KeyValue::getKey, // 按键分组
				LinkedHashMap::new, // 使用LinkedHashMap保持键的顺序
				Collectors.mapping(KeyValue::getValue, Collectors.toList()) // 收集值列表
		));
		return new DefaultMultiValueMap<>(map);
	}

	/**
	 * 将MultiValueMap编码为键值对字符串 例如：{key1=[val1], key2=[val2, val3]} 会被格式化为
	 * "key1=val1,key2=val2,key2=val3"
	 * 
	 * @param source 待编码的MultiValueMap
	 * @return 编码后的键值对字符串，键值对用delimiter分隔，键值用connector连接
	 * @throws EncodeException 当编码过程中发生错误时抛出
	 */
	@Override
	public final String encode(@NonNull MultiValueMap<String, String> source) throws EncodeException {
		// 委托给CharacterStreamFormat的format方法处理
		return format(source, TypeDescriptor.forObject(source));
	}

	/**
	 * 将对象格式化为键值对并追加到目标位置 支持将任意对象转换为Map后再格式化为键值对字符串
	 * 
	 * @param appendable           目标追加位置（如StringBuilder、FileWriter）
	 * @param source               待格式化的对象（通常为Map或可转换为Map的类型）
	 * @param sourceTypeDescriptor 源对象的类型描述符
	 * @throws IOException 当向Appendable写入数据失败时抛出
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void format(Appendable appendable, @NonNull Object source, TypeDescriptor sourceTypeDescriptor)
			throws IOException {
		// 定义目标Map类型描述符（LinkedHashMap保持顺序）
		TypeDescriptor mapTypeDescriptor = TypeDescriptor.map(LinkedHashMap.class, Object.class, Object.class);
		// 类型转换逻辑：若需要转换则使用Converter，否则直接使用
		if (converter.canConvert(sourceTypeDescriptor, mapTypeDescriptor)) {
			Map<Object, Object> sourceMap = (Map<Object, Object>) converter.convert(source, sourceTypeDescriptor,
					mapTypeDescriptor);
			Joiner.joinAll(appendable, sourceMap.entrySet().stream().map(KeyValue::wrap).iterator(), this);
		} else if (source instanceof Map) {
			Map<Object, Object> sourceMap = (Map<Object, Object>) source;
			Joiner.joinAll(appendable, sourceMap.entrySet().stream().map(KeyValue::wrap).iterator(), this);
		} else {
			Joiner.joinAll(appendable, keyValueMapper.getMapping(source, sourceTypeDescriptor).getElements().stream()
					.map((e) -> KeyValue.of(e.getKey(), e.getValue().get())).iterator(), this);
		}
	}

	/**
	 * 格式化对象为字符串（实现自CharacterStreamFormat） 内部调用format(Appendable)方法并返回字符串结果
	 */
	@Override
	public final String format(Object source, @NonNull TypeDescriptor sourceTypeDescriptor) {
		return CharacterStreamFormat.super.format(source, sourceTypeDescriptor);
	}

	/**
	 * 从字符流解析键值对并转换为目标类型 支持流式处理大文本，避免一次性加载全部内容
	 * 
	 * @param readable             字符输入流（如BufferedReader、InputStreamReader）
	 * @param targetTypeDescriptor 目标类型描述符
	 * @return 解析并转换后的对象
	 * @throws IOException         当读取流失败时抛出
	 * @throws ConversionException 当类型转换失败时抛出
	 */
	@Override
	public Object parse(Readable readable, TypeDescriptor targetTypeDescriptor)
			throws IOException, ConversionException {
		// 从流中解析键值对并分组（处理重复键）
		Map<String, List<String>> map = split(readable).collect(Collectors.groupingBy(KeyValue::getKey,
				LinkedHashMap::new, Collectors.mapping(KeyValue::getValue, Collectors.toList())));
		if (converter.canConvert(map.getClass(), targetTypeDescriptor)) {
			// 使用Converter将Map转换为目标类型（体现策略模式）
			return converter.convert(map, targetTypeDescriptor);
		}

		Object target = keyValueMapper.newInstance(targetTypeDescriptor.getResolvableType());
		MapMapping sourceMapping = new MapMapping(map, TypeDescriptor.forObject(map), converter);
		Mapping<Object, TypedValueAccessor> targetMapping = keyValueMapper.getMapping(target, targetTypeDescriptor);
		keyValueMapper.doMapping(new MappingContext<>(sourceMapping), new MappingContext<>(targetMapping));
		return target;
	}

	/**
	 * 从字符串解析为对象（实现自CharacterStreamFormat） 内部调用parse(Readable)方法处理字符串输入
	 */
	@Override
	public final Object parse(String source, @NonNull TypeDescriptor targetTypeDescriptor) throws ConversionException {
		return CharacterStreamFormat.super.parse(source, targetTypeDescriptor);
	}
}
