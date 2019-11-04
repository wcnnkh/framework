package scw.result.exception;

import scw.result.ErrorCode;
import scw.result.ErrorMessage;

public class ResultException extends RuntimeException implements ErrorCode, ErrorMessage {
	private static final long serialVersionUID = 1L;
	private final String message;
	private final int code;

	public ResultException(int code, String message) {
		super(message);
		this.message = message;
		this.code = code;
	}

	public ResultException(int code, String message, Throwable e) {
		super(message, e);
		this.message = message;
		this.code = code;
	}

	public String getErrorMessage() {
		return message;
	}

	public int getErrorCode() {
		return code;
	}

}
