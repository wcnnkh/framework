package run.soeasy.framework.messaging.convert.support;

import java.io.IOException;
import java.util.List;
import java.util.Map.Entry;

import lombok.NonNull;
import run.soeasy.framework.core.convert.value.SourceDescriptor;
import run.soeasy.framework.core.convert.value.TargetDescriptor;
import run.soeasy.framework.core.convert.value.TypedValue;
import run.soeasy.framework.io.MimeType;
import run.soeasy.framework.messaging.Headers;
import run.soeasy.framework.messaging.InputMessage;
import run.soeasy.framework.messaging.MediaType;
import run.soeasy.framework.messaging.MediaTypeRegistry;
import run.soeasy.framework.messaging.MediaTypes;
import run.soeasy.framework.messaging.Message;
import run.soeasy.framework.messaging.OutputMessage;
import run.soeasy.framework.messaging.convert.MessageConverter;

/**
 * 消息转换器抽象基类，实现{@link MessageConverter}接口并提供通用功能， 为具体消息转换器实现提供基础骨架，减少重复代码。
 * 
 * <p>
 * 该类通过{@link MediaTypeRegistry}管理支持的媒体类型，
 * 并定义抽象方法{@link #doRead(TargetDescriptor, InputMessage, MimeType)}和
 * {@link #doWrite(TypedValue, OutputMessage, MediaType)}，
 * 由子类实现具体的消息读写逻辑，同时提供消息头复制、媒体类型匹配等通用实现。
 * 
 * @author soeasy.run
 * @see MessageConverter
 * @see MediaTypeRegistry
 */
public abstract class AbstractMessageConverter implements MessageConverter {

	/**
	 * 媒体类型注册表，用于管理当前转换器支持的所有媒体类型（如"application/json"、"text/plain"），
	 * 支持添加、查询、筛选等操作，是判断媒体类型兼容性的核心依据。
	 */
	private final MediaTypeRegistry mediaTypeRegistry = new MediaTypeRegistry();

	public MediaTypeRegistry getMediaTypeRegistry() {
		return mediaTypeRegistry;
	}

	/**
	 * 子类实现此方法完成具体的消息读取和转换逻辑
	 * 
	 * <p>
	 * 该方法将输入消息的内容（如输入流中的字节）转换为目标类型的对象， 具体转换逻辑（如JSON反序列化、XML解析等）由子类实现。
	 * 
	 * @param targetDescriptor 目标类型描述符（包含待转换的类型信息，非空）
	 * @param message          待读取的输入消息（非空，包含消息头和输入流）
	 * @param contentType      实际使用的媒体类型（非空，由{@link #readFrom}确定）
	 * @return 转换后的目标类型对象（非空）
	 * @throws IOException 读取输入流或转换过程中发生I/O错误（如流读取失败、格式错误）
	 */
	protected abstract Object doRead(@NonNull TargetDescriptor targetDescriptor, @NonNull InputMessage message,
			MimeType contentType) throws IOException;

	/**
	 * 子类实现此方法完成具体的消息写入和转换逻辑
	 * 
	 * <p>
	 * 该方法将源对象转换为指定媒体类型的内容（如JSON字符串、XML字节流）， 并写入输出消息的输出流，具体序列化逻辑由子类实现。
	 * 
	 * @param source      待写入的源数据（包含值和类型信息，非空）
	 * @param message     目标输出消息（非空，包含消息头和输出流）
	 * @param contentType 实际使用的媒体类型（非空，由{@link #writeTo}确定）
	 * @throws IOException 写入输出流或转换过程中发生I/O错误（如流写入失败、序列化失败）
	 */
	protected abstract void doWrite(@NonNull TypedValue source, @NonNull OutputMessage message,
			@NonNull MediaType contentType) throws IOException;

	/**
	 * 获取当前转换器支持的所有媒体类型
	 * 
	 * <p>
	 * 直接返回{@link #mediaTypeRegistry}，委托注册表提供支持的媒体类型集合。
	 * 
	 * @return 支持的媒体类型集合（非空，由注册表管理）
	 */
	@Override
	public MediaTypes getSupportedMediaTypes() {
		return mediaTypeRegistry;
	}

	/**
	 * 将输入消息的头信息复制到输出消息中
	 * 
	 * <p>
	 * 复制的信息包括： - Content-Length：内容长度（若输出消息已设置）； - Content-Type：内容类型（从输入消息复制）； -
	 * 其他所有头字段：遍历输入消息的头集合，逐个复制到输出消息。
	 * 
	 * @param inputMessage  源输入消息（非空，提供头信息）
	 * @param outputMessage 目标输出消息（非空，接收头信息）
	 * @throws IOException 复制过程中发生I/O错误（通常不会，仅为兼容方法签名）
	 */
	public static void writeHeader(Message inputMessage, OutputMessage outputMessage) throws IOException {
		long len = outputMessage.getContentLength();
		if (len >= 0) {
			outputMessage.setContentLength(len);
		}

		MediaType mediaType = inputMessage.getContentType();
		if (mediaType != null) {
			outputMessage.setContentType(mediaType);
		}

		Headers headers = inputMessage.getHeaders();
		if (headers != null) {
			for (Entry<String, List<String>> entry : headers.entrySet()) {
				for (String value : entry.getValue()) {
					outputMessage.getHeaders().add(entry.getKey(), value);
				}
			}
		}
	}

	/**
	 * 判断当前转换器是否支持读取指定类型和媒体类型的消息
	 * 
	 * <p>
	 * 判断逻辑： - 若{@code contentType}为null（未指定），默认返回true； -
	 * 否则检查{@code contentType}是否被当前支持的媒体类型（{@link #mediaTypeRegistry}）包含（即支持该类型）。
	 * 
	 * @param targetDescriptor 目标类型描述符（非空）
	 * @param message          待读取的消息（非空）
	 * @param contentType      目标媒体类型（可为null）
	 * @return 支持读取返回true，否则返回false
	 */
	@Override
	public boolean isReadable(@NonNull TargetDescriptor targetDescriptor, @NonNull Message message,
			MimeType contentType) {
		if (contentType == null) {
			return true;
		}

		for (MimeType mimeType : mediaTypeRegistry) {
			if (mimeType.includes(contentType)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 判断当前转换器是否支持写入指定类型和媒体类型的消息
	 * 
	 * <p>
	 * 判断逻辑： -
	 * 若{@code contentType}为null（未指定）或为{@link MediaType#ALL}（*&#47;*），默认返回true； -
	 * 否则检查{@code contentType}是否与当前支持的媒体类型（{@link #mediaTypeRegistry}）兼容。
	 * 
	 * @param sourceDescriptor 源类型描述符（非空）
	 * @param message          目标消息（非空）
	 * @param contentType      目标媒体类型（可为null）
	 * @return 支持写入返回true，否则返回false
	 */
	@Override
	public boolean isWriteable(@NonNull SourceDescriptor sourceDescriptor, @NonNull Message message,
			MimeType contentType) {
		if (contentType == null || MediaType.ALL.equalsTypeAndSubtype(contentType)) {
			return true;
		}

		for (MimeType mimeType : mediaTypeRegistry) {
			if (mimeType.isCompatibleWith(contentType)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 读取输入消息并转换为目标类型对象，委托给{@link #doRead}实现具体逻辑
	 * 
	 * <p>
	 * 步骤： 1.
	 * 确定实际使用的媒体类型（{@code contentTypeToUse}）：若{@code contentType}为null，使用注册表中的第一个媒体类型；
	 * 2. 调用{@link #doRead}执行具体转换，返回结果。
	 * 
	 * @param targetDescriptor 目标类型描述符（非空）
	 * @param message          待读取的输入消息（非空）
	 * @param contentType      目标媒体类型（可为null）
	 * @return 转换后的目标类型对象（由{@link #doRead}返回）
	 * @throws IOException 读取或转换过程中发生I/O错误（由{@link #doRead}抛出）
	 */
	@Override
	public Object readFrom(@NonNull TargetDescriptor targetDescriptor, @NonNull InputMessage message,
			MimeType contentType) throws IOException {
		MimeType contentTypeToUse = contentType;
		if (contentTypeToUse == null) {
			contentTypeToUse = mediaTypeRegistry.first();
		}

		return doRead(targetDescriptor, message, contentTypeToUse);
	}

	/**
	 * 将源对象转换为指定媒体类型并写入输出消息，委托给{@link #doWrite}实现具体逻辑
	 * 
	 * <p>
	 * 步骤： 1.
	 * 确定实际使用的媒体类型（{@code contentTypeToUse}）：若{@code contentType}为null，从注册表中筛选非通配符类型的第一个媒体类型；
	 * 2. 若输出消息未设置Content-Type，将{@code contentTypeToUse}设为其Content-Type； 3.
	 * 调用{@link #doWrite}执行具体写入。
	 * 
	 * @param source      待写入的源数据（非空）
	 * @param message     目标输出消息（非空）
	 * @param contentType 目标媒体类型（可为null）
	 * @throws IOException 写入或转换过程中发生I/O错误（由{@link #doWrite}抛出）
	 */
	@Override
	public void writeTo(@NonNull TypedValue source, @NonNull OutputMessage message, MediaType contentType)
			throws IOException {
		MediaType contentTypeToUse = contentType;
		if (contentTypeToUse == null) {
			// 筛选非通配符类型的媒体类型（优先使用具体类型）
			contentTypeToUse = mediaTypeRegistry.filter((e) -> !e.isWildcardType() && !e.isWildcardSubtype()).first();
		}

		// 若输出消息未设置Content-Type，自动设置
		if (contentTypeToUse != null && message.getContentType() == null) {
			message.setContentType(contentTypeToUse);
		}

		doWrite(source, message, contentTypeToUse);
	}

}