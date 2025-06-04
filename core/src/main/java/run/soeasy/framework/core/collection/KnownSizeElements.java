package run.soeasy.framework.core.collection;

import java.util.function.ToLongFunction;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class KnownSizeElements<E, W extends Elements<E>> implements ElementsWrapper<E, W> {
	@NonNull
	private final W source;
	@NonNull
	private final ToLongFunction<? super W> statisticsSize;

	@Override
	public long count() {
		return statisticsSize.applyAsLong(source);
	}

	@Override
	public boolean isEmpty() {
		return count() == 0;
	}

	@Override
	public boolean isUnique() {
		return count() == 1;
	}
}
