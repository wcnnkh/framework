package run.soeasy.framework.codec.binary;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipInputStream;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.io.BinaryTransferrer;
import run.soeasy.framework.io.BufferConsumer;
import run.soeasy.framework.io.IOUtils;

/**
 * ZIP解码器，实现对ZIP压缩的二进制数据进行解码（解压）操作，
 * 同时实现{@link BinaryTransferrer}接口以支持流式传输解压功能，
 * 依赖指定的字符集处理ZIP条目名称等文本信息。
 * 
 * <p>该类提供了使用UTF-8字符集的默认实例{@link #UTF_8}，适用于大多数ZIP解压场景，
 * 支持直接处理{@link ZipInputStream}或自动将普通输入流包装为ZIP输入流。
 * 
 * @author soeasy.run
 * @see BinaryDecoder
 * @see BinaryTransferrer
 * @see ZipInputStream
 */
@Getter
@RequiredArgsConstructor
public class ZipDecoder implements BinaryDecoder, BinaryTransferrer {
    /**
     * 使用UTF-8字符集的ZipDecoder默认实例，适用于大多数ZIP解压场景
     */
    public static final ZipDecoder UTF_8 = new ZipDecoder(StandardCharsets.UTF_8);

    /**
     * 用于处理ZIP条目名称等文本信息的字符集
     */
    @NonNull
    private final Charset charset;

    /**
     * 将ZIP压缩的输入流数据传输到缓冲区消费者（解压过程）
     * 
     * <p>处理逻辑：
     * 1. 如果输入流不是{@link ZipInputStream}，则使用指定字符集包装为ZIP输入流
     * 2. 使用{@link IOUtils#transferTo(InputStream, int, BufferConsumer)}传输解压后的数据
     * 3. 确保自动创建的ZIP输入流在操作完成后关闭
     * 
     * @param <E> 消费者可能抛出的异常类型
     * @param source 待解压的输入流（可以是普通流或已包装的ZIP流）
     * @param bufferSize 传输缓冲区大小
     * @param target 接收解压后数据的缓冲区消费者
     * @throws IOException 当I/O操作失败或解压过程出错时抛出
     * @throws E 当消费者处理数据时抛出
     */
    @Override
    public <E extends Throwable> void transferTo(@NonNull InputStream source, int bufferSize,
            @NonNull BufferConsumer<? super byte[], ? extends E> target) throws IOException, E {
        boolean isNew = false;
        if (!(source instanceof ZipInputStream)) {
            source = new ZipInputStream(source, charset);
            isNew = true;
        }

        try {
            IOUtils.transferTo(source, bufferSize, target);
        } finally {
            if (isNew) {
                source.close();
            }
        }
    }

    /**
     * 获取当前解码器作为解压传输器
     * 
     * @return 当前{@link ZipDecoder}实例，用于ZIP解压的流式传输
     */
    @Override
    public BinaryTransferrer getDecodeTransferrer() {
        return this;
    }

}