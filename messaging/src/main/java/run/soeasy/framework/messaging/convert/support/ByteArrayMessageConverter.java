package run.soeasy.framework.messaging.convert.support;

import java.io.IOException;

import lombok.NonNull;
import run.soeasy.framework.core.convert.value.TargetDescriptor;
import run.soeasy.framework.core.convert.value.TypedData;
import run.soeasy.framework.io.MimeType;
import run.soeasy.framework.messaging.MediaType;
import run.soeasy.framework.messaging.Message;

/**
 * 字节数组消息转换器，继承自{@link AbstractBinaryMessageConverter}，
 * 专门用于处理字节数组（{@code byte[]}）与消息体之间的转换，是二进制数据的直接映射转换器，
 * 支持{@link MediaType#APPLICATION_OCTET_STREAM}（二进制流）和所有类型（{@link MediaType#ALL}）的消息。
 * 
 * <p>核心特性：
 * - 源/目标类型为{@code byte[]}，转换逻辑极简（直接复用字节数组）；
 * - 无需额外序列化/反序列化步骤，适用于原始二进制数据传输（如文件、加密数据）。
 * 
 * @author soeasy.run
 * @see AbstractBinaryMessageConverter
 * @see byte[]
 * @see MediaType#APPLICATION_OCTET_STREAM
 */
public class ByteArrayMessageConverter extends AbstractBinaryMessageConverter<byte[]> {

    /**
     * 初始化字节数组消息转换器，指定目标类型为{@code byte[]}，
     * 并注册支持的媒体类型：{@link MediaType#APPLICATION_OCTET_STREAM}（二进制流）和{@link MediaType#ALL}（所有类型）。
     */
    public ByteArrayMessageConverter() {
        super(byte[].class);
        // 注册支持的媒体类型：二进制流和所有类型
        getMediaTypeRegistry().register(MediaType.APPLICATION_OCTET_STREAM);
        getMediaTypeRegistry().register(MediaType.ALL);
    }

    /**
     * 重写二进制解析逻辑，直接返回原始字节数组（无需转换）
     * 
     * <p>由于目标类型是{@code byte[]}，输入的字节数组可直接作为结果返回，
     * 适用于读取二进制消息体（如文件内容、加密字节流）。
     * 
     * @param body 消息体的字节数组（非空，原始二进制数据）
     * @param targetDescriptor 目标类型描述符（非空，类型为{@code byte[]}）
     * @param message 关联的消息（非空）
     * @param contentType 媒体类型（非空，通常为{@link MediaType#APPLICATION_OCTET_STREAM}）
     * @return 原始字节数组（与输入{@code body}相同）
     * @throws IOException 无实际异常（字节数组直接返回，不会抛出）
     */
    @Override
    protected byte[] parseObject(byte[] body, @NonNull TargetDescriptor targetDescriptor, @NonNull Message message,
            MimeType contentType) throws IOException {
        return body;
    }

    /**
     * 重写二进制序列化逻辑，直接返回源字节数组（无需转换）
     * 
     * <p>由于源数据已是{@code byte[]}类型，可直接作为二进制结果返回，
     * 适用于写入二进制消息体（如输出文件内容、加密字节流）。
     * 
     * @param body 待转换的字节数组数据（非空，{@link TypedData}包装的{@code byte[]}）
     * @param message 关联的消息（非空）
     * @param mediaType 媒体类型（非空）
     * @return 源字节数组（与输入{@code body.get()}相同）
     * @throws IOException 无实际异常（字节数组直接返回，不会抛出）
     */
    @Override
    protected byte[] toBinary(@NonNull TypedData<byte[]> body, @NonNull Message message, MediaType mediaType)
            throws IOException {
        return body.get();
    }
}