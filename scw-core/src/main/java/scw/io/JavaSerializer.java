package scw.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

public class JavaSerializer implements Serializer {
	public final static JavaSerializer INSTANCE = new JavaSerializer();

	@SuppressWarnings("unchecked")
	@Override
	public <T> T deserialize(InputStream input) throws IOException, ClassNotFoundException {
		ObjectInputStream ois = null;
		try {
			ois = new ObjectInputStream(input);
			return (T) ois.readObject();
		} finally {
			if (ois != null) {
				ois.close();
			}
		}
	}

	public void serialize(OutputStream out, Object data) throws IOException {
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(out);
			oos.writeObject(data);
			oos.flush();
		} finally {
			IOUtils.close(oos);
		}
	}
}
