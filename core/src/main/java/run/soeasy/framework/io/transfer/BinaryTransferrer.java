package run.soeasy.framework.io.transfer;

import lombok.NonNull;
import run.soeasy.framework.io.buffer.ByteBufferInputStream;
import run.soeasy.framework.io.source.InputSource;
import run.soeasy.framework.io.source.OutputSource;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * 二进制传输器接口，专门用于处理二进制数据的传输。
 *
 * <p>本接口扩展了 {@link BufferTransferrer}，专注于二进制数据的转换和传输，
 * 提供了从各种二进制源到目标的便捷方法。
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>基于 {@link ByteBuffer} 进行高效的二进制块传输</li>
 *   <li>无缝集成 Java 标准 I/O 类（{@link InputStream}, {@link OutputStream}）</li>
 *   <li>支持 NIO 通道（{@link ReadableByteChannel}, {@link WritableByteChannel}）</li>
 *   <li>支持资源自动管理（try-with-resources）</li>
 * </ul>
 *
 * <p><b>使用示例：</b>
 * <pre>{@code
 * BinaryTransferrer transferrer = new DefaultBinaryTransferrer(8192);
 *
 * // 读取文件内容到字节数组
 * byte[] data = transferrer.toBinary(Files.newInputStream(path));
 *
 * // 将字节数组写入文件
 * transferrer.transfer(data, Files.newOutputStream(path));
 * }</pre>
 *
 * @author soeasy.run
 */
@FunctionalInterface
public interface BinaryTransferrer extends BufferTransferrer<ByteBuffer> {

    /**
     * 从 {@link BufferReader} 读取所有字节并转换为字节数组。
     *
     * @param reader 字节缓冲区读取器
     * @return 完整的字节数组内容
     * @throws IOException 如果发生 I/O 错误
     */
    default byte[] toBinary(@NonNull BufferReader<? super ByteBuffer> reader) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        transfer(reader, output);
        return output.toByteArray();
    }

    /**
     * 从 {@link InputStream} 读取所有字节并转换为字节数组。
     *
     * @param inputStream 输入流
     * @return 完整的字节数组内容
     * @throws IOException 如果发生 I/O 错误
     */
    default byte[] toBinary(@NonNull InputStream inputStream) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        transfer(inputStream, output);
        return output.toByteArray();
    }

    default byte[] toBinary(@NonNull File file) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        transfer(file, output);
        return output.toByteArray();
    }

    default byte[] toBinary(@NonNull Path path) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        transfer(path, output);
        return output.toByteArray();
    }

    /**
     * 从 {@link ReadableByteChannel} 读取所有字节并转换为字节数组。
     *
     * @param readableByteChannel 可读字节通道
     * @return 完整的字节数组内容
     * @throws IOException 如果发生 I/O 错误
     */
    default byte[] toBinary(@NonNull ReadableByteChannel readableByteChannel) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        transfer(readableByteChannel, output);
        return output.toByteArray();
    }

    /**
     * 将 {@link ByteBuffer} 转换为字节数组。
     *
     * <p>通过 {@link ByteBufferInputStream} 包装缓冲区并完全读取。
     *
     * @param byteBuffer 字节缓冲区
     * @return 转换后的字节数组
     */
    default byte[] toBinary(@NonNull ByteBuffer byteBuffer) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            transfer(new ByteBufferInputStream(byteBuffer), output);
        } catch (IOException e) {
            // ByteBufferInputStream 不会抛出 IOException，但为了接口兼容性保留
            throw new IllegalStateException("Internal error during byte buffer conversion", e);
        }
        return output.toByteArray();
    }

    /**
     * 从 {@link InputSource} 读取所有字节并转换为字节数组。
     *
     * @param inputSource 输入源
     * @return 完整的字节数组内容
     * @throws IOException 如果发生 I/O 错误
     */
    default <I extends InputStream> byte[] toBinary(@NonNull InputSource<I> inputSource) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        transfer(inputSource, output);
        return output.toByteArray();
    }

    /**
     * 从 {@link InputStream} 传输字节到 {@link BufferWriter}。
     *
     * @param input  输入流
     * @param writer 字节缓冲区写入器
     * @throws IOException 如果发生 I/O 错误
     */
    default void transfer(@NonNull InputStream input, @NonNull BufferWriter<? super ByteBuffer> writer) throws IOException {
        transfer(Channels.newChannel(input), writer);
    }

    /**
     * 从 {@link ReadableByteChannel} 传输字节到 {@link BufferWriter}。
     *
     * @param readable 可读字节通道
     * @param writer   字节缓冲区写入器
     * @throws IOException 如果发生 I/O 错误
     */
    default void transfer(@NonNull ReadableByteChannel readable, @NonNull BufferWriter<? super ByteBuffer> writer) throws IOException {
        transfer(readable::read, writer);
    }

    /**
     * 从 {@link BufferReader} 传输字节到 {@link OutputStream}。
     *
     * @param reader 字节缓冲区读取器
     * @param output 输出流
     * @throws IOException 如果发生 I/O 错误
     */
    default void transfer(@NonNull BufferReader<? super ByteBuffer> reader, @NonNull OutputStream output) throws IOException {
        transfer(reader, Channels.newChannel(output));
    }

    /**
     * 从 {@link BufferReader} 传输字节到 {@link WritableByteChannel}。
     *
     * @param reader   字节缓冲区读取器
     * @param writable 可写字节通道
     * @throws IOException 如果发生 I/O 错误
     */
    default void transfer(@NonNull BufferReader<? super ByteBuffer> reader, @NonNull WritableByteChannel writable) throws IOException {
        transfer(reader, writable::write);
    }

    /**
     * 在两个 NIO 通道之间传输字节。
     *
     * @param readable 可读字节通道
     * @param writable 可写字节通道
     * @throws IOException 如果发生 I/O 错误
     */
    default void transfer(@NonNull ReadableByteChannel readable, @NonNull WritableByteChannel writable) throws IOException {
        transfer(readable::read, writable::write);
    }

    /**
     * 从 {@link InputStream} 传输字节到 {@link WritableByteChannel}。
     *
     * @param input    输入流
     * @param writable 可写字节通道
     * @throws IOException 如果发生 I/O 错误
     */
    default void transfer(@NonNull InputStream input, @NonNull WritableByteChannel writable) throws IOException {
        transfer(Channels.newChannel(input), writable);
    }

    /**
     * 从 {@link ReadableByteChannel} 传输字节到 {@link OutputStream}。
     *
     * @param readable 可读字节通道
     * @param output   输出流
     * @throws IOException 如果发生 I/O 错误
     */
    default void transfer(@NonNull ReadableByteChannel readable, @NonNull OutputStream output) throws IOException {
        transfer(readable, Channels.newChannel(output));
    }

    /**
     * 在 {@link InputStream} 和 {@link OutputStream} 之间传输字节。
     *
     * @param input  输入流
     * @param output 输出流
     * @throws IOException 如果发生 I/O 错误
     */
    default void transfer(@NonNull InputStream input, @NonNull OutputStream output) throws IOException {
        transfer(Channels.newChannel(input), Channels.newChannel(output));
    }

    /**
     * 从 {@link InputSource} 传输字节到 {@link BufferWriter}。
     *
     * @param inputSource 输入源
     * @param writer      字节缓冲区写入器
     * @throws IOException 如果发生 I/O 错误
     */
    default <I extends InputStream> void transfer(@NonNull InputSource<I> inputSource, @NonNull BufferWriter<? super ByteBuffer> writer) throws IOException {
        try (ReadableByteChannel readableChannel = inputSource.readableChannel()) {
            transfer(readableChannel, writer);
        }
    }

    /**
     * 从 {@link InputSource} 传输字节到 {@link OutputStream}。
     *
     * @param inputSource  输入源
     * @param outputStream 输出流
     * @throws IOException 如果发生 I/O 错误
     */
    default <I extends InputStream> void transfer(@NonNull InputSource<I> inputSource, @NonNull OutputStream outputStream) throws IOException {
        try (ReadableByteChannel readableChannel = inputSource.readableChannel()) {
            transfer(readableChannel, outputStream);
        }
    }

    /**
     * 从 {@link InputSource} 传输字节到 {@link WritableByteChannel}。
     *
     * @param inputSource         输入源
     * @param writableByteChannel 可写字节通道
     * @throws IOException 如果发生 I/O 错误
     */
    default <I extends InputStream> void transfer(@NonNull InputSource<I> inputSource, @NonNull WritableByteChannel writableByteChannel) throws IOException {
        try (ReadableByteChannel readableChannel = inputSource.readableChannel()) {
            transfer(readableChannel, writableByteChannel);
        }
    }

    /**
     * 从 {@link BufferReader} 传输字节到 {@link OutputSource}。
     *
     * @param reader       字节缓冲区读取器
     * @param outputSource 输出源
     * @throws IOException 如果发生 I/O 错误
     */
    default <O extends OutputStream> void transfer(@NonNull BufferReader<? super ByteBuffer> reader, @NonNull OutputSource<O> outputSource) throws IOException {
        try (WritableByteChannel writableByteChannel = outputSource.writableChannel()) {
            transfer(reader, writableByteChannel);
        }
    }

    /**
     * 从 {@link ReadableByteChannel} 传输字节到 {@link OutputSource}。
     *
     * @param readableByteChannel 可读字节通道
     * @param outputSource        输出源
     * @throws IOException 如果发生 I/O 错误
     */
    default <O extends OutputStream> void transfer(@NonNull ReadableByteChannel readableByteChannel, @NonNull OutputSource<O> outputSource) throws IOException {
        try (WritableByteChannel writableByteChannel = outputSource.writableChannel()) {
            transfer(readableByteChannel, writableByteChannel);
        }
    }

    /**
     * 从 {@link InputStream} 传输字节到 {@link OutputSource}。
     *
     * @param inputStream  输入流
     * @param outputSource 输出源
     * @throws IOException 如果发生 I/O 错误
     */
    default <O extends OutputStream> void transfer(@NonNull InputStream inputStream, @NonNull OutputSource<O> outputSource) throws IOException {
        try (WritableByteChannel writableByteChannel = outputSource.writableChannel()) {
            transfer(inputStream, writableByteChannel);
        }
    }

    /**
     * 在 {@link InputSource} 和 {@link OutputSource} 之间传输字节。
     *
     * @param inputSource  输入源
     * @param outputSource 输出源
     * @throws IOException 如果发生 I/O 错误
     */
    default <I extends InputStream, O extends OutputStream> void transfer(@NonNull InputSource<I> inputSource, @NonNull OutputSource<O> outputSource) throws IOException {
        try (ReadableByteChannel readableChannel = inputSource.readableChannel();
             WritableByteChannel writableChannel = outputSource.writableChannel()) {
            transfer(readableChannel, writableChannel);
        }
    }

    default void transfer(@NonNull Path source, @NonNull Path target) throws IOException {
        try (ReadableByteChannel readable = Files.newByteChannel(source, StandardOpenOption.READ); WritableByteChannel writable = Files.newByteChannel(target, StandardOpenOption.WRITE)) {
            transfer(readable, writable);
        }
    }

    default void transfer(@NonNull File source, @NonNull File target) throws IOException {
        transfer(source.toPath(), target.toPath());
    }

    default void transfer(@NonNull Path source, @NonNull WritableByteChannel target) throws IOException {
        try (ReadableByteChannel readable = Files.newByteChannel(source, StandardOpenOption.READ)) {
            transfer(readable, target);
        }
    }

    default void transfer(@NonNull Path source, @NonNull OutputStream output) throws IOException {
        transfer(source, Channels.newChannel(output));
    }

    default <O extends OutputStream> void transfer(@NonNull Path source, @NonNull OutputSource<O> outputSource) throws IOException {
        transfer(source, outputSource.writableChannel());
    }

    default void transfer(@NonNull Path source, @NonNull BufferWriter<? super ByteBuffer> writer) throws IOException {
        try (ReadableByteChannel readable = Files.newByteChannel(source, StandardOpenOption.READ)) {
            transfer(readable, writer);
        }
    }

    default void transfer(@NonNull Path source, @NonNull File target) throws IOException {
        transfer(source, target.toPath());
    }

    default void transfer(@NonNull ReadableByteChannel readable, @NonNull Path target) throws IOException {
        try (WritableByteChannel writable = Files.newByteChannel(target, StandardOpenOption.WRITE)) {
            transfer(readable, writable);
        }
    }

    default <I extends InputStream> void transfer(@NonNull InputSource<I> inputSource, @NonNull Path targetPath) throws IOException {
        transfer(inputSource.readableChannel(), targetPath);
    }

    default void transfer(@NonNull InputStream inputStream, @NonNull Path targetPath) throws IOException {
        transfer(Channels.newChannel(inputStream), targetPath);
    }

    default void transfer(@NonNull File source, @NonNull Path target) throws IOException {
        transfer(source.toPath(), target);
    }

    default void transfer(@NonNull BufferReader<? super ByteBuffer> reader, @NonNull Path target) throws IOException {
        try (WritableByteChannel writable = Files.newByteChannel(target, StandardOpenOption.WRITE)) {
            transfer(reader, writable);
        }
    }

    default void transfer(@NonNull File sourceFile, @NonNull WritableByteChannel writableByteChannel) throws IOException {
        transfer(sourceFile.toPath(), writableByteChannel);
    }

    default void transfer(@NonNull File sourceFile, @NonNull BufferWriter<? super ByteBuffer> writer) throws IOException {
        transfer(sourceFile.toPath(), writer);
    }

    default void transfer(@NonNull File sourceFile, @NonNull OutputStream output) throws IOException {
        transfer(sourceFile, Channels.newChannel(output));
    }

    default <O extends OutputStream> void transfer(@NonNull File sourceFile, @NonNull OutputSource<O> outputSource) throws IOException {
        transfer(sourceFile, outputSource.writableChannel());
    }

    default void transfer(@NonNull BufferReader<? super ByteBuffer> reader, @NonNull File targetFile) throws IOException {
        transfer(reader, targetFile.toPath());
    }

    default void transfer(@NonNull ReadableByteChannel readableByteChannel, @NonNull File targetFile) throws IOException {
        transfer(readableByteChannel, targetFile.toPath());
    }

    default void transfer(@NonNull InputStream inputStream, @NonNull File targetFile) throws IOException {
        transfer(Channels.newChannel(inputStream), targetFile);
    }

    default <I extends InputStream> void transfer(@NonNull InputSource<I> inputSource, @NonNull File targetFile) throws IOException {
        transfer(inputSource.readableChannel(), targetFile);
    }
}