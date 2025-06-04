package run.soeasy.framework.core.exchange;

import java.util.EventListener;
import java.util.function.Consumer;

public interface Listener<T> extends Consumer<T>, EventListener {
	@Override
	void accept(T source);

	default BatchListener<T> batch() {
		return (FakeBatchListener<T, Listener<T>>) (() -> this);
	}
}
