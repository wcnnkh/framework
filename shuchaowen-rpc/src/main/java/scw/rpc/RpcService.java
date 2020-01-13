package scw.rpc;

public interface RpcService {
	ResponseMessage service(RequestMessage requestMessage);
}