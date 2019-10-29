package scw.application.consumer;

import scw.beans.annotation.AutoImpl;
import scw.core.Consumer;

@AutoImpl({ XmlConsumerFactory.class })
public interface ConsumerFactory {
	void bindConsumer(String name, Consumer<?> consumer);
}
