package run.soeasy.framework.codec.binary;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

import lombok.NonNull;
import run.soeasy.framework.io.BinaryTransferrer;
import run.soeasy.framework.io.BufferConsumer;
import run.soeasy.framework.io.IOUtils;

/**
 * GZIP解码器，实现对GZIP压缩的二进制数据进行解码（解压）操作，
 * 同时实现{@link BinaryTransferrer}接口以支持流式传输解压功能。
 * 
 * <p>该类提供了默认实例{@link #DEFAULT}，适用于大多数GZIP解压场景，
 * 支持直接处理{@link GZIPInputStream}或自动将普通输入流包装为GZIP输入流。
 * 
 * @author soeasy.run
 * @see BinaryDecoder
 * @see BinaryTransferrer
 * @see GZIPInputStream
 */
public class GzipDecoder implements BinaryDecoder, BinaryTransferrer {
    /**
     * GzipDecoder的默认实例，可直接用于GZIP解压操作
     */
    public static final GzipDecoder DEFAULT = new GzipDecoder();

    /**
     * 将GZIP压缩的输入流数据传输到缓冲区消费者（解压过程）
     * 
     * <p>处理逻辑：
     * 1. 如果输入流不是{@link GZIPInputStream}，则自动包装为GZIP输入流
     * 2. 使用{@link IOUtils#transferTo(InputStream, int, BufferConsumer)}传输解压后的数据
     * 3. 确保自动创建的GZIP输入流在操作完成后关闭
     * 
     * @param <E> 消费者可能抛出的异常类型
     * @param source 待解压的输入流（可以是普通流或已包装的GZIP流）
     * @param bufferSize 传输缓冲区大小
     * @param target 接收解压后数据的缓冲区消费者
     * @throws IOException 当I/O操作失败或解压过程出错时抛出
     * @throws E 当消费者处理数据时抛出
     */
    @Override
    public <E extends Throwable> void transferTo(@NonNull InputStream source, int bufferSize,
            @NonNull BufferConsumer<? super byte[], ? extends E> target) throws IOException, E {
        boolean isNew = false;
        if (!(source instanceof GZIPInputStream)) {
            source = new GZIPInputStream(source, bufferSize);
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
     * @return 当前{@link GzipDecoder}实例，实现了解压传输功能
     */
    @Override
    public final BinaryTransferrer getDecodeTransferrer() {
        return this;
    }

}