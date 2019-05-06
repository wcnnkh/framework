package scw.core.serializer.support;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import scw.core.serializer.SpecifiedTypeSerializer;

public class AutoSpecifiedTypeSerializer implements SpecifiedTypeSerializer {
	private static SpecifiedTypeSerializer serializer;

	static {
		serializer = new JavaObjectSerializer();
	}

	public <T> T deserialize(Class<T> type, InputStream input) throws IOException {
		return serializer.deserialize(type, input);
	}

	public <T> T deserialize(Class<T> type, byte[] data) {
		return serializer.deserialize(type, data);
	}

	public <T> void serialize(OutputStream out, Class<T> type, T data)
			throws IOException {
		serializer.serialize(out, type, data);
	}

	public <T> byte[] serialize(Class<T> type, T data) {
		return serializer.serialize(type, data);
	}

}
