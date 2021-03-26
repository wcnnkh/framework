package scw.rpc;

import scw.lang.NestedRuntimeException;

public class RemoteException extends NestedRuntimeException {
	private static final long serialVersionUID = 1L;

	public RemoteException(String msg) {
		super(msg);
	}
	
	public RemoteException(Throwable cause) {
		super(cause);
	}

	public RemoteException(String message, Throwable cause) {
		super(message, cause);
	}

}
