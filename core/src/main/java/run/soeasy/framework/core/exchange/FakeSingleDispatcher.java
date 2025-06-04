package run.soeasy.framework.core.exchange;

import run.soeasy.framework.core.collection.Elements;

@FunctionalInterface
public interface FakeSingleDispatcher<T, W extends Dispatcher<Elements<T>>>
		extends Dispatcher<T>, FakeSinglePublisher<T, W>, FakeSingleListenable<T, W> {
}
