package scw.redis.core;

import java.io.Serializable;

public interface Message<K, V> extends Serializable {

	/**
	 * Returns the body (or the payload) of the message.
	 *
	 * @return message body. Never {@literal null}.
	 */
	V getBody();

	/**
	 * Returns the channel associated with the message.
	 *
	 * @return message channel. Never {@literal null}.
	 */
	K getChannel();
}
