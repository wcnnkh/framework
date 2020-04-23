package scw.net.rpc;

public class RpcServiceException extends RpcException{
	private static final long serialVersionUID = 1L;

	public RpcServiceException(String message, Throwable cause) {
		super(message, cause);
	}
}
