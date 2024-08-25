package io.basc.framework.util.observe.supplier;

import java.util.function.Supplier;

import io.basc.framework.util.observe.ChangeEvent;
import io.basc.framework.util.observe.Observable;

public interface ObservableSupplier<T> extends Observable<ChangeEvent<T>>, Supplier<T> {
	@Override
	T get();
}
