package scw.mq;

import scw.beans.annotation.AutoImpl;
import scw.mq.support.XmlConsumerFactory;
import scw.util.queue.Consumer;

@AutoImpl({ XmlConsumerFactory.class })
public interface ConsumerFactory {
	void bindConsumer(String name, Consumer<?> consumer);
}
