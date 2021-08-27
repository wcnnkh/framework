package io.basc.framework.rpc.remote;

import io.basc.framework.lang.NestedRuntimeException;

public class RemoteMessageCodecException extends NestedRuntimeException{
	private static final long serialVersionUID = 1L;
	
	public RemoteMessageCodecException(String msg) {
		super(msg);
	}

	public RemoteMessageCodecException(Throwable cause) {
		super(cause);
	}
}
