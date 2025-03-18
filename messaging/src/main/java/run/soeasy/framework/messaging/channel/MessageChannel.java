package run.soeasy.framework.messaging.channel;

import java.io.Closeable;

import run.soeasy.framework.messaging.InputMessage;
import run.soeasy.framework.util.exchange.Channel;

public interface MessageChannel extends Channel<InputMessage>, Closeable {
	boolean isClosed();
}
