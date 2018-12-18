package scw.common.io.decoder;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

import scw.common.exception.ShuChaoWenRuntimeException;
import scw.common.io.Decoder;

public class JavaObjectDecoder implements Decoder<Object>{
	public static final JavaObjectDecoder DECODER = new JavaObjectDecoder();

	public Object decode(InputStream in) throws IOException {
		ObjectInputStream objectInputStream = new ObjectInputStream(in);
		try {
			return objectInputStream.readObject();
		} catch (ClassNotFoundException e) {
			throw new ShuChaoWenRuntimeException(e);
		}
	}
}
