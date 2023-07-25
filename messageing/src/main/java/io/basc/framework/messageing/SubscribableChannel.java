package io.basc.framework.messageing;

public interface SubscribableChannel extends MessageChannel {

	/**
	 * Register a message handler.
	 * 
	 * @return {@code true} if the handler was subscribed or {@code false} if it was
	 *         already subscribed.
	 */
	boolean subscribe(MessageHandler handler);

	/**
	 * Un-register a message handler.
	 * 
	 * @return {@code true} if the handler was un-registered, or {@code false} if
	 *         was not registered.
	 */
	boolean unsubscribe(MessageHandler handler);
}
