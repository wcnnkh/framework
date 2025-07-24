package run.soeasy.framework.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

import lombok.NonNull;
import run.soeasy.framework.core.function.Pipeline;
import run.soeasy.framework.core.function.ThrowingFunction;

/**
 * 输入源接口，表示一个可读的输入源，扩展自{@link InputStreamFactory}。
 * 该接口提供了便捷的方法来获取输入流，并支持流式处理和编码转换功能。
 * 
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>统一接口：简化不同输入源的操作</li>
 *   <li>流水线支持：通过{@link Pipeline}实现流式处理</li>
 *   <li>编码转换：提供多种编码方式将字节流转换为字符流</li>
 *   <li>链式调用：支持连续的编码和转换操作</li>
 * </ul>
 * 
 * <p><b>使用场景：</b>
 * <ul>
 *   <li>文件读取：从文件获取输入内容</li>
 *   <li>网络输入：从网络连接读取数据</li>
 *   <li>内存输入：从内存缓冲区读取数据</li>
 *   <li>需要编码转换的输入场景</li>
 * </ul>
 * 
 * @author soeasy.run
 * @see InputStreamFactory
 * @see DecodedInputSource
 */
@FunctionalInterface
public interface InputSource extends InputStreamFactory<InputStream> {
    /**
     * 获取输入流实例，用于从此输入源读取数据。
     * 
     * <p>每次调用此方法都会返回一个新的输入流实例，
     * 通常需要在使用后调用{@link InputStream#close()}关闭流。
     * 
     * @return 输入流实例
     * @throws IOException 如果创建输入流时发生I/O错误
     */
    @Override
    InputStream getInputStream() throws IOException;

    /**
     * 获取用于创建输入流的流水线。
     * <p>
     * 默认实现使用{@link Pipeline#forCloseable}创建一个可关闭的流水线，
     * 该流水线会在关闭时自动调用输入流的{@link InputStream#close()}方法。
     * </p>
     * 
     * @return 输入流流水线
     */
    @Override
    default @NonNull Pipeline<InputStream, IOException> getInputStreamPipeline() {
        return Pipeline.forCloseable(this::getInputStream);
    }

    /**
     * 为输入源添加指定字符集的解码转换。
     * <p>
     * 该方法返回一个新的输入源，它会将读取的字节流按照指定的字符集
     * 转换为字符流，使用{@link InputStreamReader}实现转换。
     * </p>
     * 
     * @param charset 字符集，不可为null
     * @return 带字符集解码的输入源
     */
    @Override
    default InputSource decode(@NonNull Charset charset) {
        return new DecodedInputSource<>(this, charset, (e) -> new InputStreamReader(e, charset));
    }

    /**
     * 为输入源添加指定字符集名称的解码转换。
     * <p>
     * 该方法返回一个新的输入源，它会将读取的字节流按照指定的字符集名称
     * 转换为字符流，使用{@link InputStreamReader}实现转换。
     * </p>
     * 
     * @param charsetName 字符集名称，不可为null
     * @return 带字符集解码的输入源
     */
    @Override
    default InputSource decode(@NonNull String charsetName) {
        return new DecodedInputSource<>(this, charsetName, (e) -> new InputStreamReader(e, charsetName));
    }

    /**
     * 为输入源添加自定义解码转换。
     * <p>
     * 该方法返回一个新的输入源，它会将读取的内容通过指定的解码器函数
     * 进行转换，允许实现自定义的解码逻辑。
     * </p>
     * 
     * @param decoder 解码转换函数，不可为null
     * @param <T> 目标Reader类型
     * @return 带自定义解码的输入源
     */
    @Override
    default <T extends Reader> InputSource decode(
            @NonNull ThrowingFunction<? super InputStream, ? extends T, IOException> decoder) {
        return new DecodedInputSource<>(this, null, decoder);
    }
}