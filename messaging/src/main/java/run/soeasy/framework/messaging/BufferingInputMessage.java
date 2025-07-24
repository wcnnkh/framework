package run.soeasy.framework.messaging;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import lombok.NonNull;
import lombok.Setter;
import run.soeasy.framework.core.domain.Wrapped;
import run.soeasy.framework.core.function.Pipeline;

/**
 * 带缓冲的输入消息包装器，实现{@link InputMessageWrapper}接口，用于缓存输入消息的内容，
 * 支持输入流的重复读取，解决原始输入流（如网络流）只能读取一次的问题。
 * 
 * <p>该类通过将原始输入消息的内容读取到字节数组中，并包装为{@link ByteArrayInputStream}，
 * 实现输入流的多次获取和读取，适用于需要重复处理消息内容的场景（如多次解析消息体、日志记录等）。
 * 
 * @param <W> 被包装的输入消息类型（需实现{@link InputMessage}）
 * @author soeasy.run
 * @see InputMessageWrapper
 * @see InputMessage
 * @see ByteArrayInputStream
 */
@Setter
public class BufferingInputMessage<W extends InputMessage> extends Wrapped<W> implements InputMessageWrapper<W> {

    /**
     * 缓冲的输入流，基于原始消息内容的字节数组创建，支持重复读取
     */
    private InputStream inputStream;

    /**
     * 创建带缓冲的输入消息包装器，包装指定的原始输入消息
     * 
     * @param source 被包装的原始输入消息（非空）
     */
    public BufferingInputMessage(W source) {
        super(source);
    }

    /**
     * 获取缓冲的输入流
     * 
     * <p>首次调用时，会从原始消息（{@link #getSource()}）读取所有内容到字节数组，
     * 并创建{@link ByteArrayInputStream}作为缓冲流；后续调用直接返回该缓冲流，支持重复读取。
     * 
     * @return 缓冲的输入流（非空）
     * @throws IOException 若读取原始消息内容时发生I/O错误
     */
    @Override
    public InputStream getInputStream() throws IOException {
        if (inputStream == null) {
            // 读取原始消息的所有字节并缓存
            byte[] data = getSource().toByteArray();
            inputStream = new ByteArrayInputStream(data);
        }
        return inputStream;
    }

    /**
     * 获取输入流的处理管道，用于链式处理输入流
     * 
     * <p>管道通过{@link Pipeline#forSupplier(java.util.function.Supplier)}创建，
     * 关联{@link #getInputStream()}方法，确保每次获取的都是缓冲后的输入流。
     * 
     * @return 输入流处理管道（非空）
     */
    @Override
    public @NonNull Pipeline<InputStream, IOException> getInputStreamPipeline() {
        return Pipeline.forSupplier(() -> getInputStream());
    }

    /**
     * 返回当前缓冲消息实例（已缓冲，无需再次包装）
     * 
     * @return 当前{@code BufferingInputMessage}实例
     */
    @Override
    public InputMessage buffered() {
        return this;
    }
}