package run.soeasy.framework.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import lombok.NonNull;
import run.soeasy.framework.core.function.ThrowingFunction;

/**
 * 解码输入源实现，扩展自{@link DecodedInputStreamFactory}并实现{@link InputSource}接口。
 * 该类用于将原始输入源通过指定的解码器转换为具有解码功能的输入源，
 * 支持将字节流转换为字符流进行读取。
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>解码转换：基于父类功能，提供字节流到字符流的解码转换</li>
 *   <li>接口适配：无缝实现{@link InputSource}接口，保持统一的API</li>
 *   <li>字符集支持：继承父类的字符集管理能力</li>
 *   <li>函数式扩展：通过{@link ThrowingFunction}实现自定义解码逻辑</li>
 * </ul>
 *
 * <p><b>实现原理：</b>
 * <ul>
 *   <li>通过父类的{@link DecodedInputStreamFactory}实现解码转换逻辑</li>
 *   <li>继承父类的{@link #getReaderPipeline()}方法提供解码后的Reader</li>
 *   <li>复用父类的字符集解析和管理机制</li>
 * </ul>
 *
 * @author soeasy.run
 * @param <R> 解码后的Reader类型
 * @param <W> 被包装的输入源类型
 * @see InputSource
 * @see DecodedInputStreamFactory
 * @see InputStreamFactory
 */
class DecodedInputSource<R extends Reader, W extends InputSource> extends DecodedInputStreamFactory<InputStream, R, W>
        implements InputSource {

    /**
     * 构造解码输入源实例。
     * 
     * @param source   被包装的原始输入源，不可为null
     * @param charset  字符集参数（可为Charset、String或null）
     * @param decoder  解码转换函数，不可为null
     */
    public DecodedInputSource(@NonNull W source, Object charset,
            @NonNull ThrowingFunction<? super InputStream, ? extends R, IOException> decoder) {
        super(source, charset, decoder);
    }
}