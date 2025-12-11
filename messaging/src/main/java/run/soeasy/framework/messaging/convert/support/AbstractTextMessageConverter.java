package run.soeasy.framework.messaging.convert.support;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import run.soeasy.framework.core.convert.value.TargetDescriptor;
import run.soeasy.framework.core.convert.value.TypedData;
import run.soeasy.framework.io.MimeType;
import run.soeasy.framework.messaging.MediaType;
import run.soeasy.framework.messaging.Message;
import run.soeasy.framework.messaging.OutputMessage;

/**
 * 文本消息转换器抽象基类，继承自{@link AbstractBinaryMessageConverter}，
 * 专注于文本类型消息的转换（如字符串、JSON、XML等），核心处理字符集（{@link Charset}）的解析与应用，
 * 将二进制数据与文本之间的转换逻辑标准化，简化文本类转换器的实现。
 * 
 * <p>
 * 核心流程： - 读取时：将字节数组按指定字符集转为字符串，再委托子类解析为目标对象； -
 * 写入时：将目标对象按指定字符集转为字符串，再编码为字节数组写入消息； - 字符集优先级：消息中指定的字符集 &gt;
 * 媒体类型（{@link MediaType}）中包含的字符集 &gt; 默认字符集（UTF-8）。
 * 
 * @param <T> 支持的文本转换目标类型（如String、JsonObject等）
 * @author soeasy.run
 * @see AbstractBinaryMessageConverter
 * @see Charset
 * @see StandardCharsets#UTF_8
 */
@Getter
@Setter
public abstract class AbstractTextMessageConverter<T> extends AbstractBinaryMessageConverter<T> {

	/**
	 * 默认字符集，当消息和媒体类型中未指定字符集时使用（非空），
	 * 默认值为{@link StandardCharsets#UTF_8}，可通过setter方法自定义（如GBK、ISO-8859-1）。
	 */
	@NonNull
	private Charset defaultCharset = StandardCharsets.UTF_8;

	/**
	 * 初始化文本消息转换器，指定支持的目标类型
	 * 
	 * @param requriedType 目标对象类型（非空，如String.class、JsonObject.class）
	 */
	public AbstractTextMessageConverter(@NonNull Class<T> requriedType) {
		super(requriedType);
	}

	/**
	 * 获取实际使用的字符集
	 * 
	 * <p>
	 * 字符集优先级： 1. 消息中自带的字符集（{@link Message#getCharset()}）； 2.
	 * 支持的媒体类型（{@link #getMediaTypeRegistry()}）中与目标类型兼容且包含字符集的； 3.
	 * 默认字符集（{@link #defaultCharset}）。
	 * 
	 * @param contentType 目标媒体类型（可为null，用于筛选兼容的媒体类型）
	 * @param message     关联的消息（非空，可能包含字符集信息）
	 * @return 实际使用的字符集（非空，最终 fallback 到默认字符集）
	 */
	protected final Charset getCharset(MimeType contentType, Message message) {
		// 优先使用消息中指定的字符集
		Charset charset = message.getCharset();
		if (charset == null) {
			// 从支持的媒体类型中查找与contentType兼容且包含字符集的
			Charset mediaTypeCharset = getMediaTypeRegistry().map((e) -> e.getCharset()).first();
			if (mediaTypeCharset != null) {
				return mediaTypeCharset;
			}
		}
		// 最终使用默认字符集
		return getDefaultCharset();
	}

	/**
	 * 重写二进制解析逻辑，将字节数组转为字符串后再解析为目标对象
	 * 
	 * <p>
	 * 步骤： 1. 调用{@link #getCharset(MimeType, Message)}获取字符集； 2. 将字节数组按字符集解码为字符串； 3.
	 * 调用{@link #parseObject(String, TargetDescriptor, Charset)}由子类解析字符串为目标对象。
	 * 
	 * @param body             消息体的字节数组（非空）
	 * @param targetDescriptor 目标类型描述符（非空）
	 * @param message          关联的消息（非空）
	 * @param contentType      媒体类型（非空）
	 * @return 解析后的目标对象（由子类实现返回）
	 * @throws IOException 字符集解码或字符串解析失败时抛出
	 */
	@Override
	protected T parseObject(byte[] body, @NonNull TargetDescriptor targetDescriptor, @NonNull Message message,
			MimeType contentType) throws IOException {
		Charset charset = getCharset(contentType, message);
		// 确保字符集不为null（fallback到默认）
		if (charset == null) {
			charset = getDefaultCharset();
		}
		// 字节数组转字符串
		String text = new String(body, charset);
		// 子类解析字符串
		return parseObject(text, targetDescriptor, charset);
	}

	/**
	 * 重写二进制序列化逻辑，将目标对象转为字符串后再编码为字节数组
	 * 
	 * <p>
	 * 步骤： 1. 调用{@link #getCharset(MimeType, Message)}获取字符集； 2.
	 * 调用{@link #toString(TypedData, MediaType, Charset)}由子类将对象转为字符串； 3.
	 * 将字符串按字符集编码为字节数组。
	 * 
	 * @param body      待转换的目标对象数据（非空）
	 * @param message   关联的消息（非空）
	 * @param mediaType 媒体类型（非空）
	 * @return 字符串编码后的字节数组（非空）
	 * @throws IOException 对象转字符串或字符集编码失败时抛出
	 */
	@Override
	protected byte[] toBinary(@NonNull TypedData<T> body, @NonNull Message message, MediaType mediaType)
			throws IOException {
		Charset charset = getCharset(mediaType, message);
		// 确保字符集不为null（fallback到默认）
		if (charset == null) {
			charset = getDefaultCharset();
		}
		// 子类将对象转为字符串
		String text = toString(body, mediaType, charset);
		// 字符串转字节数组
		return text.getBytes(charset);
	}

	/**
	 * 重写消息写入逻辑，确保输出消息的Content-Type包含字符集信息
	 * 
	 * <p>
	 * 扩展父类逻辑： - 若媒体类型未指定字符集，且非通配符类型，则为其添加当前字符集； -
	 * 确保输出消息的Content-Type包含字符集，便于接收方正确解码。
	 * 
	 * @param data        待写入的目标对象数据（非空）
	 * @param message     目标输出消息（非空）
	 * @param contentType 媒体类型（非空）
	 * @throws IOException 写入过程中发生I/O错误时抛出
	 */
	@Override
	protected void writeObject(@NonNull TypedData<T> data, @NonNull OutputMessage message,
			@NonNull MediaType contentType) throws IOException {
		MediaType contentTypeToUse = contentType;
		Charset charset = getCharset(contentType, message);
		// 若媒体类型未指定字符集，且为具体类型（非通配符），则补充字符集
		if (contentTypeToUse.getCharset() == null) {
			if (!contentTypeToUse.isWildcardType() && !contentTypeToUse.isWildcardSubtype()) {
				contentTypeToUse = new MediaType(contentTypeToUse, charset);
			}
		}
		// 设置消息的Content-Type（包含字符集）
		if (message.getContentType() == null) {
			message.setContentType(contentTypeToUse);
		}
		// 调用父类写入字节数组
		super.writeObject(data, message, contentTypeToUse);
	}

	/**
	 * 子类实现此方法，将字符串解析为目标类型对象
	 * 
	 * <p>
	 * 子类需处理具体的文本解析逻辑，例如： - 将JSON字符串解析为Java对象（如new Gson().fromJson(text, type)）； -
	 * 将CSV字符串解析为列表（如CSVParser.parse(text).getRecords()）。
	 * 
	 * @param body             待解析的字符串（非空，已按字符集解码）
	 * @param targetDescriptor 目标类型描述符（非空，包含类型信息）
	 * @param charset          解析使用的字符集（非空，与字符串解码一致）
	 * @return 解析后的目标对象（非空）
	 * @throws IOException 字符串格式错误或解析失败时抛出
	 */
	protected abstract T parseObject(String body, TargetDescriptor targetDescriptor, @NonNull Charset charset)
			throws IOException;

	/**
	 * 子类实现此方法，将目标对象转换为字符串
	 * 
	 * <p>
	 * 子类需处理具体的对象序列化逻辑，例如： - 将Java对象序列化为JSON字符串（如new
	 * Gson().toJson(body.getValue())）； -
	 * 将数字转为字符串（如String.valueOf(body.getValue())）。
	 * 
	 * @param body        待转换的目标对象数据（非空）
	 * @param contentType 媒体类型（非空，可能影响序列化格式）
	 * @param charset     字符串编码使用的字符集（非空，需与后续getBytes保持一致）
	 * @return 序列化后的字符串（非空）
	 * @throws IOException 对象序列化失败时抛出
	 */
	protected abstract String toString(TypedData<T> body, MediaType contentType, @NonNull Charset charset)
			throws IOException;
}