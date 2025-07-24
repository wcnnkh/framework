package run.soeasy.framework.io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.charset.Charset;

import lombok.Data;
import lombok.NonNull;
import run.soeasy.framework.core.domain.CharsetCapable;
import run.soeasy.framework.core.function.Pipeline;
import run.soeasy.framework.core.function.ThrowingFunction;

/**
 * 编码输出流工厂实现，装饰{@link OutputStreamFactory}并添加编码转换功能。
 * 该类支持将原始输出流通过指定的编码器转换为{@link Writer}，
 * 并实现{@link CharsetCapable}接口以提供字符集相关功能。
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>装饰器模式：包装原始输出流工厂以添加编码转换功能</li>
 *   <li>编码转换：通过{@link ThrowingFunction}实现自定义编码逻辑</li>
 *   <li>字符集支持：实现{@link CharsetCapable}接口以管理字符集</li>
 *   <li>流水线集成：保持与输出流流水线的兼容性</li>
 * </ul>
 *
 * <p><b>实现原理：</b>
 * <ul>
 *   <li>通过{@link #encoder}函数将原始输出流转换为Writer</li>
 *   <li>使用{@link Pipeline#map}操作实现流水线中的编码转换</li>
 *   <li>字符集信息通过{@link #charset}字段传递并解析</li>
 * </ul>
 *
 * @author soeasy.run
 * @param <T> 输出流类型
 * @param <R> 编码后的Writer类型
 * @param <W> 被包装的输出流工厂类型
 * @see OutputStreamFactory
 * @see CharsetCapable
 * @see EncodedOutputSource
 */
@Data
class EncodedOutputStreamFactory<T extends OutputStream, R extends Writer, W extends OutputStreamFactory<T>>
        implements OutputStreamFactory<T>, CharsetCapable {
    /** 被包装的原始输出流工厂，不可为null */
    @NonNull
    private final W source;
    /** 字符集相关参数（可为Charset、String或null） */
    private final Object charset;
    /** 编码转换函数，不可为null */
    @NonNull
    private final ThrowingFunction<? super T, ? extends R, IOException> encoder;

    /**
     * 获取原始输出流工厂的流水线。
     * <p>
     * 该方法直接委派给被包装的工厂，确保流水线处理逻辑的透明性。
     * 
     * @return 原始输出流流水线
     */
    @Override
    public @NonNull Pipeline<T, IOException> getOutputStreamPipeline() {
        return source.getOutputStreamPipeline();
    }

    /**
     * 获取原始输出流实例。
     * <p>
     * 该方法直接委派给被包装的工厂，编码转换在{@link #getWriterPipeline}中处理。
     * 
     * @return 原始输出流实例
     * @throws IOException 当创建输出流时发生I/O错误
     */
    @Override
    public OutputStream getOutputStream() throws IOException {
        return source.getOutputStream();
    }

    /**
     * 获取编码后的Writer流水线。
     * <p>
     * 该方法将原始输出流流水线映射为Writer流水线，使用{@link #encoder}
     * 函数执行编码转换，并在关闭时释放资源。
     * 
     * @return 编码后的Writer流水线
     */
    @Override
    public @NonNull Pipeline<Writer, IOException> getWriterPipeline() {
        return getOutputStreamPipeline()
                .map((e) -> (Writer) encoder.apply(e))
                .onClose((e) -> {
                    try {
                        e.close();
                    } catch (IOException ignored) {
                        // 编码后的Writer关闭异常忽略
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