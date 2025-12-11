package run.soeasy.framework.io;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.stream.Stream;

import lombok.NonNull;
import run.soeasy.framework.core.function.Pipeline;
import run.soeasy.framework.core.streaming.Streamable;

/**
 * 读取器工厂接口，用于创建具有特定处理流程的{@link Reader}实例。
 * 该接口定义了获取读取器管道的方法，并提供了一系列默认方法，
 * 支持将读取器转换为字符序列、按行读取或传输数据到写入器。
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>流水线处理：通过{@link Pipeline}接口支持链式处理读取操作</li>
 *   <li>资源管理：自动关闭读取器资源，避免内存泄漏</li>
 *   <li>流式操作：提供按行读取和数据传输的便捷方法</li>
 * </ul>
 *
 * <p><b>使用场景：</b>
 * <ul>
 *   <li>文件读取：创建文件读取器并处理文本内容</li>
 *   <li>数据转换：将读取内容转换为字符序列或行流</li>
 *   <li>管道操作：将数据从读取器传输到写入器</li>
 * </ul>
 *
 * @author soeasy.run
 * @param <R> 读取器类型
 * @see Reader
 * @see Pipeline
 * @see ReaderPipeline
 */
@FunctionalInterface
public interface ReaderFactory<R extends Reader> {
    /**
     * 获取用于创建读取器的流水线。
     * <p>
     * 实现类必须提供此方法的具体实现，返回一个{@link Pipeline}实例，
     * 该实例定义了如何创建和处理特定类型的读取器。
     * 
     * @return 读取器流水线，不可为null
     */
    @NonNull
    Pipeline<R, IOException> getReaderPipeline();

    /**
     * 获取默认的读取器实例，该实例基于工厂定义的流水线处理。
     * <p>
     * 此方法返回一个{@link ReaderPipeline}实例，它将工厂提供的
     * 读取器管道应用于所有读取操作，实现统一的处理流程。
     * 
     * @return 基于流水线处理的读取器实例
     * @throws IOException 如果创建读取器过程中发生I/O错误
     * @see ReaderPipeline
     */
    default Reader getReader() throws IOException {
        return new ReaderPipeline(getReaderPipeline());
    }

    /**
     * 将读取器内容转换为字符序列。
     * <p>
     * 该方法读取整个输入流并将其转换为字符序列，自动处理资源关闭。
     * 注意：对于大文件，可能会导致内存问题，建议使用流式处理方法。
     * 
     * @return 读取器内容的字符序列表示
     * @throws IOException 如果读取过程中发生I/O错误
     */
    default CharSequence toCharSequence() throws IOException {
        Reader reader = getReader();
        try {
            return IOUtils.toCharSequence(reader);
        } finally {
            reader.close();
        }
    }

    /**
     * 按行读取内容并返回元素流。
     * <p>
     * 该方法返回一个{@link Streamable}实例，支持惰性读取和流式处理。
     * 流关闭时会自动释放读取器资源。
     * 
     * @return 按行读取的元素流
     */
    default Streamable<String> readLines() {
        return Streamable.of(() -> {
            try {
                Pipeline<R, IOException> channel = getReaderPipeline();
                return IOUtils.readLines(channel.get()).onClose(() -> {
                    try {
                        channel.close();
                    } catch (IOException e) {
                        // ignore
                    }
                });
            } catch (IOException ignore) {
            }
            return Stream.empty();
        });
    }

    /**
     * 将读取器内容传输到指定的写入器工厂。
     * <p>
     * 该方法将当前读取器的全部内容传输到目标写入器，自动处理
     * 资源的打开和关闭，确保数据传输完成后所有资源被正确释放。
     * 
     * @param <T> Writer的具体类型
     * @param dest 目标写入器工厂，不可为null
     * @return 传输的字符数
     * @throws IOException 如果传输过程中发生I/O错误
     */
    default <T extends Writer> long transferTo(@NonNull WriterFactory<? extends T> dest) throws IOException {
        Reader input = getReader();
        try {
            Writer out = dest.getWriter();
            try {
                return IOUtils.transferTo(input, out::write);
            } finally {
                out.close();
            }
        } finally {
            input.close();
        }
    }
}