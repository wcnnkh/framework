package run.soeasy.framework.io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.charset.Charset;

import lombok.NonNull;
import run.soeasy.framework.core.function.Pipeline;
import run.soeasy.framework.core.function.ThrowingFunction;

/**
 * 输出源包装器接口，用于装饰和增强现有{@link OutputSource}实例。
 * 该接口结合了{@link OutputSource}和{@link OutputStreamFactoryWrapper}的功能，
 * 允许在不修改原始输出源的情况下扩展其行为。
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>装饰器模式：通过包装现有输出源实现非侵入式功能扩展</li>
 *   <li>透明代理：所有方法默认委派给被包装的输出源实例</li>
 *   <li>类型安全：通过泛型约束确保包装器与被包装对象的类型一致性</li>
 *   <li>链式调用：支持连续包装多个装饰器以叠加功能</li>
 * </ul>
 *
 * <p><b>使用场景：</b>
 * <ul>
 *   <li>为现有输出源添加缓冲、压缩等额外功能</li>
 *   <li>在输出数据前添加统一的格式校验或加密逻辑</li>
 *   <li>动态切换输出源的编码方式而不修改原始实现</li>
 *   <li>实现输出源的责任链模式以处理不同类型的输出</li>
 * </ul>
 *
 * @author soeasy.run
 * @param <W> 被包装的输出源类型
 * @see OutputSource
 * @see OutputStreamFactoryWrapper
 */
@FunctionalInterface
public interface OutputSourceWrapper<W extends OutputSource>
        extends OutputSource, OutputStreamFactoryWrapper<OutputStream, W> {

    /**
     * 获取输出流实例，默认委派给被包装的输出源实现。
     * <p>
     * 该方法直接调用被包装输出源的{@link OutputSource#getOutputStream()}方法，
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
     * 获取输出流流水线，默认委派给被包装的输出源实现。
     * <p>
     * 该方法直接调用被包装输出源的{@link OutputSource#getOutputStreamPipeline()}方法，
     * 允许包装器在不改变原始逻辑的情况下拦截或增强流水线处理过程。
     * 
     * @return 输出流流水线
     */
    @Override
    default @NonNull Pipeline<OutputStream, IOException> getOutputStreamPipeline() {
        return getSource().getOutputStreamPipeline();
    }

    /**
     * 为输出源添加指定字符集的编码转换，默认委派给被包装的输出源实现。
     * <p>
     * 该方法直接调用被包装输出源的{@link OutputSource#encode(Charset)}方法，
     * 允许包装器在不改变原始逻辑的情况下拦截或增强编码转换过程。
     * 
     * @param charset 字符集，不可为null
     * @return 带字符集编码的输出源
     */
    @Override
    default OutputSource encode(Charset charset) {
        return getSource().encode(charset);
    }

    /**
     * 为输出源添加指定字符集名称的编码转换，默认委派给被包装的输出源实现。
     * <p>
     * 该方法直接调用被包装输出源的{@link OutputSource#encode(String)}方法，
     * 允许包装器在不改变原始逻辑的情况下拦截或增强编码转换过程。
     * 
     * @param charsetName 字符集名称，不可为null
     * @return 带字符集编码的输出源
     */
    @Override
    default OutputSource encode(String charsetName) {
        return getSource().encode(charsetName);
    }

    /**
     * 为输出源添加自定义编码转换，默认委派给被包装的输出源实现。
     * <p>
     * 该方法直接调用被包装输出源的{@link OutputSource#encode(ThrowingFunction)}方法，
     * 允许包装器在不改变原始逻辑的情况下拦截或增强编码转换过程。
     * 
     * @param encoder 编码转换函数，不可为null
     * @param <T> 目标Writer类型
     * @return 带自定义编码的输出源
     */
    @Override
    default <T extends Writer> OutputSource encode(
            @NonNull ThrowingFunction<? super OutputStream, ? extends T, IOException> encoder) {
        return getSource().encode(encoder);
    }
}