package io.basc.framework.messageing;

import io.basc.framework.lang.Nullable;

public interface PollableChannel extends MessageChannel {

	/**
	 * Receive a message from this channel, blocking indefinitely if necessary.
	 * 
	 * @return the next available {@link Message} or {@code null} if interrupted
	 */
	@Nullable
	Message<?> receive();

	/**
	 * Receive a message from this channel, blocking until either a message is
	 * available or the specified timeout period elapses.
	 * 
	 * @param timeout the timeout in milliseconds or
	 *                {@link MessageChannel#INDEFINITE_TIMEOUT}.
	 * @return the next available {@link Message} or {@code null} if the specified
	 *         timeout period elapses or the message reception is interrupted
	 */
	@Nullable
	Message<?> receive(long timeout);

}
