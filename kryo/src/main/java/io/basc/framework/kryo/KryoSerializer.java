package io.basc.framework.kryo;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.lang.NamedThreadLocal;
import io.basc.framework.util.io.serializer.CrossLanguageSerializer;
import io.basc.framework.util.io.serializer.Serializer;

public class KryoSerializer implements Serializer, CrossLanguageSerializer {
	private static final ThreadLocal<Kryo> kryoLocal = new NamedThreadLocal<Kryo>(
			KryoSerializer.class.getSimpleName() + "-kryo") {
		protected Kryo initialValue() {
			Kryo kryo = new Kryo();
			return kryo;
		};
	};

	public static Kryo getKryo() {
		return kryoLocal.get();
	}

	private static final ThreadLocal<Output> outputLocal = new NamedThreadLocal<Output>(
			KryoSerializer.class.getSimpleName() + "-output") {
		protected Output initialValue() {
			Output output = new Output();
			output.setBuffer(new byte[1024], -1);
			return output;
		};
	};

	private static final ThreadLocal<Input> inputLocal = new NamedThreadLocal<Input>(
			KryoSerializer.class.getSimpleName() + "-input") {
		protected Input initialValue() {
			return new Input();
		};
	};

	public static Output getOutput() {
		Output output = outputLocal.get();
		output.reset();
		return output;
	}

	public static Output getOutput(OutputStream outputStream) {
		Output output = outputLocal.get();
		output.setOutputStream(outputStream);
		return output;
	}

	public static Input getInput(byte[] data) {
		Input input = inputLocal.get();
		input.setBuffer(data);
		return input;
	}

	public static Input getInput(InputStream inputStream) {
		Input input = inputLocal.get();
		input.setInputStream(inputStream);
		return input;
	}

	@Override
	public void serialize(Object source, OutputStream target) throws IOException {
		Output output = getOutput(target);
		getKryo().writeClassAndObject(output, source);
		output.flush();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T deserialize(InputStream input, int bufferSize) throws IOException, ClassNotFoundException {
		return (T) getKryo().readClassAndObject(getInput(input));
	}

	@Override
	public void serialize(Object source, TypeDescriptor sourceTypeDescriptor, OutputStream target) throws IOException {
		Output output = getOutput(target);
		getKryo().writeObjectOrNull(output, source, sourceTypeDescriptor.getType());
		output.flush();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T deserialize(InputStream input, TypeDescriptor type) throws IOException {
		return (T) getKryo().readObjectOrNull(getInput(input), type.getType());
	}
}
