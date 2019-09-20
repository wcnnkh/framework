package scw.io.serializer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import scw.beans.annotation.AutoImpl;
import scw.core.exception.NotSupportException;

@AutoImpl(implClassName = { "scw.io.serializer.support.Hessian2Serializer",
		"scw.io.serializer.support.HessianSerializer", "scw.io.serializer.support.JavaSerializer" })
public abstract class Serializer implements NoTypeSpecifiedSerializer, SpecifiedTypeSerializer {

	public <T> T deserialize(byte[] data) {
		throw new NotSupportException("不支持不指定类型的反序列化方式(1)");
	}

	public <T> T deserialize(InputStream input) throws IOException {
		throw new NotSupportException("不支持不指定类型的反序列化方式(2)");
	}

	public byte[] serialize(Object data) {
		throw new NotSupportException("不支持不指定类型的序列化方式(1)");
	}

	public void serialize(OutputStream out, Object data) throws IOException {
		throw new NotSupportException("不支持不指定类型的序列化方式(2)");
	}

	public <T> T deserialize(Class<T> type, byte[] data) {
		return deserialize(data);
	}

	public <T> T deserialize(Class<T> type, InputStream input) throws IOException {
		return deserialize(input);
	}

	public <T> byte[] serialize(Class<T> type, T data) {
		return serialize(data);
	}

	public <T> void serialize(OutputStream out, Class<T> type, T data) throws IOException {
		serialize(out, data);
	}
}
