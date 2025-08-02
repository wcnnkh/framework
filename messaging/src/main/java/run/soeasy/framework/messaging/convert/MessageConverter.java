package run.soeasy.framework.messaging.convert;

import lombok.NonNull;
import run.soeasy.framework.core.convert.value.AccessibleDescriptor;
import run.soeasy.framework.messaging.MediaTypes;
import run.soeasy.framework.messaging.Message;

/**
 * 消息转换器接口，继承{@link MessageReader}和{@link MessageWriter}，
 * 提供消息的双向转换能力（读取和写入），并定义了支持的媒体类型管理规范，
 * 是消息序列化与反序列化的统一接口。
 * 
 * <p>核心功能：
 * - 整合消息读取（{@link MessageReader}）和写入（{@link MessageWriter}）能力；
 * - 提供获取支持的媒体类型的方法（{@link #getSupportedMediaTypes()}）；
 * - 根据目标类型动态返回支持的媒体类型（{@link #getSupportedMediaTypes(AccessibleDescriptor, Message)}）。
 * 
 * @author soeasy.run
 * @see MessageReader
 * @see MessageWriter
 * @see MediaTypes
 */
public interface MessageConverter extends MessageReader, MessageWriter {

    /**
     * 获取当前转换器支持的所有媒体类型
     * 
     * <p>返回的{@link MediaTypes}包含该转换器能够处理的所有媒体类型（如"application/json"、"text/plain"等），
     * 用于快速判断转换器是否适用于特定媒体类型的消息。
     * 
     * @return 支持的媒体类型集合（非空，可能为空集合）
     */
    MediaTypes getSupportedMediaTypes();

    /**
     * 根据指定的类型描述符和消息，返回当前转换器支持的媒体类型子集
     * 
     * <p>该方法会判断转换器是否支持对指定类型（{@link AccessibleDescriptor}）进行读写操作，
     * 若支持则返回{@link #getSupportedMediaTypes()}，否则返回空集合（{@link MediaTypes#EMPTY}）。
     * 
     * @param requiredDescriptor 类型描述符（包含待转换的类型信息，非空）
     * @param message 关联的消息（非空）
     * @return 支持的媒体类型集合（非空，要么是{@link #getSupportedMediaTypes()}的结果，要么是{@link MediaTypes#EMPTY}）
     */
    default MediaTypes getSupportedMediaTypes(@NonNull AccessibleDescriptor requiredDescriptor, @NonNull Message message) {
        return (isReadable(requiredDescriptor, message, null) || isWriteable(requiredDescriptor, message, null))
                ? getSupportedMediaTypes()
                : MediaTypes.EMPTY;
    }
}