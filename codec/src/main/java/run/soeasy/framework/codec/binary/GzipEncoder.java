package run.soeasy.framework.codec.binary;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import lombok.NonNull;
import run.soeasy.framework.io.BinaryTransferrer;
import run.soeasy.framework.io.BufferConsumer;
import run.soeasy.framework.io.IOUtils;
import run.soeasy.framework.io.StreamTransferrer;

/**
 * GZIP编码器，实现对二进制数据的GZIP压缩（编码）操作，
 * 同时实现{@link StreamTransferrer}接口以支持流式传输压缩功能，
 * 可将输入流数据压缩后传输到输出流或缓冲区消费者。
 * 
 * <p>该类提供默认实例{@link #DEFAULT}，适用于大多数GZIP压缩场景，
 * 支持直接处理{@link GZIPInputStream}或对普通输入流进行GZIP压缩。
 * 
 * @author soeasy.run
 * @see BinaryEncoder
 * @see StreamTransferrer
 * @see GZIPOutputStream
 */
public class GzipEncoder implements BinaryEncoder, StreamTransferrer {
    /**
     * GzipEncoder的默认实例，可直接用于GZIP压缩操作
     */
    public static final GzipEncoder DEFAULT = new GzipEncoder();

    /**
     * 将输入流数据压缩后传输到缓冲区消费者
     * 
     * <p>处理逻辑：
     * 1. 若输入流是{@link GZIPInputStream}（已压缩），直接传输数据
     * 2. 否则调用父类{@link StreamTransferrer}的默认实现进行压缩传输
     * 
     * @param <E> 消费者可能抛出的异常类型
     * @param source 待压缩的输入流
     * @param bufferSize 传输缓冲区大小
     * @param target 接收压缩后数据的缓冲区消费者
     * @throws IOException 当I/O操作失败或压缩过程出错时抛出
     * @throws E 当消费者处理数据时抛出
     */
    @Override
    public final <E extends Throwable> void transferTo(@NonNull InputStream source, int bufferSize,
            @NonNull BufferConsumer<? super byte[], ? extends E> target) throws IOException, E {
        if (source instanceof GZIPInputStream) {
            IOUtils.transferTo(source, bufferSize, target);
        } else {
            StreamTransferrer.super.transferTo(source, bufferSize, target);
        }
    }

    /**
     * 将输入流数据压缩后传输到输出流（核心压缩方法）
     * 
     * <p>处理逻辑：
     * 1. 若输出流不是{@link GZIPOutputStream}，则包装为GZIP输出流
     * 2. 使用{@link IOUtils#transferTo(InputStream, int, BufferConsumer)}传输并压缩数据
     * 3. 确保自动创建的GZIP输出流在操作完成后关闭，以保证压缩数据完整写入
     * 
     * @param source 待压缩的输入流
     * @param bufferSize 压缩缓冲区大小
     * @param target 接收压缩后数据的输出流
     * @throws IOException 当I/O操作失败或压缩过程出错时抛出
     */
    @Override
    public void transferTo(@NonNull InputStream source, int bufferSize, @NonNull OutputStream target)
            throws IOException {
        boolean isNew = false;
        if (!(target instanceof GZIPOutputStream)) {
            target = new GZIPOutputStream(target, bufferSize);
            isNew = true;
        }
        try {
            IOUtils.transferTo(source, bufferSize, target::write);
        } finally {
            if (isNew) {
                target.close();
            }
        }
    }

    /**
     * 获取当前编码器作为压缩传输器
     * 
     * @return 当前{@link GzipEncoder}实例，用于压缩数据的流式传输
     */
    @Override
    public final BinaryTransferrer getEncodeTransferrer() {
        return this;
    }

}