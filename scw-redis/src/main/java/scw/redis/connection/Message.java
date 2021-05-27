package scw.redis.connection;

import java.io.Serializable;

public interface Message extends Serializable {

	/**
	 * Returns the body (or the payload) of the message.
	 *
	 * @return message body. Never {@literal null}.
	 */
	byte[] getBody();

	/**
	 * Returns the channel associated with the message.
	 *
	 * @return message channel. Never {@literal null}.
	 */
	byte[] getChannel();
}
