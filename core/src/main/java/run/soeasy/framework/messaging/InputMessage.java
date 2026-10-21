package run.soeasy.framework.messaging;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

import org.xml.sax.InputSource;

import run.soeasy.framework.io.source.InputStreamSource;
import run.soeasy.framework.io.source.ReaderSource;

/**
 * 输入消息接口，继承 {@link Message} 和 {@link InputSource} 接口，
 * 表示可读取内容的消息，提供消息内容的缓冲处理和字节体获取能力， 是处理输入消息（如 HTTP 请求消息）的核心抽象。
 *
 * <p>
 * 核心功能：
 * <ul>
 * <li>通过 {@link #buffered()} 创建缓冲包装，支持输入流的重复读取</li>
 * <li>通过 {@link #getBody()} 一次性获取消息体的字节拷贝</li>
 * <li>通过 {@link #getReader()} 自动使用消息头中声明的字符集进行解码（继承自 {@link InputSource}）</li>
 * </ul>
 *
 * @author soeasy.run
 * @see Message
 * @see InputSource
 * @see BufferingInputMessage
 */
public interface InputMessage extends Message, InputStreamSource<InputStream>, ReaderSource<Reader> {

	@Override
	default Reader getReader() throws IOException {
		Charset charset = getCharset();
		return charset == null ? new InputStreamReader(getInputStream())
				: new InputStreamReader(getInputStream(), charset);
	}
}