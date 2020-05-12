package scw.messageing.session;

import java.io.Closeable;
import java.io.IOException;

import scw.messageing.Message;

public interface Session extends Closeable {
	String getId();

	boolean isOpen();

	void sendMessage(Message<?> message) throws IOException;
}
