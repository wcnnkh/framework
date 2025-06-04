package run.soeasy.framework.core.exchange;

import java.util.concurrent.TimeUnit;

import run.soeasy.framework.core.collection.Elements;

@FunctionalInterface
public interface FakeBatchChannel<T, W extends Channel<T>>
		extends BatchChannel<T>, FakeBatchPublisher<T, W> {

	@Override
	default Receipts<?> publish(Elements<T> resource) {
		return FakeBatchPublisher.super.publish(resource);
	}

	@Override
	default Receipt publish(Elements<T> resource, long timeout, TimeUnit timeUnit) {
		Elements<Receipt> elemnets = resource.map((e) -> getSource().publish(e, timeout, timeUnit)).toList();
		return Receipts.of(elemnets);
	}
}