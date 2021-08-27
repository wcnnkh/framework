package io.basc.framework.mapper;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StreamFields extends AbstractFields implements Fields {
	private final Supplier<Stream<Field>> stream;

	public StreamFields(Class<?> cursorId, Fields fields, Supplier<Stream<Field>> stream) {
		super(cursorId, fields);
		this.stream = stream;
	}

	public StreamFields(Class<?> cursorId, Class<?> nextCursorId,
			Fields fields, Supplier<Stream<Field>> stream) {
		super(cursorId, nextCursorId, fields);
		this.stream = stream;
	}

	@Override
	public long getCount() {
		return stream.get().count();
	}

	@Override
	public List<Field> rows() {
		return stream.get().collect(Collectors.toList());
	}

	@Override
	public Stream<Field> stream() {
		return stream.get();
	}
}
