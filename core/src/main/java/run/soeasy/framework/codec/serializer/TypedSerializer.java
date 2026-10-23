package run.soeasy.framework.codec.serializer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.codec.io.DirectSerializer;
import run.soeasy.framework.core.convert.TypeDescriptor;

@RequiredArgsConstructor
final class TypedSerializer<T> implements DirectSerializer<T> {
	@NonNull
	private final SerializationConverter serializer;
	@NonNull
	private final TypeDescriptor typeDescriptor;

	@Override
	public void serialize(T source, OutputStream target) throws IOException {
		serializer.serialize(source, typeDescriptor, target);
	}

	@SuppressWarnings("unchecked")
	@Override
	public T deserialize(InputStream input) throws IOException, ClassNotFoundException {
		return (T) serializer.deserialize(input, typeDescriptor);
	}
}
