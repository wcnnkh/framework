package run.soeasy.framework.messaging.convert;

import java.io.IOException;

import lombok.NonNull;
import run.soeasy.framework.core.convert.value.SourceDescriptor;
import run.soeasy.framework.core.convert.value.TypedValue;
import run.soeasy.framework.io.MimeType;
import run.soeasy.framework.messaging.MediaType;
import run.soeasy.framework.messaging.Message;
import run.soeasy.framework.messaging.OutputMessage;

/**
 * 消息写入器接口，定义了将数据写入消息的规范，
 * 支持将指定类型的对象转换并写入到{@link OutputMessage}中，
 * 是数据序列化和消息内容生成的核心组件。
 * 
 * <p>主要用途：
 * - 判断是否支持将源对象写入消息（基于源类型和目标媒体类型）；
 * - 执行写入操作，将源对象转换为指定媒体类型的内容并写入消息。
 * 
 * @author soeasy.run
 * @see OutputMessage
 * @see TypedValue
 * @see MediaType
 */
public interface MessageWriter {

    /**
     * 判断当前写入器是否支持将源对象写入消息
     * 
     * <p>支持性判断通常基于源对象类型（{@link SourceDescriptor}）和目标媒体类型（{@link MimeType}），
     * 例如JSON写入器仅支持将Java对象转换为"application/json"类型的消息内容。
     * 
     * @param sourceDescriptor 源对象类型描述符（包含待转换的源类型信息，非空）
     * @param message 目标消息（非空）
     * @param contentType 目标媒体类型（可为null，表示不限制媒体类型）
     * @return 支持写入返回true，否则返回false
     */
    boolean isWriteable(@NonNull SourceDescriptor sourceDescriptor, @NonNull Message message, MimeType contentType);

    /**
     * 将源对象转换为指定媒体类型的内容并写入输出消息
     * 
     * <p>该方法会根据源对象类型和目标媒体类型，将{@link TypedValue}中的数据（如Java对象）序列化为对应格式（如JSON字符串），
     * 并写入到{@link OutputMessage}的输出流中，同时可能设置消息的Content-Type头字段。
     * 
     * @param source 待写入的源数据（包含值和类型信息，非空）
     * @param message 目标输出消息（非空）
     * @param contentType 目标媒体类型（可为null，表示使用默认媒体类型）
     * @throws IOException 若写入消息内容或转换过程中发生I/O错误（如流写入失败、序列化异常）
     * @throws IllegalArgumentException 若不支持源类型或媒体类型（通常应先调用{@link #isWriteable}检查）
     */
    void writeTo(@NonNull TypedValue source, @NonNull OutputMessage message, MediaType contentType) throws IOException;
}