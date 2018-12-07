package shuchaowen.connection.reader;

import java.io.IOException;
import java.io.InputStream;

import shuchaowen.common.ByteArray;
import shuchaowen.common.utils.IOUtils;
import shuchaowen.connection.Reader;

public class ByteReader implements Reader<byte[]>{

	public byte[] reader(InputStream inputStream) throws IOException {
		ByteArray byteArray = IOUtils.read(inputStream);
		return byteArray.toByteArray();
	}
}
