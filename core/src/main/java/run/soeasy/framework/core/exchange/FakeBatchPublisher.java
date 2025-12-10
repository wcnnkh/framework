package run.soeasy.framework.core.exchange;

import run.soeasy.framework.core.domain.Wrapper;
import run.soeasy.framework.core.exchange.CompositeOperation.Mode;
import run.soeasy.framework.core.streaming.Streamable;

@FunctionalInterface
interface FakeBatchPublisher<T, W extends Publisher<T>> extends BatchPublisher<T>, Wrapper<W> {
	@Override
	default Operation publish(Streamable<T> elements) {
		return Operation.batch(elements, Mode.AND, getSource()::publish);
	}
}