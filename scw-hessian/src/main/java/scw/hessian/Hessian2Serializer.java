package scw.hessian;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import scw.io.Serializer;
import scw.io.SerializerException;
import scw.io.UnsafeByteArrayInputStream;
import scw.io.UnsafeByteArrayOutputStream;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import com.caucho.hessian.io.SerializerFactory;

public class Hessian2Serializer extends Serializer {
	private final SerializerFactory serializerFactory;
	
	public Hessian2Serializer(){
		this(new DefaultSerializerFactory());
	}
	
	public Hessian2Serializer(SerializerFactory serializerFactory) {
		this.serializerFactory = serializerFactory;
	}

	@Override
	public void serialize(OutputStream out, Object data) throws IOException {
		Hessian2Output output = new Hessian2Output(out);
		output.setSerializerFactory(serializerFactory);
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
			// 不可能存在此错误
			throw new SerializerException(e);
		} finally {
			out.close();
		}
	}

	@Override
	public <T> T deserialize(byte[] data) throws ClassNotFoundException {
		UnsafeByteArrayInputStream input = new UnsafeByteArrayInputStream(data);
		try {
			return deserialize(input);
		} catch (IOException e) {
			// 不可能存在此错误
			throw new SerializerException(e);
		} finally {
			input.close();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T deserialize(InputStream input) throws IOException, ClassNotFoundException {
		Hessian2Input hi = new Hessian2Input(input);
		hi.setSerializerFactory(serializerFactory);
		try {
			return (T) hi.readObject();
		} finally {
			hi.close();
		}
	}
}
