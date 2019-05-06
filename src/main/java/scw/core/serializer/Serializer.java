package scw.core.serializer;

import java.io.IOException;
import java.io.InputStream;

import scw.core.exception.NotSupportException;

public abstract class Serializer implements NoTypeSpecifiedSerializer, SpecifiedTypeSerializer {

	public <T> T deserialize(byte[] data) {
		throw new NotSupportException("不支持不指定类型的反序列化方式(1)");
	}

	public <T> T deserialize(InputStream input) throws IOException {
		throw new NotSupportException("不支持不指定类型的反序列化方式(2)");
	}

	public <T> T deserialize(Class<T> type, byte[] data) {
		return deserialize(data);
	}

	public <T> T deserialize(Class<T> type, InputStream input) throws IOException {
		return deserialize(input);
	}
}
