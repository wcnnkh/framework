package scw.util.page;

import java.util.function.Function;

public class MapperCursor<P extends Cursor<K, S>, K, S, T> extends MapperPageable<P, K, S, T> implements Cursor<K, T> {

	public MapperCursor(P pageable, Function<? super S, ? extends T> mapper) {
		super(pageable, mapper);
	}

	@Override
	public void close() {
		wrappedTarget.close();
	}
}
