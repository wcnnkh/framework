package scw.rpc.remote;


public class DefaultRemoteResponseMessage extends DefaultRemoteMessageHeaders implements RemoteResponseMessage {
	private static final long serialVersionUID = 1L;
	private Object body;
	private Throwable throwable;

	public Object getBody() {
		return body;
	}

	public void setBody(Object body) {
		this.body = body;
	}

	public Throwable getThrowable() {
		return throwable;
	}

	public void setThrowable(Throwable throwable) {
		this.throwable = throwable;
	}
}
