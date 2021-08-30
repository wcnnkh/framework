package io.basc.framework.jms.bind;

import java.lang.reflect.AnnotatedElement;

import javax.jms.MessageConsumer;

public interface MessageConsumerFactory {
	MessageConsumer getMessageConsumer(AnnotatedElement annotatedElement);
}
