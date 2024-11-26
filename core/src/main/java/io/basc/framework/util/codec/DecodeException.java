package io.basc.framework.util.codec;


public class DecodeException extends CodecException {
	private static final long serialVersionUID = 1L;

	public DecodeException(String msg) {
		super(msg);
	}

	public DecodeException(Throwable cause) {
		super(cause);
	}

	public DecodeException(String message, Throwable cause) {
		super(message, cause);
	}
}
