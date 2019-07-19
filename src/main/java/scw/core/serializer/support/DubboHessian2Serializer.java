package scw.core.serializer.support;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.alibaba.com.caucho.hessian.io.Hessian2Input;
import com.alibaba.com.caucho.hessian.io.Hessian2Output;
import com.alibaba.com.caucho.hessian.io.SerializerFactory;

import scw.core.serializer.Serializer;
import scw.io.IOUtils;
import scw.io.UnsafeByteArrayInputStream;
import scw.io.UnsafeByteArrayOutputStream;

public class DubboHessian2Serializer extends Serializer {
	private static final SerializerFactory SERIALIZER_FACTORY = new SerializerFactory();
	
	@Override
	public void serialize(OutputStream out, Object data) throws IOException {
		Hessian2Output output = new Hessian2Output(out);
		output.setSerializerFactory(SERIALIZER_FACTORY);
		try {
			output.writeObject(data);
			output.completeMessage();
		} finally {
			output.close();
		}
	}

	@Override
	public byte[] serialize(Object data) {
		UnsafeByteArrayOutputStream out = IOUtils.getUnsafeByteArrayOutputStream();
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
		Hessian2Input hi = new Hessian2Input(input);
		hi.setSerializerFactory(SERIALIZER_FACTORY);
		try {
			return (T) hi.readObject();
		} finally {
			hi.close();
		}
	}
}
