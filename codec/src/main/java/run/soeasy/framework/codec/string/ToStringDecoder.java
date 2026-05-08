package run.soeasy.framework.codec.string;

import java.io.IOException;
import java.io.Writer;
import java.nio.CharBuffer;

import lombok.NonNull;
import run.soeasy.framework.codec.CodecException;
import run.soeasy.framework.codec.Decoder;
import run.soeasy.framework.core.function.Pipeline;
import run.soeasy.framework.core.function.ThrowingConsumer;
import run.soeasy.framework.io.IOUtils;
import run.soeasy.framework.io.source.WriterFactory;

/**
 * 将数据解码为字符序列的解码器接口
 * 
 * @param <E> 待解码的源数据类型
 */
@FunctionalInterface
public interface ToStringDecoder<E> extends Decoder<E, CharSequence> {

	/**
	 * 将源数据解码为字符序列
	 * 
	 * @param source 待解码的源数据
	 * @return 解码后的字符序列
	 * @throws CodecException 解码过程中发生的异常
	 */
	@Override
	default CharSequence decode(E source) throws CodecException {
		StringBuilder sb = new StringBuilder();
		try {
			decodeToConsumer(source, CharBuffer.allocate(IOUtils.DEFAULT_CHAR_BUFFER_SIZE), sb::append);
		} catch (IOException e) {
			throw new CodecException(e);
		}
		return sb;
	}

	/**
	 * 解码源数据到字符缓冲区，并由指定消费器处理缓冲区
	 * 
	 * @param source   待解码的源数据
	 * @param buffer   字符缓冲区
	 * @param consumer 缓冲区消费器
	 * @param <X>      消费器可能抛出的异常类型
	 * @throws CodecException 解码异常
	 * @throws IOException    IO相关异常
	 * @throws X              消费器抛出的自定义异常
	 */
	<X extends Exception> void decodeToConsumer(E source, @NonNull CharBuffer buffer,
			@NonNull ThrowingConsumer<? super CharSequence, ? extends X> consumer) throws CodecException, IOException, X;

	/**
	 * 解码源数据到Appendable目标
	 * 
	 * @param source 待解码的源数据
	 * @param buffer 字符缓冲区
	 * @param target Appendable目标
	 * @throws CodecException 解码异常
	 * @throws IOException    IO相关异常
	 */
	default void decodeToAppendable(E source, @NonNull CharBuffer buffer, @NonNull Appendable target)
			throws CodecException, IOException {
		decodeToConsumer(source, buffer, target::append);
	}

	/**
	 * 解码源数据到Writer（通过WriterFactory获取Writer管道）
	 * 
	 * @param source        待解码的源数据
	 * @param buffer        字符缓冲区
	 * @param writerFactory Writer工厂
	 * @param <W>           Writer子类类型
	 * @throws CodecException 解码异常
	 * @throws IOException    IO相关异常
	 */
	default <W extends Writer> void decodeToWriter(E source, @NonNull CharBuffer buffer,
			@NonNull WriterFactory<W> writerFactory) throws CodecException, IOException {
		Pipeline<W, IOException> pipeline = writerFactory.getWriterPipeline();
		try {
			decodeToWriter(source, buffer, pipeline.get());
		} finally {
			pipeline.close();
		}
	}

	/**
	 * 解码源数据到Writer实例
	 * 
	 * @param source 待解码的源数据
	 * @param buffer 字符缓冲区
	 * @param writer Writer实例
	 * @throws CodecException 解码异常
	 * @throws IOException    IO相关异常
	 */
	default void decodeToWriter(E source, @NonNull CharBuffer buffer, @NonNull Writer writer)
			throws CodecException, IOException {
		decodeToConsumer(source, buffer, writer::append);
	}
}