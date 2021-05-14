package scw.redis.connection;

public interface RedisSetsCommands {
	/**
	 * Add the specified members to the set stored at key. Specified members that
	 * are already a member of this set are ignored. If key does not exist, a new
	 * set is created before adding the specified members.
	 * 
	 * An error is returned when the value stored at key is not a set.
	 * 
	 * @param key
	 * @param members
	 * @return Integer reply: the number of elements that were added to the set, not
	 *         including all the elements already present in the set.
	 */
	Integer sadd(byte[] key, byte[]... members);

	/**
	 * https://redis.io/commands/scard<br/>
	 * <br/>
	 * Returns the set cardinality (number of elements) of the set stored at key.
	 * 
	 * @param key
	 * @return Integer reply: the cardinality (number of elements) of the set, or 0
	 *         if key does not exist.
	 */
	Integer scard(byte[] key);
}
