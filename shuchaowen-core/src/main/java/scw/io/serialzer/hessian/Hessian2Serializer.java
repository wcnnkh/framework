package scw.io.serialzer.hessian;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import scw.io.UnsafeByteArrayInputStream;
import scw.io.UnsafeByteArrayOutputStream;
import scw.io.serialzer.Serializer;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;

public class Hessian2Serializer extends Serializer {

	@Override
	public void serialize(OutputStream out, Object data) throws IOException {
		Hessian2Output output = HessianUtils.createHessian2Output(out);
		try {
			output.writeObject(data);
			output.completeMessage();
		} finally {
			output.close();
		}
	}

	@Override
	public byte[] serialize(Object data) throws IOException {
		UnsafeByteArrayOutputStream out = new UnsafeByteArrayOutputStream();
		try {
			serialize(out, data);
			return out.toByteArray();
		} finally {
			out.close();
		}
	}

	@Override
	public <T> T deserialize(byte[] data) throws IOException, ClassNotFoundException {
		UnsafeByteArrayInputStream input = new UnsafeByteArrayInputStream(data);
		try {
			return deserialize(input);
		} finally {
			input.close();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T deserialize(InputStream input) throws IOException, ClassNotFoundException {
		Hessian2Input hi = HessianUtils.createHessian2Input(input);
		try {
			return (T) hi.readObject();
		} finally {
			hi.close();
		}
	}
}
