package run.soeasy.framework.serializer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.convert.TypeDescriptor;

@RequiredArgsConstructor
class ObjectSerializer implements TypedSerializer {
	@NonNull
	private final Serializer serializer;
	@NonNull
	private final TypeDescriptor typeDescriptor;

	@Override
	public void serialize(Object source, OutputStream target) throws IOException {
		serializer.serialize(source, typeDescriptor, target);
	}

	@Override
	public Object deserialize(InputStream input, int bufferSize) throws IOException, ClassNotFoundException {
		return serializer.deserialize(input, typeDescriptor);
	}

	@Override
	public TypedSerializer typed(TypeDescriptor typeDescriptor) {
		return new ObjectSerializer(serializer, typeDescriptor);
	}
}
