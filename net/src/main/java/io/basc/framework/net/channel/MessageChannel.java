package io.basc.framework.net.channel;

import java.io.Closeable;

import io.basc.framework.net.InputMessage;
import io.basc.framework.util.exchange.Channel;

public interface MessageChannel extends Channel<InputMessage>, Closeable {
	boolean isClosed();
}
