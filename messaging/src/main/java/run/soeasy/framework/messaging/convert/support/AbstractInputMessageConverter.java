package run.soeasy.framework.messaging.convert.support;

import java.io.IOException;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.convert.Converter;
import run.soeasy.framework.core.convert.value.SourceDescriptor;
import run.soeasy.framework.core.convert.value.TargetDescriptor;
import run.soeasy.framework.core.convert.value.TypedValue;
import run.soeasy.framework.io.InputSource;
import run.soeasy.framework.io.MimeType;
import run.soeasy.framework.messaging.InputMessage;
import run.soeasy.framework.messaging.MediaType;
import run.soeasy.framework.messaging.Message;
import run.soeasy.framework.messaging.OutputMessage;

/**
 * 输入消息转换器抽象基类，继承自{@link AbstractMessageConverter}，
 * 专门用于处理{@link InputMessage}及其子类的转换，提供输入消息的读写逻辑骨架，
 * 适用于需要在不同输入消息类型间转换的场景（如包装、复制输入消息）。
 * 
 * <p>核心特性：
 * - 限定处理{@link InputMessage}类型，通过{@link #inputMessageClass}指定目标输入消息类型；
 * - 提供输入消息的写入逻辑：复制头信息并传输消息体内容；
 * - 定义输入消息的读取抽象方法，由子类实现具体转换逻辑。
 * 
 * @param <T> 支持的输入消息类型（需实现{@link InputMessage}）
 * @author soeasy.run
 * @see InputMessage
 * @see AbstractMessageConverter
 */
@RequiredArgsConstructor
@Getter
public abstract class AbstractInputMessageConverter<T extends InputMessage> extends AbstractMessageConverter {

    /**
     * 目标输入消息类型（非空），用于类型匹配和校验，确保转换操作仅适用于该类型及其子类
     */
    @NonNull
    private final Class<? extends T> inputMessageClass;

    /**
     * 判断是否支持将消息读取为目标输入消息类型
     * 
     * <p>支持性条件：
     * 1. 目标类型（{@link TargetDescriptor}）是{@link #inputMessageClass}的子类或相同类型；
     * 2. 父类{@link AbstractMessageConverter#isReadable}判断为支持（媒体类型匹配）。
     * 
     * @param targetDescriptor 目标类型描述符（包含待转换的输入消息类型信息，非空）
     * @param message 待读取的消息（非空）
     * @param contentType 目标媒体类型（可为null）
     * @return 支持读取返回true，否则返回false
     */
    @Override
    public boolean isReadable(@NonNull TargetDescriptor targetDescriptor, @NonNull Message message,
            MimeType contentType) {
        // 校验目标类型是否为当前输入消息类型的兼容类型
        return targetDescriptor.getRequiredTypeDescriptor().getType().isAssignableFrom(inputMessageClass)
                && super.isReadable(targetDescriptor, message, contentType);
    }

    /**
     * 判断是否支持将输入消息写入目标消息
     * 
     * <p>支持性条件：
     * 1. 源类型（{@link SourceDescriptor}）是{@link InputMessage}的子类；
     * 2. 消息头（{@link Message#getHeaders()}）非只读（允许写入头信息）；
     * 3. 父类{@link AbstractMessageConverter#isWriteable}判断为支持（媒体类型匹配）。
     * 
     * @param sourceDescriptor 源类型描述符（包含待转换的输入消息类型信息，非空）
     * @param message 目标消息（非空）
     * @param contentType 目标媒体类型（可为null）
     * @return 支持写入返回true，否则返回false
     */
    @Override
    public boolean isWriteable(@NonNull SourceDescriptor sourceDescriptor, @NonNull Message message,
            MimeType contentType) {
        // 校验源类型是否为InputMessage的子类，且消息头可写
        return InputMessage.class.isAssignableFrom(sourceDescriptor.getReturnTypeDescriptor().getType())
                && !message.getHeaders().isReadyOnly() 
                && super.isWriteable(sourceDescriptor, message, contentType);
    }

    /**
     * 将输入消息写入输出消息
     * 
     * <p>步骤：
     * 1. 将源数据转换为{@link InputMessage}类型（通过类型转换器确保兼容性）；
     * 2. 复制输入消息的头信息到输出消息（通过{@link AbstractMessageConverter#writeHeader(Message, OutputMessage)}）；
     * 3. 调用{@link InputMessage#transferTo(run.soeasy.framework.io.OutputStreamFactory)}传输消息体内容（如输入流到输出流）。
     * 
     * @param source 待写入的输入消息数据（非空，类型为{@link InputMessage}）
     * @param message 目标输出消息（非空）
     * @param contentType 实际使用的媒体类型（非空）
     * @throws IOException 转换或传输过程中发生I/O错误（如流操作失败）
     */
    @Override
    protected void doWrite(@NonNull TypedValue source, @NonNull OutputMessage message, @NonNull MediaType contentType)
            throws IOException {
        // 将源数据转换为InputMessage类型（确保类型兼容）
        InputMessage input = source.map(InputMessage.class, Converter.assignable()).get();
        // 复制头信息到输出消息
        writeHeader(input, message);
        // 传输消息体内容（委托给InputMessage的transferTo方法）
        input.transferTo(message);
    }

    /**
     * 读取消息内容并转换为目标输入消息类型
     * 
     * <p>委托给抽象方法{@link #readToInputMessage}实现具体转换，将输入消息转换为{@link #inputMessageClass}类型的实例。
     * 
     * @param targetDescriptor 目标类型描述符（非空）
     * @param message 待读取的输入消息（非空）
     * @param contentType 实际使用的媒体类型（非空）
     * @return 转换后的输入消息对象（{@link #inputMessageClass}类型）
     * @throws IOException 读取或转换过程中发生I/O错误
     */
    @Override
    protected Object doRead(@NonNull TargetDescriptor targetDescriptor, @NonNull InputMessage message,
            MimeType contentType) throws IOException {
        return readToInputMessage(message, message, targetDescriptor);
    }

    /**
     * 将消息和输入源转换为具体的输入消息对象（子类实现）
     * 
     * <p>子类需实现此方法，完成原始消息到{@link #inputMessageClass}类型实例的转换，
     * 通常包括输入消息对象的创建、头信息复制和输入源（如输入流）的关联。
     * 
     * @param message 关联的原始消息（非空，可用于获取头信息）
     * @param source 输入源（非空，包含消息体内容，如输入流）
     * @param targetDescriptor 目标类型描述符（非空，包含待转换的类型信息）
     * @return 转换后的输入消息对象（{@link #inputMessageClass}类型，非空）
     * @throws IOException 转换过程中发生I/O错误（如输入源读取失败）
     */
    protected abstract T readToInputMessage(@NonNull Message message, InputSource source,
            @NonNull TargetDescriptor targetDescriptor) throws IOException;

}