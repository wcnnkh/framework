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
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Arrays;
import java.util.function.BiConsumer;
import java.util.function.IntFunction;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import run.soeasy.framework.core.collection.CollectionUtils;
import run.soeasy.framework.core.function.ThrowingConsumer;
import run.soeasy.framework.io.buffer.BufferReader;
import run.soeasy.framework.io.buffer.BufferWriter;

/**
 * IO 操作工具类，封装 Java IO/NIO 底层操作，提供统一、高效的 IO 处理能力。
 * <p>
 * 该类为工具类（不可实例化），所有方法均为静态方法，核心能力覆盖：
 * <ul>
 * <li>安全的资源管理：支持批量关闭 Closeable 资源，提供「普通关闭（抛异常）」和「静默关闭（忽略异常）」两种模式</li>
 * <li>高效的数据拷贝：支持 Buffer/InputStream/Reader 等数据源的分段拷贝，适配大文件场景避免内存溢出</li>
 * <li>灵活的数据转换：实现流/缓冲区与字节数组/字符数组/字符序列的双向转换</li>
 * <li>文本分段处理：支持按自定义分隔符分割可读源内容，生成 Stream 流便于流式处理</li>
 * <li>空资源提供：提供单例的空输入流/空输出流，避免重复创建空资源对象</li>
 * <li>通用数据流转：基于 BufferFeeder/BufferConsumer 抽象，实现不同 IO 载体间的高效数据传输</li>
 * </ul>
 * 
 * <h3>设计原则</h3>
 * <ul>
 * <li>性能优先：优先使用缓冲区底层数组减少拷贝，默认缓冲区大小适配操作系统页大小（最优 64KB）</li>
 * <li>异常可控：区分「必须处理的异常」和「可忽略的异常」，提供不同的资源关闭策略</li>
 * <li>兼容扩展：通过 {@link BufferCopier} {@link BufferConsumer}
 * 等函数式接口支持自定义数据处理逻辑</li>
 * <li>状态安全：自动管理 Buffer 的 clear/flip 状态，避免调用者手动操作导致的状态错误</li>
 * <li>语义统一：核心数据流转方法统一命名为 transfer，通过参数类型区分字节/字符场景，降低认知成本</li>
 * </ul>
 * 
 * <h3>核心概念说明</h3>
 * <ul>
 * <li>BufferFeeder：数据供给器，定义从外部数据源（流/通道）向 Buffer 填充数据的逻辑</li>
 * <li>BufferCopier：数据拷贝器，定义从 Buffer 到数组的批量拷贝逻辑，支持自定义适配不同 Buffer 类型</li>
 * <li>BufferConsumer：数据消费者，接收分段处理的数组/Buffer 数据，支持自定义消费逻辑（如写入/解析）</li>
 * </ul>
 * 
 * @author soeasy.run
 * @see Closeable
 * @see java.nio.Buffer
 * @see BufferConsumer
 * @see BufferCopier
 * @see BufferFeeder
 */
@UtilityClass
public final class IOUtils {
	/**
	 * 默认字节缓冲区大小（兼顾性能与内存占用）：
	 * <ul>
	 * <li>最小值：1KB（1024 字节），保证基础处理能力</li>
	 * <li>参考值：操作系统页大小（os.pagesize），适配系统 IO 特性</li>
	 * <li>最大值：64KB（65536 字节），超过该值无性能提升</li>
	 * </ul>
	 */
	public static final int DEFAULT_BYTE_BUFFER_SIZE = Math.min(1024 * 64,
			Math.max(1024, Integer.getInteger("os.pagesize", 1024 * 4)));

	/**
	 * 默认字符缓冲区大小：
	 * <ul>
	 * <li>最大值：8KB（8192 字符），适配字符处理的内存上限</li>
	 * <li>兜底值：默认字节缓冲区大小的 1/2，适配字符编码的字节占比（如 UTF-16 占 2 字节/字符）</li>
	 * <li>小内存适配：当 os.pagesize=1KB 时，字符缓冲区为 512 字符，匹配系统内存页大小，降低碎片化</li>
	 * </ul>
	 */
	public static final int DEFAULT_CHAR_BUFFER_SIZE = Math.min(1024 * 8, DEFAULT_BYTE_BUFFER_SIZE / 2);

	/** 空字节数组常量，用于创建空输入流，避免重复实例化空数组 */
	public static final byte[] EMPTY_CONTENT = new byte[0];

	/** 空输入流单例（内容为空的 ByteArrayInputStream），单例模式避免重复创建 */
	private static final InputStream EMPTY_INPUT_STREAM = new ByteArrayInputStream(EMPTY_CONTENT);

	/** 输入流/Reader 的结束标记（End of File），表示无更多数据可读 */
	public static final int EOF = -1;

	public static final CharBuffer EMPTY_CHAR_BUFFER = CharBuffer.allocate(0);
	public static final ByteBuffer EMPTY_BYTE_BUFFER = ByteBuffer.allocate(0);

	/**
	 * A singleton.
	 */
	public static final OutputStream NULL_OUTPUT_STREAM = new NullOutputStream();

	/** 日志记录器，用于记录静默关闭资源时的异常（FINEST 级别，不影响正常流程） */
	private static final Logger logger = Logger.getLogger(IOUtils.class.getName());

	/**
	 * 批量关闭可关闭资源，遇到异常立即抛出，自动忽略 null 资源。
	 * <p>
	 * 适用于 try-finally 块中「必须确保资源关闭且需要处理关闭异常」的场景，遍历资源数组时跳过 null 元素， 第一个抛出的
	 * IOException 会中断流程并向上传递。
	 * 
	 * @param closeables 待关闭的资源数组（不可为 null，允许数组内包含 null 元素）
	 * @throws IOException 任意资源关闭时抛出的 IO 异常（第一个抛出的异常）
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
	 * 批量静默关闭可关闭资源，忽略所有关闭异常，自动跳过 null 资源。
	 * <p>
	 * 适用于「无需处理关闭异常」的兜底清理场景，关闭异常会被捕获并记录为 FINEST 级别日志， 不会中断后续资源的关闭流程，也不会向上抛出异常。
	 * 
	 * @param closeables 待关闭的资源数组（不可为 null，允许数组内包含 null 元素）
	 */
	public static void closeQuietly(@NonNull Closeable... closeables) {
		for (Closeable closeable : closeables) {
			if (closeable == null) {
				continue;
			}
			try {
				closeable.close();
			} catch (final IOException e) {
				logger.log(Level.FINEST, e, () -> "关闭资源时发生静默异常: " + closeable);
			}
		}
	}

	/**
	 * 重载方法：从 ByteBuffer 拷贝数据到字节数组，交由 BufferConsumer 消费。
	 * <p>
	 * 默认使用字节数组的完整长度作为单次分段长度，内置 {@link ByteBuffer#get(byte[], int, int)} 作为拷贝器，
	 * 该拷贝器严格适配 {@link BufferCopier} 接口签名，实现批量数据拷贝。
	 * 
	 * @param <E>      BufferConsumer 执行时抛出的异常类型
	 * @param source   源 ByteBuffer（不可为 null），提供待拷贝的字节数据
	 * @param buffer   目标字节数组（不可为 null），用于暂存拷贝的数据
	 * @param consumer 数据消费者（不可为 null），处理拷贝后的字节数组
	 * @return 成功拷贝并消费的总字节数
	 * @throws E BufferConsumer 消费数据时抛出的自定义异常
	 */
	public static <S extends Buffer, B, E extends Throwable> long copy(@NonNull ByteBuffer source,
			@NonNull byte[] buffer, @NonNull BufferConsumer<? super byte[], ? extends E> consumer) throws E {
		return copy(source, buffer, buffer.length, ByteBuffer::get, consumer);
	}

	/**
	 * 重载方法：从 CharBuffer 拷贝数据到字符数组，交由 BufferConsumer 消费。
	 * <p>
	 * 默认使用字符数组的完整长度作为单次分段长度，内置 {@link CharBuffer#get(char[], int, int)} 作为拷贝器，
	 * 该拷贝器严格适配 {@link BufferCopier} 接口签名，实现批量数据拷贝。
	 * 
	 * @param <E>      BufferConsumer 执行时抛出的异常类型
	 * @param source   源 CharBuffer（不可为 null），提供待拷贝的字符数据
	 * @param buffer   目标字符数组（不可为 null），用于暂存拷贝的数据
	 * @param consumer 数据消费者（不可为 null），处理拷贝后的字符数组
	 * @return 成功拷贝并消费的总字符数
	 * @throws E BufferConsumer 消费数据时抛出的自定义异常
	 */
	public static <S extends Buffer, B, E extends Throwable> long copy(@NonNull CharBuffer source,
			@NonNull char[] buffer, @NonNull BufferConsumer<? super char[], ? extends E> consumer) throws E {
		return copy(source, buffer, buffer.length, CharBuffer::get, consumer);
	}

	/**
	 * 从字节输入流读取数据，经 ByteBuffer 中转拷贝到字节数组，最终交由 BufferConsumer 消费。
	 * <p>
	 * 核心流程：
	 * 
	 * <pre>
	 * 1. InputStream → ByteBuffer（分段读取，避免内存溢出）
	 * 2. ByteBuffer → byte[]（通过 {@link BufferCopier} 批量拷贝）
	 * 3. byte[] → BufferConsumer（消费数据）
	 * </pre>
	 * 
	 * 适用于大字节流的分段处理，底层复用 transfer 方法实现流到缓冲区的传输。
	 * 
	 * @param <E>        BufferConsumer 执行时抛出的异常类型
	 * @param input      源字节输入流（不可为 null）
	 * @param readBuffer 读取缓冲区（不可为 null），用于中转输入流数据
	 * @param copyBuffer 拷贝数组（不可为 null），用于暂存待消费的字节数据
	 * @param consumer   数据消费者（不可为 null），处理拷贝后的字节数组
	 * @return 成功拷贝并消费的总字节数
	 * @throws IOException 读取输入流或操作缓冲区时抛出的 IO 异常
	 * @throws E           BufferConsumer 消费数据时抛出的自定义异常
	 */
	public static <E extends Throwable> long copy(@NonNull InputStream input, @NonNull ByteBuffer readBuffer,
			@NonNull byte[] copyBuffer, @NonNull BufferConsumer<? super byte[], ? extends E> consumer)
			throws IOException, E {
		return transfer(input, readBuffer, (buffer) -> copy(buffer, copyBuffer, consumer));
	}

	/**
	 * 从字符可读源读取数据，经 CharBuffer 中转拷贝到字符数组，最终交由 BufferConsumer 消费。
	 * <p>
	 * 核心流程：
	 * 
	 * <pre>
	 * 1. Readable → CharBuffer（分段读取，避免内存溢出）
	 * 2. CharBuffer → char[]（通过 {@link BufferCopier} 批量拷贝）
	 * 3. char[] → BufferConsumer（消费数据）
	 * </pre>
	 * 
	 * 适用于大字符流的分段处理，支持 Reader、CharSequenceReader 等所有 Readable 实现类。
	 * 
	 * @param <E>        BufferConsumer 执行时抛出的异常类型
	 * @param readable   源字符可读源（不可为 null）
	 * @param readBuffer 读取缓冲区（不可为 null），用于中转可读源数据
	 * @param copyBuffer 拷贝数组（不可为 null），用于暂存待消费的字符数据
	 * @param consumer   数据消费者（不可为 null），处理拷贝后的字符数组
	 * @return 成功拷贝并消费的总字符数
	 * @throws IOException 读取可读源或操作缓冲区时抛出的 IO 异常
	 * @throws E           BufferConsumer 消费数据时抛出的自定义异常
	 */
	public static <E extends Throwable> long copy(@NonNull Readable readable, @NonNull CharBuffer readBuffer,
			@NonNull char[] copyBuffer, @NonNull BufferConsumer<? super char[], ? extends E> consumer)
			throws IOException, E {
		return transfer(readable::read, readBuffer, (buffer) -> copy(buffer, copyBuffer, consumer));
	}

	/**
	 * 通用 Buffer 数据拷贝方法：从源 Buffer 分段拷贝数据到目标数组，交由消费者处理。
	 * <p>
	 * 核心优化点：
	 * <ul>
	 * <li>优先使用源 Buffer 的底层数组（hasArray()）直接拷贝，减少内存复制</li>
	 * <li>无底层数组时，调用自定义 {@link BufferCopier} 完成数据拷贝</li>
	 * <li>单次拷贝长度为「源 Buffer 剩余数据量」和「分段长度」的较小值，避免数组越界</li>
	 * </ul>
	 * 
	 * @param <S>         源 Buffer 类型（如 ByteBuffer/CharBuffer）
	 * @param <B>         目标数组类型（如 byte[]/char[]）
	 * @param <E>         BufferConsumer 执行时抛出的异常类型
	 * @param source      源 Buffer（不可为 null）
	 * @param array       目标数组（不可为 null）
	 * @param chunkLength 单次分段处理长度（必须 > 0）
	 * @param copier      数据拷贝器（不可为 null），定义从 Buffer 到数组的拷贝逻辑
	 * @param consumer    数据消费者（不可为 null），处理拷贝后的数组数据
	 * @return 成功拷贝并消费的总数据量（字节/字符数）
	 * @throws E BufferConsumer 消费数据时抛出的自定义异常
	 */
	public static <S extends Buffer, B, E extends Throwable> long copy(@NonNull S source, @NonNull B array,
			int chunkLength, @NonNull BufferCopier<? super S, ? super B> copier,
			@NonNull BufferConsumer<? super B, ? extends E> consumer) throws E {
		return transfer(source, array, (a, b) -> {
			int remaining = Math.min(a.remaining(), chunkLength);
			if (a.hasArray()) {
				System.arraycopy(a.array(), a.arrayOffset() + a.position(), b, 0, remaining);
				a.position(a.position() + remaining);
			} else {
				copier.copy(a, b, 0, remaining);
			}
		}, consumer);
	}

	/**
	 * 获取空输入流单例（内容为空的 ByteArrayInputStream）。
	 * <p>
	 * 单例模式避免重复创建空输入流对象，适用于「需要空输入流作为默认参数」的场景， 该流无实际内容，调用 read() 方法会直接返回 EOF（-1）。
	 * 
	 * @return 空输入流单例（非 null）
	 */
	public static InputStream emptyInput() {
		return EMPTY_INPUT_STREAM;
	}

	/**
	 * 获取空输出流单例（所有写入操作均被忽略）。
	 * <p>
	 * 单例模式的 NullOutputStream，适用于测试、占位或「不需要实际写入数据」的场景， 所有 write
	 * 方法无任何副作用，不会抛出异常，也不会写入任何数据。
	 * 
	 * @return 空输出流单例（非 null）
	 */
	public static OutputStream nullOutput() {
		return NullOutputStream.NULL_OUTPUT_STREAM;
	}

	/**
	 * 将 Reader 转换为按行分割的 Stream 流，自动关闭底层缓冲读取器。
	 * <p>
	 * 核心特性：
	 * <ul>
	 * <li>使用 BufferedReader 包装输入 Reader，提升按行读取性能</li>
	 * <li>生成的 Stream 流关闭时，自动静默关闭 BufferedReader，避免资源泄漏</li>
	 * <li>流的元素为 Reader 中的每行文本（不含换行符）</li>
	 * </ul>
	 * 
	 * @param reader 源 Reader（不可为 null）
	 * @return 按行分割的 String 类型 Stream 流（非 null）
	 */
	public static Stream<String> readLines(Reader reader) {
		BufferedReader bufferedReader = new BufferedReader(reader);
		return bufferedReader.lines().onClose(() -> closeQuietly(bufferedReader));
	}

	/**
	 * 按自定义分隔符分割可读源内容，生成 CharSequence 流（使用自定义字符缓冲区）。
	 * <p>
	 * 适用于大文本流的分段处理，避免一次性加载全部内容到内存，分隔逻辑由 SplitReadableIterator 实现， 支持任意自定义分隔符（如换行符
	 * \n、逗号 ,、分号 ; 等）。
	 * 
	 * @param source    源可读对象（不可为 null），如 Reader、CharSequenceReader 等
	 * @param buffer    字符缓冲区（不可为 null），用于分段读取数据
	 * @param delimiter 分隔符（不可为 null），如 "\n"、"," 等
	 * @return 分割后的 CharSequence 流（非 null），每个元素为分隔符分隔的文本片段
	 */
	public static Stream<CharSequence> split(@NonNull Readable source, @NonNull CharBuffer buffer,
			@NonNull CharSequence delimiter) {
		SplitReadableIterator iterator = new SplitReadableIterator(source, buffer, delimiter);
		return CollectionUtils.unknownSizeStream(iterator);
	}

	/**
	 * 按自定义分隔符分割可读源内容，生成 CharSequence 流（使用默认字符缓冲区）。
	 * <p>
	 * 重载方法，默认使用 {@link #DEFAULT_CHAR_BUFFER_SIZE} 大小的字符缓冲区， 简化「无需自定义缓冲区大小」的常用场景。
	 * 
	 * @param source    源可读对象（不可为 null）
	 * @param delimiter 分隔符（不可为 null）
	 * @return 分割后的 CharSequence 流（非 null）
	 */
	public static Stream<CharSequence> split(Readable source, CharSequence delimiter) {
		return split(source, CharBuffer.allocate(DEFAULT_CHAR_BUFFER_SIZE), delimiter);
	}

	/**
	 * 重载方法：将 ByteBuffer 剩余数据转换为字节数组。
	 * <p>
	 * 默认使用 byte[]::new 作为数组创建函数，内置 {@link ByteBuffer#get(byte[], int, int)} 作为拷贝器，
	 * 该拷贝器严格适配 {@link BufferCopier} 接口签名，实现批量数据拷贝。
	 * 
	 * @param buffer 源 ByteBuffer（不可为 null）
	 * @return 包含 ByteBuffer 剩余数据的字节数组（非 null）
	 */
	public static byte[] toArray(@NonNull ByteBuffer buffer) {
		return toArray(buffer, byte[]::new, ByteBuffer::get);
	}

	/**
	 * 重载方法：将 CharBuffer 剩余数据转换为字符数组。
	 * <p>
	 * 默认使用 char[]::new 作为数组创建函数，内置 {@link CharBuffer#get(char[], int, int)} 作为拷贝器，
	 * 该拷贝器严格适配 {@link BufferCopier} 接口签名，实现批量数据拷贝。
	 * 
	 * @param buffer 源 CharBuffer（不可为 null）
	 * @return 包含 CharBuffer 剩余数据的字符数组（非 null）
	 */
	public static char[] toArray(@NonNull CharBuffer buffer) {
		return toArray(buffer, char[]::new, CharBuffer::get);
	}

	/**
	 * 通用 Buffer 转数组方法：将源 Buffer 剩余数据转换为指定类型数组。
	 * <p>
	 * 底层复用 {@link #copy(Buffer, Object, int, BufferCopier, BufferConsumer)}
	 * 方法完成数据拷贝，若 Buffer 无剩余数据，返回空数组（长度为 0），适用于需要自定义拷贝器的场景。
	 * 
	 * @param <S>          源 Buffer 类型
	 * @param <A>          目标数组类型
	 * @param <E>          拷贝器抛出的异常类型
	 * @param source       源 Buffer（不可为 null）
	 * @param arrayFactory 数组创建函数（不可为 null），创建指定长度的目标数组
	 * @param copier       数据拷贝器（不可为 null），定义从 Buffer 到数组的拷贝逻辑
	 * @return 包含 Buffer 剩余数据的新数组（非 null）
	 * @throws E 拷贝器执行时抛出的自定义异常
	 */
	public static <S extends Buffer, A, E extends Throwable> A toArray(@NonNull S source,
			@NonNull IntFunction<? extends A> arrayFactory, @NonNull BufferCopier<? super S, ? super A> copier)
			throws E {
		int length = source.remaining();
		A array = arrayFactory.apply(length);
		if (length == 0) {
			return array;
		}
		copy(source, array, length, copier, (a, b, c) -> {
		});
		return array;
	}

	/**
	 * 将输入流内容转换为字节数组。
	 * <p>
	 * 使用 ByteArrayOutputStream 缓冲输入流内容，适用于「小文件/短流」场景（建议 ≤ 100MB），
	 * 大文件使用此方法可能导致内存溢出，建议使用 {@link #transfer(InputStream, BufferConsumer)} 分段消费。
	 * 
	 * @param input 源输入流（不可为 null）
	 * @return 包含输入流全部内容的字节数组（非 null）
	 * @throws IOException 读取输入流时抛出的 IO 异常（如流关闭、读取超时等）
	 */
	public static byte[] toByteArray(@NonNull InputStream input) throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		transfer(input, output);
		return output.toByteArray();
	}

	/**
	 * 将可读源内容转换为字符数组。
	 * <p>
	 * 使用 CharArrayWriter 缓冲可读源内容，适用于「小文本」场景， 底层通过缓冲区消费者模式分段读取，避免一次性加载全部字符到内存。
	 * 
	 * @param readable 源可读对象（不可为 null）
	 * @return 包含可读源全部内容的字符数组（非 null）
	 * @throws IOException 读取可读源时抛出的 IO 异常
	 */
	public static char[] toCharArray(@NonNull Readable readable) throws IOException {
		CharArrayWriter sw = new CharArrayWriter();
		transfer(readable, sw);
		return sw.toCharArray();
	}

	/**
	 * 将可读源内容转换为 StringBuilder（可变字符序列）。
	 * <p>
	 * 适用于「需要后续编辑文本内容」的场景，通过 append 方式分段累加字符数据， 最终返回的 StringBuilder 包含可读源的全部内容。
	 * 
	 * @param readable 源可读对象（不可为 null）
	 * @return 包含可读源全部内容的 StringBuilder（非 null）
	 * @throws IOException 读取可读源时抛出的 IO 异常
	 */
	public static CharSequence toCharSequence(@NonNull Readable readable) throws IOException {
		StringBuilder builder = new StringBuilder();
		transfer(readable, builder);
		return builder;
	}

	/**
	 * 通用 Buffer 数据传输方法：从 BufferFeeder 读取数据到 Buffer，交由消费者处理。
	 * <p>
	 * 自动管理 Buffer 状态（clear/flip），调用者无需手动调整 Buffer 状态，核心流程：
	 * 
	 * <pre>
	 * 1. clear() → Buffer 切换为写模式，清空已有状态
	 * 2. feeder.read(buffer) → 向 Buffer 填充数据，返回读取长度
	 * 3. flip() → Buffer 切换为读模式，准备消费数据
	 * 4. consumer.accept(buffer) → 消费 Buffer 中的数据
	 * 5. 循环直至 feeder 返回 EOF（-1）
	 * </pre>
	 * 
	 * @param <B>      Buffer 类型（如 ByteBuffer/CharBuffer）
	 * @param <E>      消费者抛出的异常类型
	 * @param feeder   数据供给器（不可为 null），从外部数据源读取数据到 Buffer
	 * @param buffer   中转 Buffer（不可为 null），必须有可用写入空间（capacity > 0）
	 * @param consumer 数据消费者（不可为 null），消费 Buffer 中的数据
	 * @return 成功传输的总数据量（字节/字符数）
	 * @throws IOException           供给器读取数据时抛出的 IO 异常
	 * @throws IllegalStateException Buffer 无可用写入空间（remaining = 0）时抛出
	 * @throws E                     消费者处理数据时抛出的自定义异常
	 */
	public static <B extends Buffer, E extends Throwable> long transfer(@NonNull BufferFeeder<? super B> feeder,
			@NonNull B buffer, @NonNull ThrowingConsumer<? super B, ? extends E> consumer) throws IOException, E {
		buffer.clear();
		if (buffer.remaining() == 0) {
			throw new IllegalStateException("Buffer 无可用写入空间，容量必须大于 0");
		}
		long total = 0;
		int count = 0;
		while ((count = feeder.read(buffer)) != -1) {
			buffer.flip();
			try {
				consumer.accept(buffer);
			} finally {
				buffer.clear();
				total += count;
			}
		}
		return total;
	}

	public static <B extends Buffer, I extends Throwable, O extends Throwable> void transfer(
			@NonNull BufferReader<? super B, ? extends I> reader, @NonNull B buffer,
			@NonNull BufferWriter<? super B, ? extends O> writer) throws IOException, I, O {
		buffer.clear();
		if (buffer.remaining() == 0) {
			throw new IOException("Buffer 无可用写入空间，容量必须大于 0");
		}

		while (reader.read(buffer) != -1) {
			buffer.flip();
			try {
				writer.write(buffer);
			} finally {
				buffer.clear();
			}
		}
	}

	/**
	 * 将 BufferFeeder 提供的字节数据传输到 OutputStream。
	 * <p>
	 * 底层将 OutputStream 包装为 WritableByteChannel，复用
	 * {@link #transfer(BufferFeeder, Buffer, ThrowingConsumer)} 核心逻辑，
	 * 实现字节数据到输出流的高效传输。
	 * 
	 * @param <B>          字节缓冲区类型（限定为 ByteBuffer 及其子类）
	 * @param bufferFeeder 数据供给器（不可为 null），提供待传输的字节数据
	 * @param buffer       中转字节缓冲区（不可为 null），需提前分配有效容量
	 * @param output       目标输出流（不可为 null），接收最终传输的字节数据
	 * @return 成功传输的总字节数
	 * @throws IOException 数据供给或流写入过程中发生的 IO 异常
	 */
	public static <B extends ByteBuffer> long transfer(@NonNull BufferFeeder<B> bufferFeeder, @NonNull B buffer,
			@NonNull OutputStream output) throws IOException {
		WritableByteChannel channel = Channels.newChannel(output);
		return transfer(bufferFeeder, buffer, channel::write);
	}

	/**
	 * 重载方法：将 InputStream 数据传输到 OutputStream（使用默认字节缓冲区）。
	 * <p>
	 * 默认使用 {@link #DEFAULT_BYTE_BUFFER_SIZE} 大小的 ByteBuffer 作为中转，
	 * 简化「无需自定义缓冲区大小」的字节流传输场景。
	 * 
	 * @param input  源输入流（不可为 null）
	 * @param output 目标输出流（不可为 null）
	 * @return 成功传输的总字节数
	 * @throws IOException 流读取/写入过程中发生的 IO 异常
	 */
	public static long transfer(@NonNull InputStream input, @NonNull OutputStream output) throws IOException {
		return transfer(input, ByteBuffer.allocate(DEFAULT_BYTE_BUFFER_SIZE), output);
	}

	/**
	 * 重载方法：将 Readable 字符数据传输到 Appendable（使用默认字符缓冲区）。
	 * <p>
	 * 默认使用 {@link #DEFAULT_CHAR_BUFFER_SIZE} 大小的 CharBuffer 作为中转， 底层通过
	 * readable::read 构建 BufferFeeder，appendable::append 构建消费者， 实现字符数据的高效传输。
	 * 
	 * @param readable   源可读对象（不可为 null）
	 * @param appendable 目标可追加对象（不可为 null），如 StringBuilder、Writer 等
	 * @return 成功传输的总字符数
	 * @throws IOException 读取可读源或追加数据时发生的 IO 异常
	 */
	public static long transfer(@NonNull Readable readable, @NonNull Appendable appendable) throws IOException {
		return transfer(readable::read, CharBuffer.allocate(DEFAULT_CHAR_BUFFER_SIZE), appendable::append);
	}

	/**
	 * 将 InputStream 中的字节数据，经 ByteBuffer 中转传输到 OutputStream。
	 * <p>
	 * 底层将 InputStream 包装为 ReadableByteChannel，复用
	 * {@link #transfer(BufferFeeder, ByteBuffer, OutputStream)} 核心逻辑，
	 * 消除重复的通道包装代码，实现输入流到输出流的高效传输。
	 * 
	 * @param <B>    字节缓冲区类型（限定为 ByteBuffer 及其子类）
	 * @param input  源输入流（不可为 null），提供待传输的字节数据
	 * @param buffer 中转字节缓冲区（不可为 null），需提前分配有效容量
	 * @param output 目标输出流（不可为 null），接收最终传输的字节数据
	 * @return 成功传输的总字节数
	 * @throws IOException 流读取/写入过程中发生的 IO 异常
	 */
	public static long transfer(@NonNull InputStream input, @NonNull ByteBuffer buffer, @NonNull OutputStream output)
			throws IOException {
		ReadableByteChannel sourceChannel = Channels.newChannel(input);
		return transfer(sourceChannel::read, buffer, output);
	}

	/**
	 * 重载方法：从 InputStream 读取字节数据，经 ByteBuffer 中转传输到消费者。
	 * <p>
	 * 底层将 InputStream 包装为 ReadableByteChannel，复用
	 * {@link #transfer(BufferFeeder, Buffer, ThrowingConsumer)} 核心逻辑，
	 * 实现字节流到自定义消费者的便捷传输。
	 * 
	 * @param <B>      字节缓冲区类型（限定为 ByteBuffer 及其子类）
	 * @param <E>      消费者抛出的异常类型
	 * @param input    源输入流（不可为 null），提供待传输的字节数据
	 * @param buffer   中转字节缓冲区（不可为 null），需提前分配有效容量
	 * @param consumer 数据消费者（不可为 null），处理缓冲区中的字节数据
	 * @return 成功传输的总字节数
	 * @throws IOException 流读取或消费者处理数据时发生的 IO 异常
	 * @throws E           消费者执行过程中抛出的自定义异常
	 */
	public static <B extends ByteBuffer, E extends Throwable> long transfer(@NonNull InputStream input,
			@NonNull B buffer, @NonNull ThrowingConsumer<? super B, ? extends E> consumer) throws IOException, E {
		ReadableByteChannel channel = Channels.newChannel(input);
		return transfer(channel::read, buffer, consumer);
	}

	/**
	 * 重载方法：从 InputStream 读取字节数据，交由 BufferConsumer 消费（使用默认字节缓冲区）。
	 * <p>
	 * 默认使用 {@link #DEFAULT_BYTE_BUFFER_SIZE} 大小的字节缓冲区，简化「无需自定义缓冲区大小」的常用场景。
	 * 
	 * @param <E>            BufferConsumer 执行时抛出的异常类型
	 * @param input          源输入流（不可为 null）
	 * @param bufferConsumer 数据消费者（不可为 null），处理分段读取的字节数组
	 * @return 成功传输并消费的总字节数
	 * @throws IOException 读取输入流时抛出的 IO 异常
	 * @throws E           消费者执行过程中抛出的自定义异常
	 */
	public static <E extends Throwable> long transfer(@NonNull InputStream input,
			@NonNull BufferConsumer<? super byte[], ? extends E> bufferConsumer) throws E, IOException {
		return transfer(input, new byte[DEFAULT_BYTE_BUFFER_SIZE], bufferConsumer);
	}

	/**
	 * 从 InputStream 读取字节数据到指定字节数组，交由 BufferConsumer 消费。
	 * <p>
	 * 核心逻辑：分段读取字节数据到数组，每次读取后交由消费者处理，直至流结束（返回 EOF）， 适用于大字节流的分段消费，避免内存溢出。
	 * 
	 * @param <E>            BufferConsumer 执行时抛出的异常类型
	 * @param input          源输入流（不可为 null）
	 * @param buffer         字节数组缓冲区（不可为 null），用于分段读取数据
	 * @param bufferConsumer 数据消费者（不可为 null），处理读取到的字节数组
	 * @return 成功传输并消费的总字节数
	 * @throws IOException 读取输入流时抛出的 IO 异常
	 * @throws E           消费者执行过程中抛出的自定义异常
	 */
	public static <E extends Throwable> long transfer(@NonNull InputStream input, @NonNull byte[] buffer,
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
	 * 重载方法：从 InputStream 读取字节数据，经 ByteBuffer 中转交由消费者处理（使用默认字节缓冲区大小）。
	 * <p>
	 * 默认使用 {@link #DEFAULT_BYTE_BUFFER_SIZE} 大小的 ByteBuffer，简化缓冲区创建逻辑。
	 * 
	 * @param <E>      消费者抛出的异常类型
	 * @param input    源输入流（不可为 null）
	 * @param consumer 数据消费者（不可为 null），处理 ByteBuffer 中的字节数据
	 * @return 成功传输的总字节数
	 * @throws IOException 读取输入流或操作缓冲区时抛出的 IO 异常
	 * @throws E           消费者执行过程中抛出的自定义异常
	 */
	public static <E extends Throwable> long transfer(@NonNull InputStream input,
			@NonNull ThrowingConsumer<? super ByteBuffer, ? extends E> consumer) throws IOException, E {
		return transfer(input, ByteBuffer.allocate(DEFAULT_BYTE_BUFFER_SIZE), consumer);
	}

	/**
	 * 从 Readable 读取字符数据，经 CharBuffer 中转交由消费者处理（使用默认字符缓冲区）。
	 * <p>
	 * 默认使用 {@link #DEFAULT_CHAR_BUFFER_SIZE} 大小的 CharBuffer，底层通过 readable::read 构建
	 * BufferFeeder，实现字符数据的分段传输。
	 * 
	 * @param <E>      消费者抛出的异常类型
	 * @param readable 源可读对象（不可为 null）
	 * @param consumer 数据消费者（不可为 null），处理 CharBuffer 中的字符数据
	 * @return 成功传输的总字符数
	 * @throws IOException 读取可读源或操作缓冲区时抛出的 IO 异常
	 * @throws E           消费者执行过程中抛出的自定义异常
	 */
	public static <E extends Throwable> long transfer(@NonNull Readable readable,
			@NonNull ThrowingConsumer<? super CharBuffer, ? extends E> consumer) throws IOException, E {
		return transfer(readable::read, CharBuffer.allocate(DEFAULT_CHAR_BUFFER_SIZE), consumer);
	}

	/**
	 * 重载方法：从 Reader 读取字符数据，交由 BufferConsumer 消费（使用默认字符缓冲区）。
	 * <p>
	 * 默认使用 {@link #DEFAULT_CHAR_BUFFER_SIZE} 大小的字符数组，简化「无需自定义缓冲区大小」的常用场景。
	 * 
	 * @param <E>      BufferConsumer 执行时抛出的异常类型
	 * @param input    源 Reader（不可为 null）
	 * @param consumer 数据消费者（不可为 null），处理分段读取的字符数组
	 * @return 成功传输并消费的总字符数
	 * @throws IOException 读取 Reader 时抛出的 IO 异常
	 * @throws E           消费者执行过程中抛出的自定义异常
	 */
	public static <E extends Throwable> long transfer(@NonNull Reader input,
			@NonNull BufferConsumer<? super char[], ? extends E> consumer) throws E, IOException {
		return transfer(input, new char[DEFAULT_CHAR_BUFFER_SIZE], consumer);
	}

	/**
	 * 从 Reader 读取字符数据到指定字符数组，交由 BufferConsumer 消费。
	 * <p>
	 * 核心逻辑：分段读取字符数据到数组，每次读取后交由消费者处理，直至 Reader 结束（返回 EOF）， 适用于大字符流的分段消费，避免内存溢出。
	 * 
	 * @param <E>            BufferConsumer 执行时抛出的异常类型
	 * @param input          源 Reader（不可为 null）
	 * @param buffer         字符数组缓冲区（不可为 null），用于分段读取数据
	 * @param bufferConsumer 数据消费者（不可为 null），处理读取到的字符数组
	 * @return 成功传输并消费的总字符数
	 * @throws IOException 读取 Reader 时抛出的 IO 异常
	 * @throws E           消费者执行过程中抛出的自定义异常
	 */
	public static <E extends Throwable> long transfer(@NonNull Reader input, @NonNull char[] buffer,
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
	 * 从源 Buffer 转移数据到目标载体，交由消费者处理。
	 * <p>
	 * 核心特性：
	 * <ul>
	 * <li>支持 Buffer position 任意移动</li>
	 * <li>由 transferrer 自定义数据转移逻辑，适配不同类型的 Buffer/载体组合</li>
	 * <li>仅消费有效转移数据（transferredLength > 0），避免空消费</li>
	 * <li>最终恢复 Buffer 初始 position，保证状态安全</li>
	 * </ul>
	 * 
	 * @param <S>         源 Buffer 类型（如 ByteBuffer/CharBuffer）
	 * @param <B>         目标载体类型（如 byte[]/char[]）
	 * @param <E>         消费者抛出的异常类型
	 * @param source      源 Buffer（不可为 null），待转移数据的缓冲区
	 * @param buffer      目标载体（不可为 null），存储转移到的数据
	 * @param transferrer 数据转移器（不可为 null），定义从 source 到 buffer 的转移逻辑
	 * @param consumer    数据消费者（不可为 null），处理转移到的载体数据
	 * @return 累计转移的有效数据长度（字节/字符数）
	 * @throws E 转移或消费过程中抛出的自定义异常
	 */
	public static <S extends Buffer, B, E extends Throwable> long transfer(@NonNull S source, @NonNull B buffer,
			@NonNull BiConsumer<? super S, ? super B> transferrer,
			@NonNull BufferConsumer<? super B, ? extends E> consumer) throws E {

		long totalTransferred = 0;
		int initPos = source.position();
		try {
			while (source.hasRemaining()) {
				int pos = source.position();
				transferrer.accept(source, buffer);
				int transferredLength = Math.abs(source.position() - pos);
				if (transferredLength == 0 && !source.hasRemaining()) {
					break;
				}

				if (transferredLength > 0) {
					totalTransferred += transferredLength;
					consumer.accept(buffer, 0, transferredLength);
				}
			}
		} finally {
			source.position(initPos);
		}
		return totalTransferred;
	}
}