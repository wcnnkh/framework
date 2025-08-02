package run.soeasy.framework.codec.binary;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import lombok.NonNull;
import run.soeasy.framework.codec.CodecException;
import run.soeasy.framework.codec.DecodeException;
import run.soeasy.framework.codec.Decoder;
import run.soeasy.framework.io.BufferConsumer;
import run.soeasy.framework.io.OutputSource;

/**
 * 字节数组解码器接口，继承自{@link Decoder}，用于将源数据解码为字节数组，
 * 提供流式解码、文件解码和消费者模式处理能力，适用于二进制数据解析场景。
 * 
 * <p>该接口采用函数式接口设计，仅需实现核心的消费者模式解码方法，
 * 其他解码形式通过默认方法提供，简化了不同场景下的解码实现。
 * 
 * <p><b>接口特性：</b>
 * <ul>
 * <li>多形式输出：支持字节数组直接返回、输出流写入、输出源操作和消费者处理</li>
 * <li>资源安全：自动管理输出流资源，确保解码后资源正确释放</li>
 * <li>异常统一：解码过程中的异常统一封装为{@link CodecException}及其子类</li>
 * <li>灵活扩展：通过泛型支持多种源数据类型，适应不同解码场景</li>
 * </ul>
 * 
 * <p><b>方法调用关系：</b>
 * <ul>
 * <li>{@link #decode(Object)} 依赖 {@link #decode(Object, OutputStream)}</li>
 * <li>{@link #decode(Object, OutputSource)} 依赖 {@link #decode(Object, OutputStream)}</li>
 * <li>{@link #decode(Object, OutputStream)} 依赖 {@link #decode(Object, BufferConsumer)}</li>
 * <li>{@link #decode(Object, BufferConsumer)} 是核心抽象方法，需要具体实现</li>
 * </ul>
 * 
 * @author soeasy.run
 * @param <E> 待解码的源数据类型，可根据实际需求指定为String、InputStream等
 * @see Decoder 基础解码接口定义
 * @see OutputSource 输出源接口，用于抽象文件等输出目标
 * @see BufferConsumer 字节数组消费者接口，支持函数式处理结果
 */
@FunctionalInterface
public interface ToBinaryDecoder<E> extends Decoder<E, byte[]> {

	/**
	 * 将源数据解码为字节数组（实现{@link Decoder}接口的抽象方法）
	 * 
	 * <p>此方法通过默认实现提供基础功能，内部流程为：
	 * 1. 创建{@link ByteArrayOutputStream}作为临时缓冲区
	 * 2. 调用{@link #decode(Object, OutputStream)}方法执行解码
	 * 3. 将缓冲区内容转换为字节数组并返回
	 * 
	 * @param source 待解码的源数据，不能为null
	 * @return 解码后的字节数组，不会返回null
	 * @throws DecodeException 当解码过程中发生IO异常时抛出
	 * @throws CodecException 解码逻辑失败时抛出的通用异常
	 */
	@Override
	default byte[] decode(E source) throws CodecException {
		ByteArrayOutputStream target = new ByteArrayOutputStream();
		try {
			decode(source, target::write);
		} catch (IOException e) {
			throw new DecodeException("Internal IO exception", e);
		}
		return target.toByteArray();
	}

	/**
	 * 将源数据解码并写入指定的输出源
	 * 
	 * <p>此方法通过默认实现提供输出源操作能力，内部流程为：
	 * 1. 从{@link OutputSource}获取输出流
	 * 2. 调用{@link #decode(Object, OutputStream)}执行解码写入
	 * 3. 在finally块中确保输出流被关闭
	 * 
	 * @param source 待解码的源数据，不能为null
	 * @param target 目标输出源，不能为null（由{@link NonNull}注解保证）
	 * @throws CodecException 解码逻辑失败时抛出
	 * @throws IOException 当输出源操作发生IO错误时抛出
	 */
	default void decode(E source, @NonNull OutputSource target) throws CodecException, IOException {
		OutputStream outputStream = target.getOutputStream();
		try {
			decode(source, outputStream);
		} finally {
			outputStream.close();
		}
	}

	/**
	 * 将源数据解码并写入输出流
	 * 
	 * <p>此方法通过默认实现提供流式处理能力，将输出流转换为{@link BufferConsumer}，
	 * 然后委托给{@link #decode(Object, BufferConsumer)}方法处理。
	 * 
	 * @param source 待解码的源数据，不能为null
	 * @param target 目标输出流，不能为null（由{@link NonNull}注解保证）
	 * @throws CodecException 解码逻辑失败时抛出
	 * @throws IOException 当输出流操作发生错误时抛出
	 */
	default void decode(E source, @NonNull OutputStream target) throws CodecException, IOException {
		decode(source, target::write);
	}

	/**
	 * 核心解码方法：将源数据解码后通过消费者处理字节数组
	 * 
	 * <p>此方法是接口的唯一抽象方法，需要实现类提供具体的解码逻辑，主要职责包括：
	 * 1. 对源数据进行解码处理，转换为字节数组
	 * 2. 通过{@link BufferConsumer}处理解码得到的字节数组
	 * 3. 处理过程中需正确抛出各类异常
	 * 
	 * @param source 待解码的源数据，不能为null
	 * @param target 字节数组消费者，用于处理解码结果，不能为null（由{@link NonNull}注解保证）
	 * @param <S> 消费者可能抛出的异常类型
	 * @throws CodecException 解码逻辑失败时抛出
	 * @throws IOException 解码过程中发生IO错误时抛出
	 * @throws S 消费者处理过程中可能抛出的异常
	 */
	<S extends Throwable> void decode(E source, @NonNull BufferConsumer<? super byte[], ? extends S> target)
			throws CodecException, IOException, S;
}
    