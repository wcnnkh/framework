package run.soeasy.framework.messaging.convert.support;

import java.io.IOException;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import run.soeasy.framework.core.convert.ConversionService;
import run.soeasy.framework.core.convert.support.SystemConversionService;
import run.soeasy.framework.core.convert.value.SourceDescriptor;
import run.soeasy.framework.core.convert.value.TargetDescriptor;
import run.soeasy.framework.core.convert.value.TypedValue;
import run.soeasy.framework.core.mapping.property.PropertyAccessor;
import run.soeasy.framework.core.mapping.property.PropertyDescriptor;
import run.soeasy.framework.io.MimeType;
import run.soeasy.framework.messaging.InputMessage;
import run.soeasy.framework.messaging.MediaType;
import run.soeasy.framework.messaging.Message;
import run.soeasy.framework.messaging.OutputMessage;

/**
 * 基于属性的消息转换器抽象基类，继承自{@link AbstractMessageConverter}，
 * 专注于处理与属性描述符（{@link PropertyDescriptor}）和属性访问器（{@link PropertyAccessor}）相关的消息转换，
 * 适用于需要从消息中提取属性值或向消息写入属性值的场景（如参数绑定、表单数据处理）。
 * 
 * <p>核心特性：
 * - 集成{@link ConversionService}用于属性值的类型转换，默认使用系统级转换服务；
 * - 针对{@link PropertyDescriptor}和{@link PropertyAccessor}提供专用的读写方法；
 * - 重写通用读写逻辑，仅处理属性相关的描述符，其他类型描述符默认不支持。
 * 
 * @author soeasy.run
 * @see AbstractMessageConverter
 * @see PropertyDescriptor
 * @see PropertyAccessor
 * @see ConversionService
 */
@Getter
@Setter
public abstract class AbstractPropertyMessageConverter extends AbstractMessageConverter {

    /**
     * 属性值类型转换服务，用于在属性值与目标类型之间进行转换（非空），
     * 默认使用{@link SystemConversionService#getInstance()}，可通过setter方法替换为自定义实现。
     */
    @NonNull
    private ConversionService conversionService = SystemConversionService.getInstance();

    /**
     * 子类实现此方法，从输入消息中读取并转换指定属性描述符对应的属性值
     * 
     * <p>该方法专注于处理{@link PropertyDescriptor}，通常用于从消息的特定位置（如消息头、表单参数、路径变量）
     * 提取属性值，并转换为描述符指定的类型（如将字符串参数转换为整数）。
     * 
     * @param parameterDescriptor 属性描述符（包含属性名称、类型等信息，非空）
     * @param message 待读取的输入消息（非空，包含属性值的来源）
     * @return 提取并转换后的属性值（符合属性描述符的类型要求）
     * @throws IOException 读取或转换过程中发生I/O错误（如参数解析失败）
     */
    protected abstract Object doRead(@NonNull PropertyDescriptor parameterDescriptor, @NonNull InputMessage message)
            throws IOException;

    /**
     * 重写通用消息读取方法，仅处理{@link PropertyDescriptor}类型的目标描述符
     * 
     * <p>逻辑：
     * - 若目标描述符是{@link PropertyDescriptor}，调用{@link #doRead(PropertyDescriptor, InputMessage)}处理；
     * - 若目标描述符标记为"必需"（{@link TargetDescriptor#isRequired()}）但不支持，抛出{@link UnsupportedOperationException}；
     * - 其他情况返回null（非必需描述符）。
     * 
     * @param targetDescriptor 目标类型描述符（可能为{@link PropertyDescriptor}，非空）
     * @param message 待读取的输入消息（非空）
     * @param contentType 实际使用的媒体类型（非空）
     * @return 转换后的属性值（仅当描述符为{@link PropertyDescriptor}时有效）
     * @throws IOException 读取或转换过程中发生I/O错误
     * @throws UnsupportedOperationException 当必需的描述符不被支持时
     */
    @Override
    protected Object doRead(@NonNull TargetDescriptor targetDescriptor, @NonNull InputMessage message,
            MimeType contentType) throws IOException {
        if (targetDescriptor instanceof PropertyDescriptor) {
            return doRead((PropertyDescriptor) targetDescriptor, message);
        }
        // 对于必需但不支持的描述符，抛出不支持异常
        if (targetDescriptor.isRequired()) {
            throw new UnsupportedOperationException("Unsupported required descriptor: " + targetDescriptor);
        }
        return null;
    }

    /**
     * 子类实现此方法，将属性访问器中的属性值写入输出消息
     * 
     * <p>该方法专注于处理{@link PropertyAccessor}，通常用于将属性值写入消息的特定位置（如构建表单数据、设置消息头），
     * 支持批量写入多个属性（通过访问器遍历属性）。
     * 
     * @param parameter 属性访问器（包含多个属性的名称和值，非空）
     * @param message 目标输出消息（非空，属性值的写入目标）
     * @throws IOException 写入过程中发生I/O错误（如参数序列化失败）
     */
    protected abstract void doWrite(@NonNull PropertyAccessor parameter, @NonNull OutputMessage message)
            throws IOException;

    /**
     * 重写通用消息写入方法，仅处理{@link PropertyAccessor}类型的源数据
     * 
     * <p>若源数据是{@link PropertyAccessor}，调用{@link #doWrite(PropertyAccessor, OutputMessage)}处理；
     * 其他类型源数据默认不处理（无操作）。
     * 
     * @param source 待写入的源数据（可能为{@link PropertyAccessor}，非空）
     * @param message 目标输出消息（非空）
     * @param contentType 实际使用的媒体类型（非空）
     * @throws IOException 写入过程中发生I/O错误
     */
    @Override
    protected void doWrite(@NonNull TypedValue source, @NonNull OutputMessage message, @NonNull MediaType contentType)
            throws IOException {
        if (source instanceof PropertyAccessor) {
            doWrite((PropertyAccessor) source, message);
        }
    }

    /**
     * 子类实现此方法，判断是否支持读取指定属性描述符对应的属性值
     * 
     * <p>支持性判断通常基于属性名称、消息类型、媒体类型等（如判断消息是否包含该属性的来源）。
     * 
     * @param parameterDescriptor 属性描述符（非空）
     * @param message 关联的消息（非空）
     * @return 支持读取返回true，否则返回false
     */
    protected abstract boolean isReadable(@NonNull PropertyDescriptor parameterDescriptor, @NonNull Message message);

    /**
     * 重写通用可读性判断方法，仅处理{@link PropertyDescriptor}类型的目标描述符
     * 
     * <p>逻辑：
     * - 若目标描述符是{@link PropertyDescriptor}，结合子类的{@link #isReadable(PropertyDescriptor, Message)}和
     *   父类的媒体类型匹配结果（{@link AbstractMessageConverter#isReadable}）判断；
     * - 其他类型描述符默认返回false（不支持）。
     * 
     * @param targetDescriptor 目标类型描述符（非空）
     * @param message 关联的消息（非空）
     * @param contentType 目标媒体类型（可为null）
     * @return 支持读取返回true，否则返回false
     */
    @Override
    public boolean isReadable(@NonNull TargetDescriptor targetDescriptor, @NonNull Message message,
            MimeType contentType) {
        if (targetDescriptor instanceof PropertyDescriptor) {
            return isReadable((PropertyDescriptor) targetDescriptor, message)
                    && super.isReadable(targetDescriptor, message, contentType);
        }
        return false;
    }

    /**
     * 子类实现此方法，判断是否支持写入指定属性描述符对应的属性值
     * 
     * <p>支持性判断通常基于属性类型、消息可写性、媒体类型等（如判断消息是否允许设置该属性）。
     * 
     * @param parameterDescriptor 属性描述符（非空）
     * @param message 关联的消息（非空）
     * @return 支持写入返回true，否则返回false
     */
    protected abstract boolean isWriteable(@NonNull PropertyDescriptor parameterDescriptor, @NonNull Message message);

    /**
     * 重写通用可写性判断方法，仅处理{@link PropertyDescriptor}类型的源描述符
     * 
     * <p>逻辑：
     * - 若源描述符是{@link PropertyDescriptor}，结合子类的{@link #isWriteable(PropertyDescriptor, Message)}和
     *   父类的媒体类型匹配结果（{@link AbstractMessageConverter#isWriteable}）判断；
     * - 其他类型描述符默认返回false（不支持）。
     * 
     * @param sourceDescriptor 源类型描述符（非空）
     * @param message 关联的消息（非空）
     * @param contentType 目标媒体类型（可为null）
     * @return 支持写入返回true，否则返回false
     */
    @Override
    public boolean isWriteable(@NonNull SourceDescriptor sourceDescriptor, @NonNull Message message,
            MimeType contentType) {
        if (sourceDescriptor instanceof PropertyDescriptor) {
            return isWriteable((PropertyDescriptor) sourceDescriptor, message)
                    && super.isWriteable(sourceDescriptor, message, contentType);
        }
        return false;
    }
}