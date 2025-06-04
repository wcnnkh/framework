package run.soeasy.framework.core.exchange;

import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.domain.Wrapper;

@FunctionalInterface
public interface FakeBatchPublisher<T, W extends Publisher<T>> extends BatchPublisher<T>, Wrapper<W> {
	@Override
	default Receipts<?> publish(Elements<T> resource) {
		Elements<Receipt> elemnets = resource.map((e) -> getSource().publish(e)).toList();
		return Receipts.of(elemnets);
	}
}