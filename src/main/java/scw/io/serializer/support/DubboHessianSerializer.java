package scw.io.serializer.support;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.alibaba.com.caucho.hessian.io.HessianInput;
import com.alibaba.com.caucho.hessian.io.HessianOutput;
import com.alibaba.com.caucho.hessian.io.SerializerFactory;

import scw.io.IOUtils;
import scw.io.UnsafeByteArrayInputStream;
import scw.io.UnsafeByteArrayOutputStream;
import scw.io.serializer.Serializer;

public class DubboHessianSerializer extends Serializer {
	private static final SerializerFactory SERIALIZER_FACTORY = new SerializerFactory();

	private static final ThreadLocal<HessianOutput> OUTPUT_LOCAL = new ThreadLocal<HessianOutput>() {
		protected HessianOutput initialValue() {
			HessianOutput output = new HessianOutput();
			output.setSerializerFactory(SERIALIZER_FACTORY);
			return output;
		};
	};

	private static final ThreadLocal<HessianInput> INPUT_LOCAL = new ThreadLocal<HessianInput>() {
		protected HessianInput initialValue() {
			HessianInput input = new HessianInput();
			input.setSerializerFactory(SERIALIZER_FACTORY);
			return input;
		};
	};

	public static HessianOutput getOutput(OutputStream out) {
		HessianOutput hos = OUTPUT_LOCAL.get();
		hos.init(out);
		return hos;
	}

	public static HessianInput getInput(InputStream input) {
		HessianInput in = INPUT_LOCAL.get();
		in.init(input);
		return in;
	}

	@Override
	public byte[] serialize(Object data) {
		UnsafeByteArrayOutputStream bos = IOUtils.getUnsafeByteArrayOutputStream();
		try {
			serialize(bos, data);
			return bos.toByteArray();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void serialize(OutputStream out, Object data) throws IOException {
		HessianOutput output = getOutput(out);
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
		HessianInput in = getInput(input);
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
