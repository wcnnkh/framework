package run.soeasy.framework.io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;

import lombok.NonNull;
import run.soeasy.framework.core.function.Pipeline;
import run.soeasy.framework.core.function.ThrowingFunction;

/**
 * 输出流工厂接口，用于创建具有特定处理流程的{@link OutputStream}实例，
 * 并提供将输出流转换为{@link Writer}的功能。
 * 该接口扩展了{@link WriterFactory}，支持通过流水线处理输出操作，
 * 并提供编码转换、通道适配等高级功能。
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>流水线处理：通过{@link Pipeline}接口支持链式处理输出操作</li>
 *   <li>编码支持：提供多种编码转换方法，支持字符集指定</li>
 *   <li>资源管理：自动关闭输出流资源，避免内存泄漏</li>
 *   <li>通道适配：支持将输出流转换为{@link WritableByteChannel}</li>
 * </ul>
 *
 * <p><b>使用场景：</b>
 * <ul>
 *   <li>文件输出：创建带编码的文件输出流</li>
 *   <li>网络传输：构建带缓冲或加密的输出流管道</li>
 *   <li>数据转换：将字节流转换为字符流并应用编码</li>
 *   <li>通道操作：将输出流适配为NIO通道进行异步写入</li>
 * </ul>
 *
 * @author soeasy.run
 * @param <O> 输出流类型
 * @see OutputStream
 * @see WriterFactory
 * @see OutputStreamPipeline
 */
@FunctionalInterface
public interface OutputStreamFactory<O extends OutputStream> extends WriterFactory<Writer> {

    /**
     * 获取用于创建输出流的流水线。
     * <p>
     * 实现类必须提供此方法的具体实现，返回一个{@link Pipeline}实例，
     * 该实例定义了如何创建和处理特定类型的输出流。
     * 
     * @return 输出流流水线，不可为null
     * @throws IOException 如果获取流水线过程中发生I/O错误
     */
    @NonNull
    Pipeline<O, IOException> getOutputStreamPipeline();

    /**
     * 获取默认的输出流实例，该实例基于工厂定义的流水线处理。
     * <p>
     * 此方法返回一个{@link OutputStreamPipeline}实例，它将工厂提供的
     * 输出流管道应用于所有写入操作，实现统一的处理流程。
     * 
     * @return 基于流水线处理的输出流实例
     * @throws IOException 如果创建输出流过程中发生I/O错误
     * @see OutputStreamPipeline
     */
    default OutputStream getOutputStream() throws IOException {
        return new OutputStreamPipeline(getOutputStreamPipeline());
    }

    /**
     * 获取用于创建Writer的流水线，默认将输出流转换为Writer。
     * <p>
     * 该方法将输出流流水线映射为Writer流水线，使用{@link OutputStreamWriter}
     * 实现字节流到字符流的转换，并确保关闭时释放资源。
     * 
     * @return Writer流水线
     */
    @Override
    default @NonNull Pipeline<Writer, IOException> getWriterPipeline() {
        return getOutputStreamPipeline().map((e) -> (Writer) new OutputStreamWriter(e)).onClose((e) -> e.close());
    }

    /**
     * 判断输出流是否已编码。
     * <p>
     * 默认为false，表示未应用编码转换。子类可重写此方法以提供编码状态。
     * 
     * @return true表示已编码，false表示未编码
     */
    default boolean isEncoded() {
        return false;
    }

    /**
     * 为输出流添加自定义编码转换。
     * <p>
     * 该方法返回一个新的输出流工厂，将原始输出流通过指定的编码器
     * 转换为目标Writer类型，支持自定义编码逻辑。
     * 
     * @param encoder 编码转换函数，不可为null
     * @param <T> 目标Writer类型
     * @return 带编码的输出流工厂
     */
    default <T extends Writer> OutputStreamFactory<O> encode(
            @NonNull ThrowingFunction<? super O, ? extends T, IOException> encoder) {
        return new EncodedOutputStreamFactory<>(this, null, encoder);
    }

    /**
     * 为输出流添加指定字符集的编码转换。
     * <p>
     * 该方法返回一个新的输出流工厂，使用指定的{@link Charset}
     * 将输出流转换为{@link OutputStreamWriter}。
     * 
     * @param charset 字符集，不可为null
     * @return 带字符集编码的输出流工厂
     */
    default OutputStreamFactory<O> encode(Charset charset) {
        return new EncodedOutputStreamFactory<>(this, charset, (e) -> new OutputStreamWriter(e, charset));
    }

    /**
     * 为输出流添加指定字符集名称的编码转换。
     * <p>
     * 该方法返回一个新的输出流工厂，使用指定的字符集名称
     * 将输出流转换为{@link OutputStreamWriter}。
     * 
     * @param charsetName 字符集名称，不可为null
     * @return 带字符集编码的输出流工厂
     */
    default OutputStreamFactory<O> encode(String charsetName) {
        return new EncodedOutputStreamFactory<>(this, charsetName, (e) -> new OutputStreamWriter(e, charsetName));
    }

    /**
     * 将输出流转换为可写字节通道。
     * <p>
     * 该方法使用{@link Channels#newChannel(OutputStream)}
     * 将当前输出流转换为{@link WritableByteChannel}，
     * 适用于需要NIO通道操作的场景。
     * 
     * @return 可写字节通道
     * @throws IOException 如果转换过程中发生I/O错误
     */
    default WritableByteChannel writableChannel() throws IOException {
        return Channels.newChannel(getOutputStream());
    }
}