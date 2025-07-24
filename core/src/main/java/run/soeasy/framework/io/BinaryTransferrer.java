package run.soeasy.framework.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import lombok.NonNull;

/**
 * 二进制数据传输器接口，提供多种方式的数据流传输功能
 * <p>
 * 支持输入流到输出流、输入源到输出源、输入流到数据消费者等多种传输模式，
 * 并提供链式操作、缓冲传输、重复传输等增强功能。
 * 
 * @author soeasy.run
 */
@FunctionalInterface
public interface BinaryTransferrer {
    
    /**
     * 将两个传输器链接起来，形成链式传输
     * 
     * @param fromStreamTransferrer 源传输器，不可为null
     * @param toStreamTransferrer 目标传输器，不可为null
     * @return 链式传输器实例
     */
    public static BinaryTransferrer chain(@NonNull BinaryTransferrer fromStreamTransferrer,
            @NonNull BinaryTransferrer toStreamTransferrer) {
        return fromStreamTransferrer.to(toStreamTransferrer);
    }

    /**
     * 创建一个重复执行指定次数的传输器
     * 
     * @param repetitions 重复次数
     * @param streamTransferrer 要重复执行的传输器，不可为null
     * @return 重复传输器实例
     */
    public static BinaryTransferrer repeatTransferTo(int repetitions, @NonNull BinaryTransferrer streamTransferrer) {
        return streamTransferrer.repeat(repetitions);
    }

    /**
     * 创建一个链式传输器，当前传输器作为目标，参数传输器作为源
     * 
     * @param streamTransferrer 源传输器，不可为null
     * @return 链式传输器实例
     */
    default BinaryTransferrer from(@NonNull final BinaryTransferrer streamTransferrer) {
        return new ChainBinaryTransferrer() {

            @Override
            public BinaryTransferrer getFromStreamTransferrer() {
                return streamTransferrer;
            }

            @Override
            public BinaryTransferrer getToStreamTransferrer() {
                return BinaryTransferrer.this;
            }
        };
    }

    /**
     * 创建一个带缓冲的传输器
     * 
     * @param bufferSize 缓冲区大小
     * @return 带缓冲的传输器实例
     */
    default BinaryTransferrer buffered(int bufferSize) {
        return new BufferTransferrer<BinaryTransferrer>() {

            @Override
            public BinaryTransferrer getSource() {
                return BinaryTransferrer.this;
            }

            @Override
            public int getBufferSize() {
                return bufferSize;
            }
        };
    }

    /**
     * 创建一个可重复执行指定次数的传输器
     * 
     * @param repetitions 重复次数
     * @return 可重复执行的传输器实例
     */
    default BinaryTransferrer repeat(int repetitions) {
        return new BinaryTransferrerWrapper<BinaryTransferrer>() {

            @Override
            public int getRepetitions() {
                return repetitions;
            }

            @Override
            public BinaryTransferrer getSource() {
                return BinaryTransferrer.this;
            }
        };
    }

    /**
     * 创建一个链式传输器，当前传输器作为源，参数传输器作为目标
     * 
     * @param streamTransferrer 目标传输器，不可为null
     * @return 链式传输器实例
     */
    default BinaryTransferrer to(@NonNull final BinaryTransferrer streamTransferrer) {
        return new ChainBinaryTransferrer() {

            @Override
            public BinaryTransferrer getFromStreamTransferrer() {
                return BinaryTransferrer.this;
            }

            @Override
            public BinaryTransferrer getToStreamTransferrer() {
                return streamTransferrer;
            }
        };
    }

    /**
     * 将字节数组的指定部分转换为字节数组（通过输入流传输）
     * 
     * @param source 源字节数组，不可为null
     * @param offset 起始偏移量
     * @param length 数据长度
     * @return 转换后的字节数组
     * @throws IllegalStateException 当发生IO异常时抛出
     */
    default byte[] toByteArray(@NonNull byte[] source, int offset, int length) throws IllegalStateException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(source, offset, length);
        try {
            return toByteArray(inputStream, length);
        } catch (IOException e) {
            throw new IllegalStateException("Internal IO exception", e);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    /**
     * 从输入源读取数据并转换为字节数组
     * 
     * @param source 输入源，不可为null
     * @param bufferSize 缓冲区大小
     * @return 读取到的字节数组
     * @throws IOException 当IO操作失败时抛出
     */
    default byte[] toByteArray(@NonNull InputSource source, int bufferSize) throws IOException {
        InputStream inputStream = source.getInputStream();
        try {
            return toByteArray(inputStream, bufferSize);
        } finally {
            inputStream.close();
        }
    }

    /**
     * 从输入流读取数据并转换为字节数组
     * 
     * @param source 输入流，不可为null
     * @param bufferSize 缓冲区大小
     * @return 读取到的字节数组
     * @throws IOException 当IO操作失败时抛出
     */
    default byte[] toByteArray(@NonNull InputStream source, int bufferSize) throws IOException {
        ByteArrayOutputStream target = new ByteArrayOutputStream(bufferSize);
        try {
            transferTo(source, bufferSize, target);
        } finally {
            target.close();
        }
        return target.toByteArray();
    }

    /**
     * 从输入源传输数据到消费者（自动管理输入流生命周期）。
     * <p>
     * 执行流程：
     * <ol>
     * <li>从InputSource获取输入流</li>
     * <li>调用{@link #transferTo(InputStream, int, BufferConsumer)}执行核心传输</li>
     * <li>通过try-finally确保输入流关闭</li>
     * </ol>
     * 
     * @param source     输入源，不可为null
     * @param bufferSize 传输缓冲区大小（建议≥4096）
     * @param target     数据消费者，不可为null
     * @param <E>        消费者可能抛出的异常类型
     * @throws IOException 输入流获取或传输失败时抛出
     * @throws E           消费者处理数据时抛出的异常
     */
    default <E extends Throwable> void transferTo(@NonNull InputSource source, int bufferSize,
            @NonNull BufferConsumer<? super byte[], ? extends E> target) throws IOException, E {
        InputStream inputStream = source.getInputStream();
        try {
            this.transferTo(inputStream, bufferSize, target);
        } finally {
            inputStream.close();
        }
    }

    /**
     * 从输入源传输数据到输出源（自动管理输入流和输出流生命周期）。
     * <p>
     * 执行流程：
     * <ol>
     * <li>从InputSource获取输入流</li>
     * <li>调用{@link #transferTo(InputStream, int, OutputSource)}执行传输</li>
     * <li>通过try-finally确保输入流关闭</li>
     * </ol>
     * 
     * @param source     输入源，不可为null
     * @param bufferSize 传输缓冲区大小
     * @param target     输出源，不可为null
     * @throws IOException 流获取或传输失败时抛出
     */
    default void transferTo(@NonNull InputSource source, int bufferSize, @NonNull OutputSource target)
            throws IOException {
        InputStream inputStream = source.getInputStream();
        try {
            this.transferTo(inputStream, bufferSize, target);
        } finally {
            inputStream.close();
        }
    }

    /**
     * 从输入源传输数据到输出流（自动管理输入流生命周期）。
     * <p>
     * 执行流程：
     * <ol>
     * <li>从InputSource获取输入流</li>
     * <li>调用{@link #transferTo(InputStream, int, OutputStream)}执行传输</li>
     * <li>通过try-finally确保输入流关闭（输出流由调用方管理）</li>
     * </ol>
     * 
     * @param source     输入源，不可为null
     * @param bufferSize 传输缓冲区大小
     * @param target     输出流，不可为null
     * @throws IOException 流获取或传输失败时抛出
     */
    default void transferTo(@NonNull InputSource source, int bufferSize, @NonNull OutputStream target)
            throws IOException {
        InputStream inputStream = source.getInputStream();
        try {
            this.transferTo(inputStream, bufferSize, target);
        } finally {
            inputStream.close();
        }
    }

    /**
     * 从输入流传输数据到消费者（核心传输方法，需子类实现具体逻辑）。
     * <p>
     * 实现类应实现以下逻辑：
     * <ol>
     * <li>创建指定大小的缓冲区（byte[]）</li>
     * <li>循环读取输入流数据到缓冲区</li>
     * <li>通过BufferConsumer处理缓冲区数据</li>
     * <li>直到输入流读取完毕（返回-1）</li>
     * </ol>
     * 
     * @param source     输入流，不可为null
     * @param bufferSize 传输缓冲区大小（>0）
     * @param target     数据消费者，不可为null
     * @param <E>        消费者可能抛出的异常类型
     * @throws IOException 输入流读取失败时抛出
     * @throws E           消费者处理数据时抛出的异常
     */
    <E extends Throwable> void transferTo(@NonNull InputStream source, int bufferSize,
            @NonNull BufferConsumer<? super byte[], ? extends E> target) throws IOException, E;

    /**
     * 从输入流传输数据到输出源（自动管理输出流生命周期）。
     * <p>
     * 执行流程：
     * <ol>
     * <li>从OutputSource获取输出流</li>
     * <li>调用{@link #transferTo(InputStream, int, OutputStream)}执行传输</li>
     * <li>通过try-finally确保输出流关闭</li>
     * </ol>
     * 
     * @param source     输入流，不可为null
     * @param bufferSize 传输缓冲区大小
     * @param target     输出源，不可为null
     * @throws IOException 输出流获取或传输失败时抛出
     */
    default void transferTo(@NonNull InputStream source, int bufferSize, @NonNull OutputSource target)
            throws IOException {
        OutputStream outputStream = target.getOutputStream();
        try {
            this.transferTo(source, bufferSize, outputStream);
        } finally {
            outputStream.close();
        }
    }

    /**
     * 从输入流传输数据到输出流（通过消费者模式适配）。
     * <p>
     * 将输出流的write方法适配为BufferConsumer，调用核心传输方法执行数据传输。
     * 
     * @param source     输入流，不可为null
     * @param bufferSize 传输缓冲区大小
     * @param target     输出流，不可为null
     * @throws IOException 流读写失败时抛出
     */
    default void transferTo(@NonNull InputStream source, int bufferSize, @NonNull OutputStream target)
            throws IOException {
        this.transferTo(source, bufferSize, target::write);
    }
}
    