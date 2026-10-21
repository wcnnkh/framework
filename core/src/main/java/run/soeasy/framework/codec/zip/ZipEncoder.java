package run.soeasy.framework.codec.zip;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.codec.io.BinaryCoder;
import run.soeasy.framework.core.function.ThrowingFunction;
import run.soeasy.framework.io.IOUtils;
import run.soeasy.framework.io.buffer.StreamByteBufferTransporter;

/**
 * ZIP编码器，实现对数据的ZIP压缩（编码）操作，继承自{@link BinaryCoder}和{@link StreamByteBufferTransporter}，
 * 支持指定字符集处理ZIP条目名称，并允许自定义{@link ZipOutputStream}的属性配置，适用于流式ZIP压缩场景。
 * 
 * <p>
 * 提供默认UTF-8字符集实例{@link #UTF_8}，支持对普通输入流进行ZIP压缩或直接处理{@link ZipInputStream}。
 * 
 * @author soeasy.run
 * @see BinaryCoder
 * @see StreamByteBufferTransporter
 * @see ZipOutputStream
 */
@Getter
@RequiredArgsConstructor
public class ZipEncoder implements BinaryCoder, StreamByteBufferTransporter {
	/**
	 * 使用UTF-8字符集的默认ZIP编码器实例
	 */
	public static final ZipEncoder UTF_8 = new ZipEncoder(StandardCharsets.UTF_8);

	@NonNull
	private final ThrowingFunction<? super OutputStream, ? extends ZipOutputStream, ? extends IOException> mapper;

	/**
	 * 构造ZIP编码器（使用默认的属性设置消费者）
	 * 
	 * @param charset 处理ZIP文本信息的字符集
	 */
	public ZipEncoder(@NonNull Charset charset) {
		this((o) -> new ZipOutputStream(o, charset));
	}

	@Override
	public void transfer(@NonNull InputStream input, @NonNull OutputStream output) throws IOException {
		output = output instanceof ZipOutputStream ? IOUtils.ignoreClose(output)
				: mapper.apply(IOUtils.ignoreClose(output));
		try {
			IOUtils.binary().transfer(input, output);
		} finally {
			output.close();
		}
	}
}