package run.soeasy.framework.core.exchange;

/**
 * 发布者
 * 
 * @author shuchaowen
 *
 * @param <T>
 */
public interface Publisher<T> {

	@SuppressWarnings("unchecked")
	public static <E> Publisher<E> ignore() {
		return (Publisher<E>) IgnorePublisher.INSTANCE;
	}

	default BatchPublisher<T> batch() {
		return (FakeBatchPublisher<T, Publisher<T>>) (() -> this);
	}

	/**
	 * 发布
	 * 
	 * @param resource
	 * @return
	 */
	Receipt publish(T resource);
}