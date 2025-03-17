package run.soeasy.framework.net.channel;

import java.io.Closeable;

import run.soeasy.framework.net.InputMessage;
import run.soeasy.framework.util.exchange.Channel;

public interface MessageChannel extends Channel<InputMessage>, Closeable {
	boolean isClosed();
}
