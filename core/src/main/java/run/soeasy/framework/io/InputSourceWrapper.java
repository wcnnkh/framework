package run.soeasy.framework.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;

import lombok.NonNull;
import run.soeasy.framework.core.function.Pipeline;
import run.soeasy.framework.core.function.ThrowingFunction;

/**
 * 输入源包装器接口，用于装饰和增强现有{@link InputSource}实例。
 * 该接口结合了{@link InputSource}和{@link InputStreamFactoryWrapper}的功能，
 * 允许在不修改原始输入源的情况下扩展其行为。
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>装饰器模式：通过包装现有输入源实现非侵入式功能扩展</li>
 *   <li>透明代理：所有方法默认委派给被包装的输入源实例</li>
 *   <li>类型安全：通过泛型约束确保包装器与被包装对象的类型一致性</li>
 *   <li>链式调用：支持连续包装多个装饰器以叠加功能</li>
 * </ul>
 *
 * <p><b>使用场景：</b>
 * <ul>
 *   <li>为现有输入源添加缓冲、压缩等额外功能</li>
 *   <li>在输入数据前添加统一的格式校验或解密逻辑</li>
 *   <li>动态切换输入源的解码方式而不修改原始实现</li>
 *   <li>实现输入源的责任链模式以处理不同类型的输入</li>
 * </ul>
 *
 * @author soeasy.run
 * @param <W> 被包装的输入源类型
 * @see InputSource
 * @see InputStreamFactoryWrapper
 */
@FunctionalInterface
public interface InputSourceWrapper<W extends InputSource>
        extends InputSource, InputStreamFactoryWrapper<InputStream, W> {

    /**
     * 获取输入流实例，默认委派给被包装的输入源实现。
     * <p>
     * 该方法直接调用被包装输入源的{@link InputSource#getInputStream()}方法，
     * 允许包装器在不改变原始逻辑的情况下拦截或增强输入流创建过程。
     * </p>
     * 
     * @return 输入流实例
     * @throws IOException 如果创建输入流过程中发生I/O错误
     */
    @Override
    default InputStream getInputStream() throws IOException {
        return getSource().getInputStream();
    }

    /**
     * 获取输入流流水线，默认委派给被包装的输入源实现。
     * <p>
     * 该方法直接调用被包装输入源的{@link InputSource#getInputStreamPipeline()}方法，
     * 允许包装器在不改变原始逻辑的情况下拦截或增强流水线处理过程。
     * </p>
     * 
     * @return 输入流流水线
     */
    @Override
    default @NonNull Pipeline<InputStream, IOException> getInputStreamPipeline() {
        return getSource().getInputStreamPipeline();
    }

    /**
     * 为输入源添加指定字符集的解码转换，默认委派给被包装的输入源实现。
     * <p>
     * 该方法直接调用被包装输入源的{@link InputSource#decode(Charset)}方法，
     * 允许包装器在不改变原始逻辑的情况下拦截或增强解码转换过程。
     * </p>
     * 
     * @param charset 字符集，不可为null
     * @return 带字符集解码的输入源
     */
    @Override
    default InputSource decode(@NonNull Charset charset) {
        return getSource().decode(charset);
    }

    /**
     * 为输入源添加指定字符集名称的解码转换，默认委派给被包装的输入源实现。
     * <p>
     * 该方法直接调用被包装输入源的{@link InputSource#decode(String)}方法，
     * 允许包装器在不改变原始逻辑的情况下拦截或增强解码转换过程。
     * </p>
     * 
     * @param charsetName 字符集名称，不可为null
     * @return 带字符集解码的输入源
     */
    @Override
    default InputSource decode(@NonNull String charsetName) {
        return getSource().decode(charsetName);
    }

    /**
     * 为输入源添加自定义解码转换，默认委派给被包装的输入源实现。
     * <p>
     * 该方法直接调用被包装输入源的{@link InputSource#decode(ThrowingFunction)}方法，
     * 允许包装器在不改变原始逻辑的情况下拦截或增强解码转换过程。
     * </p>
     * 
     * @param decoder 解码转换函数，不可为null
     * @param <T> 目标Reader类型
     * @return 带自定义解码的输入源
     */
    @Override
    default <T extends Reader> InputSource decode(
            @NonNull ThrowingFunction<? super InputStream, ? extends T, IOException> decoder) {
        return getSource().decode(decoder);
    }
}