package scw.net.http.server.mvc.exception;

/**
 * 带描述的通用异常处理
 * 
 * @author shuchaowen
 *
 */
public class TapeDescriptionException extends RuntimeException implements ErrorMessage {
	private static final long serialVersionUID = 1L;

	public TapeDescriptionException(String message) {
		super(message);
	}

	public String getErrorMessage() {
		return getMessage();
	}

}
