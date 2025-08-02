package run.soeasy.framework.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import lombok.NonNull;
import run.soeasy.framework.sequences.UUIDSequence;

/**
 * 链式二进制传输器接口，继承自{@link BinaryTransferrer}，
 * 通过组合两个二进制传输器实现链式数据处理，支持先将输入流数据写入临时文件，
 * 再通过第二个传输器处理临时文件数据，适用于需要分步处理的二进制数据传输场景。
 * 
 * <p>该接口的核心设计是将数据传输过程分为两个阶段：
 * 1. 从输入源读取数据（通过{@link #getFromStreamTransferrer()}）
 * 2. 处理并传输数据（通过{@link #getToStreamTransferrer()}）
 * 两个阶段通过临时文件实现数据衔接，适用于大文件处理或需要中间存储的场景。
 * 
 * @author soeasy.run
 * @see BinaryTransferrer
 * @see UUIDSequence
 */
public interface ChainBinaryTransferrer extends BinaryTransferrer {

    /**
     * 获取用于从输入流读取数据的传输器
     * 
     * @return 输入流数据读取传输器
     */
    BinaryTransferrer getFromStreamTransferrer();

    /**
     * 获取用于处理并传输数据的传输器
     * 
     * @return 数据处理与传输器
     */
    BinaryTransferrer getToStreamTransferrer();

    /**
     * 将输入流数据传输到缓冲区消费者（链式实现）
     * 
     * <p>实现逻辑：
     * 1. 创建临时文件作为中间存储
     * 2. 使用{@link #getFromStreamTransferrer()}将输入流写入临时文件
     * 3. 使用{@link #getToStreamTransferrer()}从临时文件读取数据并传输到消费者
     * 4. 无论成功失败，最终删除临时文件
     * 
     * @param <E> 消费者可能抛出的异常类型
     * @param source 输入流数据源
     * @param bufferSize 缓冲区大小
     * @param target 缓冲区消费者
     * @throws IOException 当I/O操作失败时抛出
     * @throws E 当消费者处理数据时抛出
     */
    @Override
    default <E extends Throwable> void transferTo(@NonNull InputStream source, int bufferSize,
            @NonNull BufferConsumer<? super byte[], ? extends E> target) throws IOException, E {
        File tempFile = File.createTempFile(UUIDSequence.random().next(), ChainBinaryTransferrer.class.getSimpleName());
        try {
            getFromStreamTransferrer().transferTo(source, bufferSize, Resource.forFile(tempFile));
            getToStreamTransferrer().transferTo(Resource.forFile(tempFile), bufferSize, target);
        } finally {
            tempFile.delete();
        }
    }

    /**
     * 将输入源数据传输到缓冲区消费者（默认实现）
     * 
     * @param <E> 消费者可能抛出的异常类型
     * @param source 输入源
     * @param bufferSize 缓冲区大小
     * @param target 缓冲区消费者
     * @throws IOException 当I/O操作失败时抛出
     * @throws E 当消费者处理数据时抛出
     */
    @Override
    default <E extends Throwable> void transferTo(@NonNull InputSource source, int bufferSize,
            @NonNull BufferConsumer<? super byte[], ? extends E> target) throws IOException, E {
        BinaryTransferrer.super.transferTo(source, bufferSize, target);
    }

    /**
     * 将输入源数据传输到输出源（默认实现）
     * 
     * @param source 输入源
     * @param bufferSize 缓冲区大小
     * @param target 输出源
     * @throws IOException 当I/O操作失败时抛出
     */
    @Override
    default void transferTo(@NonNull InputSource source, int bufferSize, @NonNull OutputSource target)
            throws IOException {
        BinaryTransferrer.super.transferTo(source, bufferSize, target);
    }

    /**
     * 将输入源数据传输到输出流（默认实现）
     * 
     * @param source 输入源
     * @param bufferSize 缓冲区大小
     * @param target 输出流
     * @throws IOException 当I/O操作失败时抛出
     */
    @Override
    default void transferTo(@NonNull InputSource source, int bufferSize, @NonNull OutputStream target)
            throws IOException {
        BinaryTransferrer.super.transferTo(source, bufferSize, target);
    }

    /**
     * 将输入流数据传输到输出源（默认实现）
     * 
     * @param source 输入流
     * @param bufferSize 缓冲区大小
     * @param target 输出源
     * @throws IOException 当I/O操作失败时抛出
     */
    @Override
    default void transferTo(@NonNull InputStream source, int bufferSize, @NonNull OutputSource target)
            throws IOException {
        BinaryTransferrer.super.transferTo(source, bufferSize, target);
    }

    /**
     * 将输入流数据传输到输出流（默认实现）
     * 
     * @param source 输入流
     * @param bufferSize 缓冲区大小
     * @param target 输出流
     * @throws IOException 当I/O操作失败时抛出
     */
    @Override
    default void transferTo(@NonNull InputStream source, int bufferSize, @NonNull OutputStream target)
            throws IOException {
        BinaryTransferrer.super.transferTo(source, bufferSize, target);
    }

    /**
     * 将字节数组转换为目标字节数组（链式实现）
     * 
     * <p>实现逻辑：
     * 1. 使用{@link #getFromStreamTransferrer()}处理原始字节数组
     * 2. 将中间结果传递给{@link #getToStreamTransferrer()}进行二次处理
     * 
     * @param source 源字节数组
     * @param offset 起始偏移量
     * @param length 数据长度
     * @return 处理后的字节数组
     * @throws IllegalStateException 当转换状态非法时抛出
     */
    @Override
    default byte[] toByteArray(byte[] source, int offset, int length) throws IllegalStateException {
        byte[] temp = getFromStreamTransferrer().toByteArray(source, offset, length);
        return getToStreamTransferrer().toByteArray(temp, 0, temp.length);
    }

    /**
     * 将输入流数据转换为字节数组（链式实现）
     * 
     * <p>实现逻辑：
     * 1. 使用{@link #getFromStreamTransferrer()}从输入流读取数据
     * 2. 将中间结果传递给{@link #getToStreamTransferrer()}进行二次处理
     * 
     * @param source 输入流
     * @param bufferSize 缓冲区大小
     * @return 处理后的字节数组
     * @throws IOException 当I/O操作失败时抛出
     */
    @Override
    default byte[] toByteArray(InputStream source, int bufferSize) throws IOException {
        byte[] temp = getFromStreamTransferrer().toByteArray(source, bufferSize);
        return getToStreamTransferrer().toByteArray(temp, 0, temp.length);
    }
}