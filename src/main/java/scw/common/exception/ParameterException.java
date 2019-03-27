package scw.common.exception;

/**
 * 参数异常
 * @author shuchaowen
 *
 */
public class ParameterException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public ParameterException(String message) {
		super(message);
	}

	public ParameterException(Throwable e) {
		super(e);
	}

	public ParameterException(Throwable e, String message) {
		super(message, e);
	}
}
