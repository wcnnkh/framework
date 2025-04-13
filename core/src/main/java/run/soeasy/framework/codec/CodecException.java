package run.soeasy.framework.codec;

public class CodecException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public CodecException(String msg) {
		super(msg);
	}

	public CodecException(Throwable cause) {
		super(cause);
	}

	public CodecException(String message, Throwable cause) {
		super(message, cause);
	}
}
