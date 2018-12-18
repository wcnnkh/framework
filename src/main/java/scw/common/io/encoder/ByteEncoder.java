package scw.common.io.encoder;

import java.io.IOException;
import java.io.OutputStream;

import scw.common.io.Encoder;

public class ByteEncoder implements Encoder<byte[]>{
	public static final ByteEncoder ENCODER = new ByteEncoder();

	public void encode(OutputStream out, byte[] data) throws IOException {
		out.write(data);
	}

}
