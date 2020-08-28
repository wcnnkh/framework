package scw.io.serialzer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import scw.beans.annotation.AopEnable;

@AopEnable(false)
public interface NoTypeSpecifiedSerializer {
	void serialize(OutputStream out, Object data) throws IOException;

	byte[] serialize(Object data) throws IOException;
	
	<T> T deserialize(InputStream input) throws IOException, ClassNotFoundException;

	<T> T deserialize(byte[] data) throws IOException, ClassNotFoundException;
}
