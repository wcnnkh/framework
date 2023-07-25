package io.basc.framework.messageing;

public interface MessageHandler {
	/**
	 * Handle the given message.
	 * 
	 * @param message the message to be handled
	 * @throws MessagingException if the handler failed to process the message
	 */
	void handleMessage(Message<?> message) throws MessagingException;
}
