package io.basc.framework.messageing;

public class MessageHandlingException extends MessagingException {
	private static final long serialVersionUID = 1L;

	public MessageHandlingException(Message<?> failedMessage) {
		super(failedMessage);
	}

	public MessageHandlingException(Message<?> message, String description) {
		super(message, description);
	}

	public MessageHandlingException(Message<?> failedMessage, Throwable cause) {
		super(failedMessage, cause);
	}

	public MessageHandlingException(Message<?> message, String description, Throwable cause) {
		super(message, description, cause);
	}

}