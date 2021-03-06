package scw.rpc.messageing;

import scw.lang.NestedRuntimeException;

public class RemoteMessageCodecException extends NestedRuntimeException{
	private static final long serialVersionUID = 1L;

	public RemoteMessageCodecException(Throwable cause) {
		super(cause);
	}
}
