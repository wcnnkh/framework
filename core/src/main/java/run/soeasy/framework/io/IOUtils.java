package run.soeasy.framework.io;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import run.soeasy.framework.core.collection.CollectionUtils;
import run.soeasy.framework.core.function.OffsetConsumer;
import run.soeasy.framework.core.page.OffsetCopier;

import java.io.*;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.Arrays;
import java.util.function.BiConsumer;
import java.util.function.IntFunction;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

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
 * <li>兼容扩展：通过 {@link OffsetCopier} {@link OffsetConsumer}
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
 * @see OffsetConsumer
 * @see OffsetCopier
 */
@UtilityClass
public final class IOUtils {
    /**
     * 空字节数组常量，用于创建空输入流，避免重复实例化空数组
     */
    public static final byte[] EMPTY_CONTENT = new byte[0];
    /**
     * 输入流/Reader 的结束标记（End of File），表示无更多数据可读
     */
    public static final int EOF = -1;
    public static final CharBuffer EMPTY_CHAR_BUFFER = CharBuffer.allocate(0);
    public static final ByteBuffer EMPTY_BYTE_BUFFER = ByteBuffer.allocate(0);
    /**
     * A singleton.
     */
    public static final OutputStream NULL_OUTPUT_STREAM = new NullOutputStream();
    /**
     * 空输入流单例（内容为空的 ByteArrayInputStream），单例模式避免重复创建
     */
    private static final InputStream EMPTY_INPUT_STREAM = new ByteArrayInputStream(EMPTY_CONTENT);
    /**
     * 日志记录器，用于记录静默关闭资源时的异常（FINEST 级别，不影响正常流程）
     */
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
     * 该拷贝器严格适配 {@link OffsetCopier} 接口签名，实现批量数据拷贝。
     *
     * @param <E>      BufferConsumer 执行时抛出的异常类型
     * @param source   源 ByteBuffer（不可为 null），提供待拷贝的字节数据
     * @param buffer   目标字节数组（不可为 null），用于暂存拷贝的数据
     * @param consumer 数据消费者（不可为 null），处理拷贝后的字节数组
     * @return 成功拷贝并消费的总字节数
     * @throws E BufferConsumer 消费数据时抛出的自定义异常
     */
    public static <S extends Buffer, B, E extends Throwable> long copy(@NonNull ByteBuffer source,
                                                                       @NonNull byte[] buffer, @NonNull OffsetConsumer<? super byte[], ? extends E> consumer) throws E {
        return copy(source, buffer, buffer.length, ByteBuffer::get, consumer);
    }

    /**
     * 通用 Buffer 数据拷贝方法：从源 Buffer 分段拷贝数据到目标数组，交由消费者处理。
     * <p>
     * 核心优化点：
     * <ul>
     * <li>优先使用源 Buffer 的底层数组（hasArray()）直接拷贝，减少内存复制</li>
     * <li>无底层数组时，调用自定义 {@link OffsetCopier} 完成数据拷贝</li>
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
                                                                       int chunkLength, @NonNull OffsetCopier<? super S, ? super B> copier,
                                                                       @NonNull OffsetConsumer<? super B, ? extends E> consumer) throws E {
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
        return NULL_OUTPUT_STREAM;
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
     * 重载方法，默认使用 {@link Text#DEFAULT_CHAR_BUFFER_SIZE} 大小的字符缓冲区， 简化「无需自定义缓冲区大小」的常用场景。
     *
     * @param source    源可读对象（不可为 null）
     * @param delimiter 分隔符（不可为 null）
     * @return 分割后的 CharSequence 流（非 null）
     */
    public static Stream<CharSequence> split(Readable source, CharSequence delimiter) {
        return split(source, CharBuffer.allocate(Text.DEFAULT_CHAR_BUFFER_SIZE), delimiter);
    }

    /**
     * 重载方法：将 ByteBuffer 剩余数据转换为字节数组。
     * <p>
     * 默认使用 byte[]::new 作为数组创建函数，内置 {@link ByteBuffer#get(byte[], int, int)} 作为拷贝器，
     * 该拷贝器严格适配 {@link OffsetCopier} 接口签名，实现批量数据拷贝。
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
     * 该拷贝器严格适配 {@link OffsetCopier} 接口签名，实现批量数据拷贝。
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
     * 底层复用 {@link #copy(Buffer, Object, int, OffsetCopier, OffsetConsumer)}
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
                                                                       @NonNull IntFunction<? extends A> arrayFactory, @NonNull OffsetCopier<? super S, ? super A> copier)
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
                                                                           @NonNull OffsetConsumer<? super B, ? extends E> consumer) throws E {

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

    /**
     * 包装输入流，使其忽略关闭操作。
     *
     * <p>返回的包装流在调用 {@link InputStream#close()} 时不会关闭底层输入流。
     *
     * @param inputStream 原始输入流（不可为 null）
     * @return 忽略关闭操作的包装输入流
     */
    public static InputStream ignoreClose(@NonNull InputStream inputStream) {
        return new FilterInputStream(inputStream) {
            @Override
            public void close() throws IOException {
                // 忽略关闭操作
            }
        };
    }

    /**
     * 包装输出流，使其忽略关闭操作。
     *
     * <p>返回的包装流在调用 {@link OutputStream#close()} 时不会关闭底层输出流。
     *
     * @param outputStream 原始输出流（不可为 null）
     * @return 忽略关闭操作的包装输出流
     */
    public static OutputStream ignoreClose(@NonNull OutputStream outputStream) {
        return new FilterOutputStream(outputStream) {
            @Override
            public void close() throws IOException {
                // 忽略关闭操作
            }
        };
    }

    /**
     * 包装字符读取器，使其忽略关闭操作。
     *
     * <p>返回的包装器在调用 {@link Reader#close()} 时不会关闭底层读取器。
     *
     * @param reader 原始字符读取器（不可为 null）
     * @return 忽略关闭操作的包装字符读取器
     */
    public static Reader ignoreClose(@NonNull Reader reader) {
        return new FilterReader(reader) {
            @Override
            public void close() throws IOException {
                // 忽略关闭操作
            }
        };
    }

    /**
     * 包装字符写入器，使其忽略关闭操作。
     *
     * <p>返回的包装器在调用 {@link Writer#close()} 时不会关闭底层写入器。
     *
     * @param writer 原始字符写入器（不可为 null）
     * @return 忽略关闭操作的包装字符写入器
     */
    public static Writer ignoreClose(@NonNull Writer writer) {
        return new FilterWriter(writer) {
            @Override
            public void close() throws IOException {
                // 忽略关闭操作
            }
        };
    }

    public static Binary binary() {
        return Binary.INSTANCE;
    }

    public static Text text() {
        return Text.INSTANCE;
    }
}