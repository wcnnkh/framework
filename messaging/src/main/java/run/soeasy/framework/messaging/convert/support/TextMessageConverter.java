package run.soeasy.framework.messaging.convert.support;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;

import lombok.NonNull;
import run.soeasy.framework.core.convert.Converter;
import run.soeasy.framework.core.convert.ConverterAware;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.convert.support.SystemConversionService;
import run.soeasy.framework.core.convert.value.SourceDescriptor;
import run.soeasy.framework.core.convert.value.TargetDescriptor;
import run.soeasy.framework.core.convert.value.TypedData;
import run.soeasy.framework.io.MimeType;
import run.soeasy.framework.messaging.MediaType;
import run.soeasy.framework.messaging.Message;

/**
 * 文本消息转换器，继承自{@link AbstractTextMessageConverter}并实现{@link ConverterAware}，
 * 专注于文本字符串与Java对象之间的转换，支持所有文本类型（text/*），是处理纯文本消息的通用转换器。
 * 
 * <p>
 * 核心特性： - 基于文本消息转换器框架，支持字符集自动处理（默认UTF-8）； -
 * 依赖{@link Converter}进行字符串与目标类型的转换（默认使用{@link SystemConversionService}）； -
 * 适用于纯文本消息场景（如text/plain），可将文本转换为任意支持的Java类型（如String、Integer、Date等），或反之。
 * 
 * @author soeasy.run
 * @see AbstractTextMessageConverter
 * @see Converter
 * @see MediaType#TEXT_PLAIN
 */
public class TextMessageConverter extends AbstractTextMessageConverter<Object> implements ConverterAware {

	/**
	 * 匹配所有文本媒体类型的通用类型（text/*），用于支持所有文本子类型（如text/plain、text/html等）
	 */
	public static final MediaType TEXT_ALL = new MediaType("text", "*");

	/**
	 * 类型转换器，用于字符串与Java对象之间的转换（非空），
	 * 默认使用系统级转换服务{@link SystemConversionService#getInstance()}，可通过setter方法替换为自定义实现。
	 */
	@NonNull
	private Converter converter = SystemConversionService.getInstance();

	public Converter getConverter() {
		return converter;
	}

	@Override
	public void setConverter(Converter converter) {
		this.converter = converter;
	}

	/**
	 * 初始化文本消息转换器，设置目标类型为{@link Object}（支持任意类型），
	 * 并注册支持的媒体类型：标准文本类型（text/plain）和所有文本类型（text/*）。
	 */
	public TextMessageConverter() {
		super(Object.class);
		getMediaTypeRegistry().addAll(Arrays.asList(MediaType.TEXT_PLAIN, TEXT_ALL));
	}

	/**
	 * 判断是否支持将文本消息转换为目标类型
	 * 
	 * <p>
	 * 支持性条件： 1. 转换器（{@link #converter}）能够将字符串（{@link String}）转换为目标类型； 2.
	 * 父类的媒体类型匹配检查通过（{@link AbstractTextMessageConverter#isReadable}）。
	 * 
	 * @param targetDescriptor 目标类型描述符（非空，包含待转换的类型信息）
	 * @param message          关联的消息（非空）
	 * @param contentType      目标媒体类型（可为null）
	 * @return 支持转换返回true，否则返回false
	 */
	@Override
	public boolean isReadable(@NonNull TargetDescriptor targetDescriptor, @NonNull Message message,
			MimeType contentType) {
		// 检查转换器是否支持String到目标类型的转换
		return getConverter().canConvert(TypeDescriptor.valueOf(String.class),
				targetDescriptor.getRequiredTypeDescriptor())
				&& super.isReadable(targetDescriptor, message, contentType);
	}

	/**
	 * 判断是否支持将源对象转换为文本消息
	 * 
	 * <p>
	 * 支持性条件： 1. 转换器（{@link #converter}）能够将源类型转换为字符串（{@link String}）； 2.
	 * 父类的媒体类型匹配检查通过（{@link AbstractTextMessageConverter#isWriteable}）。
	 * 
	 * @param sourceDescriptor 源类型描述符（非空，包含待转换的源类型信息）
	 * @param message          关联的消息（非空）
	 * @param contentType      目标媒体类型（可为null）
	 * @return 支持转换返回true，否则返回false
	 */
	@Override
	public boolean isWriteable(@NonNull SourceDescriptor sourceDescriptor, @NonNull Message message,
			MimeType contentType) {
		// 检查转换器是否支持源类型到String的转换
		return getConverter().canConvert(sourceDescriptor.getReturnTypeDescriptor(),
				TypeDescriptor.valueOf(String.class)) && super.isWriteable(sourceDescriptor, message, contentType);
	}

	/**
	 * 将文本字符串转换为目标类型的对象
	 * 
	 * <p>
	 * 通过{@link #converter}将字符串转换为目标描述符指定的类型（如将"123"转换为Integer，"2024-01-01"转换为LocalDate）。
	 * 
	 * @param body             待转换的文本字符串（非空，已按字符集解码）
	 * @param targetDescriptor 目标类型描述符（非空，包含目标类型信息）
	 * @param charset          字符集（非空，与字符串解码一致）
	 * @return 转换后的目标类型对象（符合目标描述符的类型要求）
	 * @throws IOException 转换失败时抛出（如字符串格式与目标类型不兼容）
	 */
	@Override
	protected Object parseObject(String body, TargetDescriptor targetDescriptor, @NonNull Charset charset)
			throws IOException {
		return getConverter().convert(body, TypeDescriptor.forObject(body),
				targetDescriptor.getRequiredTypeDescriptor());
	}

	/**
	 * 将源对象转换为文本字符串
	 * 
	 * <p>
	 * 通过{@link #converter}将源对象转换为字符串（如将Integer转换为"123"，LocalDate转换为"2024-01-01"）。
	 * 
	 * @param body        待转换的源数据（非空，{@link TypedData}包装的任意对象）
	 * @param contentType 媒体类型（非空，通常为text/*）
	 * @param charset     字符集（非空，用于后续字符串编码为字节数组）
	 * @return 转换后的文本字符串（非空）
	 * @throws IOException 转换失败时抛出（如源对象无法转换为字符串）
	 */
	@Override
	protected String toString(TypedData<Object> body, MediaType contentType, @NonNull Charset charset)
			throws IOException {
		return (String) getConverter().convert(body.value(), TypeDescriptor.forObject(body.value()),
				TypeDescriptor.valueOf(String.class));
	}
}