package scw.application.consumer;

import scw.core.Consumer;

public interface BindConsumer<T> {
	void bindConsumer(String name, Consumer<T> consumer);
}
