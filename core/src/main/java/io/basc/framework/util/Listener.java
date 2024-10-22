package io.basc.framework.util;

import java.util.EventListener;
import java.util.function.Consumer;

public interface Listener<T> extends Consumer<T>, EventListener {
	@Override
	void accept(T source);
}