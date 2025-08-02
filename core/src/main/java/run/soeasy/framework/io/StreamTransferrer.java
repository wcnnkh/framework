package run.soeasy.framework.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import lombok.NonNull;
import run.soeasy.framework.sequences.UUIDSequence;

/**
 * 流传输器接口，继承自{@link BinaryTransferrer}，采用函数式接口设计，
 * 专注于输入流到输出流的直接数据传输，并提供将流数据转换为缓冲区消费者可处理形式的默认实现。
 * 
 * <p>该接口通过临时文件作为中间媒介，解决了流数据无法直接重复读取的问题，
 * 适用于需要先将流数据持久化再进行消费处理的场景。
 * 
 * @author soeasy.run
 * @see BinaryTransferrer
 * @see BufferConsumer
 */
@FunctionalInterface
public interface StreamTransferrer extends BinaryTransferrer {

    /**
     * 将输入流数据传输到输出流（核心方法）
     * 
     * @param source 输入流数据源
     * @param bufferSize 传输缓冲区大小
     * @param target 输出流目标
     * @throws IOException 当I/O操作失败时抛出
     */
    @Override
    void transferTo(@NonNull InputStream source, int bufferSize, @NonNull OutputStream target) throws IOException;

    /**
     * 将输入流数据传输到缓冲区消费者（默认实现）
     * 
     * <p>实现逻辑：
     * 1. 创建临时文件作为流数据的中间存储
     * 2. 使用{@link #transferTo(InputStream, int, OutputStream)}将输入流写入临时文件
     * 3. 通过{@link FileUtils#copy(File, int, BufferConsumer)}将临时文件数据传递给消费者
     * 4. 无论成功失败，最终删除临时文件
     * 
     * @param <E> 消费者可能抛出的异常类型
     * @param source 输入流数据源
     * @param bufferSize 传输缓冲区大小
     * @param target 缓冲区消费者
     * @throws IOException 当I/O操作失败时抛出
     * @throws E 当消费者处理数据时抛出
     */
    @Override
    default <E extends Throwable> void transferTo(@NonNull InputStream source, int bufferSize,
            @NonNull BufferConsumer<? super byte[], ? extends E> target) throws IOException, E {
        File tempFile = File.createTempFile(UUIDSequence.random().next(), StreamTransferrer.class.getSimpleName());
        try {
            transferTo(source, bufferSize, Resource.forFile(tempFile));
            FileUtils.copy(tempFile, bufferSize, target);
        } finally {
            tempFile.delete();
        }
    }
}