package io.basc.framework.messageing.session;

import io.basc.framework.messageing.Message;

import java.io.Closeable;
import java.io.IOException;

public interface Session extends Closeable {
	String getId();

	boolean isOpen();

	void sendMessage(Message<?> message) throws IOException;
}
