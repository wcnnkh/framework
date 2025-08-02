package run.soeasy.framework.messaging.convert;

import java.io.IOException;

import lombok.NonNull;
import run.soeasy.framework.core.convert.value.TargetDescriptor;
import run.soeasy.framework.io.MimeType;
import run.soeasy.framework.messaging.InputMessage;
import run.soeasy.framework.messaging.Message;

/**
 * 消息读取器接口，定义了从消息中读取并转换数据的规范，
 * 支持将{@link InputMessage}中的内容转换为指定类型的对象，
 * 是消息内容解析和类型转换的核心组件。
 * 
 * <p>主要用途：
 * - 判断是否支持将指定消息转换为目标类型（{@link #isReadable(TargetDescriptor, Message, MimeType)}）；
 * - 执行转换操作，将消息内容读取为目标类型的对象（{@link #readFrom(TargetDescriptor, InputMessage, MimeType)}）。
 * 
 * @author soeasy.run
 * @see InputMessage
 * @see TargetDescriptor
 * @see MimeType
 */
public interface MessageReader {

    /**
     * 判断当前读取器是否支持将指定消息转换为目标类型
     * 
     * <p>支持性判断通常基于目标类型（{@link TargetDescriptor}）和消息的媒体类型（{@link MimeType}），
     * 例如JSON读取器仅支持将"application/json"类型的消息转换为Java对象。
     * 
     * @param targetDescriptor 目标类型描述符（包含待转换的类型信息，非空）
     * @param message 待读取的消息（非空）
     * @param contentType 目标媒体类型（可为null，表示不限制媒体类型）
     * @return 支持转换返回true，否则返回false
     */
    boolean isReadable(@NonNull TargetDescriptor targetDescriptor, @NonNull Message message, MimeType contentType);

    /**
     * 将输入消息的内容读取并转换为目标类型的对象
     * 
     * <p>该方法会根据目标类型和媒体类型，解析{@link InputMessage}中的内容（如从输入流读取字节并反序列化），
     * 最终返回目标类型的实例。
     * 
     * @param targetDescriptor 目标类型描述符（包含待转换的类型信息，非空）
     * @param message 待读取的输入消息（非空）
     * @param contentType 目标媒体类型（可为null，表示使用消息自带的媒体类型）
     * @return 转换后的目标类型对象（非null）
     * @throws IOException 若读取消息内容或转换过程中发生I/O错误（如流读取失败、解析异常）
     * @throws IllegalArgumentException 若不支持目标类型或媒体类型（通常应先调用{@link #isReadable}检查）
     */
    Object readFrom(@NonNull TargetDescriptor targetDescriptor, @NonNull InputMessage message, MimeType contentType)
            throws IOException;
}