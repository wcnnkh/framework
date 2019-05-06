package scw.core.serializer.support;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import scw.core.Constants;
import scw.core.serializer.SpecifiedTypeSerializer;

public class AutoSpecifiedTypeSerializer implements SpecifiedTypeSerializer {
	private static SpecifiedTypeSerializer serializer;

	static {
		serializer = Constants.JAVA_SERIALIZER;
	}

	public void serialize(OutputStream out, Object data) throws IOException {
		serializer.serialize(out, data);
	}

	public byte[] serialize(Object data) {
		return serializer.serialize(data);
	}

	public <T> T deserialize(Class<T> type, InputStream input) throws IOException {
		return serializer.deserialize(type, input);
	}

	public <T> T deserialize(Class<T> type, byte[] data) {
		return serializer.deserialize(type, data);
	}

}
