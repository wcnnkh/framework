package run.soeasy.framework.core.collection;

import java.io.Serializable;
import java.util.stream.Stream;

public class EmptyStreamable<E> implements Streamable<E>, Serializable {
	private static final long serialVersionUID = 1L;
	static final EmptyStreamable<Object> EMPTY_STREAMABLE = new EmptyStreamable<>();

	@Override
	public Stream<E> stream() {
		return Stream.empty();
	}
}