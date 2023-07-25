package io.basc.framework.messageing;

public class MessageDeliveryException extends MessagingException {
	private static final long serialVersionUID = 1L;

	public MessageDeliveryException(String description) {
		super(description);
	}

	public MessageDeliveryException(Message<?> undeliveredMessage) {
		super(undeliveredMessage);
	}

	public MessageDeliveryException(Message<?> undeliveredMessage, String description) {
		super(undeliveredMessage, description);
	}

	public MessageDeliveryException(Message<?> message, Throwable cause) {
		super(message, cause);
	}

	public MessageDeliveryException(Message<?> undeliveredMessage, String description, Throwable cause) {
		super(undeliveredMessage, description, cause);
	}

}