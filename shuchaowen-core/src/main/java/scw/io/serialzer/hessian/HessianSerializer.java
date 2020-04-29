package scw.io.serialzer.hessian;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;

import scw.core.instance.annotation.Configuration;
import scw.io.UnsafeByteArrayInputStream;
import scw.io.UnsafeByteArrayOutputStream;
import scw.io.serialzer.Serializer;

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
			HessianUtils.writeProxyObject(output, data);
			output.flush();
		} finally {
			output.close();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T deserialize(InputStream input) throws IOException, ClassNotFoundException {
		HessianInput in = HessianUtils.createHessianInput(input);
		try {
			return (T) HessianUtils.readProxyObject(in, null);
		} finally {
			in.close();
		}
	}

	@Override
	public <T> T deserialize(byte[] data) throws IOException, ClassNotFoundException {
		UnsafeByteArrayInputStream input = new UnsafeByteArrayInputStream(data);
		try {
			return deserialize(input);
		} finally {
			try {
				input.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
