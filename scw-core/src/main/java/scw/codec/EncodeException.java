package scw.codec;


public class EncodeException extends CodecException {
	private static final long serialVersionUID = 1L;

	public EncodeException(String msg) {
		super(msg);
	}

	public EncodeException(Throwable cause) {
		super(cause);
	}

	public EncodeException(String message, Throwable cause) {
		super(message, cause);
	}
}
