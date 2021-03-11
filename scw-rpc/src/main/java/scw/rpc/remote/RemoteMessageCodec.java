package scw.rpc.remote;

import java.io.IOException;

import scw.lang.Nullable;
import scw.net.message.InputMessage;
import scw.net.message.OutputMessage;

public interface RemoteMessageCodec {
	/**
	 * reference
	 * @param output
	 * @param requestMessage
	 * @throws IOException
	 * @throws RemoteMessageCodecException
	 */
	void encode(OutputMessage output, RemoteRequestMessage requestMessage) throws IOException, RemoteMessageCodecException;
	
	/**
	 * reference
	 * @param input
	 * @param requestMessage
	 * @return
	 * @throws IOException
	 * @throws RemoteMessageCodecException
	 */
	RemoteResponseMessage decode(InputMessage input, RemoteRequestMessage requestMessage) throws IOException, RemoteMessageCodecException;
	
	/**
	 * service
	 * @param input
	 * @return
	 * @throws IOException
	 * @throws RemoteMessageCodecException
	 */
	RemoteRequestMessage decode(InputMessage input) throws IOException, RemoteMessageCodecException;
	
	/**
	 * service
	 * @param output
	 * @param responseMessage
	 * @param requestMessage 如果在调用{@link #decode(InputMessage)}时出现异常则可能是空
	 * @throws IOException
	 * @throws RemoteMessageCodecException
	 */
	void encode(OutputMessage output, RemoteResponseMessage responseMessage, @Nullable RemoteRequestMessage requestMessage) throws IOException, RemoteMessageCodecException;
}
