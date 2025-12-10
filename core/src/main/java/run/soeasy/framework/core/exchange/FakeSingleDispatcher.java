package run.soeasy.framework.core.exchange;

import run.soeasy.framework.core.streaming.Streamable;

@FunctionalInterface
interface FakeSingleDispatcher<T, W extends Dispatcher<Streamable<T>>>
		extends Dispatcher<T>, FakeSinglePublisher<T, W>, FakeSingleListenable<T, W> {
}
