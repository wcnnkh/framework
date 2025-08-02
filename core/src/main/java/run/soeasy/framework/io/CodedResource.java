package run.soeasy.framework.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

import lombok.Getter;
import lombok.NonNull;
import run.soeasy.framework.core.function.Pipeline;
import run.soeasy.framework.core.function.ThrowingFunction;

/**
 * 编解码资源包装器，用于为资源添加自定义编解码功能。
 * 该类继承自{@link EncodedOutputSource}并实现{@link ResourceWrapper}，
 * 支持同时指定输出流编码器和输入流解码器，实现完整的编解码转换。
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>双向编解码：同时支持输出流编码（父类实现）和输入流解码</li>
 *   <li>自定义转换：通过函数式接口支持任意编解码逻辑</li>
 *   <li>流水线集成：将编解码操作整合到资源流水线中</li>
 *   <li>透明代理：除编解码外，其他操作均委派给原始资源</li>
 * </ul>
 *
 * <p><b>实现原理：</b>
 * <ul>
 *   <li>输出流编码：使用父类的{@link EncodedOutputSource#encoder}处理输出流</li>
 *   <li>输入流解码：通过{@link #decoder}函数处理输入流到Reader的转换</li>
 *   <li>流水线操作：使用{@link Pipeline#map}实现流的转换和资源关闭</li>
 * </ul>
 *
 * @author soeasy.run
 * @param <W> 被包装的资源类型，需继承{@link Resource}
 * @see ResourceWrapper
 * @see EncodedOutputSource
 * @see Resource#codec(ThrowingFunction, ThrowingFunction)
 */
@Getter
class CodedResource<W extends Resource> extends EncodedOutputSource<Writer, W> implements ResourceWrapper<W> {
    /** 输入流解码函数（InputStream→Reader），不可为null */
    @NonNull
    private final ThrowingFunction<? super InputStream, ? extends Reader, IOException> decoder;

    /**
     * 创建编解码资源包装器。
     * <p>
     * 同时指定输出流编码器和输入流解码器，实现完整的编解码功能。
     * 
     * @param source    被包装的原始资源，不可为null
     * @param charset   字符集参数（可为Charset、String或null）
     * @param encoder   输出流编码函数，不可为null
     * @param decoder   输入流解码函数，不可为null
     */
    public CodedResource(@NonNull W source, Object charset,
            @NonNull ThrowingFunction<? super OutputStream, ? extends Writer, IOException> encoder,
            @NonNull ThrowingFunction<? super InputStream, ? extends Reader, IOException> decoder) {
        super(source, charset, encoder);
        this.decoder = decoder;
    }

    /**
     * 获取解码后的Reader流水线。
     * <p>
     * 将原始输入流流水线通过{@link #decoder}函数转换为Reader流水线，
     * 并在关闭时释放资源。
     * 
     * @return 包含解码操作的Reader流水线
     */
    @Override
    public @NonNull Pipeline<Reader, IOException> getReaderPipeline() {
        return getInputStreamPipeline()
                .map((e) -> (Reader) decoder.apply(e))
                .onClose((e) -> {
                    try {
                        e.close();
                    } catch (IOException ignored) {
                        // 解码后的Reader关闭异常忽略
                    }
                }).closeable();
    }
}