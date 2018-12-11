package shuchaowen.common.io.decoder;

import java.io.IOException;
import java.io.InputStream;

import shuchaowen.common.ByteArray;
import shuchaowen.common.io.Decoder;

public class ByteArrayDecoder implements Decoder<ByteArray>{
	public static final ByteArrayDecoder DECODER = new ByteArrayDecoder();

	public ByteArray decode(InputStream in) throws IOException {
		ByteArray byteArray = new ByteArray();
		byte[] b = new byte[1024];
		int len = 0;
		while((len = in.read(b)) != -1){
			byteArray.write(b, 0, len);
		}
		return byteArray;
	}

}
