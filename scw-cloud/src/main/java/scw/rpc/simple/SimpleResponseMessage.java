package scw.rpc.simple;

import java.io.Serializable;

import scw.util.attribute.SimpleAttributes;

public class SimpleResponseMessage extends SimpleAttributes<String, Object> implements Serializable {
	private static final long serialVersionUID = 1L;
	private SimpleObjectRequestMessage requestMessage;
	private Object response;
	private Throwable error;

	public Object getResponse() {
		return response;
	}

	public Throwable getError() {
		return error;
	}

	public SimpleObjectRequestMessage getRequestMessage() {
		return requestMessage;
	}

	public void setResponse(Object response) {
		this.response = response;
	}

	public void setRequestMessage(SimpleObjectRequestMessage requestMessage) {
		this.requestMessage = requestMessage;
	}

	public void setError(Throwable error) {
		this.error = error;
	}
}
