package run.soeasy.framework.io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;

import lombok.NonNull;
import run.soeasy.framework.core.function.Pipeline;
import run.soeasy.framework.core.function.ThrowingFunction;

/**
 * 输出流工厂包装器接口，用于装饰和增强现有{@link OutputStreamFactory}实例。
 * 该接口结合了{@link OutputStreamFactory}和{@link WriterFactoryWrapper}的功能，
 * 允许在不修改原始工厂的情况下扩展其行为。
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>装饰器模式：通过包装现有工厂实现非侵入式功能扩展</li>
 *   <li>透明代理：默认方法直接委派给被包装的工厂实例</li>
 *   <li>类型安全：通过泛型约束确保包装器与被包装对象的类型一致性</li>
 *   <li>链式调用：支持连续包装和功能叠加</li>
 * </ul>
 *
 * <p><b>使用场景：</b>
 * <ul>
 *   <li>增强现有输出流工厂的功能</li>
 *   <li>在输出流创建过程中添加统一的预处理或后处理逻辑</li>
 *   <li>实现工厂的链式组合和责任链模式</li>
 *   <li>动态添加编码转换或其他处理步骤</li>
 * </ul>
 *
 * @author soeasy.run
 * @param <O> 输出流类型
 * @param <W> 被包装的输出流工厂类型
 * @see OutputStreamFactory
 * @see WriterFactoryWrapper
 */
@FunctionalInterface
public interface OutputStreamFactoryWrapper<O extends OutputStream, W extends OutputStreamFactory<O>>
        extends OutputStreamFactory<O>, WriterFactoryWrapper<Writer, W> {

    /**
     * 获取输出流流水线，默认委派给被包装的工厂实现。
     * <p>
     * 该方法直接调用被包装工厂的{@link OutputStreamFactory#getOutputStreamPipeline()}方法，
     * 允许包装器在不改变原始逻辑的情况下拦截或增强流水线处理过程。
     * 
     * @return 输出流流水线
     */
    @Override
    default Pipeline<O, IOException> getOutputStreamPipeline() {
        return getSource().getOutputStreamPipeline();
    }

    /**
     * 获取输出流实例，默认委派给被包装的工厂实现。
     * <p>
     * 该方法直接调用被包装工厂的{@link OutputStreamFactory#getOutputStream()}方法，
     * 允许包装器在不改变原始逻辑的情况下拦截或增强输出流创建过程。
     * 
     * @return 输出流实例
     * @throws IOException 如果创建输出流过程中发生I/O错误
     */
    @Override
    default OutputStream getOutputStream() throws IOException {
        return getSource().getOutputStream();
    }

    /**
     * 获取用于创建Writer的流水线，默认委派给被包装的工厂实现。
     * <p>
     * 该方法直接调用被包装工厂的{@link OutputStreamFactory#getWriterPipeline()}方法，
     * 允许包装器在不改变原始逻辑的情况下拦截或增强Writer创建过程。
     * 
     * @return Writer流水线
     */
    @Override
    default @NonNull Pipeline<Writer, IOException> getWriterPipeline() {
        return getSource().getWriterPipeline();
    }

    /**
     * 为输出流添加自定义编码转换，默认委派给被包装的工厂实现。
     * <p>
     * 该方法直接调用被包装工厂的{@link OutputStreamFactory#encode(ThrowingFunction)}方法，
     * 允许包装器在不改变原始逻辑的情况下拦截或增强编码转换过程。
     * 
     * @param encoder 编码转换函数，不可为null
     * @param <T> 目标Writer类型
     * @return 带编码的输出流工厂
     */
    @Override
    default <T extends Writer> OutputStreamFactory<O> encode(
            @NonNull ThrowingFunction<? super O, ? extends T, IOException> encoder) {
        return getSource().encode(encoder);
    }

    /**
     * 为输出流添加指定字符集的编码转换，默认委派给被包装的工厂实现。
     * <p>
     * 该方法直接调用被包装工厂的{@link OutputStreamFactory#encode(Charset)}方法，
     * 允许包装器在不改变原始逻辑的情况下拦截或增强编码转换过程。
     * 
     * @param charset 字符集，不可为null
     * @return 带字符集编码的输出流工厂
     */
    @Override
    default OutputStreamFactory<O> encode(Charset charset) {
        return getSource().encode(charset);
    }

    /**
     * 为输出流添加指定字符集名称的编码转换，默认委派给被包装的工厂实现。
     * <p>
     * 该方法直接调用被包装工厂的{@link OutputStreamFactory#encode(String)}方法，
     * 允许包装器在不改变原始逻辑的情况下拦截或增强编码转换过程。
     * 
     * @param charsetName 字符集名称，不可为null
     * @return 带字符集编码的输出流工厂
     */
    @Override
    default OutputStreamFactory<O> encode(String charsetName) {
        return getSource().encode(charsetName);
    }

    /**
     * 判断输出流是否已编码，默认委派给被包装的工厂实现。
     * <p>
     * 该方法直接调用被包装工厂的{@link OutputStreamFactory#isEncoded()}方法，
     * 允许包装器在不改变原始逻辑的情况下拦截或增强编码状态判断。
     * 
     * @return true表示已编码，false表示未编码
     */
    @Override
    default boolean isEncoded() {
        return getSource().isEncoded();
    }

    /**
     * 将输出流转换为可写字节通道，默认委派给被包装的工厂实现。
     * <p>
     * 该方法直接调用被包装工厂的{@link OutputStreamFactory#writableChannel()}方法，
     * 允许包装器在不改变原始逻辑的情况下拦截或增强通道转换过程。
     * 
     * @return 可写字节通道
     * @throws IOException 如果转换过程中发生I/O错误
     */
    @Override
    default WritableByteChannel writableChannel() throws IOException {
        return getSource().writableChannel();
    }
}