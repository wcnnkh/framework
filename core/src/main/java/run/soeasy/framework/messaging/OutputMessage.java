package run.soeasy.framework.messaging;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;

import lombok.NonNull;
import run.soeasy.framework.io.source.OutputStreamSource;
import run.soeasy.framework.io.source.WriterSource;

/**
 * 可输出消息接口，继承自 {@link Message}，
 * 扩展了消息的写入能力，支持设置消息元数据（内容类型、长度）和输出流操作， 是消息发送场景（如 HTTP 响应、消息队列发送）中表示可写入消息的核心接口。
 *
 * <p>
 * 该接口通过默认方法提供了元数据的便捷设置入口，内容类型、字符集等由调用方 通过 {@link MediaType}
 * 显式构造后设置，接口本身不做隐式合并或转换。
 *
 * @author soeasy.run
 * @see Message
 * @see MediaType
 * @see Headers
 */
public interface OutputMessage extends Message, OutputStreamSource<OutputStream>, WriterSource<Writer> {

	/**
	 * 设置消息的内容类型。
	 *
	 * <p>
	 * 直接将指定的 {@link MediaType} 设置到消息头部，不做任何字符集合并或转换。 如需携带字符集，请使用
	 * {@link MediaType#withCharset(String)} 构造。
	 *
	 * @param contentType 消息的内容类型（非空）
	 */
	default void setContentType(@NonNull MediaType contentType) {
		getHeaders().setContentType(contentType);
	}

	/**
	 * 设置消息内容的长度（字节数）。
	 *
	 * <p>
	 * 将内容长度设置到消息头部，通常在消息体写入完成后调用，用于接收方验证消息完整性。
	 *
	 * @param contentLength 消息内容的长度（非负整数）
	 */
	default void setContentLength(long contentLength) {
		getHeaders().setContentLength(contentLength);
	}

	@Override
	default Writer getWriter() throws IOException {
		Charset charset = getCharset();
		return charset == null ? new OutputStreamWriter(getOutputStream())
				: new OutputStreamWriter(getOutputStream(), charset);
	}
}