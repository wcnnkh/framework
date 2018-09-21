package shuchaowen.core.http.rpc.serialization;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import shuchaowen.core.exception.ShuChaoWenRuntimeException;

public class JavaObjectSerializer implements Serializer {

	public void encode(OutputStream out, Object data) throws IOException {
		ObjectOutputStream oos = new ObjectOutputStream(out);
		oos.writeObject(data);
	}

	@SuppressWarnings("unchecked")
	public <T> T decode(InputStream in, Class<T> type) throws IOException {
		ObjectInputStream ois = new ObjectInputStream(in);
		try {
			return (T) ois.readObject();
		} catch (ClassNotFoundException e) {
			throw new ShuChaoWenRuntimeException(e);
		}
	}

}
