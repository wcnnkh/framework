package run.soeasy.framework.io;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.CharArrayWriter;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.Buffer;
import java.nio.CharBuffer;
import java.util.Arrays;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.Stream;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import run.soeasy.framework.core.collection.CollectionUtils;

/**
 * IO工具类，提供流操作、缓冲区处理、资源关闭等功能。
 * <p>
 * 封装常见IO操作，支持输入输出流转换、按分隔符分割读取、缓冲区数据处理等功能， 采用工具类模式设计，所有方法均为静态方法，避免实例化。
 * </p>
 */
@UtilityClass
public final class IOUtils {
	public static final int DEFAULT_BYTE_BUFFER_SIZE = 1024 * 4;
	public static final int DEFAULT_CHAR_BUFFER_SIZE = 512;

	private static final byte[] EMPTY_CONTENT = new byte[0];

	private final static InputStream EMPTY_INPUT_STREAM = new ByteArrayInputStream(EMPTY_CONTENT);

	private static final int EOF = -1;

	/**
	 * 关闭多个可关闭资源，支持空值忽略和异常抛出。
	 * <p>
	 * 利用CollectionUtils.acceptAll实现遍历，自动跳过null资源， 遇到异常时会中断后续关闭操作并抛出异常。
	 * </p>
	 * 
	 * @param closeables 待关闭的资源数组（非空）
	 * @throws IOException 关闭资源时抛出的异常
	 */
	public static void close(@NonNull Closeable... closeables) throws IOException {
		CollectionUtils.acceptAll(Arrays.asList(closeables), (e) -> {
			if (e == null) {
				return;
			}
			e.close();
		});
	}

	/**
	 * 静默关闭多个可关闭资源，忽略所有异常。
	 * 
	 * @param closeables 待关闭的资源数组（非空）
	 */
	public static void closeQuietly(@NonNull Closeable... closeables) {
		for (Closeable closeable : closeables) {
			if (closeable == null) {
				continue;
			}

			try {
				closeable.close();
			} catch (final IOException e) {
				// 忽略异常
			}
		}
	}

	/**
	 * 返回空输入流单例。
	 * 
	 * @return 空输入流实例
	 */
	public static InputStream emptyInput() {
		return EMPTY_INPUT_STREAM;
	}

	/**
	 * 返回空输出流单例。
	 * 
	 * @return 空输出流实例
	 */
	public static OutputStream nullOutput() {
		return NullOutputStream.NULL_OUTPUT_STREAM;
	}

	/**
	 * 处理具有底层数组的缓冲区数据（内部工具方法）。
	 * 
	 * @param <B>            缓冲区类型，需继承Buffer
	 * @param <A>            数据数组类型
	 * @param <E>            异常类型
	 * @param buffer         源缓冲区
	 * @param length         处理数据长度
	 * @param arrayReader    读取缓冲区数组的函数
	 * @param bufferConsumer 数据处理消费者
	 * @throws E 处理数据时抛出的异常
	 */
	private static <B extends Buffer, A, E extends Throwable> void processArrayBuffer(B buffer, int length,
			Function<? super B, ? extends A> arrayReader,
			@NonNull BufferConsumer<? super A, ? extends E> bufferConsumer) throws E {
		A array = arrayReader.apply(buffer);
		int offset = buffer.arrayOffset() + buffer.position();

		bufferConsumer.accept(array, offset, length);
		buffer.position(buffer.position() + length);
	}

	/**
	 * 将Reader转换为行流，自动关闭缓冲读取器。
	 * 
	 * @param reader 输入读取器
	 * @return 行流实例
	 */
	public static Stream<String> readLines(Reader reader) {
		BufferedReader bufferedReader = new BufferedReader(reader);
		return readLines(bufferedReader).onClose(() -> closeQuietly(bufferedReader));
	}

	/**
	 * 按指定分隔符分割可读源内容（使用自定义缓冲区）。
	 * 
	 * @param source    可读源（非空）
	 * @param buffer    字符缓冲区（非空）
	 * @param delimiter 分隔符（非空）
	 * @return 分割后的字符序列流
	 */
	public static Stream<CharSequence> split(@NonNull Readable source, @NonNull CharBuffer buffer,
			@NonNull CharSequence delimiter) {
		SplitReadableIterator iterator = new SplitReadableIterator(source, buffer, delimiter);
		return CollectionUtils.unknownSizeStream(iterator);
	}

	/**
	 * 按指定分隔符分割可读源内容（使用默认缓冲区）。
	 * 
	 * @param source    可读源
	 * @param delimiter 分隔符
	 * @return 分割后的字符序列流
	 */
	public static Stream<CharSequence> split(Readable source, CharSequence delimiter) {
		return split(source, CharBuffer.allocate(DEFAULT_CHAR_BUFFER_SIZE), delimiter);
	}

	/**
	 * 将缓冲区剩余数据转换为指定类型数组（支持底层数组优化）。
	 * 
	 * @param <B>          缓冲区类型
	 * @param <A>          目标数组类型
	 * @param buffer       源缓冲区（非空）
	 * @param arrayReader  读取缓冲区数组的函数
	 * @param arrayCopyer  复制缓冲区数据的函数
	 * @param arrayCreator 数组创建函数
	 * @return 包含缓冲区数据的新数组
	 */
	public static <B extends Buffer, A> A toArray(@NonNull B buffer, Function<? super B, ? extends A> arrayReader,
			BiConsumer<? super B, ? super A> arrayCopyer, IntFunction<? extends A> arrayCreator) {
		int length = buffer.remaining();
		A newArray = arrayCreator.apply(length);
		if (buffer.hasArray()) {
			processArrayBuffer(buffer, length, arrayReader, (array, offset, len) -> {
				System.arraycopy(array, offset, newArray, 0, len);
			});
		} else {
			arrayCopyer.accept(buffer, newArray);
		}
		return newArray;
	}

	/**
	 * 将输入流内容转换为字节数组。
	 * <p>
	 * 内部缓冲输入流，无需使用BufferedInputStream， 注意：不适合处理超大输入流，可能导致内存溢出。
	 * </p>
	 * 
	 * @param input 输入流
	 * @return 字节数组
	 * @throws NullPointerException 输入流为null时抛出
	 * @throws IOException          读取流时发生异常
	 */
	public static byte[] toByteArray(InputStream input) throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		transferTo(input, output::write);
		return output.toByteArray();
	}

	/**
	 * 将可读源内容转换为字符数组。
	 * 
	 * @param readable 可读源
	 * @return 字符数组
	 * @throws IOException 读取时发生异常
	 */
	public static char[] toCharArray(Readable readable) throws IOException {
		CharArrayWriter sw = new CharArrayWriter();
		transferTo(readable, sw::write);
		return sw.toCharArray();
	}

	/**
	 * 将可读源内容转换为字符序列。
	 * 
	 * @param readable 可读源（非空）
	 * @return 字符序列（StringBuilder实例）
	 * @throws IOException 读取时发生异常
	 */
	public static CharSequence toCharSequence(@NonNull Readable readable) throws IOException {
		StringBuilder builder = new StringBuilder();
		transferTo(readable, builder::append);
		return builder;
	}

	/**
	 * 分批次传输缓冲区数据（支持自定义批次大小）。
	 * 
	 * @param <B>            缓冲区类型
	 * @param <A>            数组类型
	 * @param <E>            异常类型
	 * @param buffer         源缓冲区（非空）
	 * @param array          目标数组（非空）
	 * @param arrayLength    批次长度（需为正数）
	 * @param arrayCopyer    复制缓冲区数据的函数
	 * @param bufferConsumer 数据处理消费者
	 * @return 传输的总数据量
	 * @throws E                        处理数据时抛出的异常
	 * @throws IllegalArgumentException 当arrayLength非正时时抛出
	 */
	public static <B extends Buffer, A, E extends Throwable> long transferTo(@NonNull B buffer, @NonNull A array,
			int arrayLength, BiConsumer<? super B, ? super A> arrayCopyer,
			@NonNull BufferConsumer<? super A, ? extends E> bufferConsumer) throws E {
		if (arrayLength <= 0) {
			throw new IllegalArgumentException("Array length must be positive");
		}

		long total = 0;
		int remaining = buffer.remaining();

		while (remaining > 0) {
			int chunkSize = Math.min(remaining, arrayLength);
			int originalPosition = buffer.position();
			buffer.limit(originalPosition + chunkSize);
			arrayCopyer.accept(buffer, array);
			buffer.limit(buffer.capacity());
			bufferConsumer.accept(array, 0, chunkSize);
			total += chunkSize;
			buffer.position(originalPosition + chunkSize);
			remaining -= chunkSize;
		}

		return total;
	}

	/**
	 * 传输缓冲区数据（支持底层数组和直接缓冲区）。
	 * 
	 * @param <B>                      缓冲区类型
	 * @param <A>                      数组类型
	 * @param <E>                      异常类型
	 * @param buffer                   源缓冲区（非空）
	 * @param arrayReader              读取缓冲区数组的函数
	 * @param directBufferArrayCreator 直接缓冲区数组创建函数
	 * @param directBufferCopyer       直接缓冲区数据复制函数
	 * @param bufferConsumer           数据处理消费者
	 * @return 传输的数据量
	 * @throws E 处理数据时抛出的异常
	 */
	public static <B extends Buffer, A, E extends Throwable> int transferTo(@NonNull B buffer,
			@NonNull Function<? super B, ? extends A> arrayReader, IntFunction<? extends A> directBufferArrayCreator,
			BiConsumer<? super B, ? super A> directBufferCopyer,
			@NonNull BufferConsumer<? super A, ? extends E> bufferConsumer) throws E {
		int length = buffer.remaining();
		if (length == 0) {
			return 0;
		}

		if (buffer.hasArray()) {
			processArrayBuffer(buffer, length, arrayReader, bufferConsumer);
		} else {
			A array = directBufferArrayCreator.apply(length);
			directBufferCopyer.accept(buffer, array);
			bufferConsumer.accept(array, 0, length);
		}
		return length;
	}

	/**
	 * 传输输入流数据（使用默认缓冲区大小）。
	 * 
	 * @param <E>            异常类型
	 * @param input          输入流
	 * @param bufferConsumer 数据处理消费者
	 * @return 传输的总字节数
	 * @throws E           处理数据时抛出的异常
	 * @throws IOException 读取流时发生异常
	 */
	public static <E extends Throwable> long transferTo(InputStream input,
			BufferConsumer<? super byte[], ? extends E> bufferConsumer) throws E, IOException {
		return transferTo(input, DEFAULT_BYTE_BUFFER_SIZE, bufferConsumer);
	}

	/**
	 * 传输输入流数据（使用自定义缓冲区）。
	 * 
	 * @param <E>            异常类型
	 * @param input          输入流（非空）
	 * @param buffer         字节缓冲区（非空）
	 * @param bufferConsumer 数据处理消费者
	 * @return 传输的总字节数
	 * @throws E           处理数据时抛出的异常
	 * @throws IOException 读取流时发生异常
	 */
	public static <E extends Throwable> long transferTo(@NonNull InputStream input, byte[] buffer,
			@NonNull BufferConsumer<? super byte[], ? extends E> bufferConsumer) throws E, IOException {
		long count = 0;
		int n = 0;
		while (EOF != (n = input.read(buffer))) {
			bufferConsumer.accept(buffer, 0, n);
			count += n;
		}
		return count;
	}

	/**
	 * 传输输入流数据（使用自定义缓冲区大小）。
	 * 
	 * @param <E>            异常类型
	 * @param input          输入流
	 * @param bufferSize     缓冲区大小
	 * @param bufferConsumer 数据处理消费者
	 * @return 传输的总字节数
	 * @throws E           处理数据时抛出的异常
	 * @throws IOException 读取流时发生异常
	 */
	public static <E extends Throwable> long transferTo(InputStream input, int bufferSize,
			BufferConsumer<? super byte[], ? extends E> bufferConsumer) throws E, IOException {
		return transferTo(input, new byte[bufferSize], bufferConsumer);
	}

	/**
	 * 传输可读源数据（使用默认缓冲区大小）。
	 * 
	 * @param <E>            异常类型
	 * @param readable       可读源（非空）
	 * @param bufferConsumer 数据处理消费者
	 * @return 传输的总字符数
	 * @throws E           处理数据时抛出的异常
	 * @throws IOException 读取时发生异常
	 */
	public static <E extends Throwable> long transferTo(@NonNull Readable readable,
			@NonNull BufferConsumer<? super char[], ? extends E> bufferConsumer) throws E, IOException {
		return transferTo(readable, DEFAULT_CHAR_BUFFER_SIZE, bufferConsumer);
	}

	/**
	 * 将Readable数据源中的数据分批读取到CharBuffer，并传递给消费者处理。
	 * <p>
	 * 持续从Readable读取数据至缓冲区，处理后压缩缓冲区以便后续读取， 适用于大文本流处理，避免一次性加载全部数据到内存。
	 * </p>
	 * 
	 * @param <E>            异常类型
	 * @param readable       可读源（非空）
	 * @param buffer         字符缓冲区（非空）
	 * @param bufferConsumer 数据处理消费者
	 * @return 传输的总字符数
	 * @throws E           处理数据时抛出的异常
	 * @throws IOException 读取时发生异常
	 */
	public static <E extends Throwable> long transferTo(@NonNull Readable readable, @NonNull CharBuffer buffer,
			@NonNull BufferConsumer<? super char[], ? extends E> bufferConsumer) throws E, IOException {
		long total = 0;

		while (readable.read(buffer) != -1) {
			buffer.flip();
			if (buffer.remaining() == 0) {
				buffer.clear();
				continue;
			}
			total += transferTo(buffer, CharBuffer::array, char[]::new, CharBuffer::get, bufferConsumer);
			buffer.compact();
		}

		if (buffer.position() > 0) {
			buffer.flip();
			if (buffer.remaining() > 0) {
				total += transferTo(buffer, CharBuffer::array, char[]::new, CharBuffer::get, bufferConsumer);
			}
			buffer.clear();
		}

		return total;
	}

	/**
	 * 传输可读源数据（使用自定义缓冲区大小）。
	 * 
	 * @param <E>            异常类型
	 * @param readable       可读源（非空）
	 * @param bufferSize     缓冲区大小
	 * @param bufferConsumer 数据处理消费者
	 * @return 传输的总字符数
	 * @throws E           处理数据时抛出的异常
	 * @throws IOException 读取时发生异常
	 */
	public static <E extends Throwable> long transferTo(@NonNull Readable readable, int bufferSize,
			@NonNull BufferConsumer<? super char[], ? extends E> bufferConsumer) throws E, IOException {
		return transferTo(readable, CharBuffer.allocate(bufferSize), bufferConsumer);
	}

	/**
	 * 传输读取器数据（使用默认缓冲区大小）。
	 * 
	 * @param <E>            异常类型
	 * @param input          读取器（非空）
	 * @param bufferConsumer 数据处理消费者
	 * @return 传输的总字符数
	 * @throws E           处理数据时抛出的异常
	 * @throws IOException 读取时发生异常
	 */
	public static <E extends Throwable> long transferTo(@NonNull Reader input,
			@NonNull BufferConsumer<? super char[], ? extends E> bufferConsumer) throws E, IOException {
		return transferTo(input, DEFAULT_CHAR_BUFFER_SIZE, bufferConsumer);
	}

	/**
	 * 传输读取器数据（使用自定义缓冲区）。
	 * 
	 * @param <E>            异常类型
	 * @param input          读取器（非空）
	 * @param buffer         字符缓冲区（非空）
	 * @param bufferConsumer 数据处理消费者
	 * @return 传输的总字符数
	 * @throws E           处理数据时抛出的异常
	 * @throws IOException 读取时发生异常
	 */
	public static <E extends Throwable> long transferTo(@NonNull Reader input, char[] buffer,
			@NonNull BufferConsumer<? super char[], ? extends E> bufferConsumer) throws E, IOException {
		long count = 0;
		int n = 0;
		while (EOF != (n = input.read(buffer))) {
			bufferConsumer.accept(buffer, 0, n);
			count += n;
		}
		return count;
	}

	/**
	 * 传输读取器数据（使用自定义缓冲区大小）。
	 * 
	 * @param <E>            异常类型
	 * @param input          读取器（非空）
	 * @param bufferSize     缓冲区大小
	 * @param bufferConsumer 数据处理消费者
	 * @return 传输的总字符数
	 * @throws E           处理数据时抛出的异常
	 * @throws IOException 读取时发生异常
	 */
	public static <E extends Throwable> long transferTo(@NonNull Reader input, int bufferSize,
			@NonNull BufferConsumer<? super char[], ? extends E> bufferConsumer) throws E, IOException {
		return transferTo(input, new char[bufferSize], bufferConsumer);
	}
}