package run.soeasy.framework.serializer;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import run.soeasy.framework.core.io.IOUtils;

public class JavaSerializer implements Serializer {
	public final static JavaSerializer INSTANCE = new JavaSerializer();

	@Override
	public void serialize(Object source, OutputStream target) throws IOException {
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(target);
			oos.writeObject(source);
			oos.flush();
		} finally {
			IOUtils.close(oos);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T deserialize(InputStream input, int bufferSize) throws IOException, ClassNotFoundException {
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
}
