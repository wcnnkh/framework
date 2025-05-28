package run.soeasy.framework.core.exchange;

import run.soeasy.framework.core.collection.Elements;

public interface BatchListener<T> extends Listener<Elements<T>> {

	default Listener<T> single() {
		return (FakeSingleListener<T, BatchListener<T>>) (() -> this);
	}
}