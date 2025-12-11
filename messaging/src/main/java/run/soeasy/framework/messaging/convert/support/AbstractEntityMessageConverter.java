package run.soeasy.framework.messaging.convert.support;

import java.io.IOException;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.convert.value.SourceDescriptor;
import run.soeasy.framework.core.convert.value.TargetDescriptor;
import run.soeasy.framework.core.convert.value.TypedData;
import run.soeasy.framework.core.convert.value.TypedValue;
import run.soeasy.framework.io.MimeType;
import run.soeasy.framework.messaging.Entity;
import run.soeasy.framework.messaging.InputMessage;
import run.soeasy.framework.messaging.MediaType;
import run.soeasy.framework.messaging.Message;
import run.soeasy.framework.messaging.OutputMessage;
import run.soeasy.framework.messaging.convert.MessageConverter;

/**
 * 实体消息转换器抽象基类，继承自{@link AbstractNestedMessageConverter}，
 * 专门用于处理{@link Entity}类型消息的转换，支持将消息内容转换为实体对象或反之，
 * 适用于包含业务实体数据的消息场景（如带实体内容的HTTP请求/响应）。
 * 
 * <p>核心特性：
 * - 限定处理{@link Entity}及其子类的转换，通过{@link #entityClass}指定目标实体类型；
 * - 利用嵌套的{@link MessageConverter}处理实体内部数据（{@link Entity#getBody()}）的转换；
 * - 自动处理实体消息头的复制（写入时）和类型校验（读写时）。
 * 
 * @param <T> 支持的实体类型（需实现{@link Entity}）
 * @author soeasy.run
 * @see Entity
 * @see AbstractNestedMessageConverter
 */
@RequiredArgsConstructor
@Getter
public abstract class AbstractEntityMessageConverter<T extends Entity<?>> extends AbstractNestedMessageConverter {

    /**
     * 目标实体类型（非空），用于类型匹配和校验，确保转换操作仅适用于该类型及其子类
     */
    @NonNull
    private final Class<? extends T> entityClass;

    /**
     * 判断是否支持将消息读取为目标实体类型
     * 
     * <p>支持性条件：
     * 1. 目标类型（{@link TargetDescriptor}）是{@link #entityClass}的子类或相同类型；
     * 2. 父类{@link AbstractMessageConverter#isReadable}判断为支持（媒体类型匹配）。
     * 
     * @param targetDescriptor 目标类型描述符（包含待转换的实体类型信息，非空）
     * @param message 待读取的消息（非空）
     * @param contentType 目标媒体类型（可为null）
     * @return 支持读取返回true，否则返回false
     */
    @Override
    public boolean isReadable(@NonNull TargetDescriptor targetDescriptor, @NonNull Message message,
            MimeType contentType) {
        // 校验目标类型是否为当前实体类型的兼容类型
        return targetDescriptor.getRequiredTypeDescriptor().getType().isAssignableFrom(entityClass)
                && super.isReadable(targetDescriptor, message, contentType);
    }

    /**
     * 判断是否支持将实体对象写入消息
     * 
     * <p>支持性条件：
     * 1. 源类型（{@link SourceDescriptor}）是{@link Entity}的子类；
     * 2. 消息头（{@link Message#getHeaders()}）非只读（允许写入头信息）；
     * 3. 父类{@link AbstractMessageConverter#isWriteable}判断为支持（媒体类型匹配）。
     * 
     * @param sourceDescriptor 源类型描述符（包含待转换的实体类型信息，非空）
     * @param message 目标消息（非空）
     * @param contentType 目标媒体类型（可为null）
     * @return 支持写入返回true，否则返回false
     */
    @Override
    public boolean isWriteable(@NonNull SourceDescriptor sourceDescriptor, @NonNull Message message,
            MimeType contentType) {
        // 校验源类型是否为Entity的子类，且消息头可写
        return Entity.class.isAssignableFrom(sourceDescriptor.getReturnTypeDescriptor().getType())
                && super.isWriteable(sourceDescriptor, message, contentType);
    }

    /**
     * 将消息内容读取并转换为目标实体类型对象
     * 
     * <p>步骤：
     * 1. 利用嵌套的消息转换器（{@link #getMessageConverter()}）读取消息内容为原始数据；
     * 2. 解析实体中body的实际类型（通过{@link TypeDescriptor}获取Entity的泛型参数）；
     * 3. 调用{@link #readToEntity(TypedValue, InputMessage)}将原始数据转换为具体实体对象。
     * 
     * @param targetDescriptor 目标类型描述符（非空）
     * @param message 待读取的输入消息（非空）
     * @param contentType 实际使用的媒体类型（非空）
     * @return 转换后的实体对象（{@link #entityClass}类型）
     * @throws IOException 读取或转换过程中发生I/O错误
     */
    @Override
    protected Object doRead(@NonNull TargetDescriptor targetDescriptor, @NonNull InputMessage message,
            MimeType contentType) throws IOException {
        // 用嵌套转换器读取原始数据
        Object value = getMessageConverter().readFrom(
                () -> targetDescriptor.getRequiredTypeDescriptor(), message, contentType);
        
        // 获取实体中body的实际类型（Entity<T>的泛型参数T）
        TypeDescriptor typeDescriptor = targetDescriptor.getRequiredTypeDescriptor().upcast(Entity.class);
        typeDescriptor = typeDescriptor.map((e) -> e.getActualTypeArgument(0));
        
        // 转换为实体对象
        return readToEntity(TypedValue.of(value, typeDescriptor), message);
    }

    /**
     * 将原始数据转换为具体的实体对象（子类实现）
     * 
     * <p>子类需实现此方法，完成原始数据到{@link #entityClass}类型实体的转换，
     * 通常包括实体对象的创建和body的设置（{@link Entity#getBody()}）。
     * 
     * @param body 原始数据（包含值和类型信息，非空）
     * @param message 关联的输入消息（非空，可用于获取头信息）
     * @return 转换后的实体对象（{@link #entityClass}类型，非空）
     */
    protected abstract T readToEntity(@NonNull TypedValue body, @NonNull InputMessage message);

    /**
     * 将实体对象写入输出消息
     * 
     * <p>步骤：
     * 1. 复制实体的头信息到输出消息（通过{@link AbstractMessageConverter#writeHeader(Message, OutputMessage)}）；
     * 2. 若实体包含body（{@link Entity#getBody()}），利用嵌套转换器将body写入消息体。
     * 
     * @param source 待写入的实体数据（非空，类型为{@link Entity}）
     * @param message 目标输出消息（非空）
     * @param contentType 实际使用的媒体类型（非空）
     * @throws IOException 写入或转换过程中发生I/O错误
     */
    @Override
    protected void doWrite(@NonNull TypedValue source, @NonNull OutputMessage message, @NonNull MediaType contentType)
            throws IOException {
        Entity<?> entity = (Entity<?>) source.get();
        
        // 复制实体的头信息到输出消息
        writeHeader(entity, message);
        
        // 写入实体的body内容
        TypedData<?> entityBody = entity.getBody();
        if (entityBody != null) {
            getMessageConverter().writeTo(entityBody.value(), message, contentType);
        }
    }
}