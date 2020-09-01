package scw.io.serialzer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import scw.lang.NotSupportedException;

/**
 * 序列化
 * @author shuchaowen
 *
 */
public abstract class Serializer implements NoTypeSpecifiedSerializer, SpecifiedTypeSerializer {

	public <T> T deserialize(byte[] data) throws IOException, ClassNotFoundException{
		throw new NotSupportedException("不支持不指定类型的反序列化方式(1)");
	}

	public <T> T deserialize(InputStream input) throws IOException, ClassNotFoundException {
		throw new NotSupportedException("不支持不指定类型的反序列化方式(2)");
	}

	public byte[] serialize(Object data) throws IOException{
		throw new NotSupportedException("不支持不指定类型的序列化方式(1)");
	}

	public void serialize(OutputStream out, Object data) throws IOException {
		throw new NotSupportedException("不支持不指定类型的序列化方式(2)");
	}

	public <T> T deserialize(Class<T> type, byte[] data) throws IOException, ClassNotFoundException{
		return deserialize(data);
	}

	public <T> T deserialize(Class<T> type, InputStream input) throws IOException, ClassNotFoundException {
		return deserialize(input);
	}

	public <T> byte[] serialize(Class<T> type, T data) throws IOException{
		return serialize(data);
	}

	public <T> void serialize(OutputStream out, Class<T> type, T data) throws IOException {
		serialize(out, data);
	}
}
