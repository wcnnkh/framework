package io.basc.framework.jms;

import javax.jms.Message;

public interface MessageSelector<T> {
	String getSelector(T source);

	void write(Message message, T source);
}
