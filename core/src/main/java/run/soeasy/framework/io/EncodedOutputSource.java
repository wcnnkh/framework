package run.soeasy.framework.io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import lombok.NonNull;
import run.soeasy.framework.core.function.ThrowingFunction;

/**
 * 编码输出源实现，扩展自{@link EncodedOutputStreamFactory}并实现{@link OutputSource}接口。
 * 该类用于将原始输出源通过指定的编码器转换为具有编码功能的输出源，
 * 支持将字节流转换为字符流进行输出。
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>编码转换：基于父类功能，提供字节流到字符流的编码转换</li>
 *   <li>接口适配：无缝实现{@link OutputSource}接口，保持统一的API</li>
 *   <li>字符集支持：继承父类的字符集管理能力</li>
 *   <li>函数式扩展：通过{@link ThrowingFunction}实现自定义编码逻辑</li>
 * </ul>
 *
 * <p><b>实现原理：</b>
 * <ul>
 *   <li>通过父类的{@link EncodedOutputStreamFactory}实现编码转换逻辑</li>
 *   <li>继承父类的{@link #getWriterPipeline()}方法提供编码后的Writer</li>
 *   <li>复用父类的字符集解析和管理机制</li>
 * </ul>
 *
 * @author soeasy.run
 * @param <R> 编码后的Writer类型
 * @param <W> 被包装的输出源类型
 * @see OutputSource
 * @see EncodedOutputStreamFactory
 * @see OutputStreamFactory
 */
class EncodedOutputSource<R extends Writer, W extends OutputSource>
        extends EncodedOutputStreamFactory<OutputStream, R, W> implements OutputSource {

    /**
     * 构造编码输出源实例。
     * 
     * @param source  被包装的原始输出源，不可为null
     * @param charset 字符集参数（可为Charset、String或null）
     * @param encoder 编码转换函数，不可为null
     */
    public EncodedOutputSource(@NonNull W source, Object charset,
            @NonNull ThrowingFunction<? super OutputStream, ? extends R, IOException> encoder) {
        super(source, charset, encoder);
    }
}