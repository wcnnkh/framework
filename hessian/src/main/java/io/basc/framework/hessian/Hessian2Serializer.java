package io.basc.framework.hessian;

import io.basc.framework.io.Serializer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import com.caucho.hessian.io.SerializerFactory;

public class Hessian2Serializer implements Serializer {
	private final SerializerFactory serializerFactory;

	public Hessian2Serializer() {
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
