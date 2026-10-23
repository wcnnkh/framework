package run.soeasy.framework.messaging;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

import run.soeasy.framework.io.source.InputStreamSource;
import run.soeasy.framework.io.source.ReaderSource;

/**
 * 输入消息接口，继承 {@link Message} 和 {@link InputStreamSource} 接口，
 * 表示可读取内容的消息，提供消息内容的缓冲处理和字节体获取能力， 是处理输入消息（如 HTTP 请求消息）的核心抽象。
 *
 * @author soeasy.run
 * @see Message
 * @see InputStreamSource
 */
public interface InputMessage extends Message, InputStreamSource<InputStream>, ReaderSource<Reader> {

	@Override
	default Reader getReader() throws IOException {
		Charset charset = getCharset();
		return charset == null ? new InputStreamReader(getInputStream())
				: new InputStreamReader(getInputStream(), charset);
	}
}