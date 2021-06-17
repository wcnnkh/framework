package scw.util.stream;

public interface CallableProcessor<T, E extends Throwable> {
	T process() throws E;
}
