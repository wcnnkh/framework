package scw.core.serializer.support;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;

import scw.core.Bits;
import scw.core.io.ByteArray;
import scw.core.serializer.Serializer;

public class ProtostuffSerializer extends Serializer {
	public static final ProtostuffSerializer instance = new ProtostuffSerializer();

	private static final Map<Class<?>, byte[]> CLASS_TO_BYTES = new HashMap<Class<?>, byte[]>();
	private static final Map<byte[], Class<?>> BYTES_TO_CLASS = new HashMap<byte[], Class<?>>();

	private final ThreadLocal<LinkedBuffer> bufferLocal = new ThreadLocal<LinkedBuffer>() {
		protected LinkedBuffer initialValue() {
			return LinkedBuffer.allocate(1024);
		};
	};

	public LinkedBuffer getLinkedBuffer() {
		return bufferLocal.get().clear();
	}

	// 不用担心并发，因为最终结果都是一致的
	private byte[] classToBytes(Class<?> clazz) {
		byte[] data = CLASS_TO_BYTES.get(clazz);
		if (data == null) {
			String name = clazz.getName();
			ByteArray byteArray = new ByteArray();
			for (char c : name.toCharArray()) {
				byte[] bs = { 0, 0 };
				Bits.putChar(bs, 0, c);
				byteArray.write(bs, 0, bs.length);
			}
			data = byteArray.toByteArray();
			CLASS_TO_BYTES.put(clazz, data);
		}
		return data;
	}

	private Class<?> bytesToClass(byte[] bs) {
		Class<?> clz = BYTES_TO_CLASS.get(bs);
		if (clz == null) {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < bs.length; i += 2) {
				char c = Bits.getChar(bs, i);
				sb.append(c);
			}
			try {
				clz = Class.forName(sb.toString());
				BYTES_TO_CLASS.put(bs, clz);
			} catch (ClassNotFoundException e) {
				throw new RuntimeException(e);
			}
		}
		return clz;
	}

	@Override
	public <T> byte[] serialize(Class<T> type, T data) {
		return ProtostuffIOUtil.toByteArray(data, RuntimeSchema.getSchema(type), getLinkedBuffer());
	}

	@Override
	public <T> T deserialize(Class<T> type, byte[] data) {
		Schema<T> schema = RuntimeSchema.getSchema(type);
		T t = schema.newMessage();
		ProtostuffIOUtil.mergeFrom(data, t, schema);
		return t;
	}

	@Override
	public <T> void serialize(OutputStream out, Class<T> type, T data) throws IOException {
		ProtostuffIOUtil.writeTo(out, data, RuntimeSchema.getSchema(type), getLinkedBuffer());
		out.flush();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public byte[] serialize(Object data) {
		if (data == null) {
			return null;
		}

		byte[] nameBytes = classToBytes(data.getClass());
		byte[] objBytes = serialize((Class) data.getClass(), data);
		byte[] buff = new byte[4 + nameBytes.length + objBytes.length];
		Bits.putInt(buff, 0, nameBytes.length);
		System.arraycopy(nameBytes, 0, buff, 4, nameBytes.length);
		System.arraycopy(objBytes, 0, buff, 4 + nameBytes.length, objBytes.length);
		return buff;
	}

	@Override
	public void serialize(OutputStream out, Object data) throws IOException {
		byte[] bs = serialize(data);
		if (bs == null) {
			return;
		}
		out.write(bs);
		out.flush();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public <T> T deserialize(byte[] data) {
		int len = Bits.getInt(data, 0);
		byte[] nameBytes = new byte[len];
		System.arraycopy(data, 4, nameBytes, 0, len);
		Class<?> type = bytesToClass(nameBytes);
		Schema schema = RuntimeSchema.getSchema(type);
		Object t = schema.newMessage();
		ProtostuffIOUtil.mergeFrom(data, t, schema);
		return (T) t;
	}

	@Override
	public <T> T deserialize(Class<T> type, InputStream input) throws IOException {
		Schema<T> schema = RuntimeSchema.getSchema(type);
		T t = schema.newMessage();
		ProtostuffIOUtil.mergeFrom(input, t, schema, getLinkedBuffer());
		return t;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public <T> T deserialize(InputStream input) throws IOException {
		byte[] lenBytes = new byte[2];
		if (input.read(lenBytes) == -1) {
			throw new RuntimeException("解析错误");
		}

		byte[] nameBytes = new byte[Bits.getInt(lenBytes, 0)];
		input.read(nameBytes);

		Class<?> type = bytesToClass(nameBytes);
		Schema schema = RuntimeSchema.getSchema(type);
		Object t = schema.newMessage();
		ProtostuffIOUtil.mergeFrom(input, t, schema, getLinkedBuffer());
		return (T) t;
	}
}
