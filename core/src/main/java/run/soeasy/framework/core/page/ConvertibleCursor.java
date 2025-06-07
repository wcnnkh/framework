package run.soeasy.framework.core.page;

import java.util.function.Function;

import lombok.NonNull;
import run.soeasy.framework.core.collection.Elements;

public class ConvertibleCursor<M extends Cursor<SK, ST>, SK, ST, K, T> implements Cursor<K, T> {
	protected final M source;
	protected final Function<? super SK, ? extends K> cursorIdConverter;
	protected final Function<? super Elements<ST>, ? extends Elements<T>> elementsConverter;

	public ConvertibleCursor(@NonNull M source, @NonNull Function<? super SK, ? extends K> cursorIdConverter,
			@NonNull Function<? super Elements<ST>, ? extends Elements<T>> elementsConverter) {
		this.source = source;
		this.cursorIdConverter = cursorIdConverter;
		this.elementsConverter = elementsConverter;
	}

	@Override
	public K getCursorId() {
		SK value = source.getCursorId();
		return value == null ? null : cursorIdConverter.apply(value);
	}

	@Override
	public K getNextCursorId() {
		SK value = source.getNextCursorId();
		return value == null ? null : cursorIdConverter.apply(value);
	}

	@Override
	public boolean hasNext() {
		return source.hasNext();
	}

	@Override
	public Elements<T> getElements() {
		return elementsConverter.apply(source.getElements());
	}
}
