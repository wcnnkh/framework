package scw.integration.sms;

/**
 * 发短信异常
 * @author shuchaowen
 *
 */
public class SmsException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public SmsException(String message) {
		super(message);
	}

	public SmsException(String message, Throwable error) {
		super(message, error);
	}
}
