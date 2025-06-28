package run.soeasy.framework.serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import run.soeasy.framework.codec.DecodeException;
import run.soeasy.framework.codec.EncodeException;
import run.soeasy.framework.codec.binary.ToBytesCodec;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.io.IOUtils;

public interface TypedSerializer extends Serializer, ToBytesCodec<Object> {

	/**
	 * 将对象序列化为输出流
	 * 
	 * @param source 源对象
	 * @param target 目标输出流
	 * @throws IOException 序列化过程中发生IO异常时抛出
	 */
	void serialize(Object source, OutputStream target) throws IOException;

	/**
	 * 实现Serializer的serialize方法
	 */
	@Override
	default void serialize(Object source, TypeDescriptor sourceTypeDescriptor, OutputStream target) throws IOException {
		serialize(source, target);
	}

	/**
	 * 实现Serializer的deserialize方法
	 */
	@Override
	default Object deserialize(InputStream source, TypeDescriptor targetTypeDescriptor)
			throws IOException, DecodeException {
		try {
			return deserialize(source);
		} catch (ClassNotFoundException e) {
			throw new DecodeException(e);
		}
	}

	/**
	 * 实现ToBytesCodec的encode方法
	 */
	@Override
	default void encode(Object source, OutputStream target) throws IOException, EncodeException {
		serialize(source, target);
	}

	/**
	 * 将对象序列化为字节数组
	 * 
	 * @param data 源对象
	 * @return 序列化后的字节数组
	 * @throws SerializerException 序列化失败时抛出
	 */
	default byte[] serialize(Object data) throws SerializerException {
		ByteArrayOutputStream target = new ByteArrayOutputStream();
		try {
			serialize(data, target);
			return target.toByteArray();
		} catch (IOException e) {
			throw new SerializerException(e);
		} finally {
			IOUtils.closeQuietly(target);
		}
	}

	/**
	 * 从输入流反序列化对象（支持指定缓冲区大小）
	 * 
	 * @param input      源输入流
	 * @param bufferSize 缓冲区大小
	 * @return 反序列化后的对象
	 * @throws IOException            读取输入流时发生异常
	 * @throws ClassNotFoundException 类未找到异常
	 */
	Object deserialize(InputStream input, int bufferSize) throws IOException, ClassNotFoundException;

	/**
	 * 从输入流反序列化对象（使用默认缓冲区大小）
	 */
	default Object deserialize(InputStream input) throws IOException, ClassNotFoundException {
		return deserialize(input, IOUtils.DEFAULT_BYTE_BUFFER_SIZE);
	}

	/**
	 * 从字节数组反序列化对象
	 */
	default Object deserialize(byte[] data) throws ClassNotFoundException, SerializerException {
		ByteArrayInputStream input = new ByteArrayInputStream(data);
		try {
			return deserialize(input);
		} catch (IOException e) {
			throw new SerializerException(e);
		} finally {
			IOUtils.closeQuietly(input);
		}
	}

	/**
	 * 实现ToBytesCodec的encode方法
	 */
	@Override
	default byte[] encode(Object source) throws EncodeException {
		return serialize(source);
	}

	/**
	 * 实现ToBytesCodec的decode方法
	 */
	@Override
	default Object decode(InputStream source, int bufferSize) throws IOException, DecodeException {
		try {
			return deserialize(source, bufferSize);
		} catch (ClassNotFoundException e) {
			throw new DecodeException(e);
		}
	}

	/**
	 * 类型安全序列化器适配
	 */
	@Override
	default TypedSerializer typed(TypeDescriptor typeDescriptor) {
		return this;
	}
}