package run.soeasy.framework.core.exchange;

import run.soeasy.framework.core.domain.Wrapper;
import run.soeasy.framework.core.streaming.Streamable;

@FunctionalInterface
interface FakeBatchPublisher<T, W extends Publisher<T>> extends BatchPublisher<T>, Wrapper<W> {
	@Override
	default Signal publish(Streamable<T> elements) {
		return Signal.batch(elements.map(getSource()::publish));
	}
}