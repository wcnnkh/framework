package run.soeasy.framework.core.function;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class AlwaysBooleanPredicat<S, E extends Throwable> implements ThrowingPredicate<S, E> {
	static final AlwaysBooleanPredicat<?, ?> TRUE = new AlwaysBooleanPredicat<>(true);
	static final AlwaysBooleanPredicat<?, ?> FALSE = new AlwaysBooleanPredicat<>(false);

	private final boolean value;

	@Override
	public boolean test(S source) throws E {
		return value;
	}
}