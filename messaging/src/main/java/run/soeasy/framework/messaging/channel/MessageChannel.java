package run.soeasy.framework.messaging.channel;

import java.io.Closeable;

import run.soeasy.framework.core.exchange.Channel;
import run.soeasy.framework.messaging.InputMessage;

public interface MessageChannel extends Channel<InputMessage>, Closeable {
	boolean isClosed();
}
