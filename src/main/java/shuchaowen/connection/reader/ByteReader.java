package shuchaowen.connection.reader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import shuchaowen.connection.Reader;
import shuchaowen.core.util.IOUtils;

public class ByteReader implements Reader<byte[]>{

	public byte[] reader(InputStream inputStream) throws IOException {
		ByteArrayOutputStream baos = IOUtils.read(inputStream);
		return baos.toByteArray();
	}
}
