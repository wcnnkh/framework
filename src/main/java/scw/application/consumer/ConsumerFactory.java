package scw.application.consumer;

import scw.core.Consumer;

public interface ConsumerFactory {
	void bindConsumer(String name, Consumer<?> consumer);
}
