package run.soeasy.framework.codec.string;

import java.io.IOException;
import java.io.Reader;
import java.nio.CharBuffer;

import lombok.NonNull;
import run.soeasy.framework.codec.CodecException;
import run.soeasy.framework.codec.Decoder;
import run.soeasy.framework.core.function.Pipeline;
import run.soeasy.framework.io.source.ReaderFactory;

/**
 * 将字符序列解码为目标数据类型的解码器接口
 * 
 * @param <D> 解码后的目标数据类型
 */
@FunctionalInterface
public interface FromStringDecoder<D> extends Decoder<CharSequence, D> {

	/**
	 * 从Readable源解码为目标数据类型
	 * 
	 * @param readableSource Readable类型的源数据
	 * @param buffer         字符缓冲区
	 * @return 解码后的目标数据
	 * @throws CodecException 解码异常
	 * @throws IOException    IO相关异常
	 */
	D decodeFromReadable(@NonNull Readable readableSource, @NonNull CharBuffer buffer)
			throws CodecException, IOException;

	/**
	 * 从Reader工厂解码为目标数据类型
	 * 
	 * @param readerFactory Reader工厂
	 * @param buffer        字符缓冲区
	 * @param <R>           Reader子类类型
	 * @return 解码后的目标数据
	 * @throws CodecException 解码异常
	 * @throws IOException    IO相关异常
	 */
	default <R extends Reader> D decodeFromReaderFactory(@NonNull ReaderFactory<R> readerFactory,
			@NonNull CharBuffer buffer) throws CodecException, IOException {
		Pipeline<R, IOException> pipeline = readerFactory.getReaderPipeline();
		try {
			return decodeFromReadable(pipeline.get(), buffer);
		} finally {
			pipeline.close();
		}
	}

	/**
	 * 从字符序列解码为目标数据类型
	 * 
	 * @param charSequenceSource 字符序列类型的源数据
	 * @return 解码后的目标数据
	 * @throws CodecException 解码异常
	 */
	@Override
	default D decode(@NonNull CharSequence charSequenceSource) throws CodecException {
		try {
			return decodeFromReadable(CharBuffer.wrap(charSequenceSource),
					CharBuffer.allocate(charSequenceSource.length()));
		} catch (IOException e) {
			throw new CodecException(e);
		}
	}
}