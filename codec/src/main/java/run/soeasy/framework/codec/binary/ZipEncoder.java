package run.soeasy.framework.codec.binary;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.function.ThrowingConsumer;
import run.soeasy.framework.io.BinaryTransferrer;
import run.soeasy.framework.io.BufferConsumer;
import run.soeasy.framework.io.IOUtils;
import run.soeasy.framework.io.StreamTransferrer;

/**
 * ZIP编码器，实现对数据的ZIP压缩（编码）操作，继承自{@link BinaryEncoder}和{@link StreamTransferrer}，
 * 支持指定字符集处理ZIP条目名称，并允许自定义{@link ZipOutputStream}的属性配置，适用于流式ZIP压缩场景。
 * 
 * <p>提供默认UTF-8字符集实例{@link #UTF_8}，支持对普通输入流进行ZIP压缩或直接处理{@link ZipInputStream}。
 * 
 * @author soeasy.run
 * @see BinaryEncoder
 * @see StreamTransferrer
 * @see ZipOutputStream
 */
@Getter
@RequiredArgsConstructor
public class ZipEncoder implements BinaryEncoder, StreamTransferrer {
    /**
     * 使用UTF-8字符集的默认ZIP编码器实例
     */
    public static final ZipEncoder UTF_8 = new ZipEncoder(StandardCharsets.UTF_8);

    /**
     * 用于处理ZIP条目名称等文本信息的字符集
     */
    @NonNull
    private final Charset charset;

    /**
     * 用于在{@link ZipOutputStream}创建后设置其属性的消费者（如压缩级别、注释等）
     */
    @NonNull
    private final ThrowingConsumer<? super ZipOutputStream, ? extends IOException> afterProperties;

    /**
     * 构造ZIP编码器（使用默认的属性设置消费者）
     * 
     * @param charset 处理ZIP文本信息的字符集
     */
    public ZipEncoder(Charset charset) {
        this(charset, ThrowingConsumer.ignore());
    }

    /**
     * 将输入流数据压缩为ZIP格式并传输到输出流（核心压缩方法）
     * 
     * <p>处理逻辑：
     * 1. 若输出流是{@link ZipOutputStream}，直接使用；否则创建新的ZIP输出流并应用字符集
     * 2. 对新创建的ZIP输出流执行{@link #afterProperties}配置（如设置压缩级别）
     * 3. 通过{@link IOUtils}将输入流数据传输到ZIP输出流（完成压缩）
     * 4. 若为新创建的ZIP输出流，操作完成后关闭以保证压缩数据完整
     * 
     * @param source 待压缩的输入流
     * @param bufferSize 压缩缓冲区大小
     * @param target 接收ZIP压缩数据的输出流
     * @throws IOException 当I/O操作失败或压缩配置出错时抛出
     */
    @Override
    public void transferTo(@NonNull InputStream source, int bufferSize, @NonNull OutputStream target)
            throws IOException {
        ZipOutputStream zip;
        boolean isNew = false;
        if (target instanceof ZipOutputStream) {
            zip = (ZipOutputStream) target;
        } else {
            zip = new ZipOutputStream(target, charset);
            afterProperties.accept(zip);
            isNew = true;
        }

        try {
            IOUtils.transferTo(source, bufferSize, zip::write);
        } finally {
            if (isNew) {
                target.close();
            }
        }
    }

    /**
     * 将输入流数据压缩为ZIP格式并传输到缓冲区消费者
     * 
     * <p>处理逻辑：
     * 1. 若输入流是{@link ZipInputStream}（已压缩），直接传输数据
     * 2. 否则调用父类{@link StreamTransferrer}的默认实现进行压缩传输
     * 
     * @param <E> 消费者可能抛出的异常类型
     * @param source 待压缩的输入流
     * @param bufferSize 传输缓冲区大小
     * @param target 接收ZIP压缩数据的缓冲区消费者
     * @throws IOException 当I/O操作失败或压缩过程出错时抛出
     * @throws E 当消费者处理数据时抛出
     */
    @Override
    public final <E extends Throwable> void transferTo(@NonNull InputStream source, int bufferSize,
            @NonNull BufferConsumer<? super byte[], ? extends E> target) throws IOException, E {
        if (source instanceof ZipInputStream) {
            IOUtils.transferTo(source, bufferSize, target);
        } else {
            StreamTransferrer.super.transferTo(source, bufferSize, target);
        }
    }

    /**
     * 获取当前编码器作为压缩传输器
     * 
     * @return 当前{@link ZipEncoder}实例，用于ZIP压缩的流式传输
     */
    @Override
    public final BinaryTransferrer getEncodeTransferrer() {
        return this;
    }
}