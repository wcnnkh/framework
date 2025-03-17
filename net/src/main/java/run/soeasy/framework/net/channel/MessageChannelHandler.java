package run.soeasy.framework.net.channel;

import java.io.IOException;

import run.soeasy.framework.net.InputMessage;

public interface MessageChannelHandler {
	void onOpen(MessageChannel channel) throws IOException;

	void onMessage(MessageChannel channel, InputMessage message) throws IOException;

	void onError(MessageChannel channel, Throwable throwable) throws IOException;

	void onClose(MessageChannel channel) throws IOException;
}
