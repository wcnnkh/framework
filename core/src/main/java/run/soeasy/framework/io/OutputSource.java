package run.soeasy.framework.io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;

import lombok.NonNull;
import run.soeasy.framework.core.function.Pipeline;
import run.soeasy.framework.core.function.ThrowingFunction;

/**
 * 输出源接口，表示一个可写的输出目标，扩展自{@link OutputStreamFactory}。
 * 该接口提供了便捷的方法来获取输出流，并支持流式处理和编码转换功能。
 * 
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>统一接口：简化不同输出目标的操作</li>
 *   <li>流水线支持：通过{@link Pipeline}实现流式处理</li>
 *   <li>编码转换：提供多种编码方式将字节流转换为字符流</li>
 *   <li>链式调用：支持连续的编码和转换操作</li>
 * </ul>
 * 
 * <p><b>使用场景：</b>
 * <ul>
 *   <li>文件输出：将内容写入文件</li>
 *   <li>网络输出：将内容发送到网络连接</li>
 *   <li>内存输出：将内容写入内存缓冲区</li>
 *   <li>需要编码转换的输出场景</li>
 * </ul>
 * 
 * @author soeasy.run
 * @see OutputStreamFactory
 * @see EncodedOutputSource
 */
public interface OutputSource extends OutputStreamFactory<OutputStream> {
    /**
     * 获取输出流实例，用于向此输出源写入数据。
     * 
     * <p>每次调用此方法都会返回一个新的输出流实例，
     * 通常需要在使用后调用{@link OutputStream#close()}关闭流。
     * 
     * @return 输出流实例
     * @throws IOException 如果创建输出流时发生I/O错误
     */
    @Override
    OutputStream getOutputStream() throws IOException;

    /**
     * 获取用于创建输出流的流水线。
     * <p>
     * 默认实现使用{@link Pipeline#forCloseable}创建一个可关闭的流水线，
     * 该流水线会在关闭时自动调用输出流的{@link OutputStream#close()}方法。
     * </p>
     * 
     * @return 输出流流水线
     */
    @Override
    default @NonNull Pipeline<OutputStream, IOException> getOutputStreamPipeline() {
        return Pipeline.forCloseable(this::getOutputStream);
    }

    /**
     * 为输出源添加指定字符集的编码转换。
     * <p>
     * 该方法返回一个新的输出源，它会将写入的字符流按照指定的字符集
     * 转换为字节流，使用{@link OutputStreamWriter}实现转换。
     * </p>
     * 
     * @param charset 字符集，不可为null
     * @return 带字符集编码的输出源
     */
    @Override
    default OutputSource encode(Charset charset) {
        return new EncodedOutputSource<>(this, charset, (e) -> new OutputStreamWriter(e, charset));
    }

    /**
     * 为输出源添加指定字符集名称的编码转换。
     * <p>
     * 该方法返回一个新的输出源，它会将写入的字符流按照指定的字符集名称
     * 转换为字节流，使用{@link OutputStreamWriter}实现转换。
     * </p>
     * 
     * @param charsetName 字符集名称，不可为null
     * @return 带字符集编码的输出源
     */
    @Override
    default OutputSource encode(String charsetName) {
        return new EncodedOutputSource<>(this, charsetName, (e) -> new OutputStreamWriter(e, charsetName));
    }

    /**
     * 为输出源添加自定义编码转换。
     * <p>
     * 该方法返回一个新的输出源，它会将写入的内容通过指定的编码器函数
     * 进行转换，允许实现自定义的编码逻辑。
     * </p>
     * 
     * @param encoder 编码转换函数，不可为null
     * @param <T> 目标Writer类型
     * @return 带自定义编码的输出源
     */
    @Override
    default <T extends Writer> OutputSource encode(
            @NonNull ThrowingFunction<? super OutputStream, ? extends T, IOException> encoder) {
        return new EncodedOutputSource<>(this, null, encoder);
    }
}