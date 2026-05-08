package run.soeasy.framework.codec.string;

import java.io.IOException;
import java.io.Reader;
import java.nio.CharBuffer;

import lombok.NonNull;
import run.soeasy.framework.codec.CodecException;
import run.soeasy.framework.codec.Encoder;
import run.soeasy.framework.core.function.Pipeline;
import run.soeasy.framework.io.IOUtils;
import run.soeasy.framework.io.source.ReaderFactory;

/**
 * 将字符序列相关源数据编码为目标类型的编码器接口
 * 
 * @param <E> 编码后的目标数据类型
 */
public interface FromStringEncoder<E> extends Encoder<CharSequence, E> {

	/**
	 * 从Readable源编码为目标数据类型
	 * 
	 * @param readableSource Readable类型的源数据
	 * @param buffer         字符缓冲区
	 * @return 编码后的目标数据
	 * @throws IOException    IO相关异常
	 * @throws CodecException 编码异常
	 */
	E encodeFromReadable(@NonNull Readable readableSource, @NonNull CharBuffer buffer)
			throws IOException, CodecException;

	/**
	 * 从Reader工厂编码为目标数据类型
	 * 
	 * @param readerFactory Reader工厂
	 * @param buffer        字符缓冲区
	 * @param <R>           Reader子类类型
	 * @return 编码后的目标数据
	 * @throws IOException    IO相关异常
	 * @throws CodecException 编码异常
	 */
	default <R extends Reader> E encodeFromReader(@NonNull ReaderFactory<R> readerFactory,
			@NonNull CharBuffer buffer) throws IOException, CodecException {
		Pipeline<R, IOException> pipeline = readerFactory.getReaderPipeline();
		try {
			return encodeFromReadable(pipeline.get(), buffer);
		} finally {
			pipeline.close();
		}
	}

	/**
	 * 从字符序列编码为目标数据类型
	 * 
	 * @param charSequenceSource 字符序列类型的源数据
	 * @return 编码后的目标数据
	 * @throws CodecException 编码异常
	 */
	@Override
	default E encode(@NonNull CharSequence charSequenceSource) throws CodecException {
		try {
			return encodeFromReadable(CharBuffer.wrap(charSequenceSource),
					CharBuffer.allocate(IOUtils.DEFAULT_CHAR_BUFFER_SIZE));
		} catch (IOException e) {
			throw new CodecException(e);
		}
	}
}