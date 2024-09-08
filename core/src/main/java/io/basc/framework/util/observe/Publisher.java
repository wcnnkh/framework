package io.basc.framework.util.observe;

/**
 * 发布者
 * 
 * @author shuchaowen
 *
 * @param <T>
 */
public interface Publisher<T> {
	public static Publisher<?> EMPTY_PUBLISHER = new EmptyPublisher<>();

	@SuppressWarnings("unchecked")
	public static <E> Publisher<E> empty() {
		return (Publisher<E>) EMPTY_PUBLISHER;
	}

	/**
	 * 发布
	 * 
	 * @param resource
	 * @return
	 */
	Listenable<? extends Receipt> publish(T resource);
}