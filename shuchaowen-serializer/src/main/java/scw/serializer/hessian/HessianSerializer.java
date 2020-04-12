package scw.serializer.hessian;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import scw.core.instance.annotation.Configuration;
import scw.io.UnsafeByteArrayInputStream;
import scw.io.UnsafeByteArrayOutputStream;
import scw.serializer.Serializer;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;

@Configuration(order=Integer.MIN_VALUE + 100)
public class HessianSerializer extends Serializer {
	@Override
	public byte[] serialize(Object data) {
		UnsafeByteArrayOutputStream bos = new UnsafeByteArrayOutputStream();
		try {
			serialize(bos, data);
			return bos.toByteArray();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void serialize(OutputStream out, Object data) throws IOException {
		HessianOutput output = HessianUtils.createHessianOutput(out);
		try {
			output.writeObject(data);
			output.flush();
		} finally {
			output.close();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T deserialize(InputStream input) throws IOException {
		HessianInput in = HessianUtils.createHessianInput(input);
		try {
			return (T) in.readObject();
		} finally {
			in.close();
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
}
