package scw.mq;

import scw.beans.annotation.AutoImpl;
import scw.core.Consumer;
import scw.mq.support.XmlConsumerFactory;

@AutoImpl({ XmlConsumerFactory.class })
public interface ConsumerFactory {
	void bindConsumer(String name, Consumer<?> consumer);
}
