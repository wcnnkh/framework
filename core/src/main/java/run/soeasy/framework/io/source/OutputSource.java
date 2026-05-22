package run.soeasy.framework.io.source;

import lombok.NonNull;
import run.soeasy.framework.core.function.ThrowingFunction;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;

/**
 * 输出源接口，用于延迟提供 {@link OutputStream} 实例。
 *
 * <p>本接口是 {@link WriterSource} 的字节特化版本，充当字节流与字符流之间的桥梁。
 * 它定义了字节输出源的标准契约，并提供了向 NIO 通道和字符流转换的能力。
 *
 * <p><b>核心契约：</b>
 * <ul>
 *   <li>实现类必须保证 {@link #getOutputStream()} 返回有效的输出流</li>
 *   <li>调用方负责关闭返回的流或通道</li>
 *   <li>编码操作不应改变原始字节源的状态</li>
 * </ul>
 *
 * @param <O> 具体的输出流类型，必须是 {@link OutputStream} 的子类
 * @author soeasy.run
 */
@FunctionalInterface
public interface OutputSource<O extends OutputStream> extends WriterSource<Writer> {

    /**
     * 获取输出流实例。
     *
     * @return 输出流实例（不可为 null）
     * @throws IOException 如果获取输出流失败
     */
    @NonNull
    O getOutputStream() throws IOException;

    /**
     * 将输出流适配为 NIO 可写通道。
     *
     * <p>默认实现基于 {@link java.nio.channels.Channels#newChannel(OutputStream)}。
     * 这是字节输出源向 NIO 体系转换的标准方式。
     *
     * @return 可写字节通道
     * @throws IOException 如果获取输出流失败
     */
    @NonNull
    default WritableByteChannel writableChannel() throws IOException {
        return Channels.newChannel(getOutputStream());
    }

    /**
     * 获取字符写入器。
     *
     * <p>默认实现使用平台默认字符集将字节流包装为 {@link OutputStreamWriter}。
     * 这是从字节到字符的最基础转换。
     *
     * @return 字符写入器
     * @throws IOException 如果获取输出流失败
     */
    @NonNull
    @Override
    default Writer getWriter() throws IOException {
        return new OutputStreamWriter(getOutputStream());
    }

    /**
     * 使用自定义编码函数将字符流转换为字节流。
     *
     * <p>适用于需要复杂处理逻辑的场景（如压缩、加密、协议封装）。
     *
     * @param encoder 编码函数，接收原始输出流并返回处理后的字符流
     * @param <W>     目标字符流类型
     * @return 新的 WriterSource，提供编码后的字符流
     */
    default <W extends Writer> WriterSource<W> encode(
            @NonNull ThrowingFunction<? super O, ? extends W, ? extends IOException> encoder
    ) {
        return () -> encoder.apply(getOutputStream());
    }

    /**
     * 使用指定字符集将字符流编码为字节流。
     *
     * <p>这是最常用的编码方式，返回一个语义明确的输出源包装器。
     *
     * @param charset 字符集
     * @return 支持指定字符集编码的 OutputSource
     */
    default OutputSource<O> encode(@NonNull Charset charset) {
        return new EncodeOutputSource<>(this, charset);
    }
}