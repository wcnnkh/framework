package run.soeasy.framework.messaging.convert.support;

import java.io.IOException;

import lombok.NonNull;
import run.soeasy.framework.core.convert.Converter;
import run.soeasy.framework.core.convert.value.TargetDescriptor;
import run.soeasy.framework.core.convert.value.TypedData;
import run.soeasy.framework.core.convert.value.TypedValue;
import run.soeasy.framework.io.MimeType;
import run.soeasy.framework.messaging.InputMessage;
import run.soeasy.framework.messaging.MediaType;
import run.soeasy.framework.messaging.OutputMessage;

/**
 * 特定类型对象的消息转换器基类，继承自{@link AbstractMessageConverter}，
 * 专注于处理指定类型{@code T}的对象与消息之间的转换，提供类型安全的读写操作骨架，
 * 简化特定类型转换器的实现（如String、byte[]、JavaBean等类型的专用转换器）。
 * 
 * <p>核心特性：
 * - 通过构造函数指定目标类型{@code T}（{@link #requriedType}），确保转换的对象类型一致；
 * - 抽象出{@link #readObject(TargetDescriptor, InputMessage, MimeType)}和{@link #writeObject(TypedData, OutputMessage, MediaType)}方法，
 *   由子类实现具体类型的读写逻辑；
 * - 在写入时自动校验源对象是否为{@code T}类型或其子类型，确保类型安全。
 * 
 * @param <T> 转换器支持的目标对象类型（如String、User、byte[]等）
 * @author soeasy.run
 * @see AbstractMessageConverter
 * @see TypedData
 */
public abstract class ObjectMessageConverter<T> extends AbstractMessageConverter {

    /**
     * 当前转换器支持的目标对象类型（非空），用于限制转换的对象类型，确保类型安全
     */
    private final Class<T> requriedType;

    /**
     * 初始化特定类型对象的消息转换器，指定支持的目标类型
     * 
     * @param requriedType 目标对象类型（非空，如String.class、User.class）
     */
    public ObjectMessageConverter(@NonNull Class<T> requriedType) {
        this.requriedType = requriedType;
    }

    /**
     * 重写父类的消息读取方法，委托给{@link #readObject}实现特定类型的读取逻辑
     * 
     * <p>该方法将父类的通用读取逻辑聚焦到类型{@code T}的读取，确保返回值为{@code T}类型或其子类型。
     * 
     * @param targetDescriptor 目标类型描述符（包含待转换的类型信息，非空）
     * @param message 待读取的输入消息（非空）
     * @param contentType 实际使用的媒体类型（非空）
     * @return 转换后的{@code T}类型对象（由{@link #readObject}返回）
     * @throws IOException 读取或转换过程中发生I/O错误（由{@link #readObject}抛出）
     */
    @Override
    protected Object doRead(@NonNull TargetDescriptor targetDescriptor, @NonNull InputMessage message,
            MimeType contentType) throws IOException {
        return readObject(targetDescriptor, message, contentType);
    }

    /**
     * 子类实现此方法，完成特定类型{@code T}的消息读取和转换逻辑
     * 
     * <p>该方法需要将输入消息的内容（如输入流中的字节）转换为{@code T}类型的对象，
     * 具体转换逻辑（如反序列化、解析）由子类实现，确保返回值符合{@code T}类型。
     * 
     * @param targetDescriptor 目标类型描述符（包含待转换的类型信息，非空）
     * @param message 待读取的输入消息（非空，包含消息内容）
     * @param contentType 实际使用的媒体类型（非空）
     * @return 转换后的{@code T}类型对象（非空）
     * @throws IOException 读取输入流或转换过程中发生I/O错误（如格式错误、类型不匹配）
     */
    protected abstract T readObject(@NonNull TargetDescriptor targetDescriptor, @NonNull InputMessage message,
            MimeType contentType) throws IOException;

    /**
     * 重写父类的消息写入方法，先校验源对象类型，再委托给{@link #writeObject}实现特定类型的写入逻辑
     * 
     * <p>步骤：
     * 1. 通过{@link TypedValue#map(Class, Converter)}将源数据转换为{@code T}类型的{@link TypedData}，
     *    使用{@link Converter#assignable()}确保源对象是{@code requriedType}的实例或子类；
     * 2. 调用{@link #writeObject}将{@code T}类型对象写入输出消息。
     * 
     * @param source 待写入的源数据（非空，包含任意类型的对象）
     * @param message 目标输出消息（非空，包含输出流）
     * @param contentType 实际使用的媒体类型（非空）
     * @throws IOException 写入输出流或转换过程中发生I/O错误（由{@link #writeObject}抛出）
     * @throws IllegalArgumentException 若源对象类型与{@code requriedType}不兼容（由类型映射失败导致）
     */
    @Override
    protected void doWrite(@NonNull TypedValue source, @NonNull OutputMessage message, @NonNull MediaType contentType)
            throws IOException {
        // 将源数据映射为T类型的TypedData，确保类型兼容
        TypedData<T> data = source.map(requriedType, Converter.assignable());
        writeObject(data, message, contentType);
    }

    /**
     * 子类实现此方法，完成特定类型{@code T}的消息写入和转换逻辑
     * 
     * <p>该方法需要将{@code T}类型的对象转换为指定媒体类型的内容（如序列化为JSON字节流），
     * 并写入输出消息的输出流，具体序列化逻辑由子类实现。
     * 
     * @param data 待写入的{@code T}类型数据（非空，包含值和类型信息）
     * @param message 目标输出消息（非空，包含输出流）
     * @param contentType 实际使用的媒体类型（非空）
     * @throws IOException 写入输出流或转换过程中发生I/O错误（如流写入失败、序列化失败）
     */
    protected abstract void writeObject(@NonNull TypedData<T> data, @NonNull OutputMessage message,
            @NonNull MediaType contentType) throws IOException;
}