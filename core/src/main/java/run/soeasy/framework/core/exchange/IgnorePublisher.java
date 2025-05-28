package run.soeasy.framework.core.exchange;

public class IgnorePublisher<T> implements Publisher<T> {
	static final Publisher<?> INSTANCE = new IgnorePublisher<>();

	@Override
	public Receipt publish(T resource) {
		return Receipt.SUCCESS;
	}
}