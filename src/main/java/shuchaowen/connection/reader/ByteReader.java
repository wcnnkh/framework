package shuchaowen.connection.reader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import shuchaowen.common.utils.IOUtils;
import shuchaowen.connection.Reader;

public class ByteReader implements Reader<byte[]>{

	public byte[] reader(InputStream inputStream) throws IOException {
		ByteArrayOutputStream baos = IOUtils.read(inputStream);
		return baos.toByteArray();
	}
}
