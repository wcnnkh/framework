package run.soeasy.framework.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.domain.CharsetCapable;
import run.soeasy.framework.core.function.Pipeline;
import run.soeasy.framework.core.function.ThrowingFunction;

/**
 * 解码输入流工厂实现，装饰{@link InputStreamFactory}并添加解码转换功能。
 * 该类支持将原始输入流通过指定的解码器转换为{@link Reader}，
 * 并实现{@link CharsetCapable}接口以提供字符集相关功能。
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>装饰器模式：包装原始输入流工厂以添加解码转换功能</li>
 *   <li>解码转换：通过{@link ThrowingFunction}实现自定义解码逻辑</li>
 *   <li>字符集支持：实现{@link CharsetCapable}接口以管理字符集</li>
 *   <li>流水线集成：保持与输入流流水线的兼容性</li>
 * </ul>
 *
 * <p><b>实现原理：</b>
 * <ul>
 *   <li>通过{@link #decoder}函数将原始输入流转换为Reader</li>
 *   <li>使用{@link Pipeline#map}操作实现流水线中的解码转换</li>
 *   <li>字符集信息通过{@link #charset}字段传递并解析</li>
 * </ul>
 *
 * @author soeasy.run
 * @param <I> 输入流类型
 * @param <R> 解码后的Reader类型
 * @param <W> 被包装的输入流工厂类型
 * @see InputStreamFactory
 * @see CharsetCapable
 * @see DecodedInputSource
 */
@RequiredArgsConstructor
@Getter
class DecodedInputStreamFactory<I extends InputStream, R extends Reader, W extends InputStreamFactory<I>>
        implements InputStreamFactory<I>, CharsetCapable {
    /** 被包装的原始输入流工厂，不可为null */
    @NonNull
    private final W source;
    /** 字符集相关参数（可为Charset、String或null） */
    private final Object charset;
    /** 解码转换函数，不可为null */
    @NonNull
    private final ThrowingFunction<? super I, ? extends R, IOException> decoder;

    /**
     * 获取原始输入流工厂的流水线。
     * <p>
     * 该方法直接委派给被包装的工厂，确保流水线处理逻辑的透明性。
     * 
     * @return 原始输入流流水线
     */
    @Override
    public @NonNull Pipeline<I, IOException> getInputStreamPipeline() {
        return source.getInputStreamPipeline();
    }

    /**
     * 获取原始输入流实例。
     * <p>
     * 该方法直接委派给被包装的工厂，解码转换在{@link #getReaderPipeline}中处理。
     * 
     * @return 原始输入流实例
     * @throws IOException 当创建输入流时发生I/O错误
     */
    @Override
    public InputStream getInputStream() throws IOException {
        return source.getInputStream();
    }

    /**
     * 获取解码后的Reader流水线。
     * <p>
     * 该方法将原始输入流流水线映射为Reader流水线，使用{@link #decoder}
     * 函数执行解码转换，并在关闭时释放资源。
     * 
     * @return 解码后的Reader流水线
     */
    @Override
    public Pipeline<Reader, IOException> getReaderPipeline() {
        return getInputStreamPipeline()
                .map((e) -> (Reader) decoder.apply(e))
                .onClose((e) -> {
                    try {
                        e.close();
                    } catch (IOException ignored) {
                        // 解码后的Reader关闭异常忽略
                    }
                });
    }

    /**
     * 获取当前使用的字符集。
     * <p>
     * 该方法通过{@link CharsetCapable#getCharset(Object)}工具方法
     * 从{@link #charset}字段解析字符集，支持Charset实例或字符集名称。
     * 
     * @return 解析后的字符集，默认为UTF-8
     */
    @Override
    public Charset getCharset() {
        return CharsetCapable.getCharset(charset);
    }

    /**
     * 获取当前使用的字符集名称。
     * <p>
     * 该方法通过{@link CharsetCapable#getCharsetName(Object)}工具方法
     * 从{@link #charset}字段解析字符集名称。
     * 
     * @return 字符集名称，默认为"UTF-8"
     */
    @Override
    public String getCharsetName() {
        return CharsetCapable.getCharsetName(charset);
    }
}