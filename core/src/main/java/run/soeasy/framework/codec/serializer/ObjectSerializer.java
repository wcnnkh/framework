package run.soeasy.framework.codec.serializer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import run.soeasy.framework.codec.io.DirectSerializer;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.io.IOUtils;

/**
 * 类型化序列化器接口，继承自{@link SerializationConverter}和{@link ToBinaryCodec}，
 * 提供类型感知的对象序列化与反序列化能力，支持对象与字节流/字节数组的双向转换，
 * 并通过{@link TypeDescriptor}处理泛型类型信息，是框架中序列化组件的核心抽象。
 * 
 * <p>
 * 核心特性： -
 * 整合序列化与二进制编解码能力：同时实现{@link SerializationConverter}（框架标准序列化）和{@link ToBinaryCodec}（二进制编解码），
 * 统一对象与字节流的转换接口； - 类型安全支持：通过{@link TypeDescriptor}传递类型信息，解决泛型在序列化过程中的类型擦除问题； -
 * 灵活的IO操作：支持输出流、输入流、字节数组等多种数据载体，适配不同场景（如网络传输、文件存储）。
 * 
 * @author soeasy.run
 * @see SerializationConverter
 * @see ToBinaryCodec
 * @see TypeDescriptor
 */
public interface ObjectSerializer extends SerializationConverter, DirectSerializer<Object> {
	/**
	 * 将对象序列化为输出流（基础序列化操作）
	 * 
	 * <p>
	 * 此方法为核心序列化逻辑的入口，实现类需在此方法中完成对象到字节流的转换， 无需关注类型描述符（由上层方法处理），适用于简单类型的序列化。
	 * 
	 * @param source 待序列化的对象（可为null，具体实现需支持null值处理）
	 * @param target 目标输出流（非空，序列化结果写入此流）
	 * @throws IOException 序列化过程中发生IO错误时抛出（如流关闭、写入失败）
	 */
	void serialize(Object source, OutputStream target) throws IOException;

	/**
	 * 实现{@link SerializationConverter}接口的序列化方法，忽略源对象的类型描述符
	 * 
	 * <p>
	 * 默认实现直接调用{@link #serialize(Object, OutputStream)}，适用于无需关注源对象类型的场景，
	 * 若需要基于源类型进行特殊处理，实现类可重写此方法。
	 * 
	 * @param source               待序列化的对象
	 * @param sourceTypeDescriptor 源对象的类型描述符（未使用，仅为接口兼容）
	 * @param target               目标输出流
	 * @throws IOException 序列化过程中发生IO错误时抛出
	 */
	@Override
	default void serialize(Object source, TypeDescriptor sourceTypeDescriptor, OutputStream target) throws IOException {
		serialize(source, target);
	}

	/**
	 * 实现{@link SerializationConverter}接口的反序列化方法，基于目标类型描述符还原对象
	 * 
	 * <p>
	 * 默认实现调用{@link #deserialize(InputStream)}，并将可能的{@link ClassNotFoundException}包装为{@link SerializerException}，
	 * 确保异常类型符合{@link SerializationConverter}接口的定义。
	 * 
	 * @param source               源输入流（非空，包含序列化后的字节数据）
	 * @param targetTypeDescriptor 目标对象的类型描述符（非空，用于指导反序列化的类型还原，尤其是泛型类型）
	 * @return 反序列化得到的对象（类型与targetTypeDescriptor匹配）
	 * @throws IOException         读取输入流时发生IO错误时抛出
	 * @throws SerializerException 反序列化失败时抛出（如格式错误、类未找到）
	 */
	@Override
	default Object deserialize(InputStream source, TypeDescriptor targetTypeDescriptor)
			throws IOException, SerializerException {
		try {
			return deserialize(source);
		} catch (ClassNotFoundException e) {
			// 将类未找到异常转换为序列化异常（符合接口规范）
			throw new SerializerException("Failed to deserialize: class not found", e);
		}
	}

	/**
	 * 从输入流反序列化对象（使用默认缓冲区大小）
	 * 
	 * <p>
	 * 默认实现调用{@link #deserialize(InputStream, int)}，使用{@link IOUtils#DEFAULT_BYTE_BUFFER_SIZE}作为缓冲区大小，
	 * 适用于无需自定义缓冲区大小的通用场景。
	 * 
	 * @param input 源输入流（非空）
	 * @return 反序列化得到的对象
	 * @throws IOException            读取输入流时发生IO错误时抛出
	 * @throws ClassNotFoundException 反序列化需要的类未找到时抛出
	 */
	Object deserialize(InputStream input) throws IOException, ClassNotFoundException;

	@Override
	default Object clone(Object source) {
		return DirectSerializer.super.clone(source);
	}
}