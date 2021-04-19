package scw.jms.bind;

import java.lang.reflect.AnnotatedElement;

import javax.jms.MessageConsumer;

public interface MessageConsumerFactory {
	MessageConsumer getMessageConsumer(AnnotatedElement annotatedElement);
}
