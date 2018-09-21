package shuchaowen.core.http.rpc.serialization;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;

public class JavaObjectSerializer implements Serializer {

	public void encode(OutputStream out, Object data) throws IOException {
		ObjectOutputStream oos = new ObjectOutputStream(out);
		oos.writeObject(data);
	}

	@SuppressWarnings("unchecked")
	public <T> T decode(InputStream in, Type type) throws IOException, ClassNotFoundException {
		ObjectInputStream ois = new ObjectInputStream(in);
		return (T) ois.readObject();
	}

}
