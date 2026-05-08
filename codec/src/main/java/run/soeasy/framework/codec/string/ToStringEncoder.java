package run.soeasy.framework.codec.string;

import java.io.IOException;
import java.io.Writer;
import java.nio.CharBuffer;

import lombok.NonNull;
import run.soeasy.framework.codec.CodecException;
import run.soeasy.framework.codec.Encoder;
import run.soeasy.framework.core.function.Pipeline;
import run.soeasy.framework.core.function.ThrowingConsumer;
import run.soeasy.framework.io.IOUtils;
import run.soeasy.framework.io.source.WriterFactory;

/**
 * 将数据编码为字符序列的编码器接口
 * 
 * @author soeasy.run
 * @param <D> 待编码的源数据类型
 * 
 */
public interface ToStringEncoder<D> extends Encoder<D, CharSequence> {

	/**
	 * 将源数据编码为字符序列
	 * 
	 * @param source 待编码的源数据
	 * @return 编码后的字符序列
	 * @throws CodecException 编码过程中发生的异常
	 */
	@Override
	default CharSequence encode(D source) throws CodecException {
		StringBuilder sb = new StringBuilder();
		try {
			encodeToConsumer(source, CharBuffer.allocate(IOUtils.DEFAULT_CHAR_BUFFER_SIZE), sb::append);
		} catch (IOException e) {
			throw new CodecException(e);
		}
		return sb;
	}

	/**
	 * 编码源数据到字符缓冲区，并由指定消费器处理缓冲区
	 * 
	 * @param source   待编码的源数据
	 * @param buffer   字符缓冲区
	 * @param consumer 缓冲区消费器
	 * @param <X>      消费器可能抛出的异常类型
	 * @throws CodecException 编码异常
	 * @throws IOException    IO相关异常
	 * @throws X              消费器抛出的自定义异常
	 */
	<X extends Exception> void encodeToConsumer(D source, @NonNull CharBuffer buffer,
			@NonNull ThrowingConsumer<? super CharSequence, ? extends X> consumer) throws CodecException, IOException, X;
	
	/**
	 * 编码源数据到Appendable目标
	 * 
	 * @param source 待编码的源数据
	 * @param buffer 字符缓冲区
	 * @param target Appendable目标
	 * @throws CodecException 编码异常
	 * @throws IOException    IO相关异常
	 */
	default void encodeToAppendable(D source, @NonNull CharBuffer buffer, @NonNull Appendable target)
			throws CodecException, IOException {
		encodeToConsumer(source, buffer, target::append);
	}

	/**
	 * 编码源数据到Writer（通过WriterFactory获取Writer管道）
	 * 
	 * @param source        待编码的源数据
	 * @param buffer        字符缓冲区
	 * @param writerFactory Writer工厂
	 * @param <W>           Writer子类类型
	 * @throws CodecException 编码异常
	 * @throws IOException    IO相关异常
	 */
	default <W extends Writer> void encodeToWriter(D source, @NonNull CharBuffer buffer,
			@NonNull WriterFactory<W> writerFactory) throws CodecException, IOException {
		Pipeline<W, IOException> pipeline = writerFactory.getWriterPipeline();
		try {
			encodeToWriter(source, buffer, pipeline.get());
		} finally {
			pipeline.close();
		}
	}

	/**
	 * 编码源数据到Writer实例
	 * 
	 * @param source 待编码的源数据
	 * @param buffer 字符缓冲区
	 * @param writer Writer实例
	 * @throws CodecException 编码异常
	 * @throws IOException    IO相关异常
	 */
	default void encodeToWriter(D source, @NonNull CharBuffer buffer, @NonNull Writer writer)
			throws CodecException, IOException {
		encodeToConsumer(source, buffer, writer::append);
	}
}