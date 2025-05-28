package run.soeasy.framework.core.exchange;

import run.soeasy.framework.core.collection.Elements;

@FunctionalInterface
public interface FakeSingleListenableChannel<T, W extends ListenableChannel<Elements<T>>>
		extends ListenableChannel<T>, FakeSingleChannel<T, W>, FakeSingleDispatcher<T, W> {
}