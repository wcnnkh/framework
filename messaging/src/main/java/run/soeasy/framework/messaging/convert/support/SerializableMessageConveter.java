package run.soeasy.framework.messaging.convert.support;

import java.io.IOException;
import java.util.Arrays;

import lombok.NonNull;
import run.soeasy.framework.core.convert.value.TargetDescriptor;
import run.soeasy.framework.core.convert.value.TypedData;
import run.soeasy.framework.io.MimeType;
import run.soeasy.framework.messaging.MediaType;
import run.soeasy.framework.messaging.Message;
import run.soeasy.framework.serializer.Serializer;

/**
 * 可序列化消息转换器，继承自{@link AbstractBinaryMessageConverter}，基于{@link Serializer}实现对象与二进制数据的转换，
 * 支持在序列化/反序列化过程中传入类型描述符，确保类型转换的准确性，适用于需要通过二进制序列化（如通用二进制协议、跨平台数据传输）处理消息的场景。
 * 
 * <p>核心特性：
 * - 依赖{@link Serializer}处理对象与字节数组的双向转换，支持带类型描述符的序列化/反序列化；
 * - 仅支持{@link MediaType#APPLICATION_OCTET_STREAM}（二进制流）媒体类型；
 * - 适用于任意Java对象的二进制传输，通过类型描述符解决泛型、多态等场景的类型匹配问题。
 * 
 * @author soeasy.run
 * @see AbstractBinaryMessageConverter
 * @see Serializer
 * @see MediaType#APPLICATION_OCTET_STREAM
 */
public class SerializableMessageConveter extends AbstractBinaryMessageConverter<Object> {

    /**
     * 序列化工具，用于执行对象的序列化（对象→字节数组）和反序列化（字节数组→对象），
     * 支持传入类型描述符以确保转换的类型准确性（如泛型类型、接口实现类等）。
     */
    private final Serializer serializer;

    /**
     * 初始化可序列化消息转换器，指定序列化工具并注册支持的媒体类型
     * 
     * @param serializer 序列化工具（非空，需支持带类型描述符的序列化与反序列化操作）
     */
    public SerializableMessageConveter(Serializer serializer) {
        super(Object.class); // 支持任意对象类型作为转换目标
        // 注册支持的媒体类型：二进制流（application/octet-stream）
        getMediaTypeRegistry().addAll(Arrays.asList(MediaType.APPLICATION_OCTET_STREAM));
        this.serializer = serializer;
    }

    /**
     * 将二进制字节数组反序列化为目标类型的对象
     * 
     * <p>通过{@link Serializer#deserialize(byte[], TypeDescriptor)}将字节数组转换为目标类型对象，
     * 传入目标类型描述符（{@link TargetDescriptor#getRequiredTypeDescriptor()}）以确保反序列化结果
     * 与预期类型一致（解决泛型擦除、多态类型转换等问题，如将字节数组准确反序列化为List<String>而非List）。
     * 
     * @param body 待反序列化的字节数组（非空，二进制序列化数据）
     * @param targetDescriptor 目标类型描述符（非空，包含预期的对象类型信息）
     * @param message 关联的消息（非空，可用于获取额外上下文信息）
     * @param contentType 媒体类型（非空，通常为{@link MediaType#APPLICATION_OCTET_STREAM}）
     * @return 反序列化后的目标类型对象（类型与targetDescriptor指定的类型一致）
     * @throws IOException 反序列化过程中发生I/O错误（如流读取异常、格式不兼容）
     */
    @Override
    protected Object parseObject(byte[] body, @NonNull TargetDescriptor targetDescriptor, @NonNull Message message,
            MimeType contentType) throws IOException {
        return serializer.deserialize(body, targetDescriptor.getRequiredTypeDescriptor());
    }

    /**
     * 将对象序列化为二进制字节数组
     * 
     * <p>通过{@link Serializer#serialize(Object, TypeDescriptor)}将对象转换为字节数组，
     * 传入对象的返回类型描述符（{@link TypedData#getReturnTypeDescriptor()}）以确保序列化过程
     * 保留准确的类型信息（如对接口类型的对象，序列化其实际实现类的信息）。
     * 
     * @param body 待序列化的对象数据（非空，{@link TypedData}包装的任意对象）
     * @param message 关联的消息（非空，可用于设置额外上下文信息）
     * @param mediaType 媒体类型（非空，通常为{@link MediaType#APPLICATION_OCTET_STREAM}）
     * @return 序列化后的字节数组（非空，可用于后续反序列化为原对象）
     * @throws IOException 序列化过程中发生I/O错误（如流写入异常、对象不可序列化）
     */
    @Override
    protected byte[] toBinary(@NonNull TypedData<Object> body, @NonNull Message message, MediaType mediaType)
            throws IOException {
        return serializer.serialize(body.get(), body.getReturnTypeDescriptor());
    }

}