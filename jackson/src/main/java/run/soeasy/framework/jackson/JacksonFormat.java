package run.soeasy.framework.jackson;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.convert.strings.StringFormat;
import run.soeasy.framework.io.AppendableWriter;
import run.soeasy.framework.io.ReadableReader;

/**
 * Jackson对象映射器接口，定义基于Jackson的序列化与反序列化标准操作。
 * 该接口封装了ObjectMapper核心功能，提供流式处理和字符串转换的统一操作规范，
 * 支持通过泛型类型描述符{@link TypeDescriptor}实现精确的类型转换。
 * 
 * <p>
 * 接口特点：
 * <ul>
 * <li>标准化转换流程，隔离Jackson底层实现细节</li>
 * <li>支持{@link ReadableReader}/{@link AppendableWriter}流式IO操作</li>
 * <li>通过{@link TypeFactory}实现泛型类型的精确解析</li>
 * <li>统一异常封装，将Jackson原生异常转换为{@link ConversionException}</li>
 * </ul>
 * 
 * <h3>实现示例</h3>
 * 
 * <pre>{@code
 * // 基础实现类示例
 * public class DefaultJacksonFormat implements JacksonFormat {
 * 	private final ObjectMapper objectMapper;
 * 
 * 	public DefaultJacksonFormat(ObjectMapper objectMapper) {
 * 		this.objectMapper = objectMapper;
 * 	} @Override
 * 	public ObjectMapper getObjectMapper() {
 * 		return objectMapper;
 * 	}
 * }
 * 
 * // 使用接口进行转换
 * JacksonFormat mapper = new DefaultJacksonFormat(new ObjectMapper());
 * String json = mapper.to(user, TypeDescriptor.forObject(user), TypeDescriptor.valueOf(String.class));
 * }</pre>
 * 
 * @see StringFormat
 * @see ObjectMapper
 * @see TypeDescriptor
 */
@FunctionalInterface
public interface JacksonFormat extends StringFormat<Object> {

	/**
	 * 获取底层的Jackson对象映射器，用于访问原生API或自定义配置。
	 * 
	 * @return ObjectMapper实例，不可为null
	 */
	ObjectMapper getObjectMapper();

	/**
	 * 从可读流中反序列化对象，支持泛型类型解析。
	 * <p>
	 * 该默认实现通过{@link ReadableReader}包装输入流， 使用{@link TypeFactory}构建目标类型后执行反序列化。
	 * 
	 * @param readable             输入流数据源
	 * @param targetTypeDescriptor 目标类型描述符，支持泛型类型
	 * @return 反序列化后的对象实例
	 * @throws ConversionException 当格式错误或类型不匹配时抛出
	 * @throws IOException         流操作异常
	 */
	@Override
	default Object from(Readable readable, TypeDescriptor targetTypeDescriptor)
			throws ConversionException, IOException {
		JavaType javaType = TypeFactory.defaultInstance().constructType(targetTypeDescriptor.getResolvableType());
		try {
			return getObjectMapper().readValue(new ReadableReader(readable), javaType);
		} catch (JsonProcessingException e) {
			throw new ConversionException("Unsuccessful deserialization from stream", e);
		}
	}

	/**
	 * 从字符串反序列化对象，支持泛型类型参数。
	 * <p>
	 * 默认实现通过{@link TypeDescriptor}解析目标类型， 自动处理格式验证和类型映射。
	 * 
	 * @param source               字符串
	 * @param sourceTypeDescriptor 源类型描述符（通常为String.class）
	 * @param targetTypeDescriptor 目标类型描述符
	 * @return 反序列化后的对象实例
	 * @throws ConversionException 当格式错误或类型不匹配时抛出
	 */
	@Override
	default Object from(String source, TypeDescriptor sourceTypeDescriptor, TypeDescriptor targetTypeDescriptor)
			throws ConversionException {
		JavaType javaType = TypeFactory.defaultInstance().constructType(targetTypeDescriptor.getResolvableType());
		try {
			return getObjectMapper().readValue(source, javaType);
		} catch (JsonProcessingException e) {
			throw new ConversionException("Desialization failed", e);
		}
	}

	/**
	 * 将对象序列化为流式输出，支持{@link Appendable}目标。
	 * <p>
	 * 默认实现通过{@link AppendableWriter}包装输出目标， 自动过滤null值并处理特殊字符。
	 * 
	 * @param source               待序列化对象
	 * @param sourceTypeDescriptor 源类型描述符
	 * @param appendable           输出目标（如FileWriter、StringBuilder）
	 * @throws ConversionException 当对象包含不可序列化字段时抛出
	 * @throws IOException         流操作异常
	 */
	@Override
	default void to(Object source, TypeDescriptor sourceTypeDescriptor, Appendable appendable)
			throws ConversionException, IOException {
		try {
			getObjectMapper().writeValue(new AppendableWriter(appendable), source);
		} catch (JsonProcessingException e) {
			throw new ConversionException("Object serialization to stream failed", e);
		}
	}

	/**
	 * 将对象序列化为字符串，自动处理类型转换。
	 * <p>
	 * 默认实现使用{@link ObjectMapper#writeValueAsString}方法， 支持POJO、集合、Map等常见数据结构。
	 * 
	 * @param source               待序列化对象
	 * @param sourceTypeDescriptor 源类型描述符
	 * @param targetTypeDescriptor 目标类型描述符（通常为String.class）
	 * @return 标准格式字符串
	 * @throws ConversionException 当对象包含不可序列化字段时抛出
	 */
	@Override
	default String to(Object source, TypeDescriptor sourceTypeDescriptor, TypeDescriptor targetTypeDescriptor)
			throws ConversionException {
		try {
			return getObjectMapper().writeValueAsString(source);
		} catch (JsonProcessingException e) {
			throw new ConversionException("Object serialization failed", e);
		}
	}
}