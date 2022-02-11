package io.basc.framework.codec.support;

import io.basc.framework.codec.DecodeException;
import io.basc.framework.codec.EncodeException;
import io.basc.framework.codec.StreamCodec;
import io.basc.framework.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 直接拷贝
 * 
 * @author shuchaowen
 *
 */
public class FastStreamCodec implements StreamCodec {
	/**
	 * 为了节省内存，默认使用的缓冲区较小，所以相对的执行速度也会相对较慢
	 */
	static final int DEFAULT_BUFF_SIZE = Integer.getInteger("codec.stream.buffsize", 256);

	private final int buffSize;

	public FastStreamCodec(int buffSize) {
		this.buffSize = buffSize;
	}

	@Override
	public void encode(InputStream source, OutputStream target) throws IOException, EncodeException {
		IOUtils.write(source, target, buffSize);
	}

	@Override
	public void decode(InputStream source, OutputStream target) throws IOException, DecodeException {
		IOUtils.write(source, target, buffSize);
	}
}
