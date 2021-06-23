package scw.util.stream;

public class StreamProcessorSupport {
	public static <T, E extends Throwable> StreamProcessor<T, E> stream(CallableProcessor<T, E> processor) {
		return new DefaultStreamProcessor<T, E>(processor);
	}

	public static <T, E extends Throwable> StreamProcessor<T, E> stream(T source) {
		return stream(() -> {
			return source;
		});
	}
}
