package io.basc.framework.util.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.util.io.serializer.CrossLanguageSerializer;
import io.basc.framework.util.io.serializer.Serializer;

public class ObjectSerializer implements Serializer {
	private final CrossLanguageSerializer serializer;
	private final TypeDescriptor typeDescriptor;

	public ObjectSerializer(CrossLanguageSerializer serializer, TypeDescriptor typeDescriptor) {
		this.serializer = serializer;
		this.typeDescriptor = typeDescriptor;
	}

	public final CrossLanguageSerializer getSerializer() {
		return serializer;
	}

	public final TypeDescriptor getTypeDescriptor() {
		return typeDescriptor;
	}

	@Override
	public void serialize(Object source, OutputStream target) throws IOException {
		serializer.serialize(source, typeDescriptor, target);
	}

	@Override
	public <T> T deserialize(InputStream input, int bufferSize) throws IOException, ClassNotFoundException {
		return serializer.deserialize(input, typeDescriptor);
	}
}
