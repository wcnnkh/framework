package run.soeasy.framework.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;

import lombok.NonNull;
import run.soeasy.framework.core.function.ThrowingFunction;

/**
 * 资源包装器接口，用于装饰和增强现有{@link Resource}实例。
 * 该接口继承{@link Resource}并整合{@link InputSourceWrapper}和{@link OutputSourceWrapper}，
 * 实现对资源的透明代理和功能扩展。
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>装饰器模式：通过包装现有资源实现非侵入式功能扩展</li>
 *   <li>透明代理：所有方法默认委派给被包装的资源实例</li>
 *   <li>类型安全：通过泛型约束确保包装器与被包装资源的类型一致性</li>
 *   <li>功能叠加：支持连续包装多个装饰器以叠加编码、重命名等功能</li>
 * </ul>
 *
 * <p><b>使用场景：</b>
 * <ul>
 *   <li>为资源添加加密/解密功能而不修改原始实现</li>
 *   <li>动态切换资源的字符集编码方式</li>
 *   <li>在资源操作前后添加日志记录或权限校验</li>
 *   <li>实现资源的责任链模式以处理不同类型的操作</li>
 * </ul>
 *
 * @author soeasy.run
 * @param <W> 被包装的资源类型，需继承{@link Resource}
 * @see Resource
 * @see InputSourceWrapper
 * @see OutputSourceWrapper
 */
@FunctionalInterface
public interface ResourceWrapper<W extends Resource> extends Resource, InputSourceWrapper<W>, OutputSourceWrapper<W> {

    /**
     * 获取被包装资源的名称，默认委派给被包装资源。
     * <p>
     * 该方法直接调用{@link Resource#getName()}，
     * 允许包装器在不改变原始逻辑的情况下拦截名称获取过程。
     * 
     * @return 被包装资源的名称
     */
    @Override
    default String getName() {
        return getSource().getName();
    }

    /**
     * 判断被包装资源是否存在，默认委派给被包装资源。
     * <p>
     * 该方法直接调用{@link Resource#exists()}，
     * 通过检查资源的可读/可写状态确定存在性。
     * 
     * @return true表示资源存在，false表示不存在
     */
    @Override
    default boolean exists() {
        return getSource().exists();
    }

    /**
     * 获取被包装资源的描述信息，默认委派给被包装资源。
     * <p>
     * 该方法直接调用{@link Resource#getDescription()}，
     * 通常返回资源实现类的全限定名。
     * 
     * @return 资源描述字符串
     */
    @Override
    default String getDescription() {
        return getSource().getDescription();
    }

    /**
     * 获取被包装资源的最后修改时间，默认委派给被包装资源。
     * <p>
     * 该方法直接调用{@link Resource#lastModified()}，
     * 实现类需根据资源类型返回实际修改时间（如文件资源）。
     * 
     * @return 最后修改时间戳（毫秒）
     * @throws IOException 当获取修改时间时发生I/O错误
     */
    @Override
    default long lastModified() throws IOException {
        return getSource().lastModified();
    }

    /**
     * 获取被包装资源的内容长度，默认委派给被包装资源。
     * <p>
     * 该方法直接调用{@link Resource#contentLength()}，
     * 长度未知时返回-1（如流式资源）。
     * 
     * @return 内容长度，未知时返回-1
     * @throws IOException 当获取长度时发生I/O错误
     */
    @Override
    default long contentLength() throws IOException {
        return getSource().contentLength();
    }

    /**
     * 判断被包装资源是否已打开，默认委派给被包装资源。
     * <p>
     * 该方法直接调用{@link Resource#isOpen()}，
     * true表示多次获取可能得到相同流实例。
     * 
     * @return true表示资源已打开，false表示每次获取新流
     */
    @Override
    default boolean isOpen() {
        return getSource().isOpen();
    }

    /**
     * 判断被包装资源是否可读，默认委派给被包装资源。
     * <p>
     * 该方法直接调用{@link Resource#isReadable()}，
     * 由资源实现类根据实际状态返回结果。
     * 
     * @return true表示资源可读，false表示不可读
     */
    @Override
    default boolean isReadable() {
        return getSource().isReadable();
    }

    /**
     * 判断被包装资源是否可写，默认委派给被包装资源。
     * <p>
     * 该方法直接调用{@link Resource#isWritable()}，
     * 由资源实现类根据实际状态返回结果。
     * 
     * @return true表示资源可写，false表示不可写
     */
    @Override
    default boolean isWritable() {
        return getSource().isWritable();
    }

    /**
     * 为被包装资源添加指定字符集的编码转换，默认委派给被包装资源。
     * <p>
     * 该方法直接调用{@link Resource#encode(Charset)}，
     * 返回带编码功能的新资源实例。
     * 
     * @param charset 字符集，不可为null
     * @return 带字符集编码的资源
     */
    @Override
    default Resource encode(Charset charset) {
        return getSource().encode(charset);
    }

    /**
     * 为被包装资源添加指定字符集名称的编码转换，默认委派给被包装资源。
     * <p>
     * 该方法直接调用{@link Resource#encode(String)}，
     * 使用字符集名称实现编码转换。
     * 
     * @param charsetName 字符集名称，不可为null
     * @return 带字符集编码的资源
     */
    @Override
    default Resource encode(String charsetName) {
        return getSource().encode(charsetName);
    }

    /**
     * 为被包装资源添加指定字符集的解码转换，默认委派给被包装资源。
     * <p>
     * 该方法直接调用{@link Resource#decode(Charset)}，
     * 返回带解码功能的新资源实例。
     * 
     * @param charset 字符集，不可为null
     * @return 带字符集解码的资源
     */
    @Override
    default Resource decode(@NonNull Charset charset) {
        return getSource().decode(charset);
    }

    /**
     * 为被包装资源添加指定字符集名称的解码转换，默认委派给被包装资源。
     * <p>
     * 该方法直接调用{@link Resource#decode(String)}，
     * 使用字符集名称实现解码转换。
     * 
     * @param charsetName 字符集名称，不可为null
     * @return 带字符集解码的资源
     */
    @Override
    default Resource decode(@NonNull String charsetName) {
        return getSource().decode(charsetName);
    }

    /**
     * 为被包装资源添加指定字符集的编解码转换，默认委派给被包装资源。
     * <p>
     * 该方法直接调用{@link Resource#codec(Charset)}，
     * 同时实现输入输出流的字符集转换。
     * 
     * @param charset 字符集，不可为null
     * @return 带字符集编解码的资源
     */
    @Override
    default Resource codec(@NonNull Charset charset) {
        return getSource().codec(charset);
    }

    /**
     * 为被包装资源添加指定字符集名称的编解码转换，默认委派给被包装资源。
     * <p>
     * 该方法直接调用{@link Resource#codec(String)}，
     * 使用字符集名称实现编解码转换。
     * 
     * @param charsetName 字符集名称，不可为null
     * @return 带字符集编解码的资源
     */
    @Override
    default Resource codec(String charsetName) {
        return getSource().codec(charsetName);
    }

    /**
     * 为被包装资源添加自定义编解码转换，默认委派给被包装资源。
     * <p>
     * 该方法直接调用{@link Resource#codec(ThrowingFunction, ThrowingFunction)}，
     * 允许分别指定输入输出流的转换逻辑。
     * 
     * @param encoder 输出流→Writer的编码函数，不可为null
     * @param decoder 输入流→Reader的解码函数，不可为null
     * @return 带自定义编解码的资源
     */
    @Override
    default Resource codec(@NonNull ThrowingFunction<? super OutputStream, ? extends Writer, IOException> encoder,
            @NonNull ThrowingFunction<? super InputStream, ? extends Reader, IOException> decoder) {
        return getSource().codec(encoder, decoder);
    }

    /**
     * 重命名被包装资源并返回新资源实例，默认委派给被包装资源。
     * <p>
     * 该方法直接调用{@link Resource#rename(String)}，
     * 由资源实现类处理重命名逻辑（如文件资源执行物理重命名）。
     * 
     * @param name 新名称，不可为null
     * @return 重命名后的资源实例
     */
    @Override
    default Resource rename(String name) {
        return getSource().rename(name);
    }
}