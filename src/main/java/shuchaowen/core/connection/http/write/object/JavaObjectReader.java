package shuchaowen.core.connection.http.write.object;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

import shuchaowen.core.connection.Reader;
import shuchaowen.core.exception.ShuChaoWenRuntimeException;

public class JavaObjectReader<T> implements Reader<T>{
	
	@SuppressWarnings("unchecked")
	public T reader(InputStream inputStream) throws IOException {
		ObjectInputStream ois = new ObjectInputStream(inputStream);
		try {
			return (T) ois.readObject();
		} catch (ClassNotFoundException e) {
			throw new ShuChaoWenRuntimeException(e);
		}
	}

}
