package run.soeasy.framework.core.exchange;

import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.domain.Wrapper;

/**
 * 发布者
 * 
 * @author shuchaowen
 *
 * @param <T>
 */
public interface Publisher<T> {

	public static interface BatchPublisher<T> extends Publisher<Elements<T>> {

		default Publisher<T> single() {
			return (FakeSinglePublisher<T, BatchPublisher<T>>) (() -> this);
		}
	}

	public static class IgnorePublisher<T> implements Publisher<T> {
		public static final Publisher<?> INSTANCE = new IgnorePublisher<>();

		@Override
		public Receipt publish(T resource) {
			return Receipt.SUCCESS;
		}
	}

	@FunctionalInterface
	public static interface FakeBatchPublisher<T, W extends Publisher<T>> extends BatchPublisher<T>, Wrapper<W> {
		@Override
		default Receipts<?> publish(Elements<T> resource) {
			Elements<Receipt> elemnets = resource.map((e) -> getSource().publish(e)).toList();
			return Receipts.of(elemnets);
		}
	}

	@FunctionalInterface
	public static interface FakeSinglePublisher<T, W extends Publisher<Elements<T>>> extends Publisher<T>, Wrapper<W> {

		@Override
		default Receipt publish(T resource) {
			return getSource().publish(Elements.singleton(resource));
		}

	}

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