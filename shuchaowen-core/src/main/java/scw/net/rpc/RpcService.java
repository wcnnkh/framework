package scw.net.rpc;

import scw.net.message.InputMessage;
import scw.net.message.OutputMessage;

public interface RpcService {
	void service(InputMessage inputMessage, OutputMessage outputMessage) throws RpcServiceException;
}
