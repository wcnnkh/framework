package run.soeasy.framework.io.source;

import lombok.NonNull;
import run.soeasy.framework.core.function.ThrowingFunction;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;

/**
 * 输入源接口，用于延迟提供 {@link InputStream} 实例。
 *
 * <p>本接口是 {@link ReaderSource} 的字节特化版本，充当字节流与字符流之间的桥梁。
 * 它定义了字节输入源的标准契约，并提供了向 NIO 通道和字符流转换的能力。
 *
 * <p><b>核心契约：</b>
 * <ul>
 *   <li>实现类必须保证 {@link #getInputStream()} 返回有效的输入流</li>
 *   <li>调用方负责关闭返回的流或通道</li>
 *   <li>解码操作不应改变原始字节源的状态</li>
 * </ul>
 *
 * @param <I> 具体的输入流类型，必须是 {@link InputStream} 的子类
 * @author soeasy.run
 */
@FunctionalInterface
public interface InputSource<I extends InputStream> extends ReaderSource<Reader> {

    /**
     * 获取输入流实例。
     *
     * @return 输入流实例（不可为 null）
     * @throws IOException 如果获取输入流失败
     */
    @NonNull
    I getInputStream() throws IOException;

    /**
     * 将输入流适配为 NIO 可读通道。
     *
     * <p>默认实现基于 {@link java.nio.channels.Channels#newChannel(InputStream)}。
     * 这是字节输入源向 NIO 体系转换的标准方式。
     *
     * @return 可读字节通道
     * @throws IOException 如果获取输入流失败
     */
    @NonNull
    default ReadableByteChannel readableChannel() throws IOException {
        return Channels.newChannel(getInputStream());
    }

    /**
     * 获取字符读取器。
     *
     * <p>默认实现使用平台默认字符集将字节流包装为 {@link InputStreamReader}。
     * 这是从字节到字符的最基础转换。
     *
     * @return 字符读取器
     * @throws IOException 如果获取输入流失败
     */
    @NonNull
    @Override
    default Reader getReader() throws IOException {
        return new InputStreamReader(getInputStream());
    }

    /**
     * 使用自定义解码函数将字节流转换为字符流。
     *
     * <p>适用于需要复杂处理逻辑的场景（如解压、解密、协议解析）。
     *
     * @param decoder 解码函数，接收原始输入流并返回处理后的字符流
     * @param <R>     目标字符流类型
     * @return 新的 ReaderSource，提供解码后的字符流
     */
    default <R extends Reader> ReaderSource<R> decode(@NonNull ThrowingFunction<? super I, ? extends R, ? extends IOException> decoder) {
        return () -> decoder.apply(getInputStream());
    }

    /**
     * 使用指定字符集将字节流解码为字符流。
     *
     * <p>这是最常用的解码方式，返回一个语义明确的输入源包装器。
     *
     * @param charset 字符集
     * @return 支持指定字符集解码的 InputSource
     */
    default InputSource<I> decode(@NonNull Charset charset) {
        return new DecodeInputSource<>(this, charset);
    }
}