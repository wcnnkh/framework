package scw.rpc;

import scw.lang.NestedRuntimeException;

public class RpcException extends NestedRuntimeException{
	private static final long serialVersionUID = 1L;

	public RpcException(String message, Throwable cause) {
		super(message, cause);
	}
}
