package run.soeasy.framework.messaging.convert.support;

import java.io.IOException;

import lombok.NonNull;
import run.soeasy.framework.core.convert.value.TargetDescriptor;
import run.soeasy.framework.core.convert.value.TypedData;
import run.soeasy.framework.io.MimeType;
import run.soeasy.framework.messaging.InputMessage;
import run.soeasy.framework.messaging.MediaType;
import run.soeasy.framework.messaging.Message;
import run.soeasy.framework.messaging.OutputMessage;

/**
 * 二进制消息转换器抽象基类，继承自{@link ObjectMessageConverter}，
 * 专注于处理二进制格式消息（如字节流、文件内容）的转换，将消息体以字节数组为中间格式进行读写，
 * 简化二进制与对象之间的转换逻辑（如图片、二进制协议数据等场景）。
 * 
 * <p>核心逻辑：
 * - 读取时：先将输入消息内容转换为字节数组，再委托子类{@link #parseObject}解析为目标类型{@code T}；
 * - 写入时：先委托子类{@link #toBinary}将目标类型{@code T}转换为字节数组，再写入输出消息。
 * 
 * @param <T> 转换器支持的目标对象类型（如byte[]、Image、自定义二进制对象等）
 * @author soeasy.run
 * @see ObjectMessageConverter
 * @see InputMessage#toByteArray()
 */
public abstract class AbstractBinaryMessageConverter<T> extends ObjectMessageConverter<T> {

    /**
     * 初始化二进制消息转换器，指定支持的目标类型
     * 
     * @param requriedType 目标对象类型（非空，如byte[].class、Image.class）
     */
    public AbstractBinaryMessageConverter(@NonNull Class<T> requriedType) {
        super(requriedType);
    }

    /**
     * 将字节数组解析为目标类型{@code T}的对象
     * 
     * <p>子类需实现此方法，完成具体的二进制解析逻辑，例如：
     * - 将字节数组转换为图片对象（如ImageIO.read(new ByteArrayInputStream(bytes))）；
     * - 解析自定义二进制协议（如按固定格式解析字节流为业务对象）。
     * 
     * @param body 消息体的字节数组（非空，从输入消息读取的原始二进制数据）
     * @param targetDescriptor 目标类型描述符（包含待转换的类型信息，非空）
     * @param message 关联的消息（非空，可用于获取头信息辅助解析）
     * @param contentType 媒体类型（非空，用于区分不同二进制格式）
     * @return 解析后的{@code T}类型对象（非空）
     * @throws IOException 解析过程中发生错误（如格式不匹配、字节数组损坏）
     */
    protected abstract T parseObject(byte[] body, @NonNull TargetDescriptor targetDescriptor, @NonNull Message message,
            MimeType contentType) throws IOException;

    /**
     * 将目标类型{@code T}的对象转换为二进制字节数组
     * 
     * <p>子类需实现此方法，完成具体的二进制序列化逻辑，例如：
     * - 将图片对象转换为字节数组（如通过ByteArrayOutputStream写入）；
     * - 将业务对象序列化为自定义二进制协议格式。
     * 
     * @param body 待转换的{@code T}类型数据（非空，包含值和类型信息）
     * @param message 关联的消息（非空，可用于获取头信息辅助序列化）
     * @param mediaType 媒体类型（非空，用于指定二进制格式）
     * @return 序列化后的字节数组（非空）
     * @throws IOException 序列化过程中发生错误（如对象无法转换为二进制、流操作失败）
     */
    protected abstract byte[] toBinary(@NonNull TypedData<T> body, @NonNull Message message, MediaType mediaType)
            throws IOException;

    /**
     * 重写消息读取逻辑：先获取消息体字节数组，再委托{@link #parseObject}解析为目标类型
     * 
     * <p>步骤：
     * 1. 调用{@link InputMessage#toByteArray()}获取消息体的字节数组；
     * 2. 调用{@link #parseObject}将字节数组解析为{@code T}类型对象。
     * 
     * @param targetDescriptor 目标类型描述符（非空）
     * @param message 待读取的输入消息（非空）
     * @param contentType 媒体类型（非空）
     * @return 解析后的{@code T}类型对象（由{@link #parseObject}返回）
     * @throws IOException 获取字节数组或解析过程中发生错误
     */
    @Override
    protected T readObject(@NonNull TargetDescriptor targetDescriptor, @NonNull InputMessage message,
            MimeType contentType) throws IOException {
        byte[] body = message.toByteArray();
        return parseObject(body, targetDescriptor, message, contentType);
    }

    /**
     * 重写消息写入逻辑：先将对象转换为字节数组，再写入输出消息
     * 
     * <p>步骤：
     * 1. 调用{@link #toBinary}将{@code T}类型对象转换为字节数组；
     * 2. 若输出消息未设置Content-Length，自动设置为字节数组长度；
     * 3. 将字节数组写入输出消息的输出流。
     * 
     * @param data 待写入的{@code T}类型数据（非空）
     * @param message 目标输出消息（非空）
     * @param contentType 媒体类型（非空）
     * @throws IOException 转换为字节数组或写入流过程中发生错误
     */
    @Override
    protected void writeObject(@NonNull TypedData<T> data, @NonNull OutputMessage message,
            @NonNull MediaType contentType) throws IOException {
        byte[] body = toBinary(data, message, contentType);
        if (body == null) {
            return;
        }

        // 自动设置内容长度（若未手动设置）
        if (message.getContentLength() < 0) {
            message.setContentLength(body.length);
        }
        // 写入字节数组到输出流
        message.getOutputStreamPipeline().optional().ifPresent((outputStream) -> {
            try {
                outputStream.write(body);
            } catch (IOException e) {
                throw new RuntimeException("Failed to write binary data to output stream", e);
            }
        });
    }
}