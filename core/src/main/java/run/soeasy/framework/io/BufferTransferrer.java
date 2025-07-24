package run.soeasy.framework.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import lombok.NonNull;

/**
 * 带缓冲区的传输器接口，继承自{@link BinaryTransferrerWrapper}，
 * 提供基于缓冲区的二进制数据传输能力，支持自定义缓冲区大小，
 * 是框架中处理流式数据传输的核心接口之一。
 * 
 * <p>该接口通过封装底层二进制传输器（{@link BinaryTransferrer}），
 * 统一管理缓冲区大小，简化数据传输过程中的缓冲区配置，
 * 所有传输操作均使用接口自身定义的缓冲区大小。
 * 
 * @param <W> 底层二进制传输器类型
 * @author soeasy.run
 * @see BinaryTransferrerWrapper
 * @see BinaryTransferrer
 */
public interface BufferTransferrer<W extends BinaryTransferrer> extends BinaryTransferrerWrapper<W> {
    /**
     * 获取传输重复次数（默认实现）
     * 
     * @return 固定返回1，表示单次传输
     */
    @Override
    default int getRepetitions() {
        return 1;
    }

    /**
     * 获取缓冲区大小
     * 
     * @return 缓冲区大小（字节数）
     */
    int getBufferSize();

    /**
     * 将字节数组转换为目标字节数组（默认实现）
     * 
     * @param source 源字节数组
     * @param offset 起始偏移量
     * @param length 数据长度
     * @return 转换后的字节数组
     * @throws IllegalStateException 当转换状态非法时抛出
     */
    @Override
    default byte[] toByteArray(@NonNull byte[] source, int offset, int length) throws IllegalStateException {
        return BinaryTransferrerWrapper.super.toByteArray(source, offset, length);
    }

    /**
     * 从输入源读取数据并转换为字节数组
     * 
     * @param source 输入源
     * @param bufferSize 缓冲区大小（当前实现忽略此参数，使用{@link #getBufferSize()}）
     * @return 读取的字节数组
     * @throws IOException 当I/O操作失败时抛出
     */
    @Override
    default byte[] toByteArray(@NonNull InputSource source, int bufferSize) throws IOException {
        return getSource().toByteArray(source, getBufferSize());
    }

    /**
     * 从输入流读取数据并转换为字节数组
     * 
     * @param source 输入流
     * @param bufferSize 缓冲区大小（当前实现忽略此参数，使用{@link #getBufferSize()}）
     * @return 读取的字节数组
     * @throws IOException 当I/O操作失败时抛出
     */
    @Override
    default byte[] toByteArray(@NonNull InputStream source, int bufferSize) throws IOException {
        return getSource().toByteArray(source, getBufferSize());
    }

    /**
     * 将输入源数据传输到缓冲区消费者
     * 
     * @param <E> 消费者可能抛出的异常类型
     * @param source 输入源
     * @param bufferSize 缓冲区大小（当前实现忽略此参数，使用{@link #getBufferSize()}）
     * @param target 缓冲区消费者
     * @throws IOException 当I/O操作失败时抛出
     * @throws E 当消费者处理失败时抛出
     */
    @Override
    default <E extends Throwable> void transferTo(@NonNull InputSource source, int bufferSize,
            @NonNull BufferConsumer<? super byte[], ? extends E> target) throws IOException, E {
        getSource().transferTo(source, getBufferSize(), target);
    }

    /**
     * 将输入源数据传输到输出源
     * 
     * @param source 输入源
     * @param bufferSize 缓冲区大小（当前实现忽略此参数，使用{@link #getBufferSize()}）
     * @param target 输出源
     * @throws IOException 当I/O操作失败时抛出
     */
    @Override
    default void transferTo(@NonNull InputSource source, int bufferSize, @NonNull OutputSource target)
            throws IOException {
        getSource().transferTo(source, getBufferSize(), target);
    }

    /**
     * 将输入源数据传输到输出流
     * 
     * @param source 输入源
     * @param bufferSize 缓冲区大小（当前实现忽略此参数，使用{@link #getBufferSize()}）
     * @param target 输出流
     * @throws IOException 当I/O操作失败时抛出
     */
    @Override
    default void transferTo(@NonNull InputSource source, int bufferSize, @NonNull OutputStream target)
            throws IOException {
        getSource().transferTo(source, getBufferSize(), target);
    }

    /**
     * 将输入流数据传输到缓冲区消费者
     * 
     * @param <E> 消费者可能抛出的异常类型
     * @param source 输入流
     * @param bufferSize 缓冲区大小（当前实现忽略此参数，使用{@link #getBufferSize()}）
     * @param target 缓冲区消费者
     * @throws IOException 当I/O操作失败时抛出
     * @throws E 当消费者处理失败时抛出
     */
    @Override
    default <E extends Throwable> void transferTo(@NonNull InputStream source, int bufferSize,
            @NonNull BufferConsumer<? super byte[], ? extends E> target) throws IOException, E {
        getSource().transferTo(source, getBufferSize(), target);
    }

    /**
     * 将输入流数据传输到输出源
     * 
     * @param source 输入流
     * @param bufferSize 缓冲区大小（当前实现忽略此参数，使用{@link #getBufferSize()}）
     * @param target 输出源
     * @throws IOException 当I/O操作失败时抛出
     */
    @Override
    default void transferTo(@NonNull InputStream source, int bufferSize, @NonNull OutputSource target)
            throws IOException {
        getSource().transferTo(source, getBufferSize(), target);
    }

    /**
     * 将输入流数据传输到输出流
     * 
     * @param source 输入流
     * @param bufferSize 缓冲区大小（当前实现忽略此参数，使用{@link #getBufferSize()}）
     * @param target 输出流
     * @throws IOException 当I/O操作失败时抛出
     */
    @Override
    default void transferTo(@NonNull InputStream source, int bufferSize, @NonNull OutputStream target)
            throws IOException {
        getSource().transferTo(source, getBufferSize(), target);
    }
}