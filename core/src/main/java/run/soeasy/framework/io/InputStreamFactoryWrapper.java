package run.soeasy.framework.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.nio.file.Path;

import lombok.NonNull;
import run.soeasy.framework.core.function.Pipeline;
import run.soeasy.framework.core.function.ThrowingFunction;

/**
 * 输入流工厂包装器接口，用于装饰和增强现有{@link InputStreamFactory}实例。
 * 该接口结合了{@link InputStreamFactory}和{@link ReaderFactoryWrapper}的功能，
 * 允许在不修改原始工厂的情况下扩展其行为。
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>装饰器模式：通过包装现有工厂实现非侵入式功能扩展</li>
 *   <li>透明代理：所有方法默认委派给被包装的工厂实例</li>
 *   <li>类型安全：通过泛型约束确保包装器与被包装对象的类型一致性</li>
 *   <li>链式调用：支持连续包装多个装饰器以叠加功能</li>
 * </ul>
 *
 * <p><b>使用场景：</b>
 * <ul>
 *   <li>为现有输入流工厂添加缓冲、压缩等额外功能</li>
 *   <li>在输入数据前添加统一的格式校验或解密逻辑</li>
 *   <li>动态切换输入流的解码方式而不修改原始实现</li>
 *   <li>实现输入流工厂的责任链模式以处理不同类型的输入</li>
 * </ul>
 *
 * @author soeasy.run
 * @param <I> 输入流类型
 * @param <W> 被包装的输入流工厂类型
 * @see InputStreamFactory
 * @see ReaderFactoryWrapper
 */
@FunctionalInterface
public interface InputStreamFactoryWrapper<I extends InputStream, W extends InputStreamFactory<I>>
        extends InputStreamFactory<I>, ReaderFactoryWrapper<Reader, W> {

    /**
     * 为输入流添加指定字符集的解码转换，默认委派给被包装的工厂实现。
     * <p>
     * 该方法直接调用被包装工厂的{@link InputStreamFactory#decode(Charset)}方法，
     * 允许包装器在不改变原始逻辑的情况下拦截或增强解码转换过程。
     * 
     * @param charset 字符集，不可为null
     * @return 带字符集解码的输入流工厂
     */
    @Override
    default InputStreamFactory<I> decode(Charset charset) {
        return getSource().decode(charset);
    }

    /**
     * 为输入流添加指定字符集名称的解码转换，默认委派给被包装的工厂实现。
     * <p>
     * 该方法直接调用被包装工厂的{@link InputStreamFactory#decode(String)}方法，
     * 允许包装器在不改变原始逻辑的情况下拦截或增强解码转换过程。
     * 
     * @param charsetName 字符集名称，不可为null
     * @return 带字符集解码的输入流工厂
     */
    @Override
    default InputStreamFactory<I> decode(String charsetName) {
        return getSource().decode(charsetName);
    }

    /**
     * 为输入流添加自定义解码转换，默认委派给被包装的工厂实现。
     * <p>
     * 该方法直接调用被包装工厂的{@link InputStreamFactory#decode(ThrowingFunction)}方法，
     * 允许包装器在不改变原始逻辑的情况下拦截或增强解码转换过程。
     * 
     * @param decoder 解码转换函数，不可为null
     * @param <T> 目标Reader类型
     * @return 带自定义解码的输入流工厂
     */
    @Override
    default <T extends Reader> InputStreamFactory<I> decode(
            @NonNull ThrowingFunction<? super I, ? extends T, IOException> decoder) {
        return getSource().decode(decoder);
    }

    /**
     * 获取输入流实例，默认委派给被包装的工厂实现。
     * <p>
     * 该方法直接调用被包装工厂的{@link InputStreamFactory#getInputStream()}方法，
     * 允许包装器在不改变原始逻辑的情况下拦截或增强输入流创建过程。
     * 
     * @return 输入流实例
     * @throws IOException 如果创建输入流过程中发生I/O错误
     */
    @Override
    default InputStream getInputStream() throws IOException {
        return getSource().getInputStream();
    }

    /**
     * 获取输入流流水线，默认委派给被包装的工厂实现。
     * <p>
     * 该方法直接调用被包装工厂的{@link InputStreamFactory#getInputStreamPipeline()}方法，
     * 允许包装器在不改变原始逻辑的情况下拦截或增强流水线处理过程。
     * 
     * @return 输入流流水线
     */
    @Override
    default Pipeline<I, IOException> getInputStreamPipeline() {
        return getSource().getInputStreamPipeline();
    }

    /**
     * 获取用于创建Reader的流水线，默认委派给被包装的工厂实现。
     * <p>
     * 该方法直接调用被包装工厂的{@link InputStreamFactory#getReaderPipeline()}方法，
     * 允许包装器在不改变原始逻辑的情况下拦截或增强Reader创建过程。
     * 
     * @return Reader流水线
     */
    @Override
    default Pipeline<Reader, IOException> getReaderPipeline() {
        return getSource().getReaderPipeline();
    }

    /**
     * 判断输入流是否已解码，默认委派给被包装的工厂实现。
     * <p>
     * 该方法直接调用被包装工厂的{@link InputStreamFactory#isDecoded()}方法，
     * 允许包装器在不改变原始逻辑的情况下拦截或增强解码状态判断。
     * 
     * @return true表示已解码，false表示未解码
     */
    @Override
    default boolean isDecoded() {
        return getSource().isDecoded();
    }

    /**
     * 将输入流转换为可读字节通道，默认委派给被包装的工厂实现。
     * <p>
     * 该方法直接调用被包装工厂的{@link InputStreamFactory#readableChannel()}方法，
     * 允许包装器在不改变原始逻辑的情况下拦截或增强通道转换过程。
     * 
     * @return 可读字节通道
     * @throws IOException 如果转换过程中发生I/O错误
     */
    @Override
    default ReadableByteChannel readableChannel() throws IOException {
        return getSource().readableChannel();
    }

    /**
     * 将输入流内容转换为字节数组，默认委派给被包装的工厂实现。
     * <p>
     * 该方法直接调用被包装工厂的{@link InputStreamFactory#toByteArray()}方法，
     * 允许包装器在不改变原始逻辑的情况下拦截或增强字节数组转换过程。
     * 
     * @return 输入流内容的字节数组表示
     * @throws IOException 如果读取过程中发生I/O错误
     */
    @Override
    default byte[] toByteArray() throws IOException {
        return getSource().toByteArray();
    }

    /**
     * 将输入流内容传输到指定文件，默认委派给被包装的工厂实现。
     * <p>
     * 该方法直接调用被包装工厂的{@link InputStreamFactory#transferTo(File)}方法，
     * 允许包装器在不改变原始逻辑的情况下拦截或增强数据传输过程。
     * 
     * @param dest 目标文件，不可为null
     * @throws IOException 如果传输过程中发生I/O错误
     * @throws IllegalStateException 如果目标文件无法写入
     */
    @Override
    default void transferTo(@NonNull File dest) throws IOException, IllegalStateException {
        getSource().transferTo(dest);
    }

    /**
     * 将输入流内容传输到指定的输出流工厂，默认委派给被包装的工厂实现。
     * <p>
     * 该方法直接调用被包装工厂的{@link InputStreamFactory#transferTo(OutputStreamFactory)}方法，
     * 允许包装器在不改变原始逻辑的情况下拦截或增强数据传输过程。
     * 
     * @param dest 目标输出流工厂，不可为null
     * @return 传输的字节数
     * @throws IOException 如果传输过程中发生I/O错误
     */
    @Override
    default <R extends OutputStream> long transferTo(@NonNull OutputStreamFactory<? extends R> dest)
            throws IOException {
        return getSource().transferTo(dest);
    }

    /**
     * 将输入流内容传输到指定路径，默认委派给被包装的工厂实现。
     * <p>
     * 该方法直接调用被包装工厂的{@link InputStreamFactory#transferTo(Path)}方法，
     * 允许包装器在不改变原始逻辑的情况下拦截或增强数据传输过程。
     * 
     * @param dest 目标路径，不可为null
     * @throws IOException 如果传输过程中发生I/O错误
     */
    @Override
    default void transferTo(@NonNull Path dest) throws IOException {
        getSource().transferTo(dest);
    }
}