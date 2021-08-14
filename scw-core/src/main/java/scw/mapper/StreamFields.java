package scw.mapper;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StreamFields extends AbstractFields implements Fields {
	private final Stream<Field> stream;

	public StreamFields(Class<?> cursorId, Fields fields, Stream<Field> stream) {
		super(cursorId, fields);
		this.stream = stream;
	}

	public StreamFields(Class<?> cursorId, Class<?> nextCursorId,
			Fields fields, Stream<Field> stream) {
		super(cursorId, nextCursorId, fields);
		this.stream = stream;
	}

	@Override
	public long getCount() {
		return stream.count();
	}

	@Override
	public List<Field> rows() {
		return stream.collect(Collectors.toList());
	}

	@Override
	public Stream<Field> stream() {
		return stream;
	}
}
