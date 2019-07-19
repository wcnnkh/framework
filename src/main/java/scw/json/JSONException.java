package scw.json;

public class JSONException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public JSONException() {
		super();
	}

	public JSONException(String message) {
		super(message);
	}

	public JSONException(String message, Throwable cause) {
		super(message, cause);
	}
}
