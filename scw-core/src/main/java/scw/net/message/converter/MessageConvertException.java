package scw.net.message.converter;


public class MessageConvertException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public MessageConvertException(Throwable cause) {
		super(cause);
	}
	
	public MessageConvertException(String message) {
		super(message);
	}

	public MessageConvertException(String message, Throwable cause) {
		super(message, cause);
	}
}