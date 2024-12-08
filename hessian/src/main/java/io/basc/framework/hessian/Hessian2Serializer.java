package io.basc.framework.hessian;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import com.caucho.hessian.io.SerializerFactory;

import io.basc.framework.util.io.serializer.Serializer;

public class Hessian2Serializer implements Serializer {
	private final SerializerFactory serializerFactory;

	public Hessian2Serializer() {
		this(new DefaultSerializerFactory());
	}

	public Hessian2Serializer(SerializerFactory serializerFactory) {
		this.serializerFactory = serializerFactory;
	}

	@Override
	public void serialize(Object source, OutputStream target) throws IOException {
		Hessian2Output output = new Hessian2Output(target);
		output.setSerializerFactory(serializerFactory);
		try {
			output.writeObject(source);
			output.completeMessage();
		} finally {
			output.close();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T deserialize(InputStream input, int bufferSize) throws IOException, ClassNotFoundException {
		Hessian2Input hi = new Hessian2Input(input);
		hi.setSerializerFactory(serializerFactory);
		try {
			return (T) hi.readObject();
		} finally {
			hi.close();
		}
	}
}
