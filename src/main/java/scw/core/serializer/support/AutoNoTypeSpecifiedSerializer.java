package scw.core.serializer.support;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import scw.core.serializer.NoTypeSpecifiedSerializer;

public class AutoNoTypeSpecifiedSerializer implements NoTypeSpecifiedSerializer {
	private static NoTypeSpecifiedSerializer serializer;
	static {
		serializer = new JavaObjectSerializer();
	}

	public void serialize(OutputStream out, Object data) throws IOException {
		serializer.serialize(out, data);
	}

	public byte[] serialize(Object data) {
		return serializer.serialize(data);
	}

	public <T> T deserialize(InputStream input) throws IOException {
		return serializer.deserialize(input);
	}

	public <T> T deserialize(byte[] data) {
		return serializer.deserialize(data);
	}
}
