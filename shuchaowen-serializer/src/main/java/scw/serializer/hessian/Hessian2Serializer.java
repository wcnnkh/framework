package scw.serializer.hessian;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import scw.core.instance.annotation.Configuration;
import scw.io.UnsafeByteArrayInputStream;
import scw.io.UnsafeByteArrayOutputStream;
import scw.serializer.Serializer;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;

@Configuration(order=Integer.MIN_VALUE + 200)
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
	public byte[] serialize(Object data) {
		UnsafeByteArrayOutputStream out = new UnsafeByteArrayOutputStream();
		try {
			serialize(out, data);
			return out.toByteArray();
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public <T> T deserialize(byte[] data) {
		UnsafeByteArrayInputStream input = new UnsafeByteArrayInputStream(data);
		try {
			return deserialize(input);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			try {
				input.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T deserialize(InputStream input) throws IOException {
		Hessian2Input hi = HessianUtils.createHessian2Input(input);
		try {
			return (T) hi.readObject();
		} finally {
			hi.close();
		}
	}
}
