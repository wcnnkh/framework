package io.basc.framework.rpc.remote;

import io.basc.framework.lang.Nullable;
import io.basc.framework.net.message.InputMessage;
import io.basc.framework.net.message.OutputMessage;

import java.io.IOException;

public interface RemoteMessageCodec {
	void encode(OutputMessage output, RemoteRequestMessage requestMessage)
			throws IOException, RemoteMessageCodecException;

	RemoteResponseMessage decode(InputMessage input, RemoteRequestMessage requestMessage)
			throws IOException, RemoteMessageCodecException;

	RemoteRequestMessage decode(InputMessage input) throws IOException, RemoteMessageCodecException;

	void encode(OutputMessage output, RemoteResponseMessage responseMessage,
			@Nullable RemoteRequestMessage requestMessage) throws IOException, RemoteMessageCodecException;
}
