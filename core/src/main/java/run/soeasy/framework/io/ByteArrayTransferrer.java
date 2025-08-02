package run.soeasy.framework.io;

import java.io.IOException;
import java.io.InputStream;

import lombok.NonNull;

/**
 * 字节数组传输器接口，继承自{@link BinaryTransferrer}，
 * 专注于将输入流数据转换为字节数组并支持数据传输到缓冲区消费者，
 * 是框架中处理字节数组与输入流转换的核心接口。
 * 
 * <p>该接口提供了将输入流读取为字节数组的基础实现，并默认实现了
 * 将字节数组传输到缓冲区消费者的逻辑，简化了字节数组形式的数据处理流程。
 * 
 * @author soeasy.run
 * @see BinaryTransferrer
 * @see BufferConsumer
 */
public interface ByteArrayTransferrer extends BinaryTransferrer {

    /**
     * 将输入流数据读取为字节数组
     * 
     * @param source 输入流数据源
     * @param bufferSize 读取缓冲区大小
     * @return 包含输入流数据的字节数组
     * @throws IOException 当I/O操作失败时抛出
     */
    @Override
    byte[] toByteArray(@NonNull InputStream source, int bufferSize) throws IOException;

    /**
     * 将输入流数据传输到缓冲区消费者
     * 
     * <p>默认实现逻辑：
     * 1. 先通过{@link #toByteArray(InputStream, int)}将输入流转换为字节数组
     * 2. 再将完整字节数组传递给缓冲区消费者
     * 
     * @param <E> 消费者可能抛出的异常类型
     * @param source 输入流数据源
     * @param bufferSize 读取缓冲区大小
     * @param target 缓冲区消费者
     * @throws IOException 当I/O操作失败时抛出
     * @throws E 当消费者处理数据时抛出
     */
    @Override
    default <E extends Throwable> void transferTo(@NonNull InputStream source, int bufferSize,
            @NonNull BufferConsumer<? super byte[], ? extends E> target) throws IOException, E {
        byte[] data = toByteArray(source, bufferSize);
        target.accept(data, 0, data.length);
    }
}