package shuchaowen.common.io.encoder;

import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import shuchaowen.common.io.Encoder;

public class JavaObjectEncoder implements Encoder<Object>{
	public static final JavaObjectEncoder ENCODER = new JavaObjectEncoder();
	
	public void encode(OutputStream out, Object data) throws IOException {
		ObjectOutput objectOutput = new ObjectOutputStream(out);
		objectOutput.writeObject(data);
	}

}
