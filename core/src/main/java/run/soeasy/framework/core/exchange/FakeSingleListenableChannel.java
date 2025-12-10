package run.soeasy.framework.core.exchange;

import run.soeasy.framework.core.streaming.Streamable;

@FunctionalInterface
interface FakeSingleListenableChannel<T, W extends ListenableChannel<Streamable<T>>>
		extends ListenableChannel<T>, FakeSingleChannel<T, W>, FakeSingleDispatcher<T, W> {
}