package scw.rcp.object;

import java.io.Serializable;

import scw.util.attribute.SimpleAttributes;

public class ObjectResponseMessage extends SimpleAttributes<String, Object> implements Serializable {
	private static final long serialVersionUID = 1L;
	private ObjectRequestMessage requestMessage;
	private Object response;
	private Throwable error;

	public Object getResponse() {
		return response;
	}

	public Throwable getError() {
		return error;
	}

	public ObjectRequestMessage getRequestMessage() {
		return requestMessage;
	}

	public void setResponse(Object response) {
		this.response = response;
	}

	public void setRequestMessage(ObjectRequestMessage requestMessage) {
		this.requestMessage = requestMessage;
	}

	public void setError(Throwable error) {
		this.error = error;
	}
}
