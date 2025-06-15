package run.soeasy.framework.core.domain;

@SuppressWarnings("rawtypes")
public interface DiscreteWrapper<T extends Comparable, W extends Discrete<T>> extends Discrete<T>, Wrapper<W> {

	@Override
	default T next(T value) {
		return getSource().next(value);
	}

	@Override
	default T previous(T value) {
		return getSource().previous(value);
	}

	@Override
	default long distance(T start, T end) {
		return getSource().distance(start, end);
	}

}
