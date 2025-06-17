package run.soeasy.framework.core.domain;

@SuppressWarnings("rawtypes")
public interface Discrete<T extends Comparable> {
	T next(T value);

	T previous(T value);

	long distance(T start, T end);
}
