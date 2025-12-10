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
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import run.soeasy.framework.core.collection.CollectionUtils;

/**
 * IO操作工具类，提供流处理、缓冲区操作、资源管理等核心IO功能。 该类封装了Java
 * IO/NIO的底层操作，提供统一接口实现流复制、资源关闭、数据转换等常用功能， 采用工具类设计模式，所有方法均为静态方法，避免实例化。
 *
 * <p>
 * <b>核心特性：</b>
 * <ul>
 * <li>资源管理：安全关闭可关闭资源，支持异常处理和静默关闭</li>
 * <li>流操作：高效实现输入输出流的数据传输和转换</li>
 * <li>缓冲区处理：支持字节/字符缓冲区的批量操作和底层数组优化</li>
 * <li>数据转换：提供流、缓冲区与数组之间的高效转换方法</li>
 * <li>分隔处理：支持按分隔符分割可读源内容并生成流</li>
 * </ul>
 *
 * <p>
 * <b>使用场景：</b>
 * <ul>
 * <li>资源管理：统一处理IO资源的打开和关闭，避免资源泄漏</li>
 * <li>大数据处理：通过缓冲区消费者模式处理大文件，避免内存溢出</li>
 * <li>流操作：实现文件复制、网络数据传输等流处理场景</li>
 * <li>数据转换：实现字节流与字符流、缓冲区与数组之间的类型转换</li>
 * <li>文本处理：按行或分隔符处理文本数据，生成Stream流</li>
 * </ul>
 *
 * @author soeasy.run
 * @see Resource
 * @see BufferConsumer
 */
@UtilityClass
public final class IOUtils {
	private static Logger logger = Logger.getLogger(IOUtils.class.getName());
	/** 默认字节缓冲区大小（4KB） */
	public static final int DEFAULT_BYTE_BUFFER_SIZE = 1024 * 4;
	/** 默认字符缓冲区大小（512B） */
	public static final int DEFAULT_CHAR_BUFFER_SIZE = 512;

	private static final byte[] EMPTY_CONTENT = new byte[0];
	/** 空输入流单例，避免重复创建 */
	private static final InputStream EMPTY_INPUT_STREAM = new ByteArrayInputStream(EMPTY_CONTENT);
	/** 输入流结束标记 */
	private static final int EOF = -1;

	/**
	 * 关闭多个可关闭资源，支持空值忽略和异常抛出。
	 * <p>
	 * 遍历资源数组，自动跳过null值，遇到异常时中断并抛出， 适用于try-finally块中确保资源关闭。
	 * 
	 * @param closeables 待关闭的资源数组（非空）
	 * @throws IOException 关闭资源时发生的I/O异常
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
	 * <p>
	 * 遍历资源数组，跳过null值，捕获所有关闭异常并忽略， 适用于不需要处理关闭异常的场景。
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
				logger.log(Level.FINEST, e, () -> closeable.toString());
			}
		}
	}

	/**
	 * 返回空输入流单例（内容为空的字节输入流）。
	 * <p>
	 * 提供单例模式的空输入流，避免重复创建， 适用于需要空输入流的场景（如默认参数）。
	 * 
	 * @return 空输入流实例
	 */
	public static InputStream emptyInput() {
		return EMPTY_INPUT_STREAM;
	}

	/**
	 * 返回空输出流单例（不实际写入数据的输出流）。
	 * <p>
	 * 提供单例模式的空输出流，所有写入操作被忽略， 适用于需要空输出流的场景（如测试或占位）。
	 * 
	 * @return 空输出流实例
	 */
	public static OutputStream nullOutput() {
		return NullOutputStream.NULL_OUTPUT_STREAM;
	}

	/**
	 * 处理具有底层数组的缓冲区数据（内部工具方法）。
	 * <p>
	 * 该方法通过函数式接口处理缓冲区底层数组， 避免直接操作缓冲区导致的性能损耗。
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
	 * <p>
	 * 使用BufferedReader包装输入读取器，生成按行分割的Stream流， 流关闭时自动关闭底层读取器。
	 * 
	 * @param reader 输入读取器
	 * @return 包含每行内容的Stream流
	 */
	public static Stream<String> readLines(Reader reader) {
		BufferedReader bufferedReader = new BufferedReader(reader);
		return readLines(bufferedReader).onClose(() -> closeQuietly(bufferedReader));
	}

	/**
	 * 按指定分隔符分割可读源内容（使用自定义缓冲区）。
	 * <p>
	 * 通过自定义字符缓冲区和分隔符分割可读源内容， 适用于大文本流的分段处理，避免一次性加载全部内容。
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
	 * <p>
	 * 使用默认字符缓冲区（{@value #DEFAULT_CHAR_BUFFER_SIZE}）和分隔符分割内容， 简化常用场景的分隔操作。
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
	 * <p>
	 * 优先使用缓冲区底层数组避免拷贝，若无可直接使用数组则通过复制操作转换， 适用于高效的缓冲区数据导出场景。
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
	 * 使用ByteArrayOutputStream缓冲输入流内容，适用于小文件场景， 大文件可能导致内存溢出，需谨慎使用。
	 * 
	 * @param input 输入流
	 * @return 包含流内容的字节数组
	 * @throws NullPointerException 输入流为null时抛出
	 * @throws IOException          读取流时发生I/O异常
	 */
	public static byte[] toByteArray(InputStream input) throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		transferTo(input, output::write);
		return output.toByteArray();
	}

	/**
	 * 将可读源内容转换为字符数组。
	 * <p>
	 * 使用CharArrayWriter缓冲可读源内容，适用于小文本场景， 通过缓冲区消费者模式实现高效转换。
	 * 
	 * @param readable 可读源
	 * @return 包含可读源内容的字符数组
	 * @throws IOException 读取时发生I/O异常
	 */
	public static char[] toCharArray(Readable readable) throws IOException {
		CharArrayWriter sw = new CharArrayWriter();
		transferTo(readable, sw::write);
		return sw.toCharArray();
	}

	/**
	 * 将可读源内容转换为字符序列。
	 * <p>
	 * 使用StringBuilder累加可读源内容，返回可变字符序列， 适用于需要后续编辑的文本处理场景。
	 * 
	 * @param readable 可读源（非空）
	 * @return 包含可读源内容的StringBuilder实例
	 * @throws IOException 读取时发生I/O异常
	 */
	public static CharSequence toCharSequence(@NonNull Readable readable) throws IOException {
		StringBuilder builder = new StringBuilder();
		transferTo(readable, builder::append);
		return builder;
	}

	/**
	 * 分批次传输缓冲区数据（支持自定义批次大小）。
	 * <p>
	 * 将缓冲区数据按指定批次大小分割传输，适用于需要控制内存占用的场景， 通过函数式接口实现自定义数据处理逻辑。
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
	 * <p>
	 * 自动判断缓冲区是否具有底层数组，优先使用底层数组避免拷贝， 直接缓冲区通过复制操作转换，确保高效传输。
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
	 * <p>
	 * 使用默认字节缓冲区（{@value #DEFAULT_BYTE_BUFFER_SIZE}）传输数据， 适用于大多数常规输入流传输场景。
	 * 
	 * @param <E>            异常类型
	 * @param input          输入流
	 * @param bufferConsumer 数据处理消费者
	 * @return 传输的总字节数
	 * @throws E           处理数据时抛出的异常
	 * @throws IOException 读取流时发生I/O异常
	 */
	public static <E extends Throwable> long transferTo(InputStream input,
			BufferConsumer<? super byte[], ? extends E> bufferConsumer) throws E, IOException {
		return transferTo(input, DEFAULT_BYTE_BUFFER_SIZE, bufferConsumer);
	}

	/**
	 * 传输输入流数据（使用自定义缓冲区）。
	 * <p>
	 * 通过自定义字节缓冲区传输数据，适用于需要控制缓冲区大小的场景， 避免默认缓冲区可能带来的内存浪费或不足。
	 * 
	 * @param <E>            异常类型
	 * @param input          输入流（非空）
	 * @param buffer         字节缓冲区（非空）
	 * @param bufferConsumer 数据处理消费者
	 * @return 传输的总字节数
	 * @throws E           处理数据时抛出的异常
	 * @throws IOException 读取流时发生I/O异常
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
	 * <p>
	 * 根据指定缓冲区大小创建新缓冲区，适用于需要动态调整缓冲区大小的场景， 如处理不同大小的数据包。
	 * 
	 * @param <E>            异常类型
	 * @param input          输入流
	 * @param bufferSize     缓冲区大小
	 * @param bufferConsumer 数据处理消费者
	 * @return 传输的总字节数
	 * @throws E           处理数据时抛出的异常
	 * @throws IOException 读取流时发生I/O异常
	 */
	public static <E extends Throwable> long transferTo(InputStream input, int bufferSize,
			BufferConsumer<? super byte[], ? extends E> bufferConsumer) throws E, IOException {
		return transferTo(input, new byte[bufferSize], bufferConsumer);
	}

	/**
	 * 传输可读源数据（使用默认缓冲区大小）。
	 * <p>
	 * 使用默认字符缓冲区（{@value #DEFAULT_CHAR_BUFFER_SIZE}）传输数据， 适用于大多数常规可读源传输场景。
	 * 
	 * @param <E>            异常类型
	 * @param readable       可读源（非空）
	 * @param bufferConsumer 数据处理消费者
	 * @return 传输的总字符数
	 * @throws E           处理数据时抛出的异常
	 * @throws IOException 读取时发生I/O异常
	 */
	public static <E extends Throwable> long transferTo(@NonNull Readable readable,
			@NonNull BufferConsumer<? super char[], ? extends E> bufferConsumer) throws E, IOException {
		return transferTo(readable, DEFAULT_CHAR_BUFFER_SIZE, bufferConsumer);
	}

	/**
	 * 将Readable数据源中的数据分批读取到CharBuffer，并传递给消费者处理。
	 * <p>
	 * 持续读取数据到缓冲区，处理后压缩缓冲区以便后续读取， 适用于大文本流处理，避免一次性加载全部数据到内存。
	 * 
	 * @param <E>            异常类型
	 * @param readable       可读源（非空）
	 * @param buffer         字符缓冲区（非空）
	 * @param bufferConsumer 数据处理消费者
	 * @return 传输的总字符数
	 * @throws E           处理数据时抛出的异常
	 * @throws IOException 读取时发生I/O异常
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
	 * <p>
	 * 根据指定缓冲区大小创建新字符缓冲区，适用于需要动态调整缓冲区大小的文本处理场景。
	 * 
	 * @param <E>            异常类型
	 * @param readable       可读源（非空）
	 * @param bufferSize     缓冲区大小
	 * @param bufferConsumer 数据处理消费者
	 * @return 传输的总字符数
	 * @throws E           处理数据时抛出的异常
	 * @throws IOException 读取时发生I/O异常
	 */
	public static <E extends Throwable> long transferTo(@NonNull Readable readable, int bufferSize,
			@NonNull BufferConsumer<? super char[], ? extends E> bufferConsumer) throws E, IOException {
		return transferTo(readable, CharBuffer.allocate(bufferSize), bufferConsumer);
	}

	/**
	 * 传输读取器数据（使用默认缓冲区大小）。
	 * <p>
	 * 使用默认字符缓冲区（{@value #DEFAULT_CHAR_BUFFER_SIZE}）传输读取器数据， 适用于常规读取器数据处理场景。
	 * 
	 * @param <E>            异常类型
	 * @param input          读取器（非空）
	 * @param bufferConsumer 数据处理消费者
	 * @return 传输的总字符数
	 * @throws E           处理数据时抛出的异常
	 * @throws IOException 读取时发生I/O异常
	 */
	public static <E extends Throwable> long transferTo(@NonNull Reader input,
			@NonNull BufferConsumer<? super char[], ? extends E> bufferConsumer) throws E, IOException {
		return transferTo(input, DEFAULT_CHAR_BUFFER_SIZE, bufferConsumer);
	}

	/**
	 * 传输读取器数据（使用自定义缓冲区）。
	 * <p>
	 * 通过自定义字符缓冲区传输读取器数据，适用于需要精确控制缓冲区的场景。
	 * 
	 * @param <E>            异常类型
	 * @param input          读取器（非空）
	 * @param buffer         字符缓冲区（非空）
	 * @param bufferConsumer 数据处理消费者
	 * @return 传输的总字符数
	 * @throws E           处理数据时抛出的异常
	 * @throws IOException 读取时发生I/O异常
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
	 * <p>
	 * 根据指定缓冲区大小创建新字符缓冲区，适用于不同规模的文本数据处理。
	 * 
	 * @param <E>            异常类型
	 * @param input          读取器（非空）
	 * @param bufferSize     缓冲区大小
	 * @param bufferConsumer 数据处理消费者
	 * @return 传输的总字符数
	 * @throws E           处理数据时抛出的异常
	 * @throws IOException 读取时发生I/O异常
	 */
	public static <E extends Throwable> long transferTo(@NonNull Reader input, int bufferSize,
			@NonNull BufferConsumer<? super char[], ? extends E> bufferConsumer) throws E, IOException {
		return transferTo(input, new char[bufferSize], bufferConsumer);
	}
}