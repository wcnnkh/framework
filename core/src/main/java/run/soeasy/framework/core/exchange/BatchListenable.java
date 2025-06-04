package run.soeasy.framework.core.exchange;

import run.soeasy.framework.core.collection.Elements;

public interface BatchListenable<T> extends Listenable<Elements<T>> {
	default Listenable<T> single() {
		return (FakeSingleListenable<T, BatchListenable<T>>) (() -> this);
	}
}