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
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.Stream;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import run.soeasy.framework.core.collection.CollectionUtils;

@UtilityClass
public final class IOUtils {
	public static final int DEFAULT_BYTE_BUFFER_SIZE = 1024 * 4;
	public static final int DEFAULT_CHAR_BUFFER_SIZE = 512;

	private static final byte[] EMPTY_CONTENT = new byte[0];

	private final static InputStream EMPTY_INPUT_STREAM = new ByteArrayInputStream(EMPTY_CONTENT);

	private static final int EOF = -1;

	public static void close(Closeable... closeables) throws IOException {
		if (closeables == null) {
			return;
		}

		IOException exception = null;
		for (Closeable closeable : closeables) {
			if (closeable == null) {
				continue;
			}

			try {
				closeable.close();
			} catch (IOException e) {
				if (exception == null) {
					exception = e;
				} else {
					exception.addSuppressed(e);
				}
			}
		}
		if (exception != null) {
			throw exception;
		}
	}

	public static void closeQuietly(Closeable... closeables) {
		if (closeables == null) {
			return;
		}

		for (Closeable closeable : closeables) {
			if (closeable == null) {
				continue;
			}

			try {
				closeable.close();
			} catch (final IOException e) {
				// ignore
			}
		}
	}

	/**
	 * 一个空的输入
	 * 
	 * @return 返回一个单例
	 */
	public static InputStream emptyInput() {
		return EMPTY_INPUT_STREAM;
	}

	/**
	 * 一个空的输出
	 * 
	 * @return 返回一个单例
	 */
	public static OutputStream nullOutput() {
		return NullOutputStream.NULL_OUTPUT_STREAM;
	}

	/**
	 * 处理具有底层数组的缓冲区数据 该方法为内部工具方法，用于代码复用
	 * 
	 * @param <B>            Buffer类型，需继承Buffer
	 * @param <A>            数据数组类型
	 * @param <E>            可能抛出的异常类型
	 * @param buffer         源缓冲区
	 * @param length         需要处理的数据长度
	 * @param arrayReader    读取Buffer底层数组的函数
	 * @param bufferConsumer 数据消费者接口
	 * @throws E 当消费者处理数据时抛出异常
	 */
	private static <B extends Buffer, A, E extends Throwable> void processArrayBuffer(B buffer, int length,
			Function<? super B, ? extends A> arrayReader,
			@NonNull BufferConsumer<? super A, ? extends E> bufferConsumer) throws E {
		A array = arrayReader.apply(buffer);
		int offset = buffer.arrayOffset() + buffer.position();

		// 调用消费者处理数据，并更新缓冲区position
		bufferConsumer.accept(array, offset, length);
		buffer.position(buffer.position() + length);
	}

	public static Stream<String> readLines(Reader reader) {
		BufferedReader bufferedReader = new BufferedReader(reader);
		return readLines(bufferedReader).onClose(() -> closeQuietly(bufferedReader));
	}

	/**
	 * 分割读取
	 * 
	 * @param source
	 * @param buffer
	 * @param delimiter
	 * @return
	 */
	public static Stream<CharSequence> split(@NonNull Readable source, @NonNull CharBuffer buffer,
			@NonNull CharSequence delimiter) {
		SplitReadableIterator iterator = new SplitReadableIterator(source, buffer, delimiter);
		return CollectionUtils.unknownSizeStream(iterator);
	}

	/**
	 * 分割读取
	 * 
	 * @param source
	 * @param delimiter
	 * @return
	 */
	public static Stream<CharSequence> split(Readable source, CharSequence delimiter) {
		return split(source, CharBuffer.allocate(DEFAULT_CHAR_BUFFER_SIZE), delimiter);
	}

	/**
	 * 将Buffer中的剩余数据转换为指定类型的数组
	 * 
	 * @param <B>          Buffer类型，需继承Buffer
	 * @param <A>          目标数组类型
	 * @param buffer       源缓冲区
	 * @param arrayReader  读取Buffer底层数组的函数
	 * @param arrayCopyer  复制Buffer数据到数组的函数
	 * @param arrayCreator 创建指定长度数组的函数
	 * @return 包含Buffer中剩余数据的新数组
	 */
	public static <B extends Buffer, A> A toArray(@NonNull B buffer, Function<? super B, ? extends A> arrayReader,
			BiConsumer<? super B, ? super A> arrayCopyer, IntFunction<? extends A> arrayCreator) {
		int length = buffer.remaining();
		A newArray = arrayCreator.apply(length);
		// 检查是否有可直接访问的底层数组，提高复制效率
		if (buffer.hasArray()) {
			processArrayBuffer(buffer, length, arrayReader, (array, offset, len) -> {
				System.arraycopy(array, offset, newArray, 0, len);
			});
		} else {
			// 处理直接缓冲区或只读缓冲区，使用提供的复制器
			arrayCopyer.accept(buffer, newArray);
		}
		return newArray;
	}

	/**
	 * Get the contents of an <code>InputStream</code> as a <code>byte[]</code>.
	 * <p>
	 * This method buffers the input internally, so there is no need to use a
	 * <code>BufferedInputStream</code>.
	 * 
	 * @param input the <code>InputStream</code> to read from
	 * @return the requested byte array
	 * @throws NullPointerException if the input is null
	 * @throws IOException          if an I/O error occurs
	 */
	public static byte[] toByteArray(InputStream input) throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		transferTo(input, output::write);
		return output.toByteArray();
	}

	public static char[] toCharArray(Readable readable) throws IOException {
		CharArrayWriter sw = new CharArrayWriter();
		transferTo(readable, sw::write);
		return sw.toCharArray();
	}

	public static CharSequence toCharSequence(@NonNull Readable readable) throws IOException {
		StringBuilder builder = new StringBuilder();
		transferTo(readable, builder::append);
		return builder;
	}

	/**
	 * 将Buffer中的数据按指定数组长度分批次传输到消费者 适用于处理大数据量场景，避免一次性加载全部数据
	 * 
	 * @param <B>            Buffer类型，需继承Buffer
	 * @param <A>            数据数组类型
	 * @param <E>            可能抛出的异常类型
	 * @param buffer         源缓冲区
	 * @param array          用于临时存储数据的数组
	 * @param arrayLength    数组的有效长度（决定每次处理的批次大小）
	 * @param arrayCopyer    将Buffer数据复制到数组的操作
	 * @param bufferConsumer 数据消费者接口
	 * @return 传输的数据总长度
	 * @throws E                        当消费者处理数据时抛出异常
	 * @throws IllegalArgumentException 当数组长度不合法时抛出
	 */
	public static <B extends Buffer, A, E extends Throwable> long transferTo(@NonNull B buffer, @NonNull A array,
			int arrayLength, BiConsumer<? super B, ? super A> arrayCopyer,
			@NonNull BufferConsumer<? super A, ? extends E> bufferConsumer) throws E {
		// 参数校验：确保数组长度为正数
		if (arrayLength <= 0) {
			throw new IllegalArgumentException("Array length must be positive");
		}

		long total = 0;
		int remaining = buffer.remaining();

		// 分批次处理数据，每次处理不超过数组长度的数据量
		while (remaining > 0) {
			int chunkSize = Math.min(remaining, arrayLength);

			// 记录当前处理位置，用于精准控制数据复制范围
			int originalPosition = buffer.position();

			// 临时调整limit以限制本次复制的数据范围
			buffer.limit(originalPosition + chunkSize);

			// 复制当前批次数据到临时数组
			arrayCopyer.accept(buffer, array);

			// 恢复limit，确保缓冲区状态正确
			buffer.limit(buffer.capacity());

			// 调用消费者处理当前批次数据
			bufferConsumer.accept(array, 0, chunkSize);

			// 更新统计信息和缓冲区位置
			total += chunkSize;
			buffer.position(originalPosition + chunkSize);
			remaining -= chunkSize;
		}

		return total;
	}

	/**
	 * 将Buffer中的剩余数据传输到消费者，支持底层数组优化和直接缓冲区处理
	 * 
	 * @param <B>            Buffer类型（需继承Buffer）
	 * @param <A>            数据数组类型
	 * @param <E>            可能抛出的异常类型
	 * @param buffer         源缓冲区
	 * @param arrayReader    读取Buffer底层数组的函数（用于有数组的缓冲区）
	 * @param arrayCreator   创建指定长度数组的函数（用于直接缓冲区）
	 * @param arrayCopyer    复制Buffer数据到数组的函数（用于直接缓冲区）
	 * @param bufferConsumer 数据消费者接口
	 * @return 传输的数据总长度
	 * @throws E 当消费者处理数据时抛出异常
	 */
	public static <B extends Buffer, A, E extends Throwable> int transferTo(@NonNull B buffer,
			@NonNull Function<? super B, ? extends A> arrayReader, IntFunction<? extends A> directBufferArrayCreator,
			BiConsumer<? super B, ? super A> directBufferCopyer,
			@NonNull BufferConsumer<? super A, ? extends E> bufferConsumer) throws E {
		int length = buffer.remaining();
		if (length == 0) {
			return 0;
		}

		// 优先处理有底层数组的缓冲区，提高数据访问效率
		if (buffer.hasArray()) {
			processArrayBuffer(buffer, length, arrayReader, bufferConsumer);
		} else {
			// 直接缓冲区需要通过分页查询接口获取数据
			A array = directBufferArrayCreator.apply(length);
			directBufferCopyer.accept(buffer, array);
			bufferConsumer.accept(array, 0, length);
		}
		return length;
	}

	public static <E extends Throwable> long transferTo(InputStream input,
			BufferConsumer<? super byte[], ? extends E> bufferConsumer) throws E, IOException {
		return transferTo(input, DEFAULT_BYTE_BUFFER_SIZE, bufferConsumer);
	}

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

	public static <E extends Throwable> long transferTo(InputStream input, int bufferSize,
			BufferConsumer<? super byte[], ? extends E> bufferConsumer) throws E, IOException {
		return transferTo(input, new byte[bufferSize], bufferConsumer);
	}

	public static <E extends Throwable> long transferTo(@NonNull Readable readable,
			@NonNull BufferConsumer<? super char[], ? extends E> bufferConsumer) throws E, IOException {
		return transferTo(readable, DEFAULT_CHAR_BUFFER_SIZE, bufferConsumer);
	}

	/**
	 * 将Readable数据源中的数据分批读取到CharBuffer，并传递给消费者处理
	 * 
	 * 该方法会持续从Readable读取数据，直到数据源结束（返回-1）。
	 * 每次读取的数据会被放入CharBuffer，然后通过bufferConsumer进行处理。 适用于处理大文本流，避免一次性加载全部数据到内存。
	 * 
	 * @param <E>            消费者可能抛出的异常类型
	 * @param readable       可读数据源
	 * @param buffer         用于临时存储数据的字符缓冲区
	 * @param bufferConsumer 处理字符数组的消费者
	 * @return 成功传输的字符总数
	 * @throws E           如果消费者处理数据时抛出异常
	 * @throws IOException 如果读取数据源时发生IO错误
	 */
	public static <E extends Throwable> long transferTo(@NonNull Readable readable, @NonNull CharBuffer buffer,
			@NonNull BufferConsumer<? super char[], ? extends E> bufferConsumer) throws E, IOException {
		long total = 0;

		// 循环读取数据源，直到结束（read()返回-1）
		while (readable.read(buffer) != -1) {
			// 切换缓冲区为读模式（position=0，limit=写入位置）
			buffer.flip();

			// 处理特殊情况：缓冲区中没有数据（例如空行）
			if (buffer.remaining() == 0) {
				buffer.clear(); // 重置缓冲区，准备下次写入
				continue;
			}

			// 将缓冲区中的数据传输给消费者处理
			// 使用重载的transferTo方法处理CharBuffer
			total += transferTo(buffer, CharBuffer::array, char[]::new, CharBuffer::get, bufferConsumer);

			// 压缩缓冲区：将未处理的数据移至缓冲区头部，为下次写入做准备
			// 注意：compact()会保留未处理的数据，而clear()会清空所有数据
			buffer.compact();
		}

		// 处理最后一批可能剩余的数据（当readable.read()返回-1时）
		if (buffer.position() > 0) {
			buffer.flip(); // 切换到读模式
			if (buffer.remaining() > 0) {
				total += transferTo(buffer, CharBuffer::array, char[]::new, CharBuffer::get, bufferConsumer);
			}
			buffer.clear(); // 清空缓冲区，释放资源
		}

		return total;
	}

	public static <E extends Throwable> long transferTo(@NonNull Readable readable, int bufferSize,
			@NonNull BufferConsumer<? super char[], ? extends E> bufferConsumer) throws E, IOException {
		return transferTo(readable, CharBuffer.allocate(bufferSize), bufferConsumer);
	}

	public static <E extends Throwable> long transferTo(@NonNull Reader input,
			@NonNull BufferConsumer<? super char[], ? extends E> bufferConsumer) throws E, IOException {
		return transferTo(input, DEFAULT_CHAR_BUFFER_SIZE, bufferConsumer);
	}

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

	public static <E extends Throwable> long transferTo(@NonNull Reader input, int bufferSize,
			@NonNull BufferConsumer<? super char[], ? extends E> bufferConsumer) throws E, IOException {
		return transferTo(input, new char[bufferSize], bufferConsumer);
	}
}
